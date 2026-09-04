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
		Assert.assertEquals("Table Dictionary is wrong", "{accounts={acct_sales_count=[[@22,99:99='a',<393>,2:24], [@49,248:248='a',<393>,5:14]], last_update=[[@38,183:183='a',<393>,3:64]], emp_id=[[@18,89:89='a',<393>,2:14], [@33,165:165='a',<393>,3:46]]}, employees={score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@10,54:64='rank_bucket',<393>,1:54]], emp_id=[[@57,283:283='e',<393>,6:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={rn=[[@44,206:207='rn',<393>,3:87], [@12,68:70='src',<393>,1:68], [@65,309:311='src',<393>,6:33]], acct_sales_count=[[@24,101:116='acct_sales_count',<393>,2:26], [@6,32:34='src',<393>,1:32]], emp_id=[[@20,91:96='emp_id',<393>,2:16], [@61,294:296='src',<393>,6:18]]}, update1={score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@10,54:64='rank_bucket',<393>,1:54]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_update1={assignments={score=[{name=acct_sales_count, table_ref=src}], rank_bucket=[{name=rn, table_ref=src}]}, table_dictionary={employees={score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@10,54:64='rank_bucket',<393>,1:54]], emp_id=[[@57,283:283='e',<393>,6:7]]}}, update_dictionary={score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@10,54:64='rank_bucket',<393>,1:54]]}, def_query0={window_ordered_by=[{name=last_update, table_ref=a}], query_dictionary={acct_sales_count=[[@24,101:116='acct_sales_count',<393>,2:26], [@6,32:34='src',<393>,1:32]], rn=[[@44,206:207='rn',<393>,3:87], [@12,68:70='src',<393>,1:68], [@65,309:311='src',<393>,6:33]], emp_id=[[@20,91:96='emp_id',<393>,2:16], [@61,294:296='src',<393>,6:18]]}, table_dictionary={accounts={acct_sales_count=[[@22,99:99='a',<393>,2:24], [@49,248:248='a',<393>,5:14]], last_update=[[@38,183:183='a',<393>,3:64]], emp_id=[[@18,89:89='a',<393>,2:14], [@33,165:165='a',<393>,3:46]]}}, window_partition_by=[{name=emp_id, table_ref=a}], filters=[{name=acct_sales_count, table_ref=a}], interface={acct_sales_count=[{name=acct_sales_count, table_ref=a}], rn=[{name=emp_id, table_ref=a}, {name=last_update, table_ref=a}], emp_id=[{name=emp_id, table_ref=a}]}, table_alias={a=accounts}}, filters=[{name=emp_id, table_ref=e}, {name=emp_id, table_ref=src}, {name=rn, table_ref=src}], table_alias={e=employees, src=query0}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{quota_feed={new_quota=[[@16,77:85='new_quota',<393>,2:31]], dept_id=[[@14,68:74='dept_id',<393>,2:22]], emp_id=[[@12,60:65='emp_id',<393>,2:14]]}, employees={quota=[[@4,24:28='quota',<393>,1:24]], active_flag=[[@38,169:169='e',<393>,3:61]], dept_id=[[@30,141:141='e',<393>,3:33]], emp_id=[[@22,115:115='e',<393>,3:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={dept_id=[[@14,68:74='dept_id',<393>,2:22], [@34,153:155='src',<393>,3:45]], new_quota=[[@16,77:85='new_quota',<393>,2:31], [@6,32:34='src',<393>,1:32]], emp_id=[[@12,60:65='emp_id',<393>,2:14], [@26,126:128='src',<393>,3:18]]}, update1={quota=[[@4,24:28='quota',<393>,1:24]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_update1={assignments={quota=[{name=new_quota, table_ref=src}]}, table_dictionary={employees={active_flag=[[@38,169:169='e',<393>,3:61]], dept_id=[[@30,141:141='e',<393>,3:33]], quota=[[@4,24:28='quota',<393>,1:24]], emp_id=[[@22,115:115='e',<393>,3:7]]}}, update_dictionary={quota=[[@4,24:28='quota',<393>,1:24]]}, def_query0={query_dictionary={new_quota=[[@16,77:85='new_quota',<393>,2:31], [@6,32:34='src',<393>,1:32]], dept_id=[[@14,68:74='dept_id',<393>,2:22], [@34,153:155='src',<393>,3:45]], emp_id=[[@12,60:65='emp_id',<393>,2:14], [@26,126:128='src',<393>,3:18]]}, table_dictionary={quota_feed={new_quota=[[@16,77:85='new_quota',<393>,2:31]], dept_id=[[@14,68:74='dept_id',<393>,2:22]], emp_id=[[@12,60:65='emp_id',<393>,2:14]]}}, interface={new_quota=[{name=new_quota, table_ref=quota_feed}], dept_id=[{name=dept_id, table_ref=quota_feed}], emp_id=[{name=emp_id, table_ref=quota_feed}]}}, filters=[{name=emp_id, table_ref=e}, {name=emp_id, table_ref=src}, {name=dept_id, table_ref=e}, {name=dept_id, table_ref=src}, {name=active_flag, table_ref=e}], table_alias={e=employees, src=query0}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{perf_feed={score=[[@12,73:77='score',<393>,2:22]], emp_id=[[@10,65:70='emp_id',<393>,2:14]]}, employees={missing_flag=[[@6,38:49='missing_flag',<393>,1:38]], review_flag=[[@4,24:34='review_flag',<393>,1:24]], emp_id=[[@18,106:106='e',<393>,3:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={score=[[@12,73:77='score',<393>,2:22], [@26,132:134='src',<393>,3:33]], emp_id=[[@10,65:70='emp_id',<393>,2:14], [@22,117:119='src',<393>,3:18]]}, update1={review_flag=[[@4,24:34='review_flag',<393>,1:24]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_update1={assignments={review_flag=[{name=missing_flag, table_ref=employees}]}, table_dictionary={employees={missing_flag=[[@6,38:49='missing_flag',<393>,1:38]], review_flag=[[@4,24:34='review_flag',<393>,1:24]], emp_id=[[@18,106:106='e',<393>,3:7]]}}, update_dictionary={review_flag=[[@4,24:34='review_flag',<393>,1:24]]}, def_query0={query_dictionary={score=[[@12,73:77='score',<393>,2:22], [@26,132:134='src',<393>,3:33]], emp_id=[[@10,65:70='emp_id',<393>,2:14], [@22,117:119='src',<393>,3:18]]}, table_dictionary={perf_feed={score=[[@12,73:77='score',<393>,2:22]], emp_id=[[@10,65:70='emp_id',<393>,2:14]]}}, interface={score=[{name=score, table_ref=perf_feed}], emp_id=[{name=emp_id, table_ref=perf_feed}]}}, filters=[{name=emp_id, table_ref=e}, {name=emp_id, table_ref=src}, {name=score, table_ref=src}], table_alias={e=employees, src=query0}}}",
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
		assertDiagnosticAtPosition(
				snippet,
				"AMBIGUOUS_COLUMN_REFERENCE",
				ParseDiagnostic.Severity.SEVERE_WARNING,
				"Ambiguous column reference 'missing_flag' at (l:4 c:70)",
				"missing_flag",
				4,
				70);

		Assert.assertEquals("AST is wrong", "{SQL={update={from={join={1={table={alias=src, query={select={1={column={name=emp_id, table_ref=null}}, 2={column={name=score, table_ref=null}}}, from={table={alias=null, table=perf_feed}}}}}, 2={join=join, on={condition={left={column={name=emp_id, table_ref=src}}, right={column={name=emp_id, table_ref=af}}, operator==}}}, 3={table={alias=af, table=audit_flags}}}}, where={and={1={condition={left={column={name=emp_id, table_ref=e}}, right={column={name=emp_id, table_ref=src}}, operator==}}, 2={condition={left={column={name=missing_flag, table_ref=e}}, right={column={name=missing_flag, table_ref=af}}, operator==}}, 3={condition={left={column={name=missing_flag, table_ref=null}}, right={literal=0}, operator=>}}}}, assignments={1={set={column={name=review_flag, table_ref=null}}, to={column={name=score, table_ref=src}}}}, table={alias=e, table=employees}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[review_flag]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{perf_feed={score=[[@14,70:74='score',<393>,2:22]], emp_id=[[@12,62:67='emp_id',<393>,2:14]]}, employees={missing_flag=[[@39,176:176='e',<393>,4:33]], review_flag=[[@4,24:34='review_flag',<393>,1:24]], emp_id=[[@31,150:150='e',<393>,4:7]]}, audit_flags={missing_flag=[[@43,193:194='af',<393>,4:50]], emp_id=[[@27,133:134='af',<393>,3:37]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={score=[[@14,70:74='score',<393>,2:22], [@6,38:40='src',<393>,1:38]], emp_id=[[@12,62:67='emp_id',<393>,2:14], [@23,120:122='src',<393>,3:24], [@35,161:163='src',<393>,4:18]]}, update1={review_flag=[[@4,24:34='review_flag',<393>,1:24]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_update1={assignments={review_flag=[{name=score, table_ref=src}]}, table_dictionary={employees={missing_flag=[[@39,176:176='e',<393>,4:33]], review_flag=[[@4,24:34='review_flag',<393>,1:24]], emp_id=[[@31,150:150='e',<393>,4:7]]}, audit_flags={missing_flag=[[@43,193:194='af',<393>,4:50]], emp_id=[[@27,133:134='af',<393>,3:37]]}}, update_dictionary={review_flag=[[@4,24:34='review_flag',<393>,1:24]]}, def_query0={query_dictionary={score=[[@14,70:74='score',<393>,2:22], [@6,38:40='src',<393>,1:38]], emp_id=[[@12,62:67='emp_id',<393>,2:14], [@23,120:122='src',<393>,3:24], [@35,161:163='src',<393>,4:18]]}, table_dictionary={perf_feed={score=[[@14,70:74='score',<393>,2:22]], emp_id=[[@12,62:67='emp_id',<393>,2:14]]}}, interface={score=[{name=score, table_ref=perf_feed}], emp_id=[{name=emp_id, table_ref=perf_feed}]}}, filters=[{name=emp_id, table_ref=src}, {name=emp_id, table_ref=af}, {name=emp_id, table_ref=e}, {name=missing_flag, table_ref=e}, {name=missing_flag, table_ref=af}, {name=missing_flag, table_ref=null}], table_alias={e=employees, src=query0, af=audit_flags}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{accounts={score=[[@22,108:108='a',<393>,2:28], [@39,201:201='a',<393>,5:18]], emp_id=[[@16,94:94='a',<393>,2:14], [@33,174:174='a',<393>,4:17]]}, employees={score=[[@44,212:212='e',<393>,5:29]], stale_flag=[[@10,53:62='stale_flag',<393>,1:53]], orphan_marker=[[@12,66:78='orphan_marker',<393>,1:66]], agg_score=[[@4,24:32='agg_score',<393>,1:24]], emp_id=[[@50,232:232='e',<393>,6:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={total_score=[[@27,120:130='total_score',<393>,2:40], [@6,36:38='src',<393>,1:36]], emp_id=[[@18,96:101='emp_id',<393>,2:16], [@54,243:245='src',<393>,6:18]]}, update1={agg_score=[[@4,24:32='agg_score',<393>,1:24]], stale_flag=[[@10,53:62='stale_flag',<393>,1:53]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_update1={assignments={stale_flag=[{name=orphan_marker, table_ref=employees}], agg_score=[{name=total_score, table_ref=src}]}, table_dictionary={employees={orphan_marker=[[@12,66:78='orphan_marker',<393>,1:66]], agg_score=[[@4,24:32='agg_score',<393>,1:24]], stale_flag=[[@10,53:62='stale_flag',<393>,1:53]], emp_id=[[@50,232:232='e',<393>,6:7]]}}, update_dictionary={stale_flag=[[@10,53:62='stale_flag',<393>,1:53]], agg_score=[[@4,24:32='agg_score',<393>,1:24]]}, def_query0={query_dictionary={total_score=[[@27,120:130='total_score',<393>,2:40], [@6,36:38='src',<393>,1:36]], emp_id=[[@18,96:101='emp_id',<393>,2:16], [@54,243:245='src',<393>,6:18]]}, table_dictionary={accounts={score=[[@22,108:108='a',<393>,2:28], [@39,201:201='a',<393>,5:18]], emp_id=[[@16,94:94='a',<393>,2:14], [@33,174:174='a',<393>,4:17]]}, employees={score=[[@44,212:212='e',<393>,5:29]]}}, grouped_by=[{name=emp_id, table_ref=a}], filters=[{name=score, table_ref=a}, {name=score, table_ref=e}], interface={total_score=[{name=score, table_ref=a}], emp_id=[{name=emp_id, table_ref=a}]}, table_alias={a=accounts}}, filters=[{name=emp_id, table_ref=e}, {name=emp_id, table_ref=src}], table_alias={e=employees, src=query0}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{accounts={last_update=[[@20,111:111='a',<393>,2:24], [@28,167:167='a',<393>,4:17]], emp_id=[[@16,101:101='a',<393>,2:14]]}, employees={unknown_rhs=[[@10,62:72='unknown_rhs',<393>,1:62]], most_recent_update=[[@4,24:41='most_recent_update',<393>,1:24]], shadow_col=[[@12,76:85='shadow_col',<393>,1:76]], emp_id=[[@35,198:198='e',<393>,5:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={last_update=[[@22,113:123='last_update',<393>,2:26], [@6,45:47='src',<393>,1:45]], emp_id=[[@18,103:108='emp_id',<393>,2:16], [@39,209:211='src',<393>,5:18]]}, update1={most_recent_update=[[@4,24:41='most_recent_update',<393>,1:24]], unknown_rhs=[[@10,62:72='unknown_rhs',<393>,1:62]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_update1={assignments={unknown_rhs=[{name=shadow_col, table_ref=employees}], most_recent_update=[{name=last_update, table_ref=src}]}, table_dictionary={employees={most_recent_update=[[@4,24:41='most_recent_update',<393>,1:24]], shadow_col=[[@12,76:85='shadow_col',<393>,1:76]], unknown_rhs=[[@10,62:72='unknown_rhs',<393>,1:62]], emp_id=[[@35,198:198='e',<393>,5:7]]}}, update_dictionary={unknown_rhs=[[@10,62:72='unknown_rhs',<393>,1:62]], most_recent_update=[[@4,24:41='most_recent_update',<393>,1:24]]}, def_query0={query_dictionary={last_update=[[@22,113:123='last_update',<393>,2:26], [@6,45:47='src',<393>,1:45]], emp_id=[[@18,103:108='emp_id',<393>,2:16], [@39,209:211='src',<393>,5:18]]}, table_dictionary={accounts={last_update=[[@20,111:111='a',<393>,2:24], [@28,167:167='a',<393>,4:17]], emp_id=[[@16,101:101='a',<393>,2:14]]}}, ordered_by=[{name=last_update, table_ref=a}], interface={last_update=[{name=last_update, table_ref=a}], emp_id=[{name=emp_id, table_ref=a}]}, table_alias={a=accounts}}, filters=[{name=emp_id, table_ref=e}, {name=emp_id, table_ref=src}], table_alias={e=employees, src=query0}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{accounts={score=[[@20,104:104='a',<393>,2:24]], last_update=[[@36,177:177='a',<393>,3:64]], emp_id=[[@16,94:94='a',<393>,2:14], [@31,159:159='a',<393>,3:46]]}, employees={fallback_note=[[@10,47:59='fallback_note',<393>,1:47]], unqualified_note=[[@12,63:78='unqualified_note',<393>,1:63]], top_score=[[@4,24:32='top_score',<393>,1:24]], emp_id=[[@53,261:261='e',<393>,6:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={score=[[@22,106:110='score',<393>,2:26], [@6,36:38='src',<393>,1:36]], rn=[[@42,200:201='rn',<393>,3:87], [@47,242:243='rn',<393>,5:14]], emp_id=[[@18,96:101='emp_id',<393>,2:16], [@57,272:274='src',<393>,6:18]]}, update1={fallback_note=[[@10,47:59='fallback_note',<393>,1:47]], top_score=[[@4,24:32='top_score',<393>,1:24]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_update1={assignments={fallback_note=[{name=unqualified_note, table_ref=employees}], top_score=[{name=score, table_ref=src}]}, table_dictionary={employees={fallback_note=[[@10,47:59='fallback_note',<393>,1:47]], unqualified_note=[[@12,63:78='unqualified_note',<393>,1:63]], top_score=[[@4,24:32='top_score',<393>,1:24]], emp_id=[[@53,261:261='e',<393>,6:7]]}}, update_dictionary={fallback_note=[[@10,47:59='fallback_note',<393>,1:47]], top_score=[[@4,24:32='top_score',<393>,1:24]]}, def_query0={window_ordered_by=[{name=last_update, table_ref=a}], query_dictionary={score=[[@22,106:110='score',<393>,2:26], [@6,36:38='src',<393>,1:36]], rn=[[@42,200:201='rn',<393>,3:87], [@47,242:243='rn',<393>,5:14]], emp_id=[[@18,96:101='emp_id',<393>,2:16], [@57,272:274='src',<393>,6:18]]}, table_dictionary={accounts={score=[[@20,104:104='a',<393>,2:24]], last_update=[[@36,177:177='a',<393>,3:64]], emp_id=[[@16,94:94='a',<393>,2:14], [@31,159:159='a',<393>,3:46]]}}, window_partition_by=[{name=emp_id, table_ref=a}], filters=[{name=rn, table_ref=query0}], interface={score=[{name=score, table_ref=a}], rn=[{name=emp_id, table_ref=a}, {name=last_update, table_ref=a}], emp_id=[{name=emp_id, table_ref=a}]}, table_alias={a=accounts}}, filters=[{name=emp_id, table_ref=e}, {name=emp_id, table_ref=src}], table_alias={e=employees, src=query0}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{accounts={score=[[@22,108:108='a',<393>,2:28], [@32,171:171='a',<393>,4:14]], emp_id=[[@16,94:94='a',<393>,2:14], [@41,210:210='a',<393>,5:17]]}, employees={stale_flag=[[@10,53:62='stale_flag',<393>,1:53]], orphan_marker=[[@12,66:78='orphan_marker',<393>,1:66]], agg_score=[[@4,24:32='agg_score',<393>,1:24]], min_score=[[@36,181:181='e',<393>,4:24]], emp_id=[[@47,231:231='e',<393>,6:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={total_score=[[@27,120:130='total_score',<393>,2:40], [@6,36:38='src',<393>,1:36]], emp_id=[[@18,96:101='emp_id',<393>,2:16], [@51,242:244='src',<393>,6:18]]}, update1={agg_score=[[@4,24:32='agg_score',<393>,1:24]], stale_flag=[[@10,53:62='stale_flag',<393>,1:53]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_update1={assignments={stale_flag=[{name=orphan_marker, table_ref=employees}], agg_score=[{name=total_score, table_ref=src}]}, table_dictionary={employees={orphan_marker=[[@12,66:78='orphan_marker',<393>,1:66]], agg_score=[[@4,24:32='agg_score',<393>,1:24]], stale_flag=[[@10,53:62='stale_flag',<393>,1:53]], emp_id=[[@47,231:231='e',<393>,6:7]]}}, update_dictionary={stale_flag=[[@10,53:62='stale_flag',<393>,1:53]], agg_score=[[@4,24:32='agg_score',<393>,1:24]]}, def_query0={query_dictionary={total_score=[[@27,120:130='total_score',<393>,2:40], [@6,36:38='src',<393>,1:36]], emp_id=[[@18,96:101='emp_id',<393>,2:16], [@51,242:244='src',<393>,6:18]]}, table_dictionary={accounts={score=[[@22,108:108='a',<393>,2:28], [@32,171:171='a',<393>,4:14]], emp_id=[[@16,94:94='a',<393>,2:14], [@41,210:210='a',<393>,5:17]]}, employees={min_score=[[@36,181:181='e',<393>,4:24]]}}, grouped_by=[{name=emp_id, table_ref=a}], filters=[{name=score, table_ref=a}, {name=min_score, table_ref=e}], interface={total_score=[{name=score, table_ref=a}], emp_id=[{name=emp_id, table_ref=a}]}, table_alias={a=accounts}}, filters=[{name=emp_id, table_ref=e}, {name=emp_id, table_ref=src}], table_alias={e=employees, src=query0}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{accounts={score=[[@20,104:104='a',<393>,2:24]], dept_id=[[@32,183:183='a',<393>,4:31]], emp_id=[[@16,94:94='a',<393>,2:14]]}, departments={dept_id=[[@36,195:195='d',<393>,4:43]], region=[[@40,209:209='d',<393>,4:57]]}, employees={stale_flag=[[@10,53:62='stale_flag',<393>,1:53]], orphan_marker=[[@12,66:78='orphan_marker',<393>,1:66]], region=[[@44,220:220='e',<393>,4:68]], agg_score=[[@4,24:32='agg_score',<393>,1:24]], emp_id=[[@50,241:241='e',<393>,5:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={total_score=[[@24,115:125='total_score',<393>,2:35], [@6,36:38='src',<393>,1:36]], emp_id=[[@18,96:101='emp_id',<393>,2:16], [@54,252:254='src',<393>,5:18]]}, update1={agg_score=[[@4,24:32='agg_score',<393>,1:24]], stale_flag=[[@10,53:62='stale_flag',<393>,1:53]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_update1={assignments={stale_flag=[{name=orphan_marker, table_ref=employees}], agg_score=[{name=total_score, table_ref=src}]}, table_dictionary={employees={orphan_marker=[[@12,66:78='orphan_marker',<393>,1:66]], agg_score=[[@4,24:32='agg_score',<393>,1:24]], stale_flag=[[@10,53:62='stale_flag',<393>,1:53]], emp_id=[[@50,241:241='e',<393>,5:7]]}}, update_dictionary={stale_flag=[[@10,53:62='stale_flag',<393>,1:53]], agg_score=[[@4,24:32='agg_score',<393>,1:24]]}, def_query0={query_dictionary={total_score=[[@24,115:125='total_score',<393>,2:35], [@6,36:38='src',<393>,1:36]], emp_id=[[@18,96:101='emp_id',<393>,2:16], [@54,252:254='src',<393>,5:18]]}, table_dictionary={accounts={score=[[@20,104:104='a',<393>,2:24]], dept_id=[[@32,183:183='a',<393>,4:31]], emp_id=[[@16,94:94='a',<393>,2:14]]}, departments={dept_id=[[@36,195:195='d',<393>,4:43]], region=[[@40,209:209='d',<393>,4:57]]}, employees={region=[[@44,220:220='e',<393>,4:68]]}}, filters=[{name=dept_id, table_ref=a}, {name=dept_id, table_ref=d}, {name=region, table_ref=d}, {name=region, table_ref=e}], interface={total_score=[{name=score, table_ref=a}], emp_id=[{name=emp_id, table_ref=a}]}, table_alias={a=accounts, d=departments}}, filters=[{name=emp_id, table_ref=e}, {name=emp_id, table_ref=src}], table_alias={e=employees, src=query0}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void updateDictionaryHandlingQualifyInSubqueryWithTargetTableRefAndOrphanRhsV10() {
		final String query = " update employees e set top_score = src.score, stale_flag = orphan_marker"
				+ "\n from (select a.emp_id, a.score,"
				+ "\n              row_number() over (partition by a.emp_id order by a.score desc) as rn"
				+ "\n         from accounts a"
				+ "\n        qualify rn <= e.<max_rank>) src"
				+ "\n where e.emp_id = src.emp_id";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={update={from={table={alias=src, query={select={1={column={name=emp_id, table_ref=a}}, 2={column={name=score, table_ref=a}}, 3={alias=rn, window_function={over={partition_by={1={column={name=emp_id, table_ref=a}}}, orderby={1={null_order=null, predicand={column={name=score, table_ref=a}}, sort_order=desc}}}, function={function_name=row_number, parameters=null}}}}, from={table={alias=a, table=accounts}}, qualify={condition={left={column={name=rn, table_ref=null}}, right={column={substitution={name=<max_rank>, type=column}, table_ref=e}}, operator=<=}}}}}, where={condition={left={column={name=emp_id, table_ref=e}}, right={column={name=emp_id, table_ref=src}}, operator==}}, assignments={1={set={column={name=top_score, table_ref=null}}, to={column={name=score, table_ref=src}}}, 2={set={column={name=stale_flag, table_ref=null}}, to={column={name=orphan_marker, table_ref=null}}}}, table={alias=e, table=employees}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[stale_flag, top_score]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<max_rank>=column}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
		 "{accounts={score=[[@20,98:98='a',<393>,2:24], [@36,171:171='a',<393>,3:64]], emp_id=[[@16,88:88='a',<393>,2:14], [@31,153:153='a',<393>,3:46]]}, employees={<max_rank>=[[@49,238:238='e',<393>,5:22]], stale_flag=[[@10,47:56='stale_flag',<393>,1:47]], top_score=[[@4,24:32='top_score',<393>,1:24]], orphan_marker=[[@12,60:72='orphan_marker',<393>,1:60]], emp_id=[[@55,263:263='e',<393>,6:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", 
		"{query0={score=[[@22,100:104='score',<393>,2:26], [@6,36:38='src',<393>,1:36]], rn=[[@42,188:189='rn',<393>,3:81], [@47,232:233='rn',<393>,5:16]], emp_id=[[@18,90:95='emp_id',<393>,2:16], [@59,274:276='src',<393>,6:18]]}, update1={stale_flag=[[@10,47:56='stale_flag',<393>,1:47]], top_score=[[@4,24:32='top_score',<393>,1:24]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_update1={assignments={stale_flag=[{name=orphan_marker, table_ref=employees}], top_score=[{name=score, table_ref=src}]}, table_dictionary={employees={orphan_marker=[[@12,60:72='orphan_marker',<393>,1:60]], stale_flag=[[@10,47:56='stale_flag',<393>,1:47]], top_score=[[@4,24:32='top_score',<393>,1:24]], emp_id=[[@55,263:263='e',<393>,6:7]]}}, update_dictionary={stale_flag=[[@10,47:56='stale_flag',<393>,1:47]], top_score=[[@4,24:32='top_score',<393>,1:24]]}, def_query0={window_ordered_by=[{name=score, table_ref=a}], query_dictionary={score=[[@22,100:104='score',<393>,2:26], [@6,36:38='src',<393>,1:36]], rn=[[@42,188:189='rn',<393>,3:81], [@47,232:233='rn',<393>,5:16]], emp_id=[[@18,90:95='emp_id',<393>,2:16], [@59,274:276='src',<393>,6:18]]}, table_dictionary={accounts={score=[[@20,98:98='a',<393>,2:24], [@36,171:171='a',<393>,3:64]], emp_id=[[@16,88:88='a',<393>,2:14], [@31,153:153='a',<393>,3:46]]}, employees={<max_rank>=[[@49,238:238='e',<393>,5:22]]}}, window_partition_by=[{name=emp_id, table_ref=a}], filters=[{name=rn, table_ref=query0}, {substitution={name=<max_rank>, type=column}, table_ref=e}], interface={score=[{name=score, table_ref=a}], rn=[{name=emp_id, table_ref=a}, {name=score, table_ref=a}], emp_id=[{name=emp_id, table_ref=a}]}, table_alias={a=accounts}}, filters=[{name=emp_id, table_ref=e}, {name=emp_id, table_ref=src}], table_alias={e=employees, src=query0}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{accounts={score=[[@20,106:106='a',<393>,2:24], [@33,177:177='a',<393>,4:38]], emp_id=[[@16,96:96='a',<393>,2:14]]}, employees={stale_flag=[[@10,55:64='stale_flag',<393>,1:55]], most_recent_score=[[@4,24:40='most_recent_score',<393>,1:24]], sort_priority=[[@28,156:156='e',<393>,4:17]], orphan_marker=[[@12,68:80='orphan_marker',<393>,1:68]], emp_id=[[@40,202:202='e',<393>,5:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={score=[[@22,108:112='score',<393>,2:26], [@6,44:46='src',<393>,1:44]], emp_id=[[@18,98:103='emp_id',<393>,2:16], [@44,213:215='src',<393>,5:18]]}, update1={most_recent_score=[[@4,24:40='most_recent_score',<393>,1:24]], stale_flag=[[@10,55:64='stale_flag',<393>,1:55]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_update1={assignments={stale_flag=[{name=orphan_marker, table_ref=employees}], most_recent_score=[{name=score, table_ref=src}]}, table_dictionary={employees={most_recent_score=[[@4,24:40='most_recent_score',<393>,1:24]], orphan_marker=[[@12,68:80='orphan_marker',<393>,1:68]], stale_flag=[[@10,55:64='stale_flag',<393>,1:55]], emp_id=[[@40,202:202='e',<393>,5:7]]}}, update_dictionary={stale_flag=[[@10,55:64='stale_flag',<393>,1:55]], most_recent_score=[[@4,24:40='most_recent_score',<393>,1:24]]}, def_query0={query_dictionary={score=[[@22,108:112='score',<393>,2:26], [@6,44:46='src',<393>,1:44]], emp_id=[[@18,98:103='emp_id',<393>,2:16], [@44,213:215='src',<393>,5:18]]}, table_dictionary={accounts={score=[[@20,106:106='a',<393>,2:24], [@33,177:177='a',<393>,4:38]], emp_id=[[@16,96:96='a',<393>,2:14]]}, employees={sort_priority=[[@28,156:156='e',<393>,4:17]]}}, ordered_by=[{name=sort_priority, table_ref=e}, {name=score, table_ref=a}], interface={score=[{name=score, table_ref=a}], emp_id=[{name=emp_id, table_ref=a}]}, table_alias={a=accounts}}, filters=[{name=emp_id, table_ref=e}, {name=emp_id, table_ref=src}], table_alias={e=employees, src=query0}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{accounts={score=[[@20,101:101='a',<393>,2:24]], emp_id=[[@16,91:91='a',<393>,2:14]]}, employees={latest_score=[[@4,24:35='latest_score',<393>,1:24]], stale_flag=[[@10,50:59='stale_flag',<393>,1:50]], orphan_marker=[[@12,63:75='orphan_marker',<393>,1:63]], emp_id=[[@29,146:146='e',<393>,4:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={score=[[@22,103:107='score',<393>,2:26], [@6,39:41='src',<393>,1:39]], emp_id=[[@18,93:98='emp_id',<393>,2:16], [@33,157:159='src',<393>,4:18]]}, update1={latest_score=[[@4,24:35='latest_score',<393>,1:24]], stale_flag=[[@10,50:59='stale_flag',<393>,1:50]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_update1={assignments={latest_score=[{name=score, table_ref=src}], stale_flag=[{name=orphan_marker, table_ref=employees}]}, table_dictionary={employees={latest_score=[[@4,24:35='latest_score',<393>,1:24]], orphan_marker=[[@12,63:75='orphan_marker',<393>,1:63]], stale_flag=[[@10,50:59='stale_flag',<393>,1:50]], emp_id=[[@29,146:146='e',<393>,4:7]]}}, update_dictionary={latest_score=[[@4,24:35='latest_score',<393>,1:24]], stale_flag=[[@10,50:59='stale_flag',<393>,1:50]]}, def_query0={query_dictionary={score=[[@22,103:107='score',<393>,2:26], [@6,39:41='src',<393>,1:39]], emp_id=[[@18,93:98='emp_id',<393>,2:16], [@33,157:159='src',<393>,4:18]]}, table_dictionary={accounts={score=[[@20,101:101='a',<393>,2:24]], emp_id=[[@16,91:91='a',<393>,2:14]]}}, interface={score=[{name=score, table_ref=a}], emp_id=[{name=emp_id, table_ref=a}]}, table_alias={a=accounts}}, filters=[{name=emp_id, table_ref=e}, {name=emp_id, table_ref=src}], table_alias={e=employees, src=query0}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void updateFromNestedSubqueryDepth2CorrelatedTargetQualifiedColumnV13() {
		final String query = " update employees e set e.<agg_score> = src.total_score"
				+ "\n from (select inner_sq.<emp_id>, sum(inner_sq.<score>) as total_score"
				+ "\n         from (select a.<emp_id>, a.<score>"
				+ "\n                 from accounts a"
				+ "\n                where a.<dept_id> = e.<dept_id>) inner_sq"
				+ "\n        group by inner_sq.<emp_id>) src"
				+ "\n where e.<emp_id> = src.<emp_id>";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={update={from={table={alias=src, query={select={1={column={substitution={name=<emp_id>, type=column}, table_ref=inner_sq}}, 2={function={function_name=sum, qualifier=null, parameters={column={substitution={name=<score>, type=column}, table_ref=inner_sq}}}, alias=total_score}}, from={table={alias=inner_sq, query={select={1={column={substitution={name=<emp_id>, type=column}, table_ref=a}}, 2={column={substitution={name=<score>, type=column}, table_ref=a}}}, from={table={alias=a, table=accounts}}, where={condition={left={column={substitution={name=<dept_id>, type=column}, table_ref=a}}, right={column={substitution={name=<dept_id>, type=column}, table_ref=e}}, operator==}}}}}, groupby={1={column={substitution={name=<emp_id>, type=column}, table_ref=inner_sq}}}}}}, where={condition={left={column={substitution={name=<emp_id>, type=column}, table_ref=e}}, right={column={substitution={name=<emp_id>, type=column}, table_ref=src}}, operator==}}, assignments={1={set={column={substitution={name=<agg_score>, type=column}, table_ref=e}}, to={column={name=total_score, table_ref=src}}}}, table={alias=e, table=employees}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<agg_score>=column, <score>=column, <emp_id>=column, <dept_id>=column}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{accounts={<score>=[[@33,160:160='a',<393>,3:34]], <emp_id>=[[@29,148:148='a',<393>,3:22]], <dept_id>=[[@40,225:225='a',<393>,5:22]]}, employees={<agg_score>=[[@4,24:24='e',<393>,1:24]], <emp_id>=[[@57,308:308='e',<393>,7:7]], <dept_id>=[[@44,239:239='e',<393>,5:36]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={<score>=[[@35,162:168='<score>',<327>,3:36], [@20,93:100='inner_sq',<393>,2:37]], <emp_id>=[[@31,150:157='<emp_id>',<327>,3:24], [@14,70:77='inner_sq',<393>,2:14], [@51,278:285='inner_sq',<393>,6:17]]}, query1={total_score=[[@25,114:124='total_score',<393>,2:58], [@8,40:42='src',<393>,1:40]], <emp_id>=[[@16,79:86='<emp_id>',<327>,2:23], [@61,321:323='src',<393>,7:20]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_update2={table_dictionary={employees={<agg_score>=[[@4,24:24='e',<393>,1:24]], <emp_id>=[[@57,308:308='e',<393>,7:7]]}}, def_query1={query_dictionary={<emp_id>=[[@16,79:86='<emp_id>',<327>,2:23], [@61,321:323='src',<393>,7:20]], total_score=[[@25,114:124='total_score',<393>,2:58], [@8,40:42='src',<393>,1:40]]}, grouped_by=[{substitution={name=<emp_id>, type=column}, table_ref=inner_sq}], def_query0={query_dictionary={<score>=[[@35,162:168='<score>',<327>,3:36], [@20,93:100='inner_sq',<393>,2:37]], <emp_id>=[[@31,150:157='<emp_id>',<327>,3:24], [@14,70:77='inner_sq',<393>,2:14], [@51,278:285='inner_sq',<393>,6:17]]}, table_dictionary={accounts={<score>=[[@33,160:160='a',<393>,3:34]], <emp_id>=[[@29,148:148='a',<393>,3:22]], <dept_id>=[[@40,225:225='a',<393>,5:22]]}, employees={<dept_id>=[[@44,239:239='e',<393>,5:36]]}}, filters=[{substitution={name=<dept_id>, type=column}, table_ref=a}, {substitution={name=<dept_id>, type=column}, table_ref=e}], interface={<score>=[{substitution={name=<score>, type=column}, table_ref=a}], <emp_id>=[{substitution={name=<emp_id>, type=column}, table_ref=a}]}, table_alias={a=accounts}}, interface={<emp_id>=[{substitution={name=<emp_id>, type=column}, table_ref=inner_sq}], total_score=[{substitution={name=<score>, type=column}, table_ref=inner_sq}]}, table_alias={inner_sq=query0}}, filters=[{substitution={name=<emp_id>, type=column}, table_ref=e}, {substitution={name=<emp_id>, type=column}, table_ref=src}], table_alias={e=employees, src=query1}}}",
				extractor.getSymbolTable().toString());
		Assert.assertTrue("Expected correlated target dept_id on innermost subquery filters",
				extractor.getSymbolTable().toString().contains("filters=[{substitution={name=<dept_id>, type=column}, table_ref=a}, {substitution={name=<dept_id>, type=column}, table_ref=e}]"));
	}


	@Test
	public void updateFromNestedSubqueryDepth3CorrelatedTargetQualifiedColumnV14() {
		final String query = " update employees e set e.<agg_score> = src.val"
				+ "\n from (select l1.<emp_id>, l1.val"
				+ "\n         from (select l0.<emp_id>, l0.val"
				+ "\n                 from (select a.<emp_id>, a.<score> as val"
				+ "\n                         from accounts a"
				+ "\n                        where a.<region> = e.<region>) l0) l1) src"
				+ "\n where e.<emp_id> = src.<emp_id>";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		
		Assert.assertEquals("AST is wrong", "{SQL={update={from={table={alias=src, query={select={1={column={substitution={name=<emp_id>, type=column}, table_ref=l1}}, 2={column={name=val, table_ref=l1}}}, from={table={alias=l1, query={select={1={column={substitution={name=<emp_id>, type=column}, table_ref=l0}}, 2={column={name=val, table_ref=l0}}}, from={table={alias=l0, query={select={1={column={substitution={name=<emp_id>, type=column}, table_ref=a}}, 2={column={substitution={name=<score>, type=column}, table_ref=a}, alias=val}}, from={table={alias=a, table=accounts}}, where={condition={left={column={substitution={name=<region>, type=column}, table_ref=a}}, right={column={substitution={name=<region>, type=column}, table_ref=e}}, operator==}}}}}}}}}}}, where={condition={left={column={substitution={name=<emp_id>, type=column}, table_ref=e}}, right={column={substitution={name=<emp_id>, type=column}, table_ref=src}}, operator==}}, assignments={1={set={column={substitution={name=<agg_score>, type=column}, table_ref=e}}, to={column={name=val, table_ref=src}}}}, table={alias=e, table=employees}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<agg_score>=column, <score>=column, <emp_id>=column, <region>=column}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{accounts={<score>=[[@38,166:166='a',<393>,4:42]], <emp_id>=[[@34,154:154='a',<393>,4:30]], <region>=[[@47,254:254='a',<393>,6:30]]}, employees={<agg_score>=[[@4,24:24='e',<393>,1:24]], <emp_id>=[[@61,298:298='e',<393>,7:7]], <region>=[[@51,267:267='e',<393>,6:43]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={val=[[@42,179:181='val',<393>,4:55], [@28,117:118='l0',<393>,3:35]], <emp_id>=[[@36,156:163='<emp_id>',<327>,4:32], [@24,104:105='l0',<393>,3:22]]}, query1={val=[[@30,120:122='val',<393>,3:38], [@18,75:76='l1',<393>,2:27]], <emp_id>=[[@26,107:114='<emp_id>',<327>,3:25], [@14,62:63='l1',<393>,2:14]]}, query2={val=[[@20,78:80='val',<393>,2:30], [@8,40:42='src',<393>,1:40]], <emp_id>=[[@16,65:72='<emp_id>',<327>,2:17], [@65,311:313='src',<393>,7:20]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_update3={table_dictionary={employees={<agg_score>=[[@4,24:24='e',<393>,1:24]], <emp_id>=[[@61,298:298='e',<393>,7:7]]}}, filters=[{substitution={name=<emp_id>, type=column}, table_ref=e}, {substitution={name=<emp_id>, type=column}, table_ref=src}], table_alias={e=employees, src=query2}, def_query2={query_dictionary={val=[[@20,78:80='val',<393>,2:30], [@8,40:42='src',<393>,1:40]], <emp_id>=[[@16,65:72='<emp_id>',<327>,2:17], [@65,311:313='src',<393>,7:20]]}, def_query1={query_dictionary={val=[[@30,120:122='val',<393>,3:38], [@18,75:76='l1',<393>,2:27]], <emp_id>=[[@26,107:114='<emp_id>',<327>,3:25], [@14,62:63='l1',<393>,2:14]]}, def_query0={query_dictionary={val=[[@42,179:181='val',<393>,4:55], [@28,117:118='l0',<393>,3:35]], <emp_id>=[[@36,156:163='<emp_id>',<327>,4:32], [@24,104:105='l0',<393>,3:22]]}, table_dictionary={accounts={<score>=[[@38,166:166='a',<393>,4:42]], <emp_id>=[[@34,154:154='a',<393>,4:30]], <region>=[[@47,254:254='a',<393>,6:30]]}, employees={<region>=[[@51,267:267='e',<393>,6:43]]}}, filters=[{substitution={name=<region>, type=column}, table_ref=a}, {substitution={name=<region>, type=column}, table_ref=e}], interface={val=[{substitution={name=<score>, type=column}, table_ref=a}], <emp_id>=[{substitution={name=<emp_id>, type=column}, table_ref=a}]}, table_alias={a=accounts}}, interface={val=[{name=val, table_ref=l0}], <emp_id>=[{substitution={name=<emp_id>, type=column}, table_ref=l0}]}, table_alias={l0=query0}}, interface={val=[{name=val, table_ref=l1}], <emp_id>=[{substitution={name=<emp_id>, type=column}, table_ref=l1}]}, table_alias={l1=query1}}}}",
				extractor.getSymbolTable().toString());
		Assert.assertTrue("Expected correlated target region on innermost subquery filters",
				extractor.getSymbolTable().toString().contains("filters=[{substitution={name=<region>, type=column}, table_ref=a}, {substitution={name=<region>, type=column}, table_ref=e}]"));
	}


	/*
	===============================================================================
	  INSERT ... VALUES
	===============================================================================
	*/

	@Test
	public void insertValuesPlainMatrixNoTargetColumnsV1() {
		final String query = " insert into employees values (100, 1, 'active')";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={insert={preamble=insert_into, from={values={matrix={1={row={1={literal=100}, 2={literal=1}, 3={literal='active'}}}}}}, target_table={table={alias=null, table=employees}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[$1, $2, $3]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{employees={$1=[[@4,30:30='(',<287>,1:30]], $2=[[@4,30:30='(',<287>,1:30]], $3=[[@4,30:30='(',<287>,1:30]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{values0={$1=[[@4,30:30='(',<287>,1:30]], $2=[[@4,30:30='(',<287>,1:30]], $3=[[@4,30:30='(',<287>,1:30]]}, insert1={$1=[[@4,30:30='(',<287>,1:30]], $2=[[@4,30:30='(',<287>,1:30]], $3=[[@4,30:30='(',<287>,1:30]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_insert1={query_dictionary={$1=[[@4,30:30='(',<287>,1:30]], $2=[[@4,30:30='(',<287>,1:30]], $3=[[@4,30:30='(',<287>,1:30]]}, table_dictionary={employees={$1=[[@4,30:30='(',<287>,1:30]], $2=[[@4,30:30='(',<287>,1:30]], $3=[[@4,30:30='(',<287>,1:30]]}}, def_values0={query_dictionary={$1=[[@4,30:30='(',<287>,1:30]], $2=[[@4,30:30='(',<287>,1:30]], $3=[[@4,30:30='(',<287>,1:30]]}, interface={$1=[], $2=[], $3=[]}}, interface={$1=[{name=$1, table_ref=values0}], $2=[{name=$2, table_ref=values0}], $3=[{name=$3, table_ref=values0}]}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void insertValuesExplicitTargetColumnsV2() {
		final String query = " insert into employees (score, rank_bucket) values (100, 1)";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={insert={preamble=insert_into, from={values={matrix={1={row={1={literal=100}, 2={literal=1}}}}}}, target_table={table={alias=null, table=employees}}, columns={1={column={name=score, table_ref=null}}, 2={column={name=rank_bucket, table_ref=null}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[score, rank_bucket]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{employees={score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@6,31:41='rank_bucket',<393>,1:31]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{values0={$1=[[@9,51:51='(',<287>,1:51]], $2=[[@9,51:51='(',<287>,1:51]]}, insert1={score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@6,31:41='rank_bucket',<393>,1:31]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_insert1={query_dictionary={score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@6,31:41='rank_bucket',<393>,1:31]]}, table_dictionary={employees={score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@6,31:41='rank_bucket',<393>,1:31]]}}, def_values0={query_dictionary={$1=[[@9,51:51='(',<287>,1:51]], $2=[[@9,51:51='(',<287>,1:51]]}, interface={$1=[], $2=[]}}, interface={score=[{name=$1, table_ref=values0}], rank_bucket=[{name=$2, table_ref=values0}]}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void insertValuesMultiRowNoTargetColumnsV3() {
		final String query = " insert into employees values (100, 1), (200, 2)";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={insert={preamble=insert_into, from={values={matrix={1={row={1={literal=100}, 2={literal=1}}}, 2={row={1={literal=200}, 2={literal=2}}}}}}, target_table={table={alias=null, table=employees}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[$1, $2]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{employees={$1=[[@4,30:30='(',<287>,1:30], [@10,40:40='(',<287>,1:40]], $2=[[@4,30:30='(',<287>,1:30], [@10,40:40='(',<287>,1:40]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{values0={$1=[[@4,30:30='(',<287>,1:30], [@10,40:40='(',<287>,1:40]], $2=[[@4,30:30='(',<287>,1:30], [@10,40:40='(',<287>,1:40]]}, insert1={$1=[[@4,30:30='(',<287>,1:30], [@10,40:40='(',<287>,1:40]], $2=[[@4,30:30='(',<287>,1:30], [@10,40:40='(',<287>,1:40]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_insert1={query_dictionary={$1=[[@4,30:30='(',<287>,1:30], [@10,40:40='(',<287>,1:40]], $2=[[@4,30:30='(',<287>,1:30], [@10,40:40='(',<287>,1:40]]}, table_dictionary={employees={$1=[[@4,30:30='(',<287>,1:30], [@10,40:40='(',<287>,1:40]], $2=[[@4,30:30='(',<287>,1:30], [@10,40:40='(',<287>,1:40]]}}, def_values0={query_dictionary={$1=[[@4,30:30='(',<287>,1:30], [@10,40:40='(',<287>,1:40]], $2=[[@4,30:30='(',<287>,1:30], [@10,40:40='(',<287>,1:40]]}, interface={$1=[], $2=[]}}, interface={$1=[{name=$1, table_ref=values0}], $2=[{name=$2, table_ref=values0}]}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void insertValuesMultiRowExplicitTargetColumnsV4() {
		final String query = " insert into employees (score, rank_bucket) values (100, 1), (200, 2)";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={insert={preamble=insert_into, from={values={matrix={1={row={1={literal=100}, 2={literal=1}}}, 2={row={1={literal=200}, 2={literal=2}}}}}}, target_table={table={alias=null, table=employees}}, columns={1={column={name=score, table_ref=null}}, 2={column={name=rank_bucket, table_ref=null}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[score, rank_bucket]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{employees={score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@6,31:41='rank_bucket',<393>,1:31]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{values0={$1=[[@9,51:51='(',<287>,1:51], [@15,61:61='(',<287>,1:61]], $2=[[@9,51:51='(',<287>,1:51], [@15,61:61='(',<287>,1:61]]}, insert1={score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@6,31:41='rank_bucket',<393>,1:31]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_insert1={query_dictionary={score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@6,31:41='rank_bucket',<393>,1:31]]}, table_dictionary={employees={score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@6,31:41='rank_bucket',<393>,1:31]]}}, def_values0={query_dictionary={$1=[[@9,51:51='(',<287>,1:51], [@15,61:61='(',<287>,1:61]], $2=[[@9,51:51='(',<287>,1:51], [@15,61:61='(',<287>,1:61]]}, interface={$1=[], $2=[]}}, interface={score=[{name=$1, table_ref=values0}], rank_bucket=[{name=$2, table_ref=values0}]}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void insertValuesSingleExplicitTargetColumnV5() {
		final String query = " insert into employees (score) values (100)";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={insert={preamble=insert_into, from={values={matrix={1={row={1={literal=100}}}}}}, target_table={table={alias=null, table=employees}}, columns={1={column={name=score, table_ref=null}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[score]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{employees={score=[[@4,24:28='score',<393>,1:24]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{values0={$1=[[@7,38:38='(',<287>,1:38]]}, insert1={score=[[@4,24:28='score',<393>,1:24]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_insert1={query_dictionary={score=[[@4,24:28='score',<393>,1:24]]}, table_dictionary={employees={score=[[@4,24:28='score',<393>,1:24]]}}, def_values0={query_dictionary={$1=[[@7,38:38='(',<287>,1:38]]}, interface={$1=[]}}, interface={score=[{name=$1, table_ref=values0}]}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void insertValuesThreeExplicitTargetColumnsV6() {
		final String query = " insert into employees (score, rank_bucket, orphan_sink) values (100, 1, 'z')";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={insert={preamble=insert_into, from={values={matrix={1={row={1={literal=100}, 2={literal=1}, 3={literal='z'}}}}}}, target_table={table={alias=null, table=employees}}, columns={1={column={name=score, table_ref=null}}, 2={column={name=rank_bucket, table_ref=null}}, 3={column={name=orphan_sink, table_ref=null}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[orphan_sink, score, rank_bucket]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{employees={orphan_sink=[[@8,44:54='orphan_sink',<393>,1:44]], score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@6,31:41='rank_bucket',<393>,1:31]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{values0={$1=[[@11,64:64='(',<287>,1:64]], $2=[[@11,64:64='(',<287>,1:64]], $3=[[@11,64:64='(',<287>,1:64]]}, insert1={orphan_sink=[[@8,44:54='orphan_sink',<393>,1:44]], score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@6,31:41='rank_bucket',<393>,1:31]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_insert1={query_dictionary={orphan_sink=[[@8,44:54='orphan_sink',<393>,1:44]], score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@6,31:41='rank_bucket',<393>,1:31]]}, table_dictionary={employees={orphan_sink=[[@8,44:54='orphan_sink',<393>,1:44]], score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@6,31:41='rank_bucket',<393>,1:31]]}}, def_values0={query_dictionary={$1=[[@11,64:64='(',<287>,1:64]], $2=[[@11,64:64='(',<287>,1:64]], $3=[[@11,64:64='(',<287>,1:64]]}, interface={$1=[], $2=[], $3=[]}}, interface={score=[{name=$1, table_ref=values0}], rank_bucket=[{name=$2, table_ref=values0}], orphan_sink=[{name=$3, table_ref=values0}]}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void insertValuesSourceNamedColumnsAndAliasV7() {
		final String query = " insert into employees (score, rank_bucket)"
				+ "\n select col1, col2 from (values (100, 1)) as value_src (col1, col2)";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={insert={preamble=insert_into, from={from={values={columns={1={column={name=col1, table_ref=null}}, 2={column={name=col2, table_ref=null}}}, alias=value_src, matrix={1={row={1={literal=100}, 2={literal=1}}}}}}, select={1={column={name=col1, table_ref=null}}, 2={column={name=col2, table_ref=null}}}}, target_table={table={alias=null, table=employees}}, columns={1={column={name=score, table_ref=null}}, 2={column={name=rank_bucket, table_ref=null}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[score, rank_bucket]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{employees={score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@6,31:41='rank_bucket',<393>,1:31]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{values0={col2=[[@26,106:109='col2',<393>,2:62], [@11,58:61='col2',<393>,2:14]], col1=[[@24,100:103='col1',<393>,2:56], [@9,52:55='col1',<393>,2:8]]}, query1={col2=[[@11,58:61='col2',<393>,2:14]], col1=[[@9,52:55='col1',<393>,2:8]]}, insert2={score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@6,31:41='rank_bucket',<393>,1:31]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_insert2={query_dictionary={score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@6,31:41='rank_bucket',<393>,1:31]]}, table_dictionary={employees={score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@6,31:41='rank_bucket',<393>,1:31]]}}, def_query1={query_dictionary={col2=[[@11,58:61='col2',<393>,2:14]], col1=[[@9,52:55='col1',<393>,2:8]]}, def_values0={query_dictionary={col2=[[@26,106:109='col2',<393>,2:62], [@11,58:61='col2',<393>,2:14]], col1=[[@24,100:103='col1',<393>,2:56], [@9,52:55='col1',<393>,2:8]]}, interface={col2=[], col1=[]}}, interface={col2=[{name=col2, table_ref=values0}], col1=[{name=col1, table_ref=values0}]}, table_alias={value_src=values0}}, interface={score=[{name=col1, table_ref=query1}], rank_bucket=[{name=col2, table_ref=query1}]}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void insertValuesSourceAliasOnlyV8() {
		final String query = " insert into employees (score, rank_bucket)"
				+ " select col1, col2 from (values (100, 1)) as value_src";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		Snippet snippet = extractor.getSnippet();
		assertFatalDiagnosticCount(
				snippet,
				"UNQUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIASES",
				null,
				"col1",
				1);
		assertFatalDiagnosticCount(
				snippet,
				"UNQUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIASES",
				null,
				"col2",
				1);
		assertDiagnosticListByCodeAndSeverity(
				snippet,
				"UNRESOLVED_UNQUALIFIED_COLUMNS",
				ParseDiagnostic.Severity.ERROR,
				"token=col2, col1 line=1 char=57 code=UNRESOLVED_UNQUALIFIED_COLUMNS severity=ERROR");

		Assert.assertEquals("AST is wrong", "{SQL={insert={preamble=insert_into, from={from={values={alias=value_src, matrix={1={row={1={literal=100}, 2={literal=1}}}}}}, select={1={column={name=col1, table_ref=null}}, 2={column={name=col2, table_ref=null}}}}, target_table={table={alias=null, table=employees}}, columns={1={column={name=score, table_ref=null}}, 2={column={name=rank_bucket, table_ref=null}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[score, rank_bucket]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{employees={score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@6,31:41='rank_bucket',<393>,1:31]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{values0={$1=[[@15,75:75='(',<287>,1:75]], $2=[[@15,75:75='(',<287>,1:75]]}, query1={col2=[[@11,57:60='col2',<393>,1:57]], col1=[[@9,51:54='col1',<393>,1:51]]}, insert2={score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@6,31:41='rank_bucket',<393>,1:31]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_insert2={query_dictionary={score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@6,31:41='rank_bucket',<393>,1:31]]}, table_dictionary={employees={score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@6,31:41='rank_bucket',<393>,1:31]]}}, def_query1={query_dictionary={col2=[[@11,57:60='col2',<393>,1:57]], col1=[[@9,51:54='col1',<393>,1:51]]}, def_values0={query_dictionary={$1=[[@15,75:75='(',<287>,1:75]], $2=[[@15,75:75='(',<287>,1:75]]}, interface={$1=[], $2=[]}}, interface={col2=[{name=col2, table_ref=null}], col1=[{name=col1, table_ref=null}]}, table_alias={value_src=values0}}, interface={score=[{name=col1, table_ref=query1}], rank_bucket=[{name=col2, table_ref=query1}]}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void insertValuesExtraTargetColumnV9() {
		final String query = " insert into employees (score, rank_bucket, orphan_sink) values (100, 1)";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		Snippet snippet = extractor.getSnippet();
		assertFatalDiagnosticAtPosition(
				snippet,
				"INSERT_TARGET_SOURCE_COLUMN_COUNT_MISMATCH",
				"Insert Mismatch: Target has 3 columns, Source has 2 columns, (l:1 c:23)",
				null,
				1,
				23);

		Assert.assertEquals("AST is wrong", "{SQL={insert={preamble=insert_into, from={values={matrix={1={row={1={literal=100}, 2={literal=1}}}}}}, target_table={table={alias=null, table=employees}}, columns={1={column={name=score, table_ref=null}}, 2={column={name=rank_bucket, table_ref=null}}, 3={column={name=orphan_sink, table_ref=null}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[orphan_sink, score, rank_bucket]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{employees={orphan_sink=[[@8,44:54='orphan_sink',<393>,1:44]], score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@6,31:41='rank_bucket',<393>,1:31]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{values0={$1=[[@11,64:64='(',<287>,1:64]], $2=[[@11,64:64='(',<287>,1:64]]}, insert1={orphan_sink=[[@8,44:54='orphan_sink',<393>,1:44]], score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@6,31:41='rank_bucket',<393>,1:31]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_insert1={query_dictionary={orphan_sink=[[@8,44:54='orphan_sink',<393>,1:44]], score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@6,31:41='rank_bucket',<393>,1:31]]}, table_dictionary={employees={orphan_sink=[[@8,44:54='orphan_sink',<393>,1:44]], score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@6,31:41='rank_bucket',<393>,1:31]]}}, def_values0={query_dictionary={$1=[[@11,64:64='(',<287>,1:64]], $2=[[@11,64:64='(',<287>,1:64]]}, interface={$1=[], $2=[]}}, interface={score=[{name=$1, table_ref=values0}], rank_bucket=[{name=$2, table_ref=values0}], orphan_sink=[{name=orphan_sink, table_ref=values0}]}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void insertValuesExtraValuesColumnV10() {
		final String query = " insert into employees (score, rank_bucket) values (100, 1, 'extra')";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		Snippet snippet = extractor.getSnippet();
		assertFatalDiagnosticAtPosition(
				snippet,
				"INSERT_TARGET_SOURCE_COLUMN_COUNT_MISMATCH",
				"Insert Mismatch: Target has 2 columns, Source has 3 columns, (l:1 c:23)",
				null,
				1,
				23);

		Assert.assertEquals("AST is wrong", "{SQL={insert={preamble=insert_into, from={values={matrix={1={row={1={literal=100}, 2={literal=1}, 3={literal='extra'}}}}}}, target_table={table={alias=null, table=employees}}, columns={1={column={name=score, table_ref=null}}, 2={column={name=rank_bucket, table_ref=null}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[score, rank_bucket]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{employees={score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@6,31:41='rank_bucket',<393>,1:31]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{values0={$1=[[@9,51:51='(',<287>,1:51]], $2=[[@9,51:51='(',<287>,1:51]], $3=[[@9,51:51='(',<287>,1:51]]}, insert1={score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@6,31:41='rank_bucket',<393>,1:31]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_insert1={query_dictionary={score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@6,31:41='rank_bucket',<393>,1:31]]}, table_dictionary={employees={score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@6,31:41='rank_bucket',<393>,1:31]]}}, def_values0={query_dictionary={$1=[[@9,51:51='(',<287>,1:51]], $2=[[@9,51:51='(',<287>,1:51]], $3=[[@9,51:51='(',<287>,1:51]]}, interface={$1=[], $2=[], $3=[]}}, interface={score=[{name=$1, table_ref=values0}], rank_bucket=[{name=$2, table_ref=values0}]}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void insertSelectExtraTargetColumnV11() {
		final String query = " insert into employees (score, rank_bucket, orphan_sink)"
				+ " select emp_id, score from perf_feed";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		Snippet snippet = extractor.getSnippet();
		assertFatalDiagnosticAtPosition(
				snippet,
				"INSERT_TARGET_SOURCE_COLUMN_COUNT_MISMATCH",
				"Insert Mismatch: Target has 3 columns, Source has 2 columns, (l:1 c:23)",
				null,
				1,
				23);

		Assert.assertEquals("AST is wrong", "{SQL={insert={preamble=insert_into, from={from={table={alias=null, table=perf_feed}}, select={1={column={name=emp_id, table_ref=null}}, 2={column={name=score, table_ref=null}}}}, target_table={table={alias=null, table=employees}}, columns={1={column={name=score, table_ref=null}}, 2={column={name=rank_bucket, table_ref=null}}, 3={column={name=orphan_sink, table_ref=null}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[orphan_sink, score, rank_bucket]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{perf_feed={score=[[@13,72:76='score',<393>,1:72]], emp_id=[[@11,64:69='emp_id',<393>,1:64]]}, employees={orphan_sink=[[@8,44:54='orphan_sink',<393>,1:44]], score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@6,31:41='rank_bucket',<393>,1:31]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={score=[[@13,72:76='score',<393>,1:72]], emp_id=[[@11,64:69='emp_id',<393>,1:64]]}, insert1={orphan_sink=[[@8,44:54='orphan_sink',<393>,1:44]], score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@6,31:41='rank_bucket',<393>,1:31]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Tree is wrong", "{def_insert1={query_dictionary={orphan_sink=[[@8,44:54='orphan_sink',<393>,1:44]], score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@6,31:41='rank_bucket',<393>,1:31]]}, table_dictionary={employees={orphan_sink=[[@8,44:54='orphan_sink',<393>,1:44]], score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@6,31:41='rank_bucket',<393>,1:31]]}}, def_query0={query_dictionary={score=[[@13,72:76='score',<393>,1:72]], emp_id=[[@11,64:69='emp_id',<393>,1:64]]}, table_dictionary={perf_feed={score=[[@13,72:76='score',<393>,1:72]], emp_id=[[@11,64:69='emp_id',<393>,1:64]]}}, interface={score=[{name=score, table_ref=perf_feed}], emp_id=[{name=emp_id, table_ref=perf_feed}]}}, interface={score=[{name=emp_id, table_ref=query0}], rank_bucket=[{name=score, table_ref=query0}], orphan_sink=[{name=orphan_sink, table_ref=query0}]}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void insertSelectExtraSourceColumnV12() {
		final String query = " insert into employees (score, rank_bucket)"
				+ " select emp_id, score, dept_id from perf_feed";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		Snippet snippet = extractor.getSnippet();
		assertFatalDiagnosticAtPosition(
				snippet,
				"INSERT_TARGET_SOURCE_COLUMN_COUNT_MISMATCH",
				"Insert Mismatch: Target has 2 columns, Source has 3 columns, (l:1 c:23)",
				null,
				1,
				23);

		Assert.assertEquals("AST is wrong", "{SQL={insert={preamble=insert_into, from={from={table={alias=null, table=perf_feed}}, select={1={column={name=emp_id, table_ref=null}}, 2={column={name=score, table_ref=null}}, 3={column={name=dept_id, table_ref=null}}}}, target_table={table={alias=null, table=employees}}, columns={1={column={name=score, table_ref=null}}, 2={column={name=rank_bucket, table_ref=null}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[score, rank_bucket]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{perf_feed={score=[[@11,59:63='score',<393>,1:59]], dept_id=[[@13,66:72='dept_id',<393>,1:66]], emp_id=[[@9,51:56='emp_id',<393>,1:51]]}, employees={score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@6,31:41='rank_bucket',<393>,1:31]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={score=[[@11,59:63='score',<393>,1:59]], dept_id=[[@13,66:72='dept_id',<393>,1:66]], emp_id=[[@9,51:56='emp_id',<393>,1:51]]}, insert1={score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@6,31:41='rank_bucket',<393>,1:31]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Tree is wrong", "{def_insert1={query_dictionary={score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@6,31:41='rank_bucket',<393>,1:31]]}, table_dictionary={employees={score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@6,31:41='rank_bucket',<393>,1:31]]}}, def_query0={query_dictionary={score=[[@11,59:63='score',<393>,1:59]], dept_id=[[@13,66:72='dept_id',<393>,1:66]], emp_id=[[@9,51:56='emp_id',<393>,1:51]]}, table_dictionary={perf_feed={score=[[@11,59:63='score',<393>,1:59]], dept_id=[[@13,66:72='dept_id',<393>,1:66]], emp_id=[[@9,51:56='emp_id',<393>,1:51]]}}, interface={score=[{name=score, table_ref=perf_feed}], dept_id=[{name=dept_id, table_ref=perf_feed}], emp_id=[{name=emp_id, table_ref=perf_feed}]}}, interface={score=[{name=emp_id, table_ref=query0}], rank_bucket=[{name=score, table_ref=query0}]}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void insertSelectImplicitNoTargetColumnsV13() {
		final String query = " insert into employees select emp_id, score from perf_feed";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={insert={preamble=insert_into, from={from={table={alias=null, table=perf_feed}}, select={1={column={name=emp_id, table_ref=null}}, 2={column={name=score, table_ref=null}}}}, target_table={table={alias=null, table=employees}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[score, emp_id]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{perf_feed={score=[[@6,38:42='score',<393>,1:38]], emp_id=[[@4,30:35='emp_id',<393>,1:30]]}, employees={score=[[@6,38:42='score',<393>,1:38]], emp_id=[[@4,30:35='emp_id',<393>,1:30]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={score=[[@6,38:42='score',<393>,1:38]], emp_id=[[@4,30:35='emp_id',<393>,1:30]]}, insert1={score=[[@6,38:42='score',<393>,1:38]], emp_id=[[@4,30:35='emp_id',<393>,1:30]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Tree is wrong", "{def_insert1={query_dictionary={score=[[@6,38:42='score',<393>,1:38]], emp_id=[[@4,30:35='emp_id',<393>,1:30]]}, table_dictionary={employees={score=[[@6,38:42='score',<393>,1:38]], emp_id=[[@4,30:35='emp_id',<393>,1:30]]}}, def_query0={query_dictionary={score=[[@6,38:42='score',<393>,1:38]], emp_id=[[@4,30:35='emp_id',<393>,1:30]]}, table_dictionary={perf_feed={score=[[@6,38:42='score',<393>,1:38]], emp_id=[[@4,30:35='emp_id',<393>,1:30]]}}, interface={score=[{name=score, table_ref=perf_feed}], emp_id=[{name=emp_id, table_ref=perf_feed}]}}, interface={score=[{name=score, table_ref=query0}], emp_id=[{name=emp_id, table_ref=query0}]}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void insertSelectSwappedNamesPositionalMappingV14() {
		final String query = " insert into employees (score, rank_bucket)"
				+ " select new_quota, dept_id from quota_feed";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={insert={preamble=insert_into, from={from={table={alias=null, table=quota_feed}}, select={1={column={name=new_quota, table_ref=null}}, 2={column={name=dept_id, table_ref=null}}}}, target_table={table={alias=null, table=employees}}, columns={1={column={name=score, table_ref=null}}, 2={column={name=rank_bucket, table_ref=null}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[score, rank_bucket]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{quota_feed={new_quota=[[@9,51:59='new_quota',<393>,1:51]], dept_id=[[@11,62:68='dept_id',<393>,1:62]]}, employees={score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@6,31:41='rank_bucket',<393>,1:31]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={dept_id=[[@11,62:68='dept_id',<393>,1:62]], new_quota=[[@9,51:59='new_quota',<393>,1:51]]}, insert1={score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@6,31:41='rank_bucket',<393>,1:31]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Tree is wrong", "{def_insert1={query_dictionary={score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@6,31:41='rank_bucket',<393>,1:31]]}, table_dictionary={employees={score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@6,31:41='rank_bucket',<393>,1:31]]}}, def_query0={query_dictionary={new_quota=[[@9,51:59='new_quota',<393>,1:51]], dept_id=[[@11,62:68='dept_id',<393>,1:62]]}, table_dictionary={quota_feed={new_quota=[[@9,51:59='new_quota',<393>,1:51]], dept_id=[[@11,62:68='dept_id',<393>,1:62]]}}, interface={new_quota=[{name=new_quota, table_ref=quota_feed}], dept_id=[{name=dept_id, table_ref=quota_feed}]}}, interface={score=[{name=new_quota, table_ref=query0}], rank_bucket=[{name=dept_id, table_ref=query0}]}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void insertSelectUnionSourceWithExplicitTargetColumnsV15() {
		final String query = " insert into employees (score, rank_bucket)"
				+ " select emp_id, score from perf_feed union select dept_id, new_quota from quota_feed";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={insert={preamble=insert_into, from={union={1={select={1={column={name=emp_id, table_ref=null}}, 2={column={name=score, table_ref=null}}}, from={table={alias=null, table=perf_feed}}}, 2={union={qualifier=null, operator=union}}, 3={select={1={column={name=dept_id, table_ref=null}}, 2={column={name=new_quota, table_ref=null}}}, from={table={alias=null, table=quota_feed}}}}}, target_table={table={alias=null, table=employees}}, columns={1={column={name=score, table_ref=null}}, 2={column={name=rank_bucket, table_ref=null}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[score, rank_bucket]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{perf_feed={score=[[@11,59:63='score',<393>,1:59]], emp_id=[[@9,51:56='emp_id',<393>,1:51]]}, quota_feed={new_quota=[[@18,102:110='new_quota',<393>,1:102]], dept_id=[[@16,93:99='dept_id',<393>,1:93]]}, employees={score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@6,31:41='rank_bucket',<393>,1:31]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={score=[[@11,59:63='score',<393>,1:59]], emp_id=[[@9,51:56='emp_id',<393>,1:51]]}, query1={dept_id=[[@16,93:99='dept_id',<393>,1:93]], new_quota=[[@18,102:110='new_quota',<393>,1:102]]}, insert3={score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@6,31:41='rank_bucket',<393>,1:31]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_insert3={query_dictionary={score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@6,31:41='rank_bucket',<393>,1:31]]}, def_union2={def_query1={query_dictionary={new_quota=[[@18,102:110='new_quota',<393>,1:102]], dept_id=[[@16,93:99='dept_id',<393>,1:93]]}, table_dictionary={quota_feed={new_quota=[[@18,102:110='new_quota',<393>,1:102]], dept_id=[[@16,93:99='dept_id',<393>,1:93]]}}, setop=UNION, interface={new_quota=[{name=new_quota, table_ref=quota_feed}], dept_id=[{name=dept_id, table_ref=quota_feed}]}}, def_query0={query_dictionary={score=[[@11,59:63='score',<393>,1:59]], emp_id=[[@9,51:56='emp_id',<393>,1:51]]}, table_dictionary={perf_feed={score=[[@11,59:63='score',<393>,1:59]], emp_id=[[@9,51:56='emp_id',<393>,1:51]]}}, interface={score=[{name=score, table_ref=perf_feed}], emp_id=[{name=emp_id, table_ref=perf_feed}]}}, interface={score=query_column, emp_id=query_column}}, table_dictionary={employees={score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@6,31:41='rank_bucket',<393>,1:31]]}}, interface={score=[{name=emp_id, table_ref=union2}], rank_bucket=[{name=score, table_ref=union2}]}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void insertSelectExceptSourceWithExplicitTargetColumnsV15(){
		final String query = " insert into employees (score, rank_bucket)"
				+ " select emp_id, score from perf_feed except select dept_id, new_quota from quota_feed";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={insert={preamble=insert_into, from={union={1={select={1={column={name=emp_id, table_ref=null}}, 2={column={name=score, table_ref=null}}}, from={table={alias=null, table=perf_feed}}}, 2={union={qualifier=null, operator=except}}, 3={select={1={column={name=dept_id, table_ref=null}}, 2={column={name=new_quota, table_ref=null}}}, from={table={alias=null, table=quota_feed}}}}}, target_table={table={alias=null, table=employees}}, columns={1={column={name=score, table_ref=null}}, 2={column={name=rank_bucket, table_ref=null}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[score, rank_bucket]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{perf_feed={score=[[@11,59:63='score',<393>,1:59]], emp_id=[[@9,51:56='emp_id',<393>,1:51]]}, quota_feed={new_quota=[[@18,103:111='new_quota',<393>,1:103]], dept_id=[[@16,94:100='dept_id',<393>,1:94]]}, employees={score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@6,31:41='rank_bucket',<393>,1:31]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={score=[[@11,59:63='score',<393>,1:59]], emp_id=[[@9,51:56='emp_id',<393>,1:51]]}, query1={dept_id=[[@16,94:100='dept_id',<393>,1:94]], new_quota=[[@18,103:111='new_quota',<393>,1:103]]}, insert3={score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@6,31:41='rank_bucket',<393>,1:31]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_insert3={query_dictionary={score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@6,31:41='rank_bucket',<393>,1:31]]}, def_union2={def_query1={query_dictionary={new_quota=[[@18,103:111='new_quota',<393>,1:103]], dept_id=[[@16,94:100='dept_id',<393>,1:94]]}, table_dictionary={quota_feed={new_quota=[[@18,103:111='new_quota',<393>,1:103]], dept_id=[[@16,94:100='dept_id',<393>,1:94]]}}, setop=EXCEPT, interface={new_quota=[{name=new_quota, table_ref=quota_feed}], dept_id=[{name=dept_id, table_ref=quota_feed}]}}, def_query0={query_dictionary={score=[[@11,59:63='score',<393>,1:59]], emp_id=[[@9,51:56='emp_id',<393>,1:51]]}, table_dictionary={perf_feed={score=[[@11,59:63='score',<393>,1:59]], emp_id=[[@9,51:56='emp_id',<393>,1:51]]}}, interface={score=[{name=score, table_ref=perf_feed}], emp_id=[{name=emp_id, table_ref=perf_feed}]}}, interface={score=query_column, emp_id=query_column}}, table_dictionary={employees={score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@6,31:41='rank_bucket',<393>,1:31]]}}, interface={score=[{name=emp_id, table_ref=union2}], rank_bucket=[{name=score, table_ref=union2}]}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void insertSelectIntersectSourceWithExplicitTargetColumnsV16() {
		final String query = " insert into employees (score, rank_bucket)"
				+ " select rank, score from perf_feed intersect select dept_id, new_quota from quota_feed";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={insert={preamble=insert_into, from={intersect={1={select={1={column={name=rank, table_ref=null}}, 2={column={name=score, table_ref=null}}}, from={table={alias=null, table=perf_feed}}}, 2={intersect={qualifier=null, operator=intersect}}, 3={select={1={column={name=dept_id, table_ref=null}}, 2={column={name=new_quota, table_ref=null}}}, from={table={alias=null, table=quota_feed}}}}}, target_table={table={alias=null, table=employees}}, columns={1={column={name=score, table_ref=null}}, 2={column={name=rank_bucket, table_ref=null}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[score, rank_bucket]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{perf_feed={score=[[@11,57:61='score',<393>,1:57]], rank=[[@9,51:54='rank',<128>,1:51]]}, quota_feed={new_quota=[[@18,104:112='new_quota',<393>,1:104]], dept_id=[[@16,95:101='dept_id',<393>,1:95]]}, employees={score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@6,31:41='rank_bucket',<393>,1:31]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={score=[[@11,57:61='score',<393>,1:57]], rank=[[@9,51:54='rank',<128>,1:51]]}, query1={dept_id=[[@16,95:101='dept_id',<393>,1:95]], new_quota=[[@18,104:112='new_quota',<393>,1:104]]}, insert3={score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@6,31:41='rank_bucket',<393>,1:31]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_insert3={query_dictionary={score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@6,31:41='rank_bucket',<393>,1:31]]}, table_dictionary={employees={score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@6,31:41='rank_bucket',<393>,1:31]]}}, def_intersect2={def_query1={query_dictionary={new_quota=[[@18,104:112='new_quota',<393>,1:104]], dept_id=[[@16,95:101='dept_id',<393>,1:95]]}, table_dictionary={quota_feed={new_quota=[[@18,104:112='new_quota',<393>,1:104]], dept_id=[[@16,95:101='dept_id',<393>,1:95]]}}, setop=INTERSECTION, interface={new_quota=[{name=new_quota, table_ref=quota_feed}], dept_id=[{name=dept_id, table_ref=quota_feed}]}}, def_query0={query_dictionary={score=[[@11,57:61='score',<393>,1:57]], rank=[[@9,51:54='rank',<128>,1:51]]}, table_dictionary={perf_feed={score=[[@11,57:61='score',<393>,1:57]], rank=[[@9,51:54='rank',<128>,1:51]]}}, interface={score=[{name=score, table_ref=perf_feed}], rank=[{name=rank, table_ref=perf_feed}]}}, interface={score=query_column, rank=query_column}}, interface={score=[{name=rank, table_ref=intersect2}], rank_bucket=[{name=score, table_ref=intersect2}]}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void insertSelectExceptSourceWithExplicitTargetColumnsV16() {
		final String query = " insert into employees (score, rank_bucket)"
				+ " select rank, score from perf_feed except select dept_id, new_quota from quota_feed";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={insert={preamble=insert_into, from={union={1={select={1={column={name=rank, table_ref=null}}, 2={column={name=score, table_ref=null}}}, from={table={alias=null, table=perf_feed}}}, 2={union={qualifier=null, operator=except}}, 3={select={1={column={name=dept_id, table_ref=null}}, 2={column={name=new_quota, table_ref=null}}}, from={table={alias=null, table=quota_feed}}}}}, target_table={table={alias=null, table=employees}}, columns={1={column={name=score, table_ref=null}}, 2={column={name=rank_bucket, table_ref=null}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[score, rank_bucket]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{perf_feed={score=[[@11,57:61='score',<393>,1:57]], rank=[[@9,51:54='rank',<128>,1:51]]}, quota_feed={new_quota=[[@18,101:109='new_quota',<393>,1:101]], dept_id=[[@16,92:98='dept_id',<393>,1:92]]}, employees={score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@6,31:41='rank_bucket',<393>,1:31]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={score=[[@11,57:61='score',<393>,1:57]], rank=[[@9,51:54='rank',<128>,1:51]]}, query1={dept_id=[[@16,92:98='dept_id',<393>,1:92]], new_quota=[[@18,101:109='new_quota',<393>,1:101]]}, insert3={score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@6,31:41='rank_bucket',<393>,1:31]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_insert3={query_dictionary={score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@6,31:41='rank_bucket',<393>,1:31]]}, def_union2={def_query1={query_dictionary={new_quota=[[@18,101:109='new_quota',<393>,1:101]], dept_id=[[@16,92:98='dept_id',<393>,1:92]]}, table_dictionary={quota_feed={new_quota=[[@18,101:109='new_quota',<393>,1:101]], dept_id=[[@16,92:98='dept_id',<393>,1:92]]}}, setop=EXCEPT, interface={new_quota=[{name=new_quota, table_ref=quota_feed}], dept_id=[{name=dept_id, table_ref=quota_feed}]}}, def_query0={query_dictionary={score=[[@11,57:61='score',<393>,1:57]], rank=[[@9,51:54='rank',<128>,1:51]]}, table_dictionary={perf_feed={score=[[@11,57:61='score',<393>,1:57]], rank=[[@9,51:54='rank',<128>,1:51]]}}, interface={score=[{name=score, table_ref=perf_feed}], rank=[{name=rank, table_ref=perf_feed}]}}, interface={score=query_column, rank=query_column}}, table_dictionary={employees={score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@6,31:41='rank_bucket',<393>,1:31]]}}, interface={score=[{name=rank, table_ref=union2}], rank_bucket=[{name=score, table_ref=union2}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void insertSelectThreeWayUnionSourceWithExplicitTargetColumnsV17() {
		final String query = " insert into employees (score, rank_bucket)"
				+ " select rank, score from perf_feed"
				+ " union select dept_id, new_quota from quota_feed"
				+ " union select alpha_col, beta_col from third_feed";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={insert={preamble=insert_into, from={union={1={select={1={column={name=rank, table_ref=null}}, 2={column={name=score, table_ref=null}}}, from={table={alias=null, table=perf_feed}}}, 2={union={qualifier=null, operator=union}}, 3={select={1={column={name=dept_id, table_ref=null}}, 2={column={name=new_quota, table_ref=null}}}, from={table={alias=null, table=quota_feed}}}, 4={union={qualifier=null, operator=union}}, 5={select={1={column={name=alpha_col, table_ref=null}}, 2={column={name=beta_col, table_ref=null}}}, from={table={alias=null, table=third_feed}}}}}, target_table={table={alias=null, table=employees}}, columns={1={column={name=score, table_ref=null}}, 2={column={name=rank_bucket, table_ref=null}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[score, rank_bucket]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{perf_feed={score=[[@11,57:61='score',<393>,1:57]], rank=[[@9,51:54='rank',<128>,1:51]]}, quota_feed={new_quota=[[@18,100:108='new_quota',<393>,1:100]], dept_id=[[@16,91:97='dept_id',<393>,1:91]]}, third_feed={alpha_col=[[@23,139:147='alpha_col',<393>,1:139]], beta_col=[[@25,150:157='beta_col',<393>,1:150]]}, employees={score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@6,31:41='rank_bucket',<393>,1:31]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={score=[[@11,57:61='score',<393>,1:57]], rank=[[@9,51:54='rank',<128>,1:51]]}, insert4={score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@6,31:41='rank_bucket',<393>,1:31]]}, query1={dept_id=[[@16,91:97='dept_id',<393>,1:91]], new_quota=[[@18,100:108='new_quota',<393>,1:100]]}, query2={alpha_col=[[@23,139:147='alpha_col',<393>,1:139]], beta_col=[[@25,150:157='beta_col',<393>,1:150]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_insert4={def_union3={def_query1={query_dictionary={new_quota=[[@18,100:108='new_quota',<393>,1:100]], dept_id=[[@16,91:97='dept_id',<393>,1:91]]}, table_dictionary={quota_feed={new_quota=[[@18,100:108='new_quota',<393>,1:100]], dept_id=[[@16,91:97='dept_id',<393>,1:91]]}}, setop=UNION, interface={new_quota=[{name=new_quota, table_ref=quota_feed}], dept_id=[{name=dept_id, table_ref=quota_feed}]}}, def_query0={query_dictionary={score=[[@11,57:61='score',<393>,1:57]], rank=[[@9,51:54='rank',<128>,1:51]]}, table_dictionary={perf_feed={score=[[@11,57:61='score',<393>,1:57]], rank=[[@9,51:54='rank',<128>,1:51]]}}, interface={score=[{name=score, table_ref=perf_feed}], rank=[{name=rank, table_ref=perf_feed}]}}, interface={score=query_column, rank=query_column}, def_query2={query_dictionary={alpha_col=[[@23,139:147='alpha_col',<393>,1:139]], beta_col=[[@25,150:157='beta_col',<393>,1:150]]}, table_dictionary={third_feed={alpha_col=[[@23,139:147='alpha_col',<393>,1:139]], beta_col=[[@25,150:157='beta_col',<393>,1:150]]}}, setop=UNION, interface={alpha_col=[{name=alpha_col, table_ref=third_feed}], beta_col=[{name=beta_col, table_ref=third_feed}]}}}, query_dictionary={score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@6,31:41='rank_bucket',<393>,1:31]]}, table_dictionary={employees={score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@6,31:41='rank_bucket',<393>,1:31]]}}, interface={score=[{name=rank, table_ref=union3}], rank_bucket=[{name=score, table_ref=union3}]}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void insertSelectThreeWayExceptSourceWithExplicitTargetColumnsV17(){
		final String query = " insert into employees (score, rank_bucket)"
				+ " select rank, score from perf_feed"
				+ " except select dept_id, new_quota from quota_feed"
				+ " except select alpha_col, beta_col from third_feed";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={insert={preamble=insert_into, from={union={1={select={1={column={name=rank, table_ref=null}}, 2={column={name=score, table_ref=null}}}, from={table={alias=null, table=perf_feed}}}, 2={union={qualifier=null, operator=except}}, 3={select={1={column={name=dept_id, table_ref=null}}, 2={column={name=new_quota, table_ref=null}}}, from={table={alias=null, table=quota_feed}}}, 4={union={qualifier=null, operator=except}}, 5={select={1={column={name=alpha_col, table_ref=null}}, 2={column={name=beta_col, table_ref=null}}}, from={table={alias=null, table=third_feed}}}}}, target_table={table={alias=null, table=employees}}, columns={1={column={name=score, table_ref=null}}, 2={column={name=rank_bucket, table_ref=null}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[score, rank_bucket]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{perf_feed={score=[[@11,57:61='score',<393>,1:57]], rank=[[@9,51:54='rank',<128>,1:51]]}, quota_feed={new_quota=[[@18,101:109='new_quota',<393>,1:101]], dept_id=[[@16,92:98='dept_id',<393>,1:92]]}, third_feed={alpha_col=[[@23,141:149='alpha_col',<393>,1:141]], beta_col=[[@25,152:159='beta_col',<393>,1:152]]}, employees={score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@6,31:41='rank_bucket',<393>,1:31]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={score=[[@11,57:61='score',<393>,1:57]], rank=[[@9,51:54='rank',<128>,1:51]]}, insert4={score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@6,31:41='rank_bucket',<393>,1:31]]}, query1={dept_id=[[@16,92:98='dept_id',<393>,1:92]], new_quota=[[@18,101:109='new_quota',<393>,1:101]]}, query2={alpha_col=[[@23,141:149='alpha_col',<393>,1:141]], beta_col=[[@25,152:159='beta_col',<393>,1:152]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_insert4={def_union3={def_query1={query_dictionary={new_quota=[[@18,101:109='new_quota',<393>,1:101]], dept_id=[[@16,92:98='dept_id',<393>,1:92]]}, table_dictionary={quota_feed={new_quota=[[@18,101:109='new_quota',<393>,1:101]], dept_id=[[@16,92:98='dept_id',<393>,1:92]]}}, setop=EXCEPT, interface={new_quota=[{name=new_quota, table_ref=quota_feed}], dept_id=[{name=dept_id, table_ref=quota_feed}]}}, def_query0={query_dictionary={score=[[@11,57:61='score',<393>,1:57]], rank=[[@9,51:54='rank',<128>,1:51]]}, table_dictionary={perf_feed={score=[[@11,57:61='score',<393>,1:57]], rank=[[@9,51:54='rank',<128>,1:51]]}}, interface={score=[{name=score, table_ref=perf_feed}], rank=[{name=rank, table_ref=perf_feed}]}}, interface={score=query_column, rank=query_column}, def_query2={query_dictionary={alpha_col=[[@23,141:149='alpha_col',<393>,1:141]], beta_col=[[@25,152:159='beta_col',<393>,1:152]]}, table_dictionary={third_feed={alpha_col=[[@23,141:149='alpha_col',<393>,1:141]], beta_col=[[@25,152:159='beta_col',<393>,1:152]]}}, setop=EXCEPT, interface={alpha_col=[{name=alpha_col, table_ref=third_feed}], beta_col=[{name=beta_col, table_ref=third_feed}]}}}, query_dictionary={score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@6,31:41='rank_bucket',<393>,1:31]]}, table_dictionary={employees={score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@6,31:41='rank_bucket',<393>,1:31]]}}, interface={score=[{name=rank, table_ref=union3}], rank_bucket=[{name=score, table_ref=union3}]}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void insertSelectThreeWayIntersectSourceWithExplicitTargetColumnsV18() {
		final String query = " insert into employees (score, rank_bucket)"
				+ " select rank, score from perf_feed"
				+ " intersect select dept_id, new_quota from quota_feed"
				+ " intersect select alpha_col, beta_col from third_feed";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={insert={preamble=insert_into, from={intersect={1={select={1={column={name=rank, table_ref=null}}, 2={column={name=score, table_ref=null}}}, from={table={alias=null, table=perf_feed}}}, 2={intersect={qualifier=null, operator=intersect}}, 3={select={1={column={name=dept_id, table_ref=null}}, 2={column={name=new_quota, table_ref=null}}}, from={table={alias=null, table=quota_feed}}}, 4={intersect={qualifier=null, operator=intersect}}, 5={select={1={column={name=alpha_col, table_ref=null}}, 2={column={name=beta_col, table_ref=null}}}, from={table={alias=null, table=third_feed}}}}}, target_table={table={alias=null, table=employees}}, columns={1={column={name=score, table_ref=null}}, 2={column={name=rank_bucket, table_ref=null}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[score, rank_bucket]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{perf_feed={score=[[@11,57:61='score',<393>,1:57]], rank=[[@9,51:54='rank',<128>,1:51]]}, quota_feed={new_quota=[[@18,104:112='new_quota',<393>,1:104]], dept_id=[[@16,95:101='dept_id',<393>,1:95]]}, third_feed={alpha_col=[[@23,147:155='alpha_col',<393>,1:147]], beta_col=[[@25,158:165='beta_col',<393>,1:158]]}, employees={score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@6,31:41='rank_bucket',<393>,1:31]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={score=[[@11,57:61='score',<393>,1:57]], rank=[[@9,51:54='rank',<128>,1:51]]}, insert4={score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@6,31:41='rank_bucket',<393>,1:31]]}, query1={dept_id=[[@16,95:101='dept_id',<393>,1:95]], new_quota=[[@18,104:112='new_quota',<393>,1:104]]}, query2={alpha_col=[[@23,147:155='alpha_col',<393>,1:147]], beta_col=[[@25,158:165='beta_col',<393>,1:158]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_insert4={query_dictionary={score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@6,31:41='rank_bucket',<393>,1:31]]}, table_dictionary={employees={score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@6,31:41='rank_bucket',<393>,1:31]]}}, def_intersect3={def_query1={query_dictionary={new_quota=[[@18,104:112='new_quota',<393>,1:104]], dept_id=[[@16,95:101='dept_id',<393>,1:95]]}, table_dictionary={quota_feed={new_quota=[[@18,104:112='new_quota',<393>,1:104]], dept_id=[[@16,95:101='dept_id',<393>,1:95]]}}, setop=INTERSECTION, interface={new_quota=[{name=new_quota, table_ref=quota_feed}], dept_id=[{name=dept_id, table_ref=quota_feed}]}}, def_query0={query_dictionary={score=[[@11,57:61='score',<393>,1:57]], rank=[[@9,51:54='rank',<128>,1:51]]}, table_dictionary={perf_feed={score=[[@11,57:61='score',<393>,1:57]], rank=[[@9,51:54='rank',<128>,1:51]]}}, interface={score=[{name=score, table_ref=perf_feed}], rank=[{name=rank, table_ref=perf_feed}]}}, interface={score=query_column, rank=query_column}, def_query2={query_dictionary={alpha_col=[[@23,147:155='alpha_col',<393>,1:147]], beta_col=[[@25,158:165='beta_col',<393>,1:158]]}, table_dictionary={third_feed={alpha_col=[[@23,147:155='alpha_col',<393>,1:147]], beta_col=[[@25,158:165='beta_col',<393>,1:158]]}}, setop=INTERSECTION, interface={alpha_col=[{name=alpha_col, table_ref=third_feed}], beta_col=[{name=beta_col, table_ref=third_feed}]}}}, interface={score=[{name=rank, table_ref=intersect3}], rank_bucket=[{name=score, table_ref=intersect3}]}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void insertSelectThreeWayExceptSourceWithExplicitTargetColumnsV18() {
		final String query = " insert into employees (score, rank_bucket)"
				+ " select rank, score from perf_feed"
				+ " except select dept_id, new_quota from quota_feed"
				+ " except select alpha_col, beta_col from third_feed";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={insert={preamble=insert_into, from={union={1={select={1={column={name=rank, table_ref=null}}, 2={column={name=score, table_ref=null}}}, from={table={alias=null, table=perf_feed}}}, 2={union={qualifier=null, operator=except}}, 3={select={1={column={name=dept_id, table_ref=null}}, 2={column={name=new_quota, table_ref=null}}}, from={table={alias=null, table=quota_feed}}}, 4={union={qualifier=null, operator=except}}, 5={select={1={column={name=alpha_col, table_ref=null}}, 2={column={name=beta_col, table_ref=null}}}, from={table={alias=null, table=third_feed}}}}}, target_table={table={alias=null, table=employees}}, columns={1={column={name=score, table_ref=null}}, 2={column={name=rank_bucket, table_ref=null}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[score, rank_bucket]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{perf_feed={score=[[@11,57:61='score',<393>,1:57]], rank=[[@9,51:54='rank',<128>,1:51]]}, quota_feed={new_quota=[[@18,101:109='new_quota',<393>,1:101]], dept_id=[[@16,92:98='dept_id',<393>,1:92]]}, third_feed={alpha_col=[[@23,141:149='alpha_col',<393>,1:141]], beta_col=[[@25,152:159='beta_col',<393>,1:152]]}, employees={score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@6,31:41='rank_bucket',<393>,1:31]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={score=[[@11,57:61='score',<393>,1:57]], rank=[[@9,51:54='rank',<128>,1:51]]}, insert4={score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@6,31:41='rank_bucket',<393>,1:31]]}, query1={dept_id=[[@16,92:98='dept_id',<393>,1:92]], new_quota=[[@18,101:109='new_quota',<393>,1:101]]}, query2={alpha_col=[[@23,141:149='alpha_col',<393>,1:141]], beta_col=[[@25,152:159='beta_col',<393>,1:152]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_insert4={def_union3={def_query1={query_dictionary={new_quota=[[@18,101:109='new_quota',<393>,1:101]], dept_id=[[@16,92:98='dept_id',<393>,1:92]]}, table_dictionary={quota_feed={new_quota=[[@18,101:109='new_quota',<393>,1:101]], dept_id=[[@16,92:98='dept_id',<393>,1:92]]}}, setop=EXCEPT, interface={new_quota=[{name=new_quota, table_ref=quota_feed}], dept_id=[{name=dept_id, table_ref=quota_feed}]}}, def_query0={query_dictionary={score=[[@11,57:61='score',<393>,1:57]], rank=[[@9,51:54='rank',<128>,1:51]]}, table_dictionary={perf_feed={score=[[@11,57:61='score',<393>,1:57]], rank=[[@9,51:54='rank',<128>,1:51]]}}, interface={score=[{name=score, table_ref=perf_feed}], rank=[{name=rank, table_ref=perf_feed}]}}, interface={score=query_column, rank=query_column}, def_query2={query_dictionary={alpha_col=[[@23,141:149='alpha_col',<393>,1:141]], beta_col=[[@25,152:159='beta_col',<393>,1:152]]}, table_dictionary={third_feed={alpha_col=[[@23,141:149='alpha_col',<393>,1:141]], beta_col=[[@25,152:159='beta_col',<393>,1:152]]}}, setop=EXCEPT, interface={alpha_col=[{name=alpha_col, table_ref=third_feed}], beta_col=[{name=beta_col, table_ref=third_feed}]}}}, query_dictionary={score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@6,31:41='rank_bucket',<393>,1:31]]}, table_dictionary={employees={score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@6,31:41='rank_bucket',<393>,1:31]]}}, interface={score=[{name=rank, table_ref=union3}], rank_bucket=[{name=score, table_ref=union3}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void insertSelectUnionOverTwoIntersectsWithExplicitTargetColumnsV19A() {
		final String query = " insert into employees (score, rank_bucket)"
				+ " (select rank, score from perf_a intersect select x_col, y_col from perf_b)"
				+ " union (select dept_id, new_quota from quota_a intersect select p_col, q_col from quota_b)";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={insert={preamble=insert_into, from={union={1={intersect={1={select={1={column={name=rank, table_ref=null}}, 2={column={name=score, table_ref=null}}}, from={table={alias=null, table=perf_a}}}, 2={intersect={qualifier=null, operator=intersect}}, 3={select={1={column={name=x_col, table_ref=null}}, 2={column={name=y_col, table_ref=null}}}, from={table={alias=null, table=perf_b}}}}}, 2={union={qualifier=null, operator=union}}, 3={intersect={1={select={1={column={name=dept_id, table_ref=null}}, 2={column={name=new_quota, table_ref=null}}}, from={table={alias=null, table=quota_a}}}, 2={intersect={qualifier=null, operator=intersect}}, 3={select={1={column={name=p_col, table_ref=null}}, 2={column={name=q_col, table_ref=null}}}, from={table={alias=null, table=quota_b}}}}}}}, target_table={table={alias=null, table=employees}}, columns={1={column={name=score, table_ref=null}}, 2={column={name=rank_bucket, table_ref=null}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[score, rank_bucket]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{quota_b={p_col=[[@33,182:186='p_col',<393>,1:182]], q_col=[[@35,189:193='q_col',<393>,1:189]]}, quota_a={new_quota=[[@28,142:150='new_quota',<393>,1:142]], dept_id=[[@26,133:139='dept_id',<393>,1:133]]}, perf_a={score=[[@12,58:62='score',<393>,1:58]], rank=[[@10,52:55='rank',<128>,1:52]]}, employees={score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@6,31:41='rank_bucket',<393>,1:31]]}, perf_b={x_col=[[@17,93:97='x_col',<393>,1:93]], y_col=[[@19,100:104='y_col',<393>,1:100]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query4={p_col=[[@33,182:186='p_col',<393>,1:182]], q_col=[[@35,189:193='q_col',<393>,1:189]]}, insert7={score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@6,31:41='rank_bucket',<393>,1:31]]}, query0={score=[[@12,58:62='score',<393>,1:58]], rank=[[@10,52:55='rank',<128>,1:52]]}, query1={x_col=[[@17,93:97='x_col',<393>,1:93]], y_col=[[@19,100:104='y_col',<393>,1:100]]}, query3={dept_id=[[@26,133:139='dept_id',<393>,1:133]], new_quota=[[@28,142:150='new_quota',<393>,1:142]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
				Assert.assertEquals("Symbol Table is wrong", "{def_insert7={query_dictionary={score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@6,31:41='rank_bucket',<393>,1:31]]}, table_dictionary={employees={score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@6,31:41='rank_bucket',<393>,1:31]]}}, interface={score=[{name=rank, table_ref=union6}], rank_bucket=[{name=score, table_ref=union6}]}, def_union6={def_intersect2={def_query1={query_dictionary={x_col=[[@17,93:97='x_col',<393>,1:93]], y_col=[[@19,100:104='y_col',<393>,1:100]]}, table_dictionary={perf_b={x_col=[[@17,93:97='x_col',<393>,1:93]], y_col=[[@19,100:104='y_col',<393>,1:100]]}}, setop=INTERSECTION, interface={x_col=[{name=x_col, table_ref=perf_b}], y_col=[{name=y_col, table_ref=perf_b}]}}, def_query0={query_dictionary={score=[[@12,58:62='score',<393>,1:58]], rank=[[@10,52:55='rank',<128>,1:52]]}, table_dictionary={perf_a={score=[[@12,58:62='score',<393>,1:58]], rank=[[@10,52:55='rank',<128>,1:52]]}}, interface={score=[{name=score, table_ref=perf_a}], rank=[{name=rank, table_ref=perf_a}]}}, interface={score=query_column, rank=query_column}}, def_intersect5={interface={new_quota=query_column, dept_id=query_column}, def_query4={query_dictionary={p_col=[[@33,182:186='p_col',<393>,1:182]], q_col=[[@35,189:193='q_col',<393>,1:189]]}, table_dictionary={quota_b={p_col=[[@33,182:186='p_col',<393>,1:182]], q_col=[[@35,189:193='q_col',<393>,1:189]]}}, setop=INTERSECTION, interface={p_col=[{name=p_col, table_ref=quota_b}], q_col=[{name=q_col, table_ref=quota_b}]}}, def_query3={query_dictionary={new_quota=[[@28,142:150='new_quota',<393>,1:142]], dept_id=[[@26,133:139='dept_id',<393>,1:133]]}, table_dictionary={quota_a={new_quota=[[@28,142:150='new_quota',<393>,1:142]], dept_id=[[@26,133:139='dept_id',<393>,1:133]]}}, setop=UNION, interface={new_quota=[{name=new_quota, table_ref=quota_a}], dept_id=[{name=dept_id, table_ref=quota_a}]}}}, interface={score=query_column, rank=query_column}}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void insertSelectUnionOverTwoExceptsWithExplicitTargetColumnsV19A() {
		final String query = " insert into employees (score, rank_bucket)"
				+ " (select rank, score from perf_a except select x_col, y_col from perf_b)"
				+ " union (select dept_id, new_quota from quota_a except select p_col, q_col from quota_b)";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={insert={preamble=insert_into, from={union={1={union={1={select={1={column={name=rank, table_ref=null}}, 2={column={name=score, table_ref=null}}}, from={table={alias=null, table=perf_a}}}, 2={union={qualifier=null, operator=except}}, 3={select={1={column={name=x_col, table_ref=null}}, 2={column={name=y_col, table_ref=null}}}, from={table={alias=null, table=perf_b}}}}}, 2={union={qualifier=null, operator=union}}, 3={union={1={select={1={column={name=dept_id, table_ref=null}}, 2={column={name=new_quota, table_ref=null}}}, from={table={alias=null, table=quota_a}}}, 2={union={qualifier=null, operator=except}}, 3={select={1={column={name=p_col, table_ref=null}}, 2={column={name=q_col, table_ref=null}}}, from={table={alias=null, table=quota_b}}}}}}}, target_table={table={alias=null, table=employees}}, columns={1={column={name=score, table_ref=null}}, 2={column={name=rank_bucket, table_ref=null}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[score, rank_bucket]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{quota_b={p_col=[[@33,176:180='p_col',<393>,1:176]], q_col=[[@35,183:187='q_col',<393>,1:183]]}, quota_a={new_quota=[[@28,139:147='new_quota',<393>,1:139]], dept_id=[[@26,130:136='dept_id',<393>,1:130]]}, perf_a={score=[[@12,58:62='score',<393>,1:58]], rank=[[@10,52:55='rank',<128>,1:52]]}, employees={score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@6,31:41='rank_bucket',<393>,1:31]]}, perf_b={x_col=[[@17,90:94='x_col',<393>,1:90]], y_col=[[@19,97:101='y_col',<393>,1:97]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query4={p_col=[[@33,176:180='p_col',<393>,1:176]], q_col=[[@35,183:187='q_col',<393>,1:183]]}, insert7={score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@6,31:41='rank_bucket',<393>,1:31]]}, query0={score=[[@12,58:62='score',<393>,1:58]], rank=[[@10,52:55='rank',<128>,1:52]]}, query1={x_col=[[@17,90:94='x_col',<393>,1:90]], y_col=[[@19,97:101='y_col',<393>,1:97]]}, query3={dept_id=[[@26,130:136='dept_id',<393>,1:130]], new_quota=[[@28,139:147='new_quota',<393>,1:139]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
				Assert.assertEquals("Symbol Table is wrong", "{def_insert7={query_dictionary={score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@6,31:41='rank_bucket',<393>,1:31]]}, table_dictionary={employees={score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@6,31:41='rank_bucket',<393>,1:31]]}}, interface={score=[{name=rank, table_ref=union6}], rank_bucket=[{name=score, table_ref=union6}]}, def_union6={def_union2={def_query1={query_dictionary={x_col=[[@17,90:94='x_col',<393>,1:90]], y_col=[[@19,97:101='y_col',<393>,1:97]]}, table_dictionary={perf_b={x_col=[[@17,90:94='x_col',<393>,1:90]], y_col=[[@19,97:101='y_col',<393>,1:97]]}}, setop=EXCEPT, interface={x_col=[{name=x_col, table_ref=perf_b}], y_col=[{name=y_col, table_ref=perf_b}]}}, def_query0={query_dictionary={score=[[@12,58:62='score',<393>,1:58]], rank=[[@10,52:55='rank',<128>,1:52]]}, table_dictionary={perf_a={score=[[@12,58:62='score',<393>,1:58]], rank=[[@10,52:55='rank',<128>,1:52]]}}, interface={score=[{name=score, table_ref=perf_a}], rank=[{name=rank, table_ref=perf_a}]}}, interface={score=query_column, rank=query_column}}, interface={score=query_column, rank=query_column}, def_union5={interface={new_quota=query_column, dept_id=query_column}, def_query4={query_dictionary={p_col=[[@33,176:180='p_col',<393>,1:176]], q_col=[[@35,183:187='q_col',<393>,1:183]]}, table_dictionary={quota_b={p_col=[[@33,176:180='p_col',<393>,1:176]], q_col=[[@35,183:187='q_col',<393>,1:183]]}}, setop=EXCEPT, interface={p_col=[{name=p_col, table_ref=quota_b}], q_col=[{name=q_col, table_ref=quota_b}]}}, def_query3={query_dictionary={new_quota=[[@28,139:147='new_quota',<393>,1:139]], dept_id=[[@26,130:136='dept_id',<393>,1:130]]}, table_dictionary={quota_a={new_quota=[[@28,139:147='new_quota',<393>,1:139]], dept_id=[[@26,130:136='dept_id',<393>,1:130]]}}, setop=UNION, interface={new_quota=[{name=new_quota, table_ref=quota_a}], dept_id=[{name=dept_id, table_ref=quota_a}]}}}}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void insertSelectExceptOverTwoIntersectsWithExplicitTargetColumnsV19A(){
		final String query = " insert into employees (score, rank_bucket)"
				+ " (select rank, score from perf_a intersect select x_col, y_col from perf_b)"
				+ " except (select dept_id, new_quota from quota_a intersect select p_col, q_col from quota_b)";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={insert={preamble=insert_into, from={union={1={intersect={1={select={1={column={name=rank, table_ref=null}}, 2={column={name=score, table_ref=null}}}, from={table={alias=null, table=perf_a}}}, 2={intersect={qualifier=null, operator=intersect}}, 3={select={1={column={name=x_col, table_ref=null}}, 2={column={name=y_col, table_ref=null}}}, from={table={alias=null, table=perf_b}}}}}, 2={union={qualifier=null, operator=except}}, 3={intersect={1={select={1={column={name=dept_id, table_ref=null}}, 2={column={name=new_quota, table_ref=null}}}, from={table={alias=null, table=quota_a}}}, 2={intersect={qualifier=null, operator=intersect}}, 3={select={1={column={name=p_col, table_ref=null}}, 2={column={name=q_col, table_ref=null}}}, from={table={alias=null, table=quota_b}}}}}}}, target_table={table={alias=null, table=employees}}, columns={1={column={name=score, table_ref=null}}, 2={column={name=rank_bucket, table_ref=null}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[score, rank_bucket]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{quota_b={p_col=[[@33,183:187='p_col',<393>,1:183]], q_col=[[@35,190:194='q_col',<393>,1:190]]}, quota_a={new_quota=[[@28,143:151='new_quota',<393>,1:143]], dept_id=[[@26,134:140='dept_id',<393>,1:134]]}, perf_a={score=[[@12,58:62='score',<393>,1:58]], rank=[[@10,52:55='rank',<128>,1:52]]}, employees={score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@6,31:41='rank_bucket',<393>,1:31]]}, perf_b={x_col=[[@17,93:97='x_col',<393>,1:93]], y_col=[[@19,100:104='y_col',<393>,1:100]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query4={p_col=[[@33,183:187='p_col',<393>,1:183]], q_col=[[@35,190:194='q_col',<393>,1:190]]}, insert7={score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@6,31:41='rank_bucket',<393>,1:31]]}, query0={score=[[@12,58:62='score',<393>,1:58]], rank=[[@10,52:55='rank',<128>,1:52]]}, query1={x_col=[[@17,93:97='x_col',<393>,1:93]], y_col=[[@19,100:104='y_col',<393>,1:100]]}, query3={dept_id=[[@26,134:140='dept_id',<393>,1:134]], new_quota=[[@28,143:151='new_quota',<393>,1:143]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
				Assert.assertEquals("Symbol Table is wrong", "{def_insert7={query_dictionary={score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@6,31:41='rank_bucket',<393>,1:31]]}, table_dictionary={employees={score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@6,31:41='rank_bucket',<393>,1:31]]}}, interface={score=[{name=rank, table_ref=union6}], rank_bucket=[{name=score, table_ref=union6}]}, def_union6={def_intersect2={def_query1={query_dictionary={x_col=[[@17,93:97='x_col',<393>,1:93]], y_col=[[@19,100:104='y_col',<393>,1:100]]}, table_dictionary={perf_b={x_col=[[@17,93:97='x_col',<393>,1:93]], y_col=[[@19,100:104='y_col',<393>,1:100]]}}, setop=INTERSECTION, interface={x_col=[{name=x_col, table_ref=perf_b}], y_col=[{name=y_col, table_ref=perf_b}]}}, def_query0={query_dictionary={score=[[@12,58:62='score',<393>,1:58]], rank=[[@10,52:55='rank',<128>,1:52]]}, table_dictionary={perf_a={score=[[@12,58:62='score',<393>,1:58]], rank=[[@10,52:55='rank',<128>,1:52]]}}, interface={score=[{name=score, table_ref=perf_a}], rank=[{name=rank, table_ref=perf_a}]}}, interface={score=query_column, rank=query_column}}, def_intersect5={interface={new_quota=query_column, dept_id=query_column}, def_query4={query_dictionary={p_col=[[@33,183:187='p_col',<393>,1:183]], q_col=[[@35,190:194='q_col',<393>,1:190]]}, table_dictionary={quota_b={p_col=[[@33,183:187='p_col',<393>,1:183]], q_col=[[@35,190:194='q_col',<393>,1:190]]}}, setop=INTERSECTION, interface={p_col=[{name=p_col, table_ref=quota_b}], q_col=[{name=q_col, table_ref=quota_b}]}}, def_query3={query_dictionary={new_quota=[[@28,143:151='new_quota',<393>,1:143]], dept_id=[[@26,134:140='dept_id',<393>,1:134]]}, table_dictionary={quota_a={new_quota=[[@28,143:151='new_quota',<393>,1:143]], dept_id=[[@26,134:140='dept_id',<393>,1:134]]}}, setop=EXCEPT, interface={new_quota=[{name=new_quota, table_ref=quota_a}], dept_id=[{name=dept_id, table_ref=quota_a}]}}}, interface={score=query_column, rank=query_column}}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void insertSelectIntersectOverTwoIntersectsWithExplicitTargetColumnsV19B() {
		final String query = " insert into employees (score, rank_bucket)"
				+ " (select rank, score from perf_a intersect select x_col, y_col from perf_b)"
				+ " intersect (select dept_id, new_quota from quota_a intersect select p_col, q_col from quota_b)";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={insert={preamble=insert_into, from={intersect={1={intersect={1={select={1={column={name=rank, table_ref=null}}, 2={column={name=score, table_ref=null}}}, from={table={alias=null, table=perf_a}}}, 2={intersect={qualifier=null, operator=intersect}}, 3={select={1={column={name=x_col, table_ref=null}}, 2={column={name=y_col, table_ref=null}}}, from={table={alias=null, table=perf_b}}}}}, 2={intersect={qualifier=null, operator=intersect}}, 3={intersect={1={select={1={column={name=dept_id, table_ref=null}}, 2={column={name=new_quota, table_ref=null}}}, from={table={alias=null, table=quota_a}}}, 2={intersect={qualifier=null, operator=intersect}}, 3={select={1={column={name=p_col, table_ref=null}}, 2={column={name=q_col, table_ref=null}}}, from={table={alias=null, table=quota_b}}}}}}}, target_table={table={alias=null, table=employees}}, columns={1={column={name=score, table_ref=null}}, 2={column={name=rank_bucket, table_ref=null}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[score, rank_bucket]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{quota_b={p_col=[[@33,186:190='p_col',<393>,1:186]], q_col=[[@35,193:197='q_col',<393>,1:193]]}, quota_a={new_quota=[[@28,146:154='new_quota',<393>,1:146]], dept_id=[[@26,137:143='dept_id',<393>,1:137]]}, perf_a={score=[[@12,58:62='score',<393>,1:58]], rank=[[@10,52:55='rank',<128>,1:52]]}, employees={score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@6,31:41='rank_bucket',<393>,1:31]]}, perf_b={x_col=[[@17,93:97='x_col',<393>,1:93]], y_col=[[@19,100:104='y_col',<393>,1:100]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query4={p_col=[[@33,186:190='p_col',<393>,1:186]], q_col=[[@35,193:197='q_col',<393>,1:193]]}, insert7={score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@6,31:41='rank_bucket',<393>,1:31]]}, query0={score=[[@12,58:62='score',<393>,1:58]], rank=[[@10,52:55='rank',<128>,1:52]]}, query1={x_col=[[@17,93:97='x_col',<393>,1:93]], y_col=[[@19,100:104='y_col',<393>,1:100]]}, query3={dept_id=[[@26,137:143='dept_id',<393>,1:137]], new_quota=[[@28,146:154='new_quota',<393>,1:146]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
				Assert.assertEquals("Symbol Table is wrong", "{def_insert7={query_dictionary={score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@6,31:41='rank_bucket',<393>,1:31]]}, table_dictionary={employees={score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@6,31:41='rank_bucket',<393>,1:31]]}}, def_intersect6={def_intersect2={def_query1={query_dictionary={x_col=[[@17,93:97='x_col',<393>,1:93]], y_col=[[@19,100:104='y_col',<393>,1:100]]}, table_dictionary={perf_b={x_col=[[@17,93:97='x_col',<393>,1:93]], y_col=[[@19,100:104='y_col',<393>,1:100]]}}, setop=INTERSECTION, interface={x_col=[{name=x_col, table_ref=perf_b}], y_col=[{name=y_col, table_ref=perf_b}]}}, def_query0={query_dictionary={score=[[@12,58:62='score',<393>,1:58]], rank=[[@10,52:55='rank',<128>,1:52]]}, table_dictionary={perf_a={score=[[@12,58:62='score',<393>,1:58]], rank=[[@10,52:55='rank',<128>,1:52]]}}, interface={score=[{name=score, table_ref=perf_a}], rank=[{name=rank, table_ref=perf_a}]}}, interface={score=query_column, rank=query_column}}, def_intersect5={interface={new_quota=query_column, dept_id=query_column}, def_query4={query_dictionary={p_col=[[@33,186:190='p_col',<393>,1:186]], q_col=[[@35,193:197='q_col',<393>,1:193]]}, table_dictionary={quota_b={p_col=[[@33,186:190='p_col',<393>,1:186]], q_col=[[@35,193:197='q_col',<393>,1:193]]}}, setop=INTERSECTION, interface={p_col=[{name=p_col, table_ref=quota_b}], q_col=[{name=q_col, table_ref=quota_b}]}}, def_query3={query_dictionary={new_quota=[[@28,146:154='new_quota',<393>,1:146]], dept_id=[[@26,137:143='dept_id',<393>,1:137]]}, table_dictionary={quota_a={new_quota=[[@28,146:154='new_quota',<393>,1:146]], dept_id=[[@26,137:143='dept_id',<393>,1:137]]}}, setop=INTERSECTION, interface={new_quota=[{name=new_quota, table_ref=quota_a}], dept_id=[{name=dept_id, table_ref=quota_a}]}}}, interface={score=query_column, rank=query_column}}, interface={score=[{name=rank, table_ref=intersect6}], rank_bucket=[{name=score, table_ref=intersect6}]}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void insertSelectExceptOverTwoExceptsWithExplicitTargetColumnsV19B() {
		final String query = " insert into employees (score, rank_bucket)"
				+ " (select rank, score from perf_a except select x_col, y_col from perf_b)"
				+ " except (select dept_id, new_quota from quota_a except select p_col, q_col from quota_b)";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={insert={preamble=insert_into, from={union={1={union={1={select={1={column={name=rank, table_ref=null}}, 2={column={name=score, table_ref=null}}}, from={table={alias=null, table=perf_a}}}, 2={union={qualifier=null, operator=except}}, 3={select={1={column={name=x_col, table_ref=null}}, 2={column={name=y_col, table_ref=null}}}, from={table={alias=null, table=perf_b}}}}}, 2={union={qualifier=null, operator=except}}, 3={union={1={select={1={column={name=dept_id, table_ref=null}}, 2={column={name=new_quota, table_ref=null}}}, from={table={alias=null, table=quota_a}}}, 2={union={qualifier=null, operator=except}}, 3={select={1={column={name=p_col, table_ref=null}}, 2={column={name=q_col, table_ref=null}}}, from={table={alias=null, table=quota_b}}}}}}}, target_table={table={alias=null, table=employees}}, columns={1={column={name=score, table_ref=null}}, 2={column={name=rank_bucket, table_ref=null}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[score, rank_bucket]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{quota_b={p_col=[[@33,177:181='p_col',<393>,1:177]], q_col=[[@35,184:188='q_col',<393>,1:184]]}, quota_a={new_quota=[[@28,140:148='new_quota',<393>,1:140]], dept_id=[[@26,131:137='dept_id',<393>,1:131]]}, perf_a={score=[[@12,58:62='score',<393>,1:58]], rank=[[@10,52:55='rank',<128>,1:52]]}, employees={score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@6,31:41='rank_bucket',<393>,1:31]]}, perf_b={x_col=[[@17,90:94='x_col',<393>,1:90]], y_col=[[@19,97:101='y_col',<393>,1:97]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query4={p_col=[[@33,177:181='p_col',<393>,1:177]], q_col=[[@35,184:188='q_col',<393>,1:184]]}, insert7={score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@6,31:41='rank_bucket',<393>,1:31]]}, query0={score=[[@12,58:62='score',<393>,1:58]], rank=[[@10,52:55='rank',<128>,1:52]]}, query1={x_col=[[@17,90:94='x_col',<393>,1:90]], y_col=[[@19,97:101='y_col',<393>,1:97]]}, query3={dept_id=[[@26,131:137='dept_id',<393>,1:131]], new_quota=[[@28,140:148='new_quota',<393>,1:140]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
				Assert.assertEquals("Symbol Table is wrong", "{def_insert7={query_dictionary={score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@6,31:41='rank_bucket',<393>,1:31]]}, table_dictionary={employees={score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@6,31:41='rank_bucket',<393>,1:31]]}}, interface={score=[{name=rank, table_ref=union6}], rank_bucket=[{name=score, table_ref=union6}]}, def_union6={def_union2={def_query1={query_dictionary={x_col=[[@17,90:94='x_col',<393>,1:90]], y_col=[[@19,97:101='y_col',<393>,1:97]]}, table_dictionary={perf_b={x_col=[[@17,90:94='x_col',<393>,1:90]], y_col=[[@19,97:101='y_col',<393>,1:97]]}}, setop=EXCEPT, interface={x_col=[{name=x_col, table_ref=perf_b}], y_col=[{name=y_col, table_ref=perf_b}]}}, def_query0={query_dictionary={score=[[@12,58:62='score',<393>,1:58]], rank=[[@10,52:55='rank',<128>,1:52]]}, table_dictionary={perf_a={score=[[@12,58:62='score',<393>,1:58]], rank=[[@10,52:55='rank',<128>,1:52]]}}, interface={score=[{name=score, table_ref=perf_a}], rank=[{name=rank, table_ref=perf_a}]}}, interface={score=query_column, rank=query_column}}, interface={score=query_column, rank=query_column}, def_union5={interface={new_quota=query_column, dept_id=query_column}, def_query4={query_dictionary={p_col=[[@33,177:181='p_col',<393>,1:177]], q_col=[[@35,184:188='q_col',<393>,1:184]]}, table_dictionary={quota_b={p_col=[[@33,177:181='p_col',<393>,1:177]], q_col=[[@35,184:188='q_col',<393>,1:184]]}}, setop=EXCEPT, interface={p_col=[{name=p_col, table_ref=quota_b}], q_col=[{name=q_col, table_ref=quota_b}]}}, def_query3={query_dictionary={new_quota=[[@28,140:148='new_quota',<393>,1:140]], dept_id=[[@26,131:137='dept_id',<393>,1:131]]}, table_dictionary={quota_a={new_quota=[[@28,140:148='new_quota',<393>,1:140]], dept_id=[[@26,131:137='dept_id',<393>,1:131]]}}, setop=EXCEPT, interface={new_quota=[{name=new_quota, table_ref=quota_a}], dept_id=[{name=dept_id, table_ref=quota_a}]}}}}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void insertSelectIntersectOverTwoUnionsWithExplicitTargetColumnsV20A() {
		final String query = " insert into employees (score, rank_bucket)"
				+ " (select rank, score from perf_a union select x_col, y_col from perf_b)"
				+ " intersect (select dept_id, new_quota from quota_a union select p_col, q_col from quota_b)";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={insert={preamble=insert_into, from={intersect={1={union={1={select={1={column={name=rank, table_ref=null}}, 2={column={name=score, table_ref=null}}}, from={table={alias=null, table=perf_a}}}, 2={union={qualifier=null, operator=union}}, 3={select={1={column={name=x_col, table_ref=null}}, 2={column={name=y_col, table_ref=null}}}, from={table={alias=null, table=perf_b}}}}}, 2={intersect={qualifier=null, operator=intersect}}, 3={union={1={select={1={column={name=dept_id, table_ref=null}}, 2={column={name=new_quota, table_ref=null}}}, from={table={alias=null, table=quota_a}}}, 2={union={qualifier=null, operator=union}}, 3={select={1={column={name=p_col, table_ref=null}}, 2={column={name=q_col, table_ref=null}}}, from={table={alias=null, table=quota_b}}}}}}}, target_table={table={alias=null, table=employees}}, columns={1={column={name=score, table_ref=null}}, 2={column={name=rank_bucket, table_ref=null}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[score, rank_bucket]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{quota_b={p_col=[[@33,178:182='p_col',<393>,1:178]], q_col=[[@35,185:189='q_col',<393>,1:185]]}, quota_a={new_quota=[[@28,142:150='new_quota',<393>,1:142]], dept_id=[[@26,133:139='dept_id',<393>,1:133]]}, perf_a={score=[[@12,58:62='score',<393>,1:58]], rank=[[@10,52:55='rank',<128>,1:52]]}, employees={score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@6,31:41='rank_bucket',<393>,1:31]]}, perf_b={x_col=[[@17,89:93='x_col',<393>,1:89]], y_col=[[@19,96:100='y_col',<393>,1:96]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query4={p_col=[[@33,178:182='p_col',<393>,1:178]], q_col=[[@35,185:189='q_col',<393>,1:185]]}, insert7={score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@6,31:41='rank_bucket',<393>,1:31]]}, query0={score=[[@12,58:62='score',<393>,1:58]], rank=[[@10,52:55='rank',<128>,1:52]]}, query1={x_col=[[@17,89:93='x_col',<393>,1:89]], y_col=[[@19,96:100='y_col',<393>,1:96]]}, query3={dept_id=[[@26,133:139='dept_id',<393>,1:133]], new_quota=[[@28,142:150='new_quota',<393>,1:142]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
				Assert.assertEquals("Symbol Table is wrong", "{def_insert7={query_dictionary={score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@6,31:41='rank_bucket',<393>,1:31]]}, table_dictionary={employees={score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@6,31:41='rank_bucket',<393>,1:31]]}}, def_intersect6={def_union2={def_query1={query_dictionary={x_col=[[@17,89:93='x_col',<393>,1:89]], y_col=[[@19,96:100='y_col',<393>,1:96]]}, table_dictionary={perf_b={x_col=[[@17,89:93='x_col',<393>,1:89]], y_col=[[@19,96:100='y_col',<393>,1:96]]}}, setop=UNION, interface={x_col=[{name=x_col, table_ref=perf_b}], y_col=[{name=y_col, table_ref=perf_b}]}}, def_query0={query_dictionary={score=[[@12,58:62='score',<393>,1:58]], rank=[[@10,52:55='rank',<128>,1:52]]}, table_dictionary={perf_a={score=[[@12,58:62='score',<393>,1:58]], rank=[[@10,52:55='rank',<128>,1:52]]}}, interface={score=[{name=score, table_ref=perf_a}], rank=[{name=rank, table_ref=perf_a}]}}, interface={score=query_column, rank=query_column}}, interface={score=query_column, rank=query_column}, def_union5={setop=INTERSECTION, interface={new_quota=query_column, dept_id=query_column}, def_query4={query_dictionary={p_col=[[@33,178:182='p_col',<393>,1:178]], q_col=[[@35,185:189='q_col',<393>,1:185]]}, table_dictionary={quota_b={p_col=[[@33,178:182='p_col',<393>,1:178]], q_col=[[@35,185:189='q_col',<393>,1:185]]}}, setop=UNION, interface={p_col=[{name=p_col, table_ref=quota_b}], q_col=[{name=q_col, table_ref=quota_b}]}}, def_query3={query_dictionary={new_quota=[[@28,142:150='new_quota',<393>,1:142]], dept_id=[[@26,133:139='dept_id',<393>,1:133]]}, table_dictionary={quota_a={new_quota=[[@28,142:150='new_quota',<393>,1:142]], dept_id=[[@26,133:139='dept_id',<393>,1:133]]}}, interface={new_quota=[{name=new_quota, table_ref=quota_a}], dept_id=[{name=dept_id, table_ref=quota_a}]}}}}, interface={score=[{name=rank, table_ref=intersect6}], rank_bucket=[{name=score, table_ref=intersect6}]}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void insertSelectExceptOverTwoUnionsWithExplicitTargetColumnsV20A() {
		final String query = " insert into employees (score, rank_bucket)"
				+ " (select rank, score from perf_a union select x_col, y_col from perf_b)"
				+ " except (select dept_id, new_quota from quota_a union select p_col, q_col from quota_b)";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={insert={preamble=insert_into, from={union={1={union={1={select={1={column={name=rank, table_ref=null}}, 2={column={name=score, table_ref=null}}}, from={table={alias=null, table=perf_a}}}, 2={union={qualifier=null, operator=union}}, 3={select={1={column={name=x_col, table_ref=null}}, 2={column={name=y_col, table_ref=null}}}, from={table={alias=null, table=perf_b}}}}}, 2={union={qualifier=null, operator=except}}, 3={union={1={select={1={column={name=dept_id, table_ref=null}}, 2={column={name=new_quota, table_ref=null}}}, from={table={alias=null, table=quota_a}}}, 2={union={qualifier=null, operator=union}}, 3={select={1={column={name=p_col, table_ref=null}}, 2={column={name=q_col, table_ref=null}}}, from={table={alias=null, table=quota_b}}}}}}}, target_table={table={alias=null, table=employees}}, columns={1={column={name=score, table_ref=null}}, 2={column={name=rank_bucket, table_ref=null}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[score, rank_bucket]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{quota_b={p_col=[[@33,175:179='p_col',<393>,1:175]], q_col=[[@35,182:186='q_col',<393>,1:182]]}, quota_a={new_quota=[[@28,139:147='new_quota',<393>,1:139]], dept_id=[[@26,130:136='dept_id',<393>,1:130]]}, perf_a={score=[[@12,58:62='score',<393>,1:58]], rank=[[@10,52:55='rank',<128>,1:52]]}, employees={score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@6,31:41='rank_bucket',<393>,1:31]]}, perf_b={x_col=[[@17,89:93='x_col',<393>,1:89]], y_col=[[@19,96:100='y_col',<393>,1:96]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query4={p_col=[[@33,175:179='p_col',<393>,1:175]], q_col=[[@35,182:186='q_col',<393>,1:182]]}, insert7={score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@6,31:41='rank_bucket',<393>,1:31]]}, query0={score=[[@12,58:62='score',<393>,1:58]], rank=[[@10,52:55='rank',<128>,1:52]]}, query1={x_col=[[@17,89:93='x_col',<393>,1:89]], y_col=[[@19,96:100='y_col',<393>,1:96]]}, query3={dept_id=[[@26,130:136='dept_id',<393>,1:130]], new_quota=[[@28,139:147='new_quota',<393>,1:139]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
				Assert.assertEquals("Symbol Table is wrong", "{def_insert7={query_dictionary={score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@6,31:41='rank_bucket',<393>,1:31]]}, table_dictionary={employees={score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@6,31:41='rank_bucket',<393>,1:31]]}}, interface={score=[{name=rank, table_ref=union6}], rank_bucket=[{name=score, table_ref=union6}]}, def_union6={def_union2={def_query1={query_dictionary={x_col=[[@17,89:93='x_col',<393>,1:89]], y_col=[[@19,96:100='y_col',<393>,1:96]]}, table_dictionary={perf_b={x_col=[[@17,89:93='x_col',<393>,1:89]], y_col=[[@19,96:100='y_col',<393>,1:96]]}}, setop=UNION, interface={x_col=[{name=x_col, table_ref=perf_b}], y_col=[{name=y_col, table_ref=perf_b}]}}, def_query0={query_dictionary={score=[[@12,58:62='score',<393>,1:58]], rank=[[@10,52:55='rank',<128>,1:52]]}, table_dictionary={perf_a={score=[[@12,58:62='score',<393>,1:58]], rank=[[@10,52:55='rank',<128>,1:52]]}}, interface={score=[{name=score, table_ref=perf_a}], rank=[{name=rank, table_ref=perf_a}]}}, interface={score=query_column, rank=query_column}}, interface={score=query_column, rank=query_column}, def_union5={interface={new_quota=query_column, dept_id=query_column}, def_query4={query_dictionary={p_col=[[@33,175:179='p_col',<393>,1:175]], q_col=[[@35,182:186='q_col',<393>,1:182]]}, table_dictionary={quota_b={p_col=[[@33,175:179='p_col',<393>,1:175]], q_col=[[@35,182:186='q_col',<393>,1:182]]}}, setop=UNION, interface={p_col=[{name=p_col, table_ref=quota_b}], q_col=[{name=q_col, table_ref=quota_b}]}}, def_query3={query_dictionary={new_quota=[[@28,139:147='new_quota',<393>,1:139]], dept_id=[[@26,130:136='dept_id',<393>,1:130]]}, table_dictionary={quota_a={new_quota=[[@28,139:147='new_quota',<393>,1:139]], dept_id=[[@26,130:136='dept_id',<393>,1:130]]}}, setop=EXCEPT, interface={new_quota=[{name=new_quota, table_ref=quota_a}], dept_id=[{name=dept_id, table_ref=quota_a}]}}}}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void insertSelectIntersectOverTwoExceptsWithExplicitTargetColumnsV20A(){
		final String query = " insert into employees (score, rank_bucket)"
				+ " (select rank, score from perf_a except select x_col, y_col from perf_b)"
				+ " intersect (select dept_id, new_quota from quota_a except select p_col, q_col from quota_b)";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={insert={preamble=insert_into, from={intersect={1={union={1={select={1={column={name=rank, table_ref=null}}, 2={column={name=score, table_ref=null}}}, from={table={alias=null, table=perf_a}}}, 2={union={qualifier=null, operator=except}}, 3={select={1={column={name=x_col, table_ref=null}}, 2={column={name=y_col, table_ref=null}}}, from={table={alias=null, table=perf_b}}}}}, 2={intersect={qualifier=null, operator=intersect}}, 3={union={1={select={1={column={name=dept_id, table_ref=null}}, 2={column={name=new_quota, table_ref=null}}}, from={table={alias=null, table=quota_a}}}, 2={union={qualifier=null, operator=except}}, 3={select={1={column={name=p_col, table_ref=null}}, 2={column={name=q_col, table_ref=null}}}, from={table={alias=null, table=quota_b}}}}}}}, target_table={table={alias=null, table=employees}}, columns={1={column={name=score, table_ref=null}}, 2={column={name=rank_bucket, table_ref=null}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[score, rank_bucket]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{quota_b={p_col=[[@33,180:184='p_col',<393>,1:180]], q_col=[[@35,187:191='q_col',<393>,1:187]]}, quota_a={new_quota=[[@28,143:151='new_quota',<393>,1:143]], dept_id=[[@26,134:140='dept_id',<393>,1:134]]}, perf_a={score=[[@12,58:62='score',<393>,1:58]], rank=[[@10,52:55='rank',<128>,1:52]]}, employees={score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@6,31:41='rank_bucket',<393>,1:31]]}, perf_b={x_col=[[@17,90:94='x_col',<393>,1:90]], y_col=[[@19,97:101='y_col',<393>,1:97]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query4={p_col=[[@33,180:184='p_col',<393>,1:180]], q_col=[[@35,187:191='q_col',<393>,1:187]]}, insert7={score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@6,31:41='rank_bucket',<393>,1:31]]}, query0={score=[[@12,58:62='score',<393>,1:58]], rank=[[@10,52:55='rank',<128>,1:52]]}, query1={x_col=[[@17,90:94='x_col',<393>,1:90]], y_col=[[@19,97:101='y_col',<393>,1:97]]}, query3={dept_id=[[@26,134:140='dept_id',<393>,1:134]], new_quota=[[@28,143:151='new_quota',<393>,1:143]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
				Assert.assertEquals("Symbol Table is wrong", "{def_insert7={query_dictionary={score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@6,31:41='rank_bucket',<393>,1:31]]}, table_dictionary={employees={score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@6,31:41='rank_bucket',<393>,1:31]]}}, def_intersect6={def_union2={def_query1={query_dictionary={x_col=[[@17,90:94='x_col',<393>,1:90]], y_col=[[@19,97:101='y_col',<393>,1:97]]}, table_dictionary={perf_b={x_col=[[@17,90:94='x_col',<393>,1:90]], y_col=[[@19,97:101='y_col',<393>,1:97]]}}, setop=EXCEPT, interface={x_col=[{name=x_col, table_ref=perf_b}], y_col=[{name=y_col, table_ref=perf_b}]}}, def_query0={query_dictionary={score=[[@12,58:62='score',<393>,1:58]], rank=[[@10,52:55='rank',<128>,1:52]]}, table_dictionary={perf_a={score=[[@12,58:62='score',<393>,1:58]], rank=[[@10,52:55='rank',<128>,1:52]]}}, interface={score=[{name=score, table_ref=perf_a}], rank=[{name=rank, table_ref=perf_a}]}}, interface={score=query_column, rank=query_column}}, interface={score=query_column, rank=query_column}, def_union5={setop=INTERSECTION, interface={new_quota=query_column, dept_id=query_column}, def_query4={query_dictionary={p_col=[[@33,180:184='p_col',<393>,1:180]], q_col=[[@35,187:191='q_col',<393>,1:187]]}, table_dictionary={quota_b={p_col=[[@33,180:184='p_col',<393>,1:180]], q_col=[[@35,187:191='q_col',<393>,1:187]]}}, setop=EXCEPT, interface={p_col=[{name=p_col, table_ref=quota_b}], q_col=[{name=q_col, table_ref=quota_b}]}}, def_query3={query_dictionary={new_quota=[[@28,143:151='new_quota',<393>,1:143]], dept_id=[[@26,134:140='dept_id',<393>,1:134]]}, table_dictionary={quota_a={new_quota=[[@28,143:151='new_quota',<393>,1:143]], dept_id=[[@26,134:140='dept_id',<393>,1:134]]}}, interface={new_quota=[{name=new_quota, table_ref=quota_a}], dept_id=[{name=dept_id, table_ref=quota_a}]}}}}, interface={score=[{name=rank, table_ref=intersect6}], rank_bucket=[{name=score, table_ref=intersect6}]}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void insertSelectUnionOverTwoUnionsWithExplicitTargetColumnsV20B() {
		final String query = " insert into employees (score, rank_bucket)"
				+ " (select rank, score from perf_a union select x_col, y_col from perf_b)"
				+ " union (select dept_id, new_quota from quota_a union select p_col, q_col from quota_b)";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={insert={preamble=insert_into, from={union={1={union={1={select={1={column={name=rank, table_ref=null}}, 2={column={name=score, table_ref=null}}}, from={table={alias=null, table=perf_a}}}, 2={union={qualifier=null, operator=union}}, 3={select={1={column={name=x_col, table_ref=null}}, 2={column={name=y_col, table_ref=null}}}, from={table={alias=null, table=perf_b}}}}}, 2={union={qualifier=null, operator=union}}, 3={union={1={select={1={column={name=dept_id, table_ref=null}}, 2={column={name=new_quota, table_ref=null}}}, from={table={alias=null, table=quota_a}}}, 2={union={qualifier=null, operator=union}}, 3={select={1={column={name=p_col, table_ref=null}}, 2={column={name=q_col, table_ref=null}}}, from={table={alias=null, table=quota_b}}}}}}}, target_table={table={alias=null, table=employees}}, columns={1={column={name=score, table_ref=null}}, 2={column={name=rank_bucket, table_ref=null}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[score, rank_bucket]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{quota_b={p_col=[[@33,174:178='p_col',<393>,1:174]], q_col=[[@35,181:185='q_col',<393>,1:181]]}, quota_a={new_quota=[[@28,138:146='new_quota',<393>,1:138]], dept_id=[[@26,129:135='dept_id',<393>,1:129]]}, perf_a={score=[[@12,58:62='score',<393>,1:58]], rank=[[@10,52:55='rank',<128>,1:52]]}, employees={score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@6,31:41='rank_bucket',<393>,1:31]]}, perf_b={x_col=[[@17,89:93='x_col',<393>,1:89]], y_col=[[@19,96:100='y_col',<393>,1:96]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query4={p_col=[[@33,174:178='p_col',<393>,1:174]], q_col=[[@35,181:185='q_col',<393>,1:181]]}, insert7={score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@6,31:41='rank_bucket',<393>,1:31]]}, query0={score=[[@12,58:62='score',<393>,1:58]], rank=[[@10,52:55='rank',<128>,1:52]]}, query1={x_col=[[@17,89:93='x_col',<393>,1:89]], y_col=[[@19,96:100='y_col',<393>,1:96]]}, query3={dept_id=[[@26,129:135='dept_id',<393>,1:129]], new_quota=[[@28,138:146='new_quota',<393>,1:138]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_insert7={query_dictionary={score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@6,31:41='rank_bucket',<393>,1:31]]}, table_dictionary={employees={score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@6,31:41='rank_bucket',<393>,1:31]]}}, interface={score=[{name=rank, table_ref=union6}], rank_bucket=[{name=score, table_ref=union6}]}, def_union6={def_union2={def_query1={query_dictionary={x_col=[[@17,89:93='x_col',<393>,1:89]], y_col=[[@19,96:100='y_col',<393>,1:96]]}, table_dictionary={perf_b={x_col=[[@17,89:93='x_col',<393>,1:89]], y_col=[[@19,96:100='y_col',<393>,1:96]]}}, setop=UNION, interface={x_col=[{name=x_col, table_ref=perf_b}], y_col=[{name=y_col, table_ref=perf_b}]}}, def_query0={query_dictionary={score=[[@12,58:62='score',<393>,1:58]], rank=[[@10,52:55='rank',<128>,1:52]]}, table_dictionary={perf_a={score=[[@12,58:62='score',<393>,1:58]], rank=[[@10,52:55='rank',<128>,1:52]]}}, interface={score=[{name=score, table_ref=perf_a}], rank=[{name=rank, table_ref=perf_a}]}}, interface={score=query_column, rank=query_column}}, interface={score=query_column, rank=query_column}, def_union5={interface={new_quota=query_column, dept_id=query_column}, def_query4={query_dictionary={p_col=[[@33,174:178='p_col',<393>,1:174]], q_col=[[@35,181:185='q_col',<393>,1:181]]}, table_dictionary={quota_b={p_col=[[@33,174:178='p_col',<393>,1:174]], q_col=[[@35,181:185='q_col',<393>,1:181]]}}, setop=UNION, interface={p_col=[{name=p_col, table_ref=quota_b}], q_col=[{name=q_col, table_ref=quota_b}]}}, def_query3={query_dictionary={new_quota=[[@28,138:146='new_quota',<393>,1:138]], dept_id=[[@26,129:135='dept_id',<393>,1:129]]}, table_dictionary={quota_a={new_quota=[[@28,138:146='new_quota',<393>,1:138]], dept_id=[[@26,129:135='dept_id',<393>,1:129]]}}, setop=UNION, interface={new_quota=[{name=new_quota, table_ref=quota_a}], dept_id=[{name=dept_id, table_ref=quota_a}]}}}}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void insertSelectExceptOverTwoExceptsWithExplicitTargetColumnsV20B(){
		final String query = " insert into employees (score, rank_bucket)"
				+ " (select rank, score from perf_a except select x_col, y_col from perf_b)"
				+ " except (select dept_id, new_quota from quota_a except select p_col, q_col from quota_b)";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={insert={preamble=insert_into, from={union={1={union={1={select={1={column={name=rank, table_ref=null}}, 2={column={name=score, table_ref=null}}}, from={table={alias=null, table=perf_a}}}, 2={union={qualifier=null, operator=except}}, 3={select={1={column={name=x_col, table_ref=null}}, 2={column={name=y_col, table_ref=null}}}, from={table={alias=null, table=perf_b}}}}}, 2={union={qualifier=null, operator=except}}, 3={union={1={select={1={column={name=dept_id, table_ref=null}}, 2={column={name=new_quota, table_ref=null}}}, from={table={alias=null, table=quota_a}}}, 2={union={qualifier=null, operator=except}}, 3={select={1={column={name=p_col, table_ref=null}}, 2={column={name=q_col, table_ref=null}}}, from={table={alias=null, table=quota_b}}}}}}}, target_table={table={alias=null, table=employees}}, columns={1={column={name=score, table_ref=null}}, 2={column={name=rank_bucket, table_ref=null}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[score, rank_bucket]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{quota_b={p_col=[[@33,177:181='p_col',<393>,1:177]], q_col=[[@35,184:188='q_col',<393>,1:184]]}, quota_a={new_quota=[[@28,140:148='new_quota',<393>,1:140]], dept_id=[[@26,131:137='dept_id',<393>,1:131]]}, perf_a={score=[[@12,58:62='score',<393>,1:58]], rank=[[@10,52:55='rank',<128>,1:52]]}, employees={score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@6,31:41='rank_bucket',<393>,1:31]]}, perf_b={x_col=[[@17,90:94='x_col',<393>,1:90]], y_col=[[@19,97:101='y_col',<393>,1:97]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query4={p_col=[[@33,177:181='p_col',<393>,1:177]], q_col=[[@35,184:188='q_col',<393>,1:184]]}, insert7={score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@6,31:41='rank_bucket',<393>,1:31]]}, query0={score=[[@12,58:62='score',<393>,1:58]], rank=[[@10,52:55='rank',<128>,1:52]]}, query1={x_col=[[@17,90:94='x_col',<393>,1:90]], y_col=[[@19,97:101='y_col',<393>,1:97]]}, query3={dept_id=[[@26,131:137='dept_id',<393>,1:131]], new_quota=[[@28,140:148='new_quota',<393>,1:140]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_insert7={query_dictionary={score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@6,31:41='rank_bucket',<393>,1:31]]}, table_dictionary={employees={score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@6,31:41='rank_bucket',<393>,1:31]]}}, interface={score=[{name=rank, table_ref=union6}], rank_bucket=[{name=score, table_ref=union6}]}, def_union6={def_union2={def_query1={query_dictionary={x_col=[[@17,90:94='x_col',<393>,1:90]], y_col=[[@19,97:101='y_col',<393>,1:97]]}, table_dictionary={perf_b={x_col=[[@17,90:94='x_col',<393>,1:90]], y_col=[[@19,97:101='y_col',<393>,1:97]]}}, setop=EXCEPT, interface={x_col=[{name=x_col, table_ref=perf_b}], y_col=[{name=y_col, table_ref=perf_b}]}}, def_query0={query_dictionary={score=[[@12,58:62='score',<393>,1:58]], rank=[[@10,52:55='rank',<128>,1:52]]}, table_dictionary={perf_a={score=[[@12,58:62='score',<393>,1:58]], rank=[[@10,52:55='rank',<128>,1:52]]}}, interface={score=[{name=score, table_ref=perf_a}], rank=[{name=rank, table_ref=perf_a}]}}, interface={score=query_column, rank=query_column}}, interface={score=query_column, rank=query_column}, def_union5={interface={new_quota=query_column, dept_id=query_column}, def_query4={query_dictionary={p_col=[[@33,177:181='p_col',<393>,1:177]], q_col=[[@35,184:188='q_col',<393>,1:184]]}, table_dictionary={quota_b={p_col=[[@33,177:181='p_col',<393>,1:177]], q_col=[[@35,184:188='q_col',<393>,1:184]]}}, setop=EXCEPT, interface={p_col=[{name=p_col, table_ref=quota_b}], q_col=[{name=q_col, table_ref=quota_b}]}}, def_query3={query_dictionary={new_quota=[[@28,140:148='new_quota',<393>,1:140]], dept_id=[[@26,131:137='dept_id',<393>,1:131]]}, table_dictionary={quota_a={new_quota=[[@28,140:148='new_quota',<393>,1:140]], dept_id=[[@26,131:137='dept_id',<393>,1:131]]}}, setop=EXCEPT, interface={new_quota=[{name=new_quota, table_ref=quota_a}], dept_id=[{name=dept_id, table_ref=quota_a}]}}}}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void updateDictionaryHandlingQualifiedColumnsFromWindowedSubqueryAndOrphanRhsV1() {
		final String query = " update employees e set score = src.acct_sales_count, rank_bucket = src.rn, orphan_sink = orphan_marker"
				+ "\n from (select a.emp_id, a.acct_sales_count,"
				+ "\n              row_number() over (partition by a.emp_id order by a.last_update desc) as rn"
				+ "\n         from accounts a"
				+ "\n        where a.acct_sales_count > 0) src"
				+ "\n where e.emp_id = src.emp_id";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={update={from={table={alias=src, query={select={1={column={name=emp_id, table_ref=a}}, 2={column={name=acct_sales_count, table_ref=a}}, 3={alias=rn, window_function={over={partition_by={1={column={name=emp_id, table_ref=a}}}, orderby={1={null_order=null, predicand={column={name=last_update, table_ref=a}}, sort_order=desc}}}, function={function_name=row_number, parameters=null}}}}, from={table={alias=a, table=accounts}}, where={condition={left={column={name=acct_sales_count, table_ref=a}}, right={literal=0}, operator=>}}}}}, where={condition={left={column={name=emp_id, table_ref=e}}, right={column={name=emp_id, table_ref=src}}, operator==}}, assignments={1={set={column={name=score, table_ref=null}}, to={column={name=acct_sales_count, table_ref=src}}}, 2={set={column={name=rank_bucket, table_ref=null}}, to={column={name=rn, table_ref=src}}}, 3={set={column={name=orphan_sink, table_ref=null}}, to={column={name=orphan_marker, table_ref=null}}}}, table={alias=e, table=employees}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[orphan_sink, score, rank_bucket]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{accounts={acct_sales_count=[[@26,128:128='a',<393>,2:24], [@53,277:277='a',<393>,5:14]], last_update=[[@42,212:212='a',<393>,3:64]], emp_id=[[@22,118:118='a',<393>,2:14], [@37,194:194='a',<393>,3:46]]}, employees={orphan_sink=[[@16,76:86='orphan_sink',<393>,1:76]], score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@10,54:64='rank_bucket',<393>,1:54]], orphan_marker=[[@18,90:102='orphan_marker',<393>,1:90]], emp_id=[[@61,312:312='e',<393>,6:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={rn=[[@48,235:236='rn',<393>,3:87], [@12,68:70='src',<393>,1:68]], acct_sales_count=[[@28,130:145='acct_sales_count',<393>,2:26], [@6,32:34='src',<393>,1:32]], emp_id=[[@24,120:125='emp_id',<393>,2:16], [@65,323:325='src',<393>,6:18]]}, update1={orphan_sink=[[@16,76:86='orphan_sink',<393>,1:76]], score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@10,54:64='rank_bucket',<393>,1:54]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_update1={assignments={orphan_sink=[{name=orphan_marker, table_ref=employees}], score=[{name=acct_sales_count, table_ref=src}], rank_bucket=[{name=rn, table_ref=src}]}, table_dictionary={employees={orphan_sink=[[@16,76:86='orphan_sink',<393>,1:76]], score=[[@4,24:28='score',<393>,1:24]], orphan_marker=[[@18,90:102='orphan_marker',<393>,1:90]], rank_bucket=[[@10,54:64='rank_bucket',<393>,1:54]], emp_id=[[@61,312:312='e',<393>,6:7]]}}, update_dictionary={orphan_sink=[[@16,76:86='orphan_sink',<393>,1:76]], score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@10,54:64='rank_bucket',<393>,1:54]]}, def_query0={window_ordered_by=[{name=last_update, table_ref=a}], query_dictionary={acct_sales_count=[[@28,130:145='acct_sales_count',<393>,2:26], [@6,32:34='src',<393>,1:32]], rn=[[@48,235:236='rn',<393>,3:87], [@12,68:70='src',<393>,1:68]], emp_id=[[@24,120:125='emp_id',<393>,2:16], [@65,323:325='src',<393>,6:18]]}, table_dictionary={accounts={acct_sales_count=[[@26,128:128='a',<393>,2:24], [@53,277:277='a',<393>,5:14]], last_update=[[@42,212:212='a',<393>,3:64]], emp_id=[[@22,118:118='a',<393>,2:14], [@37,194:194='a',<393>,3:46]]}}, window_partition_by=[{name=emp_id, table_ref=a}], filters=[{name=acct_sales_count, table_ref=a}], interface={acct_sales_count=[{name=acct_sales_count, table_ref=a}], rn=[{name=emp_id, table_ref=a}, {name=last_update, table_ref=a}], emp_id=[{name=emp_id, table_ref=a}]}, table_alias={a=accounts}}, filters=[{name=emp_id, table_ref=e}, {name=emp_id, table_ref=src}], table_alias={e=employees, src=query0}}}",
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
		Snippet snippet = extractor.getSnippet();
		assertFatalDiagnosticCount(
				snippet,
				"UNQUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIASES",
				null,
				"orphan_marker",
				1);
		assertDiagnosticCountBySeverity(
				snippet,
				"UNRESOLVED_UNQUALIFIED_COLUMNS",
				ParseDiagnostic.Severity.ERROR,
				null,
				"orphan_marker",
				1);

		Assert.assertEquals("AST is wrong", "{SQL={insert={preamble=insert_into, from={from={table={alias=src, query={select={1={column={name=emp_id, table_ref=a}}, 2={column={name=acct_sales_count, table_ref=a}}, 3={alias=rn, window_function={over={partition_by={1={column={name=emp_id, table_ref=a}}}, orderby={1={null_order=null, predicand={column={name=last_update, table_ref=a}}, sort_order=desc}}}, function={function_name=row_number, parameters=null}}}}, from={table={alias=a, table=accounts}}, where={condition={left={column={name=acct_sales_count, table_ref=a}}, right={literal=0}, operator=>}}}}}, select={1={column={name=acct_sales_count, table_ref=src}}, 2={column={name=rn, table_ref=src}}, 3={column={name=orphan_marker, table_ref=null}}}}, target_table={table={alias=null, table=employees}}, columns={1={column={name=score, table_ref=null}}, 2={column={name=rank_bucket, table_ref=null}}, 3={column={name=orphan_sink, table_ref=null}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[orphan_sink, score, rank_bucket]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{accounts={acct_sales_count=[[@27,133:133='a',<393>,3:24], [@54,282:282='a',<393>,6:14]], last_update=[[@43,217:217='a',<393>,4:64]], emp_id=[[@23,123:123='a',<393>,3:14], [@38,199:199='a',<393>,4:46]]}, employees={orphan_sink=[[@8,44:54='orphan_sink',<393>,1:44]], score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@6,31:41='rank_bucket',<393>,1:31]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={rn=[[@49,240:241='rn',<393>,4:87], [@15,87:89='src',<393>,2:30]], acct_sales_count=[[@29,135:150='acct_sales_count',<393>,3:26], [@11,65:67='src',<393>,2:8]], emp_id=[[@25,125:130='emp_id',<393>,3:16]]}, query1={orphan_marker=[[@19,95:107='orphan_marker',<393>,2:38]], rn=[[@17,91:92='rn',<393>,2:34]], acct_sales_count=[[@13,69:84='acct_sales_count',<393>,2:12]]}, insert2={orphan_sink=[[@8,44:54='orphan_sink',<393>,1:44]], score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@6,31:41='rank_bucket',<393>,1:31]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_insert2={query_dictionary={orphan_sink=[[@8,44:54='orphan_sink',<393>,1:44]], score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@6,31:41='rank_bucket',<393>,1:31]]}, table_dictionary={employees={orphan_sink=[[@8,44:54='orphan_sink',<393>,1:44]], score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@6,31:41='rank_bucket',<393>,1:31]]}}, def_query1={query_dictionary={acct_sales_count=[[@13,69:84='acct_sales_count',<393>,2:12]], orphan_marker=[[@19,95:107='orphan_marker',<393>,2:38]], rn=[[@17,91:92='rn',<393>,2:34]]}, def_query0={window_ordered_by=[{name=last_update, table_ref=a}], query_dictionary={acct_sales_count=[[@29,135:150='acct_sales_count',<393>,3:26], [@11,65:67='src',<393>,2:8]], rn=[[@49,240:241='rn',<393>,4:87], [@15,87:89='src',<393>,2:30]], emp_id=[[@25,125:130='emp_id',<393>,3:16]]}, table_dictionary={accounts={acct_sales_count=[[@27,133:133='a',<393>,3:24], [@54,282:282='a',<393>,6:14]], last_update=[[@43,217:217='a',<393>,4:64]], emp_id=[[@23,123:123='a',<393>,3:14], [@38,199:199='a',<393>,4:46]]}}, window_partition_by=[{name=emp_id, table_ref=a}], filters=[{name=acct_sales_count, table_ref=a}], interface={acct_sales_count=[{name=acct_sales_count, table_ref=a}], rn=[{name=emp_id, table_ref=a}, {name=last_update, table_ref=a}], emp_id=[{name=emp_id, table_ref=a}]}, table_alias={a=accounts}}, interface={acct_sales_count=[{name=acct_sales_count, table_ref=src}], orphan_marker=[{name=orphan_marker, table_ref=null}], rn=[{name=rn, table_ref=src}]}, table_alias={src=query0}}, interface={score=[{name=acct_sales_count, table_ref=query1}], rank_bucket=[{name=rn, table_ref=query1}], orphan_sink=[{name=orphan_marker, table_ref=query1}]}}}",
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
		Snippet snippet = extractor.getSnippet();
		assertFatalDiagnosticCount(
				snippet,
				"UNQUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIASES",
				null,
				"orphan_marker",
				1);
		assertDiagnosticCountBySeverity(
				snippet,
				"UNRESOLVED_UNQUALIFIED_COLUMNS",
				ParseDiagnostic.Severity.ERROR,
				null,
				"orphan_marker",
				1);

		Assert.assertEquals("AST is wrong", "{SQL={insert={preamble=insert_into, from={from={table={alias=src, query={select={1={column={name=emp_id, table_ref=null}}, 2={column={name=dept_id, table_ref=null}}, 3={column={name=new_quota, table_ref=null}}}, from={table={alias=null, table=quota_feed}}, where={and={1={condition={left={column={name=active_flag, table_ref=null}}, right={literal=1}, operator==}}, 2={condition={left={column={name=new_quota, table_ref=null}}, right={literal=0}, operator=>}}}}}}}, select={1={column={name=new_quota, table_ref=src}}, 2={column={name=dept_id, table_ref=src}}, 3={column={name=orphan_marker, table_ref=null}}}}, target_table={table={alias=null, table=employees}}, columns={1={column={name=quota, table_ref=null}}, 2={column={name=dept_id, table_ref=null}}, 3={column={name=orphan_sink, table_ref=null}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[orphan_sink, quota, dept_id]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{quota_feed={new_quota=[[@27,134:142='new_quota',<393>,3:31], [@35,203:211='new_quota',<393>,5:34]], active_flag=[[@31,183:193='active_flag',<393>,5:14]], dept_id=[[@25,125:131='dept_id',<393>,3:22]], emp_id=[[@23,117:122='emp_id',<393>,3:14]]}, employees={orphan_sink=[[@8,40:50='orphan_sink',<393>,1:40]], quota=[[@4,24:28='quota',<393>,1:24]], dept_id=[[@6,31:37='dept_id',<393>,1:31]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={dept_id=[[@25,125:131='dept_id',<393>,3:22], [@15,76:78='src',<393>,2:23]], new_quota=[[@27,134:142='new_quota',<393>,3:31], [@11,61:63='src',<393>,2:8]], emp_id=[[@23,117:122='emp_id',<393>,3:14]]}, query1={orphan_marker=[[@19,89:101='orphan_marker',<393>,2:36]], dept_id=[[@17,80:86='dept_id',<393>,2:27]], new_quota=[[@13,65:73='new_quota',<393>,2:12]]}, insert2={orphan_sink=[[@8,40:50='orphan_sink',<393>,1:40]], dept_id=[[@6,31:37='dept_id',<393>,1:31]], quota=[[@4,24:28='quota',<393>,1:24]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_insert2={query_dictionary={orphan_sink=[[@8,40:50='orphan_sink',<393>,1:40]], dept_id=[[@6,31:37='dept_id',<393>,1:31]], quota=[[@4,24:28='quota',<393>,1:24]]}, table_dictionary={employees={orphan_sink=[[@8,40:50='orphan_sink',<393>,1:40]], quota=[[@4,24:28='quota',<393>,1:24]], dept_id=[[@6,31:37='dept_id',<393>,1:31]]}}, def_query1={query_dictionary={new_quota=[[@13,65:73='new_quota',<393>,2:12]], orphan_marker=[[@19,89:101='orphan_marker',<393>,2:36]], dept_id=[[@17,80:86='dept_id',<393>,2:27]]}, def_query0={query_dictionary={new_quota=[[@27,134:142='new_quota',<393>,3:31], [@11,61:63='src',<393>,2:8]], dept_id=[[@25,125:131='dept_id',<393>,3:22], [@15,76:78='src',<393>,2:23]], emp_id=[[@23,117:122='emp_id',<393>,3:14]]}, table_dictionary={quota_feed={new_quota=[[@27,134:142='new_quota',<393>,3:31], [@35,203:211='new_quota',<393>,5:34]], active_flag=[[@31,183:193='active_flag',<393>,5:14]], dept_id=[[@25,125:131='dept_id',<393>,3:22]], emp_id=[[@23,117:122='emp_id',<393>,3:14]]}}, filters=[{name=active_flag, table_ref=quota_feed}, {name=new_quota, table_ref=quota_feed}], interface={new_quota=[{name=new_quota, table_ref=quota_feed}], dept_id=[{name=dept_id, table_ref=quota_feed}], emp_id=[{name=emp_id, table_ref=quota_feed}]}}, interface={new_quota=[{name=new_quota, table_ref=src}], orphan_marker=[{name=orphan_marker, table_ref=null}], dept_id=[{name=dept_id, table_ref=src}]}, table_alias={src=query0}}, interface={quota=[{name=new_quota, table_ref=query1}], dept_id=[{name=dept_id, table_ref=query1}], orphan_sink=[{name=orphan_marker, table_ref=query1}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void insertDictionaryHandlingUnqualifiedFallsBackToTargetTableV3() {
		final String query = " insert into employees (review_flag, orphan_sink)"
				+ "\n select src.score, missing_flag"
				+ "\n from (select emp_id, score from perf_feed) src";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		Snippet snippet = extractor.getSnippet();
		assertFatalDiagnosticCount(
				snippet,
				"UNQUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIASES",
				null,
				"missing_flag",
				1);
		assertDiagnosticCountBySeverity(
				snippet,
				"UNRESOLVED_UNQUALIFIED_COLUMNS",
				ParseDiagnostic.Severity.ERROR,
				null,
				"missing_flag",
				1);

		Assert.assertEquals("AST is wrong", "{SQL={insert={preamble=insert_into, from={from={table={alias=src, query={select={1={column={name=emp_id, table_ref=null}}, 2={column={name=score, table_ref=null}}}, from={table={alias=null, table=perf_feed}}}}}, select={1={column={name=score, table_ref=src}}, 2={column={name=missing_flag, table_ref=null}}}}, target_table={table={alias=null, table=employees}}, columns={1={column={name=review_flag, table_ref=null}}, 2={column={name=orphan_sink, table_ref=null}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[orphan_sink, review_flag]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{perf_feed={score=[[@19,104:108='score',<393>,3:22]], emp_id=[[@17,96:101='emp_id',<393>,3:14]]}, employees={orphan_sink=[[@6,37:47='orphan_sink',<393>,1:37]], review_flag=[[@4,24:34='review_flag',<393>,1:24]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={score=[[@19,104:108='score',<393>,3:22], [@9,58:60='src',<393>,2:8]], emp_id=[[@17,96:101='emp_id',<393>,3:14]]}, query1={score=[[@11,62:66='score',<393>,2:12]], missing_flag=[[@13,69:80='missing_flag',<393>,2:19]]}, insert2={orphan_sink=[[@6,37:47='orphan_sink',<393>,1:37]], review_flag=[[@4,24:34='review_flag',<393>,1:24]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_insert2={query_dictionary={orphan_sink=[[@6,37:47='orphan_sink',<393>,1:37]], review_flag=[[@4,24:34='review_flag',<393>,1:24]]}, table_dictionary={employees={orphan_sink=[[@6,37:47='orphan_sink',<393>,1:37]], review_flag=[[@4,24:34='review_flag',<393>,1:24]]}}, def_query1={query_dictionary={score=[[@11,62:66='score',<393>,2:12]], missing_flag=[[@13,69:80='missing_flag',<393>,2:19]]}, def_query0={query_dictionary={score=[[@19,104:108='score',<393>,3:22], [@9,58:60='src',<393>,2:8]], emp_id=[[@17,96:101='emp_id',<393>,3:14]]}, table_dictionary={perf_feed={score=[[@19,104:108='score',<393>,3:22]], emp_id=[[@17,96:101='emp_id',<393>,3:14]]}}, interface={score=[{name=score, table_ref=perf_feed}], emp_id=[{name=emp_id, table_ref=perf_feed}]}}, interface={score=[{name=score, table_ref=src}], missing_flag=[{name=missing_flag, table_ref=null}]}, table_alias={src=query0}}, interface={review_flag=[{name=score, table_ref=query1}], orphan_sink=[{name=missing_flag, table_ref=query1}]}}}",
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
		Snippet snippet = extractor.getSnippet();
		assertFatalDiagnosticCount(
				snippet,
				"UNQUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIASES",
				null,
				"missing_flag",
				1);
		assertDiagnosticCountBySeverity(
				snippet,
				"UNRESOLVED_UNQUALIFIED_COLUMNS",
				ParseDiagnostic.Severity.ERROR,
				null,
				"missing_flag",
				1);

		Assert.assertEquals("AST is wrong", "{SQL={insert={preamble=insert_into, from={from={table={alias=src, query={select={1={column={name=emp_id, table_ref=p}}, 2={column={name=score, table_ref=p}}}, from={join={1={table={alias=p, table=perf_feed}}, 2={join=join, on={condition={left={column={name=emp_id, table_ref=p}}, right={column={name=emp_id, table_ref=af}}, operator==}}}, 3={table={alias=af, table=audit_flags}}}}, where={condition={left={column={name=missing_flag, table_ref=af}}, right={literal=0}, operator=>}}}}}, select={1={column={name=score, table_ref=src}}, 2={column={name=missing_flag, table_ref=null}}}}, target_table={table={alias=null, table=employees}}, columns={1={column={name=review_flag, table_ref=null}}, 2={column={name=orphan_sink, table_ref=null}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[orphan_sink, review_flag]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{perf_feed={score=[[@21,106:106='p',<393>,3:24]], emp_id=[[@17,96:96='p',<393>,3:14], [@31,172:172='p',<393>,5:32]]}, employees={orphan_sink=[[@6,37:47='orphan_sink',<393>,1:37]], review_flag=[[@4,24:34='review_flag',<393>,1:24]]}, audit_flags={missing_flag=[[@39,207:208='af',<393>,6:14]], emp_id=[[@35,183:184='af',<393>,5:43]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={score=[[@23,108:112='score',<393>,3:26], [@9,58:60='src',<393>,2:8]], emp_id=[[@19,98:103='emp_id',<393>,3:16]]}, query1={score=[[@11,62:66='score',<393>,2:12]], missing_flag=[[@13,69:80='missing_flag',<393>,2:19]]}, insert2={orphan_sink=[[@6,37:47='orphan_sink',<393>,1:37]], review_flag=[[@4,24:34='review_flag',<393>,1:24]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_insert2={query_dictionary={orphan_sink=[[@6,37:47='orphan_sink',<393>,1:37]], review_flag=[[@4,24:34='review_flag',<393>,1:24]]}, table_dictionary={employees={orphan_sink=[[@6,37:47='orphan_sink',<393>,1:37]], review_flag=[[@4,24:34='review_flag',<393>,1:24]]}}, def_query1={query_dictionary={score=[[@11,62:66='score',<393>,2:12]], missing_flag=[[@13,69:80='missing_flag',<393>,2:19]]}, def_query0={query_dictionary={score=[[@23,108:112='score',<393>,3:26], [@9,58:60='src',<393>,2:8]], emp_id=[[@19,98:103='emp_id',<393>,3:16]]}, table_dictionary={perf_feed={score=[[@21,106:106='p',<393>,3:24]], emp_id=[[@17,96:96='p',<393>,3:14], [@31,172:172='p',<393>,5:32]]}, audit_flags={missing_flag=[[@39,207:208='af',<393>,6:14]], emp_id=[[@35,183:184='af',<393>,5:43]]}}, filters=[{name=emp_id, table_ref=p}, {name=emp_id, table_ref=af}, {name=missing_flag, table_ref=af}], interface={score=[{name=score, table_ref=p}], emp_id=[{name=emp_id, table_ref=p}]}, table_alias={p=perf_feed, af=audit_flags}}, interface={score=[{name=score, table_ref=src}], missing_flag=[{name=missing_flag, table_ref=null}]}, table_alias={src=query0}}, interface={review_flag=[{name=score, table_ref=query1}], orphan_sink=[{name=missing_flag, table_ref=query1}]}}}",
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
		Snippet snippet = extractor.getSnippet();
		assertFatalDiagnosticCount(
				snippet,
				"UNQUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIASES",
				null,
				"orphan_marker",
				1);
		assertDiagnosticCountBySeverity(
				snippet,
				"UNRESOLVED_UNQUALIFIED_COLUMNS",
				ParseDiagnostic.Severity.ERROR,
				null,
				"orphan_marker",
				1);

		Assert.assertEquals("AST is wrong", "{SQL={insert={preamble=insert_into, from={from={table={alias=src, query={select={1={column={name=emp_id, table_ref=a}}, 2={function={function_name=sum, qualifier=null, parameters={column={name=score, table_ref=a}}}, alias=total_score}}, having={condition={left={function={function_name=sum, qualifier=null, parameters={column={name=score, table_ref=a}}}}, right={literal=0}, operator=>}}, from={table={alias=a, table=accounts}}, groupby={1={column={name=emp_id, table_ref=a}}}}}}, select={1={column={name=total_score, table_ref=src}}, 2={column={name=orphan_marker, table_ref=null}}}}, target_table={table={alias=null, table=employees}}, columns={1={column={name=agg_score, table_ref=null}}, 2={column={name=stale_flag, table_ref=null}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[stale_flag, agg_score]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{accounts={score=[[@23,114:114='a',<393>,3:28], [@40,207:207='a',<393>,6:18]], emp_id=[[@17,100:100='a',<393>,3:14], [@34,180:180='a',<393>,5:17]]}, employees={stale_flag=[[@6,35:44='stale_flag',<393>,1:35]], agg_score=[[@4,24:32='agg_score',<393>,1:24]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={total_score=[[@28,126:136='total_score',<393>,3:40], [@9,55:57='src',<393>,2:8]], emp_id=[[@19,102:107='emp_id',<393>,3:16]]}, query1={total_score=[[@11,59:69='total_score',<393>,2:12]], orphan_marker=[[@13,72:84='orphan_marker',<393>,2:25]]}, insert2={agg_score=[[@4,24:32='agg_score',<393>,1:24]], stale_flag=[[@6,35:44='stale_flag',<393>,1:35]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_insert2={query_dictionary={agg_score=[[@4,24:32='agg_score',<393>,1:24]], stale_flag=[[@6,35:44='stale_flag',<393>,1:35]]}, table_dictionary={employees={stale_flag=[[@6,35:44='stale_flag',<393>,1:35]], agg_score=[[@4,24:32='agg_score',<393>,1:24]]}}, def_query1={query_dictionary={total_score=[[@11,59:69='total_score',<393>,2:12]], orphan_marker=[[@13,72:84='orphan_marker',<393>,2:25]]}, def_query0={query_dictionary={total_score=[[@28,126:136='total_score',<393>,3:40], [@9,55:57='src',<393>,2:8]], emp_id=[[@19,102:107='emp_id',<393>,3:16]]}, table_dictionary={accounts={score=[[@23,114:114='a',<393>,3:28], [@40,207:207='a',<393>,6:18]], emp_id=[[@17,100:100='a',<393>,3:14], [@34,180:180='a',<393>,5:17]]}}, grouped_by=[{name=emp_id, table_ref=a}], filters=[{name=score, table_ref=a}], interface={total_score=[{name=score, table_ref=a}], emp_id=[{name=emp_id, table_ref=a}]}, table_alias={a=accounts}}, interface={total_score=[{name=total_score, table_ref=src}], orphan_marker=[{name=orphan_marker, table_ref=null}]}, table_alias={src=query0}}, interface={agg_score=[{name=total_score, table_ref=query1}], stale_flag=[{name=orphan_marker, table_ref=query1}]}}}",
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
		Snippet snippet = extractor.getSnippet();
		assertFatalDiagnosticCount(
				snippet,
				"UNQUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIASES",
				null,
				"shadow_col",
				1);
		assertDiagnosticCountBySeverity(
				snippet,
				"UNRESOLVED_UNQUALIFIED_COLUMNS",
				ParseDiagnostic.Severity.ERROR,
				null,
				"shadow_col",
				1);

		Assert.assertEquals("AST is wrong", "{SQL={insert={preamble=insert_into, from={from={table={alias=src, query={select={1={column={name=emp_id, table_ref=a}}, 2={column={name=last_update, table_ref=a}}}, orderby={1={null_order=null, predicand={column={name=last_update, table_ref=a}}, sort_order=desc}}, from={table={alias=a, table=accounts}}}}}, select={1={column={name=last_update, table_ref=src}}, 2={column={name=shadow_col, table_ref=null}}}}, target_table={table={alias=null, table=employees}}, columns={1={column={name=most_recent_update, table_ref=null}}, 2={column={name=unknown_rhs, table_ref=null}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[unknown_rhs, most_recent_update]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{accounts={last_update=[[@21,117:117='a',<393>,3:24], [@29,173:173='a',<393>,5:17]], emp_id=[[@17,107:107='a',<393>,3:14]]}, employees={unknown_rhs=[[@6,44:54='unknown_rhs',<393>,1:44]], most_recent_update=[[@4,24:41='most_recent_update',<393>,1:24]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={last_update=[[@23,119:129='last_update',<393>,3:26], [@9,65:67='src',<393>,2:8]], emp_id=[[@19,109:114='emp_id',<393>,3:16]]}, query1={shadow_col=[[@13,82:91='shadow_col',<393>,2:25]], last_update=[[@11,69:79='last_update',<393>,2:12]]}, insert2={most_recent_update=[[@4,24:41='most_recent_update',<393>,1:24]], unknown_rhs=[[@6,44:54='unknown_rhs',<393>,1:44]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_insert2={query_dictionary={most_recent_update=[[@4,24:41='most_recent_update',<393>,1:24]], unknown_rhs=[[@6,44:54='unknown_rhs',<393>,1:44]]}, table_dictionary={employees={unknown_rhs=[[@6,44:54='unknown_rhs',<393>,1:44]], most_recent_update=[[@4,24:41='most_recent_update',<393>,1:24]]}}, def_query1={query_dictionary={last_update=[[@11,69:79='last_update',<393>,2:12]], shadow_col=[[@13,82:91='shadow_col',<393>,2:25]]}, def_query0={query_dictionary={last_update=[[@23,119:129='last_update',<393>,3:26], [@9,65:67='src',<393>,2:8]], emp_id=[[@19,109:114='emp_id',<393>,3:16]]}, table_dictionary={accounts={last_update=[[@21,117:117='a',<393>,3:24], [@29,173:173='a',<393>,5:17]], emp_id=[[@17,107:107='a',<393>,3:14]]}}, ordered_by=[{name=last_update, table_ref=a}], interface={last_update=[{name=last_update, table_ref=a}], emp_id=[{name=emp_id, table_ref=a}]}, table_alias={a=accounts}}, interface={last_update=[{name=last_update, table_ref=src}], shadow_col=[{name=shadow_col, table_ref=null}]}, table_alias={src=query0}}, interface={most_recent_update=[{name=last_update, table_ref=query1}], unknown_rhs=[{name=shadow_col, table_ref=query1}]}}}",
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
		Snippet snippet = extractor.getSnippet();
		assertFatalDiagnosticCount(
				snippet,
				"UNQUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIASES",
				null,
				"unqualified_note",
				1);
		assertDiagnosticCountBySeverity(
				snippet,
				"UNRESOLVED_UNQUALIFIED_COLUMNS",
				ParseDiagnostic.Severity.ERROR,
				null,
				"unqualified_note",
				1);

		Assert.assertEquals("AST is wrong", "{SQL={insert={preamble=insert_into, from={from={table={alias=src, query={select={1={column={name=emp_id, table_ref=a}}, 2={column={name=score, table_ref=a}}, 3={alias=rn, window_function={over={partition_by={1={column={name=emp_id, table_ref=a}}}, orderby={1={null_order=null, predicand={column={name=last_update, table_ref=a}}, sort_order=desc}}}, function={function_name=row_number, parameters=null}}}}, from={table={alias=a, table=accounts}}, qualify={condition={left={column={name=rn, table_ref=null}}, right={literal=1}, operator==}}}}}, select={1={column={name=score, table_ref=src}}, 2={column={name=unqualified_note, table_ref=null}}}}, target_table={table={alias=null, table=employees}}, columns={1={column={name=top_score, table_ref=null}}, 2={column={name=fallback_note, table_ref=null}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[fallback_note, top_score]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{accounts={score=[[@21,110:110='a',<393>,3:24]], last_update=[[@37,183:183='a',<393>,4:64]], emp_id=[[@17,100:100='a',<393>,3:14], [@32,165:165='a',<393>,4:46]]}, employees={fallback_note=[[@6,35:47='fallback_note',<393>,1:35]], top_score=[[@4,24:32='top_score',<393>,1:24]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={score=[[@23,112:116='score',<393>,3:26], [@9,58:60='src',<393>,2:8]], rn=[[@43,206:207='rn',<393>,4:87], [@48,248:249='rn',<393>,6:14]], emp_id=[[@19,102:107='emp_id',<393>,3:16]]}, query1={score=[[@11,62:66='score',<393>,2:12]], unqualified_note=[[@13,69:84='unqualified_note',<393>,2:19]]}, insert2={fallback_note=[[@6,35:47='fallback_note',<393>,1:35]], top_score=[[@4,24:32='top_score',<393>,1:24]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_insert2={query_dictionary={fallback_note=[[@6,35:47='fallback_note',<393>,1:35]], top_score=[[@4,24:32='top_score',<393>,1:24]]}, table_dictionary={employees={fallback_note=[[@6,35:47='fallback_note',<393>,1:35]], top_score=[[@4,24:32='top_score',<393>,1:24]]}}, def_query1={query_dictionary={score=[[@11,62:66='score',<393>,2:12]], unqualified_note=[[@13,69:84='unqualified_note',<393>,2:19]]}, def_query0={window_ordered_by=[{name=last_update, table_ref=a}], query_dictionary={score=[[@23,112:116='score',<393>,3:26], [@9,58:60='src',<393>,2:8]], rn=[[@43,206:207='rn',<393>,4:87], [@48,248:249='rn',<393>,6:14]], emp_id=[[@19,102:107='emp_id',<393>,3:16]]}, table_dictionary={accounts={score=[[@21,110:110='a',<393>,3:24]], last_update=[[@37,183:183='a',<393>,4:64]], emp_id=[[@17,100:100='a',<393>,3:14], [@32,165:165='a',<393>,4:46]]}}, window_partition_by=[{name=emp_id, table_ref=a}], filters=[{name=rn, table_ref=query0}], interface={score=[{name=score, table_ref=a}], rn=[{name=emp_id, table_ref=a}, {name=last_update, table_ref=a}], emp_id=[{name=emp_id, table_ref=a}]}, table_alias={a=accounts}}, interface={score=[{name=score, table_ref=src}], unqualified_note=[{name=unqualified_note, table_ref=null}]}, table_alias={src=query0}}, interface={top_score=[{name=score, table_ref=query1}], fallback_note=[{name=unqualified_note, table_ref=query1}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void insertDictionaryHandlingSubqueryWithOrphanRhsV8() {
		final String query = " insert into employees (agg_score, stale_flag)"
				+ "\n select src.total_score, orphan_marker"
				+ "\n from (select a.emp_id, sum(a.score) as total_score"
				+ "\n         from accounts a"
				+ "\n        where a.score > 0"
				+ "\n        group by a.emp_id) src";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		Snippet snippet = extractor.getSnippet();
		assertFatalDiagnosticCount(
				snippet,
				"UNQUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIASES",
				null,
				"orphan_marker",
				1);
		assertDiagnosticCountBySeverity(
				snippet,
				"UNRESOLVED_UNQUALIFIED_COLUMNS",
				ParseDiagnostic.Severity.ERROR,
				null,
				"orphan_marker",
				1);

		Assert.assertEquals("AST is wrong", "{SQL={insert={preamble=insert_into, from={from={table={alias=src, query={select={1={column={name=emp_id, table_ref=a}}, 2={function={function_name=sum, qualifier=null, parameters={column={name=score, table_ref=a}}}, alias=total_score}}, from={table={alias=a, table=accounts}}, where={condition={left={column={name=score, table_ref=a}}, right={literal=0}, operator=>}}, groupby={1={column={name=emp_id, table_ref=a}}}}}}, select={1={column={name=total_score, table_ref=src}}, 2={column={name=orphan_marker, table_ref=null}}}}, target_table={table={alias=null, table=employees}}, columns={1={column={name=agg_score, table_ref=null}}, 2={column={name=stale_flag, table_ref=null}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[stale_flag, agg_score]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{accounts={score=[[@23,114:114='a',<393>,3:28], [@33,177:177='a',<393>,5:14]], emp_id=[[@17,100:100='a',<393>,3:14], [@40,206:206='a',<393>,6:17]]}, employees={stale_flag=[[@6,35:44='stale_flag',<393>,1:35]], agg_score=[[@4,24:32='agg_score',<393>,1:24]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={total_score=[[@28,126:136='total_score',<393>,3:40], [@9,55:57='src',<393>,2:8]], emp_id=[[@19,102:107='emp_id',<393>,3:16]]}, query1={total_score=[[@11,59:69='total_score',<393>,2:12]], orphan_marker=[[@13,72:84='orphan_marker',<393>,2:25]]}, insert2={agg_score=[[@4,24:32='agg_score',<393>,1:24]], stale_flag=[[@6,35:44='stale_flag',<393>,1:35]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_insert2={query_dictionary={agg_score=[[@4,24:32='agg_score',<393>,1:24]], stale_flag=[[@6,35:44='stale_flag',<393>,1:35]]}, table_dictionary={employees={stale_flag=[[@6,35:44='stale_flag',<393>,1:35]], agg_score=[[@4,24:32='agg_score',<393>,1:24]]}}, def_query1={query_dictionary={total_score=[[@11,59:69='total_score',<393>,2:12]], orphan_marker=[[@13,72:84='orphan_marker',<393>,2:25]]}, def_query0={query_dictionary={total_score=[[@28,126:136='total_score',<393>,3:40], [@9,55:57='src',<393>,2:8]], emp_id=[[@19,102:107='emp_id',<393>,3:16]]}, table_dictionary={accounts={score=[[@23,114:114='a',<393>,3:28], [@33,177:177='a',<393>,5:14]], emp_id=[[@17,100:100='a',<393>,3:14], [@40,206:206='a',<393>,6:17]]}}, grouped_by=[{name=emp_id, table_ref=a}], filters=[{name=score, table_ref=a}], interface={total_score=[{name=score, table_ref=a}], emp_id=[{name=emp_id, table_ref=a}]}, table_alias={a=accounts}}, interface={total_score=[{name=total_score, table_ref=src}], orphan_marker=[{name=orphan_marker, table_ref=null}]}, table_alias={src=query0}}, interface={agg_score=[{name=total_score, table_ref=query1}], stale_flag=[{name=orphan_marker, table_ref=query1}]}}}",
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
		Snippet snippet = extractor.getSnippet();
		assertFatalDiagnosticCount(
				snippet,
				"UNQUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIASES",
				null,
				"orphan_marker",
				1);
		assertDiagnosticCountBySeverity(
				snippet,
				"UNRESOLVED_UNQUALIFIED_COLUMNS",
				ParseDiagnostic.Severity.ERROR,
				null,
				"orphan_marker",
				1);

		Assert.assertEquals("AST is wrong", "{SQL={insert={preamble=insert_into, from={from={table={alias=src, query={select={1={column={name=emp_id, table_ref=a}}, 2={column={name=score, table_ref=a}, alias=total_score}}, from={join={1={table={alias=a, table=accounts}}, 2={join=join, on={condition={left={column={name=dept_id, table_ref=a}}, right={column={name=dept_id, table_ref=d}}, operator==}}}, 3={table={alias=d, table=departments}}}}}}}, select={1={column={name=total_score, table_ref=src}}, 2={column={name=orphan_marker, table_ref=null}}}}, target_table={table={alias=null, table=employees}}, columns={1={column={name=agg_score, table_ref=null}}, 2={column={name=stale_flag, table_ref=null}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[stale_flag, agg_score]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{accounts={score=[[@21,110:110='a',<393>,3:24]], dept_id=[[@33,189:189='a',<393>,5:31]], emp_id=[[@17,100:100='a',<393>,3:14]]}, departments={dept_id=[[@37,201:201='d',<393>,5:43]]}, employees={stale_flag=[[@6,35:44='stale_flag',<393>,1:35]], agg_score=[[@4,24:32='agg_score',<393>,1:24]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={total_score=[[@25,121:131='total_score',<393>,3:35], [@9,55:57='src',<393>,2:8]], emp_id=[[@19,102:107='emp_id',<393>,3:16]]}, query1={total_score=[[@11,59:69='total_score',<393>,2:12]], orphan_marker=[[@13,72:84='orphan_marker',<393>,2:25]]}, insert2={agg_score=[[@4,24:32='agg_score',<393>,1:24]], stale_flag=[[@6,35:44='stale_flag',<393>,1:35]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_insert2={query_dictionary={agg_score=[[@4,24:32='agg_score',<393>,1:24]], stale_flag=[[@6,35:44='stale_flag',<393>,1:35]]}, table_dictionary={employees={stale_flag=[[@6,35:44='stale_flag',<393>,1:35]], agg_score=[[@4,24:32='agg_score',<393>,1:24]]}}, def_query1={query_dictionary={total_score=[[@11,59:69='total_score',<393>,2:12]], orphan_marker=[[@13,72:84='orphan_marker',<393>,2:25]]}, def_query0={query_dictionary={total_score=[[@25,121:131='total_score',<393>,3:35], [@9,55:57='src',<393>,2:8]], emp_id=[[@19,102:107='emp_id',<393>,3:16]]}, table_dictionary={accounts={score=[[@21,110:110='a',<393>,3:24]], dept_id=[[@33,189:189='a',<393>,5:31]], emp_id=[[@17,100:100='a',<393>,3:14]]}, departments={dept_id=[[@37,201:201='d',<393>,5:43]]}}, filters=[{name=dept_id, table_ref=a}, {name=dept_id, table_ref=d}], interface={total_score=[{name=score, table_ref=a}], emp_id=[{name=emp_id, table_ref=a}]}, table_alias={a=accounts, d=departments}}, interface={total_score=[{name=total_score, table_ref=src}], orphan_marker=[{name=orphan_marker, table_ref=null}]}, table_alias={src=query0}}, interface={agg_score=[{name=total_score, table_ref=query1}], stale_flag=[{name=orphan_marker, table_ref=query1}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void insertDictionaryHandlingQualifySubqueryWithOrphanRhsV10() {
		final String query = " insert into employees (top_score, stale_flag)"
				+ "\n select src.score, orphan_marker"
				+ "\n from (select a.emp_id, a.score,"
				+ "\n              row_number() over (partition by a.emp_id order by a.score desc) as rn"
				+ "\n         from accounts a"
				+ "\n        qualify rn <= 10) src";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		Snippet snippet = extractor.getSnippet();
		assertFatalDiagnosticCount(
				snippet,
				"UNQUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIASES",
				null,
				"orphan_marker",
				1);
		assertDiagnosticCountBySeverity(
				snippet,
				"UNRESOLVED_UNQUALIFIED_COLUMNS",
				ParseDiagnostic.Severity.ERROR,
				null,
				"orphan_marker",
				1);

		Assert.assertEquals("AST is wrong", "{SQL={insert={preamble=insert_into, from={from={table={alias=src, query={select={1={column={name=emp_id, table_ref=a}}, 2={column={name=score, table_ref=a}}, 3={alias=rn, window_function={over={partition_by={1={column={name=emp_id, table_ref=a}}}, orderby={1={null_order=null, predicand={column={name=score, table_ref=a}}, sort_order=desc}}}, function={function_name=row_number, parameters=null}}}}, from={table={alias=a, table=accounts}}, qualify={condition={left={column={name=rn, table_ref=null}}, right={literal=10}, operator=<=}}}}}, select={1={column={name=score, table_ref=src}}, 2={column={name=orphan_marker, table_ref=null}}}}, target_table={table={alias=null, table=employees}}, columns={1={column={name=top_score, table_ref=null}}, 2={column={name=stale_flag, table_ref=null}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[stale_flag, top_score]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{accounts={score=[[@21,104:104='a',<393>,3:24], [@37,177:177='a',<393>,4:64]], emp_id=[[@17,94:94='a',<393>,3:14], [@32,159:159='a',<393>,4:46]]}, employees={stale_flag=[[@6,35:44='stale_flag',<393>,1:35]], top_score=[[@4,24:32='top_score',<393>,1:24]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={score=[[@23,106:110='score',<393>,3:26], [@9,55:57='src',<393>,2:8]], rn=[[@43,194:195='rn',<393>,4:81], [@48,238:239='rn',<393>,6:16]], emp_id=[[@19,96:101='emp_id',<393>,3:16]]}, query1={score=[[@11,59:63='score',<393>,2:12]], orphan_marker=[[@13,66:78='orphan_marker',<393>,2:19]]}, insert2={stale_flag=[[@6,35:44='stale_flag',<393>,1:35]], top_score=[[@4,24:32='top_score',<393>,1:24]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_insert2={query_dictionary={stale_flag=[[@6,35:44='stale_flag',<393>,1:35]], top_score=[[@4,24:32='top_score',<393>,1:24]]}, table_dictionary={employees={stale_flag=[[@6,35:44='stale_flag',<393>,1:35]], top_score=[[@4,24:32='top_score',<393>,1:24]]}}, def_query1={query_dictionary={score=[[@11,59:63='score',<393>,2:12]], orphan_marker=[[@13,66:78='orphan_marker',<393>,2:19]]}, def_query0={window_ordered_by=[{name=score, table_ref=a}], query_dictionary={score=[[@23,106:110='score',<393>,3:26], [@9,55:57='src',<393>,2:8]], rn=[[@43,194:195='rn',<393>,4:81], [@48,238:239='rn',<393>,6:16]], emp_id=[[@19,96:101='emp_id',<393>,3:16]]}, table_dictionary={accounts={score=[[@21,104:104='a',<393>,3:24], [@37,177:177='a',<393>,4:64]], emp_id=[[@17,94:94='a',<393>,3:14], [@32,159:159='a',<393>,4:46]]}}, window_partition_by=[{name=emp_id, table_ref=a}], filters=[{name=rn, table_ref=query0}], interface={score=[{name=score, table_ref=a}], rn=[{name=emp_id, table_ref=a}, {name=score, table_ref=a}], emp_id=[{name=emp_id, table_ref=a}]}, table_alias={a=accounts}}, interface={score=[{name=score, table_ref=src}], orphan_marker=[{name=orphan_marker, table_ref=null}]}, table_alias={src=query0}}, interface={top_score=[{name=score, table_ref=query1}], stale_flag=[{name=orphan_marker, table_ref=query1}]}}}",
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
		Snippet snippet = extractor.getSnippet();
		assertFatalDiagnosticCount(
				snippet,
				"UNQUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIASES",
				null,
				"orphan_marker",
				1);
		assertDiagnosticCountBySeverity(
				snippet,
				"UNRESOLVED_UNQUALIFIED_COLUMNS",
				ParseDiagnostic.Severity.ERROR,
				null,
				"orphan_marker",
				1);

		Assert.assertEquals("AST is wrong", "{SQL={insert={preamble=insert_into, from={from={table={alias=src, query={select={1={column={name=emp_id, table_ref=a}}, 2={column={name=score, table_ref=a}}}, orderby={1={null_order=null, predicand={column={name=score, table_ref=a}}, sort_order=desc}}, from={table={alias=a, table=accounts}}}}}, select={1={column={name=score, table_ref=src}}, 2={column={name=orphan_marker, table_ref=null}}}}, target_table={table={alias=null, table=employees}}, columns={1={column={name=most_recent_score, table_ref=null}}, 2={column={name=stale_flag, table_ref=null}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[stale_flag, most_recent_score]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{accounts={score=[[@21,112:112='a',<393>,3:24], [@29,162:162='a',<393>,5:17]], emp_id=[[@17,102:102='a',<393>,3:14]]}, employees={stale_flag=[[@6,43:52='stale_flag',<393>,1:43]], most_recent_score=[[@4,24:40='most_recent_score',<393>,1:24]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={score=[[@23,114:118='score',<393>,3:26], [@9,63:65='src',<393>,2:8]], emp_id=[[@19,104:109='emp_id',<393>,3:16]]}, query1={score=[[@11,67:71='score',<393>,2:12]], orphan_marker=[[@13,74:86='orphan_marker',<393>,2:19]]}, insert2={most_recent_score=[[@4,24:40='most_recent_score',<393>,1:24]], stale_flag=[[@6,43:52='stale_flag',<393>,1:43]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_insert2={query_dictionary={most_recent_score=[[@4,24:40='most_recent_score',<393>,1:24]], stale_flag=[[@6,43:52='stale_flag',<393>,1:43]]}, table_dictionary={employees={stale_flag=[[@6,43:52='stale_flag',<393>,1:43]], most_recent_score=[[@4,24:40='most_recent_score',<393>,1:24]]}}, def_query1={query_dictionary={score=[[@11,67:71='score',<393>,2:12]], orphan_marker=[[@13,74:86='orphan_marker',<393>,2:19]]}, def_query0={query_dictionary={score=[[@23,114:118='score',<393>,3:26], [@9,63:65='src',<393>,2:8]], emp_id=[[@19,104:109='emp_id',<393>,3:16]]}, table_dictionary={accounts={score=[[@21,112:112='a',<393>,3:24], [@29,162:162='a',<393>,5:17]], emp_id=[[@17,102:102='a',<393>,3:14]]}}, ordered_by=[{name=score, table_ref=a}], interface={score=[{name=score, table_ref=a}], emp_id=[{name=emp_id, table_ref=a}]}, table_alias={a=accounts}}, interface={score=[{name=score, table_ref=src}], orphan_marker=[{name=orphan_marker, table_ref=null}]}, table_alias={src=query0}}, interface={most_recent_score=[{name=score, table_ref=query1}], stale_flag=[{name=orphan_marker, table_ref=query1}]}}}",
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
		Snippet snippet = extractor.getSnippet();
		assertFatalDiagnosticCount(
				snippet,
				"UNQUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIASES",
				null,
				"orphan_marker",
				1);
		assertDiagnosticCountBySeverity(
				snippet,
				"UNRESOLVED_UNQUALIFIED_COLUMNS",
				ParseDiagnostic.Severity.ERROR,
				null,
				"orphan_marker",
				1);

		Assert.assertEquals("AST is wrong", "{SQL={insert={preamble=insert_into, from={from={table={alias=src, query={select={1={column={name=emp_id, table_ref=a}}, 2={column={name=score, table_ref=a}}}, from={table={alias=a, table=accounts}}}}}, select={1={column={name=score, table_ref=src}}, 2={column={name=orphan_marker, table_ref=null}}}}, target_table={table={alias=null, table=employees}}, columns={1={column={name=latest_score, table_ref=null}}, 2={column={name=stale_flag, table_ref=null}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[latest_score, stale_flag]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{accounts={score=[[@21,107:107='a',<393>,3:24]], emp_id=[[@17,97:97='a',<393>,3:14]]}, employees={latest_score=[[@4,24:35='latest_score',<393>,1:24]], stale_flag=[[@6,38:47='stale_flag',<393>,1:38]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={score=[[@23,109:113='score',<393>,3:26], [@9,58:60='src',<393>,2:8]], emp_id=[[@19,99:104='emp_id',<393>,3:16]]}, query1={score=[[@11,62:66='score',<393>,2:12]], orphan_marker=[[@13,69:81='orphan_marker',<393>,2:19]]}, insert2={latest_score=[[@4,24:35='latest_score',<393>,1:24]], stale_flag=[[@6,38:47='stale_flag',<393>,1:38]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_insert2={query_dictionary={latest_score=[[@4,24:35='latest_score',<393>,1:24]], stale_flag=[[@6,38:47='stale_flag',<393>,1:38]]}, table_dictionary={employees={latest_score=[[@4,24:35='latest_score',<393>,1:24]], stale_flag=[[@6,38:47='stale_flag',<393>,1:38]]}}, def_query1={query_dictionary={score=[[@11,62:66='score',<393>,2:12]], orphan_marker=[[@13,69:81='orphan_marker',<393>,2:19]]}, def_query0={query_dictionary={score=[[@23,109:113='score',<393>,3:26], [@9,58:60='src',<393>,2:8]], emp_id=[[@19,99:104='emp_id',<393>,3:16]]}, table_dictionary={accounts={score=[[@21,107:107='a',<393>,3:24]], emp_id=[[@17,97:97='a',<393>,3:14]]}}, interface={score=[{name=score, table_ref=a}], emp_id=[{name=emp_id, table_ref=a}]}, table_alias={a=accounts}}, interface={score=[{name=score, table_ref=src}], orphan_marker=[{name=orphan_marker, table_ref=null}]}, table_alias={src=query0}}, interface={latest_score=[{name=score, table_ref=query1}], stale_flag=[{name=orphan_marker, table_ref=query1}]}}}",
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
		Snippet snippet = extractor.getSnippet();
		assertNoFatalErrors(extractor);
		
		Assert.assertEquals("AST is wrong", "{SQL={update={table={alias=e, table=employees}, from={table={alias=a, table=accounts}}, assignments={1={set={column={name=emp_sales_count, table_ref=e}}, to={calc={left={column={name=acct_sales_count, table_ref=a}}, right={literal=1}, operator=+}}}, 2={set={column={name=redder, table_ref=e}}, to={column={name=greener, table_ref=null}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[emp_sales_count, redder]", 
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", 
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{accounts={acct_sales_count=[[@8,43:43='a',<393>,1:43]], greener=[[@18,78:84='greener',<393>,1:78]]}, employees={emp_sales_count=[[@4,23:23='e',<393>,1:23]], redder=[[@14,67:67='e',<393>,1:67]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{update0={emp_sales_count=[[@6,25:39='emp_sales_count',<393>,1:25]], redder=[[@16,69:74='redder',<393>,1:69]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_update0={assignments={emp_sales_count=[{name=acct_sales_count, table_ref=a}], redder=[{name=greener, table_ref=accounts}]}, table_dictionary={accounts={acct_sales_count=[[@8,43:43='a',<393>,1:43]], greener=[[@18,78:84='greener',<393>,1:78]]}, employees={emp_sales_count=[[@4,23:23='e',<393>,1:23]], redder=[[@14,67:67='e',<393>,1:67]]}}, update_dictionary={emp_sales_count=[[@6,25:39='emp_sales_count',<393>,1:25]], redder=[[@16,69:74='redder',<393>,1:69]]}, table_alias={a=accounts, e=employees}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{that_table={column1=[[@5,32:38='column1',<393>,1:32]], column3=[[@13,70:76='column3',<393>,1:70]], column2=[[@9,51:57='column2',<393>,1:51]], key=[[@21,116:125='that_table',<393>,1:116]]}, this_table={outputC=[[@11,60:66='outputC',<393>,1:60]], outputA=[[@3,22:28='outputA',<393>,1:22]], outputB=[[@7,41:47='outputB',<393>,1:41]], key=[[@17,101:110='this_table',<393>,1:101]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{update0={outputC=[[@11,60:66='outputC',<393>,1:60]], outputA=[[@3,22:28='outputA',<393>,1:22]], outputB=[[@7,41:47='outputB',<393>,1:41]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_update0={assignments={outputC=[{name=column3, table_ref=that_table}], outputA=[{name=column1, table_ref=that_table}], outputB=[{name=column2, table_ref=that_table}]}, table_dictionary={that_table={column1=[[@5,32:38='column1',<393>,1:32]], column3=[[@13,70:76='column3',<393>,1:70]], column2=[[@9,51:57='column2',<393>,1:51]], key=[[@21,116:125='that_table',<393>,1:116]]}, this_table={outputC=[[@11,60:66='outputC',<393>,1:60]], outputA=[[@3,22:28='outputA',<393>,1:22]], outputB=[[@7,41:47='outputB',<393>,1:41]], key=[[@17,101:110='this_table',<393>,1:101]]}}, update_dictionary={outputC=[[@11,60:66='outputC',<393>,1:60]], outputA=[[@3,22:28='outputA',<393>,1:22]], outputB=[[@7,41:47='outputB',<393>,1:41]]}, filters=[{name=key, table_ref=this_table}, {name=key, table_ref=that_table}]}}",
				extractor.getSymbolTable().toString());
}

// DELETE TESTS

	@Test
	public void deleteUsingSiblingAliasReferenceInFirstUsingSubqueryMustFatalV0a() {
		final String query = " delete from tab1 t"
				+ "\n using (select a.a1 from tabA a where a.a2 = b.a2 and a.extra = t.extra) u, tab2 b"
				+ "\n where t.a1 = b.a1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		Snippet snippet = extractor.getSnippet();

		assertFatalDiagnosticAtPosition(
				snippet,
				"QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE",
				"No alias or table called 'b'",
				"a2",
				2,
				17);

		assertDiagnosticCountBySeverity(
				snippet,
				null,
				ParseDiagnostic.Severity.FATAL,
				"t.extra",
				null,
				0);
		assertDiagnosticCountBySeverity(
				snippet,
				null,
				ParseDiagnostic.Severity.ERROR,
				"t.extra",
				null,
				0);

		Assert.assertEquals("AST is wrong", "{SQL={delete={table={alias=t, table=tab1}, using={1={join={1={table={alias=u, query={select={1={column={name=a1, table_ref=a}}}, from={table={alias=a, table=tabA}}, where={and={1={condition={left={column={name=a2, table_ref=a}}, right={column={name=a2, table_ref=b}}, operator==}}, 2={condition={left={column={name=extra, table_ref=a}}, right={column={name=extra, table_ref=t}}, operator==}}}}}}}, 2={table={alias=b, table=tab2}}}}}, where={condition={left={column={name=a1, table_ref=t}}, right={column={name=a1, table_ref=b}}, operator==}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{taba={a1=[[@7,35:35='a',<393>,2:15]], a2=[[@14,58:58='a',<393>,2:38]], extra=[[@22,74:74='a',<393>,2:54]]}, tab1={a1=[[@35,110:110='t',<393>,3:7]], extra=[[@26,84:84='t',<393>,2:64]]}, tab2={a1=[[@39,117:117='b',<393>,3:14]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={a1=[[@9,37:38='a1',<393>,2:17]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_delete1={query_dictionary={}, table_dictionary={tab1={a1=[[@35,110:110='t',<393>,3:7]]}, tab2={a1=[[@39,117:117='b',<393>,3:14]]}}, def_query0={query_dictionary={a1=[[@9,37:38='a1',<393>,2:17]]}, table_dictionary={taba={a1=[[@7,35:35='a',<393>,2:15]], a2=[[@14,58:58='a',<393>,2:38]], extra=[[@22,74:74='a',<393>,2:54]]}, tab1={extra=[[@26,84:84='t',<393>,2:64]]}}, filters=[{name=a2, table_ref=a}, {name=a2, table_ref=b}, {name=extra, table_ref=a}, {name=extra, table_ref=t}], interface={a1=[{name=a1, table_ref=a}]}, table_alias={a=tabA}}, filters=[{name=a1, table_ref=t}, {name=a1, table_ref=b}], interface=null, table_alias={b=tab2, t=tab1, u=query0}}, table_dictionary={}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void deleteUsingSiblingAliasReferenceInSecondUsingSubqueryMustFatalV0b() {
		final String query = " delete from tab1 t"
				+ "\n using tab2 b, (select a.a1 from tabA a where a.a2 = b.a2 and a.extra = t.extra) u"
				+ "\n where t.a1 = b.a1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		Snippet snippet = extractor.getSnippet();

		assertFatalDiagnosticAtPosition(
				snippet,
				"QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE",
				"No alias or table called 'b'",
				"a2",
				2,
				25);

		assertDiagnosticCountBySeverity(
				snippet,
				null,
				ParseDiagnostic.Severity.FATAL,
				"t.extra",
				null,
				0);
		assertDiagnosticCountBySeverity(
				snippet,
				null,
				ParseDiagnostic.Severity.ERROR,
				"t.extra",
				null,
				0);

		Assert.assertEquals("AST is wrong", "{SQL={delete={table={alias=t, table=tab1}, using={1={join={1={table={alias=b, table=tab2}}, 2={table={alias=u, query={select={1={column={name=a1, table_ref=a}}}, from={table={alias=a, table=tabA}}, where={and={1={condition={left={column={name=a2, table_ref=a}}, right={column={name=a2, table_ref=b}}, operator==}}, 2={condition={left={column={name=extra, table_ref=a}}, right={column={name=extra, table_ref=t}}, operator==}}}}}}}}}}, where={condition={left={column={name=a1, table_ref=t}}, right={column={name=a1, table_ref=b}}, operator==}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{taba={a1=[[@10,43:43='a',<393>,2:23]], a2=[[@17,66:66='a',<393>,2:46]], extra=[[@25,82:82='a',<393>,2:62]]}, tab1={a1=[[@35,110:110='t',<393>,3:7]], extra=[[@29,92:92='t',<393>,2:72]]}, tab2={a1=[[@39,117:117='b',<393>,3:14]], a2=[[@21,73:73='b',<393>,2:53]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={a1=[[@12,45:46='a1',<393>,2:25]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_delete1={query_dictionary={}, table_dictionary={tab1={a1=[[@35,110:110='t',<393>,3:7]]}, tab2={a1=[[@39,117:117='b',<393>,3:14]]}}, def_query0={query_dictionary={a1=[[@12,45:46='a1',<393>,2:25]]}, table_dictionary={taba={a1=[[@10,43:43='a',<393>,2:23]], a2=[[@17,66:66='a',<393>,2:46]], extra=[[@25,82:82='a',<393>,2:62]]}, tab1={extra=[[@29,92:92='t',<393>,2:72]]}, tab2={a2=[[@21,73:73='b',<393>,2:53]]}}, filters=[{name=a2, table_ref=a}, {name=a2, table_ref=b}, {name=extra, table_ref=a}, {name=extra, table_ref=t}], interface={a1=[{name=a1, table_ref=a}]}, table_alias={a=tabA}}, filters=[{name=a1, table_ref=t}, {name=a1, table_ref=b}], interface=null, table_alias={b=tab2, t=tab1, u=query0}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void deleteDictionaryHandlingPostgresUsingWindowedSubqueryAndOrphanRhsV1() {
		final String query = " delete from employees e"
				+ "\n using (select a.emp_id, a.acct_sales_count,"
				+ "\n              row_number() over (partition by a.emp_id order by a.last_update desc) as rn"
				+ "\n         from accounts a"
				+ "\n        where a.acct_sales_count > 0) src"
				+ "\n where e.emp_id = src.emp_id and orphan_marker = 1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={delete={table={alias=e, table=employees}, using={1={table={alias=src, query={select={1={column={name=emp_id, table_ref=a}}, 2={column={name=acct_sales_count, table_ref=a}}, 3={alias=rn, window_function={over={partition_by={1={column={name=emp_id, table_ref=a}}}, orderby={1={null_order=null, predicand={column={name=last_update, table_ref=a}}, sort_order=desc}}}, function={function_name=row_number, parameters=null}}}}, from={table={alias=a, table=accounts}}, where={condition={left={column={name=acct_sales_count, table_ref=a}}, right={literal=0}, operator=>}}}}}}, where={and={1={condition={left={column={name=emp_id, table_ref=e}}, right={column={name=emp_id, table_ref=src}}, operator==}}, 2={condition={left={column={name=orphan_marker, table_ref=null}}, right={literal=1}, operator==}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{accounts={acct_sales_count=[[@11,50:50='a',<393>,2:25], [@38,199:199='a',<393>,5:14]], last_update=[[@27,134:134='a',<393>,3:64]], emp_id=[[@7,40:40='a',<393>,2:15], [@22,116:116='a',<393>,3:46]]}, employees={orphan_marker=[[@54,260:272='orphan_marker',<393>,6:33]], emp_id=[[@46,234:234='e',<393>,6:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={rn=[[@33,157:158='rn',<393>,3:87]], acct_sales_count=[[@13,52:67='acct_sales_count',<393>,2:27]], emp_id=[[@9,42:47='emp_id',<393>,2:17], [@50,245:247='src',<393>,6:18]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_delete1={query_dictionary={}, table_dictionary={employees={orphan_marker=[[@54,260:272='orphan_marker',<393>,6:33]], emp_id=[[@46,234:234='e',<393>,6:7]]}}, def_query0={window_ordered_by=[{name=last_update, table_ref=a}], query_dictionary={acct_sales_count=[[@13,52:67='acct_sales_count',<393>,2:27]], rn=[[@33,157:158='rn',<393>,3:87]], emp_id=[[@9,42:47='emp_id',<393>,2:17], [@50,245:247='src',<393>,6:18]]}, table_dictionary={accounts={acct_sales_count=[[@11,50:50='a',<393>,2:25], [@38,199:199='a',<393>,5:14]], last_update=[[@27,134:134='a',<393>,3:64]], emp_id=[[@7,40:40='a',<393>,2:15], [@22,116:116='a',<393>,3:46]]}}, window_partition_by=[{name=emp_id, table_ref=a}], filters=[{name=acct_sales_count, table_ref=a}], interface={acct_sales_count=[{name=acct_sales_count, table_ref=a}], rn=[{name=emp_id, table_ref=a}, {name=last_update, table_ref=a}], emp_id=[{name=emp_id, table_ref=a}]}, table_alias={a=accounts}}, filters=[{name=emp_id, table_ref=e}, {name=emp_id, table_ref=src}, {name=orphan_marker, table_ref=employees}], interface=null, table_alias={e=employees, src=query0}}}",
				extractor.getSymbolTable().toString());
	}


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
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={a1=[[@46,195:197='aaa',<393>,6:7], [@60,238:240='aaa',<393>,7:11]], a2=[[@64,246:248='aaa',<393>,7:19]], a3=[[@68,254:256='aaa',<393>,7:27]]}, tab2={b2=[[@11,45:47='bbb',<393>,2:23], [@38,172:174='bbb',<393>,5:14]], b3=[[@27,115:117='bbb',<393>,3:62]], b1=[[@7,37:39='bbb',<393>,2:15], [@22,99:101='bbb',<393>,3:46]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={b2=[[@13,49:50='b2',<393>,2:27]], ccc=[[@33,131:133='ccc',<393>,3:78], [@54,215:217='ddd',<393>,6:27]], b1=[[@9,41:42='b1',<393>,2:19], [@50,204:206='ddd',<393>,6:16]]}, delete1={a1=[[@62,242:243='a1',<393>,7:15]], a2=[[@66,250:251='a2',<393>,7:23]], a3=[[@70,258:259='a3',<393>,7:31]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_delete1={query_dictionary={a1=[[@62,242:243='a1',<393>,7:15]], a2=[[@66,250:251='a2',<393>,7:23]], a3=[[@70,258:259='a3',<393>,7:31]]}, table_dictionary={tab1={a1=[[@46,195:197='aaa',<393>,6:7], [@60,238:240='aaa',<393>,7:11]], a2=[[@64,246:248='aaa',<393>,7:19]], a3=[[@68,254:256='aaa',<393>,7:27]]}}, def_query0={window_ordered_by=[{name=b3, table_ref=bbb}], query_dictionary={b2=[[@13,49:50='b2',<393>,2:27]], ccc=[[@33,131:133='ccc',<393>,3:78], [@54,215:217='ddd',<393>,6:27]], b1=[[@9,41:42='b1',<393>,2:19], [@50,204:206='ddd',<393>,6:16]]}, table_dictionary={tab2={b2=[[@11,45:47='bbb',<393>,2:23], [@38,172:174='bbb',<393>,5:14]], b3=[[@27,115:117='bbb',<393>,3:62]], b1=[[@7,37:39='bbb',<393>,2:15], [@22,99:101='bbb',<393>,3:46]]}}, window_partition_by=[{name=b1, table_ref=bbb}], filters=[{name=b2, table_ref=bbb}], interface={b2=[{name=b2, table_ref=bbb}], ccc=[{name=b1, table_ref=bbb}, {name=b3, table_ref=bbb}], b1=[{name=b1, table_ref=bbb}]}, table_alias={bbb=tab2}}, filters=[{name=a1, table_ref=aaa}, {name=b1, table_ref=ddd}, {name=ccc, table_ref=ddd}], interface={a1=[{name=a1, table_ref=aaa}], a2=[{name=a2, table_ref=aaa}], a3=[{name=a3, table_ref=aaa}]}, table_alias={aaa=tab1, ddd=query0}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={a1=[[@17,70:72='aaa',<393>,3:7], [@39,132:134='aaa',<393>,4:11]], a2=[[@25,90:92='aaa',<393>,3:27], [@43,140:142='aaa',<393>,4:19]], a3=[[@47,148:150='aaa',<393>,4:27]]}, tab2={b2=[[@9,41:42='b2',<393>,2:19]], b3=[[@11,45:46='b3',<393>,2:23]], b1=[[@7,37:38='b1',<393>,2:15]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={b2=[[@9,41:42='b2',<393>,2:19], [@29,99:101='bbb',<393>,3:36]], b3=[[@11,45:46='b3',<393>,2:23], [@33,110:112='bbb',<393>,3:47]], b1=[[@7,37:38='b1',<393>,2:15], [@21,79:81='bbb',<393>,3:16]]}, delete1={a1=[[@41,136:137='a1',<393>,4:15]], a2=[[@45,144:145='a2',<393>,4:23]], a3=[[@49,152:153='a3',<393>,4:31]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_delete1={query_dictionary={a1=[[@41,136:137='a1',<393>,4:15]], a2=[[@45,144:145='a2',<393>,4:23]], a3=[[@49,152:153='a3',<393>,4:31]]}, table_dictionary={tab1={a1=[[@17,70:72='aaa',<393>,3:7], [@39,132:134='aaa',<393>,4:11]], a2=[[@25,90:92='aaa',<393>,3:27], [@43,140:142='aaa',<393>,4:19]], a3=[[@47,148:150='aaa',<393>,4:27]]}}, def_query0={query_dictionary={b2=[[@9,41:42='b2',<393>,2:19], [@29,99:101='bbb',<393>,3:36]], b3=[[@11,45:46='b3',<393>,2:23], [@33,110:112='bbb',<393>,3:47]], b1=[[@7,37:38='b1',<393>,2:15], [@21,79:81='bbb',<393>,3:16]]}, table_dictionary={tab2={b2=[[@9,41:42='b2',<393>,2:19]], b3=[[@11,45:46='b3',<393>,2:23]], b1=[[@7,37:38='b1',<393>,2:15]]}}, interface={b2=[{name=b2, table_ref=tab2}], b3=[{name=b3, table_ref=tab2}], b1=[{name=b1, table_ref=tab2}]}}, filters=[{name=a1, table_ref=aaa}, {name=b1, table_ref=bbb}, {name=a2, table_ref=aaa}, {name=b2, table_ref=bbb}, {name=b3, table_ref=bbb}], interface={a1=[{name=a1, table_ref=aaa}], a2=[{name=a2, table_ref=aaa}], a3=[{name=a3, table_ref=aaa}]}, table_alias={aaa=tab1, bbb=query0}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={a1=[[@26,116:118='aaa',<393>,5:7], [@40,158:160='aaa',<393>,6:11]], a2=[[@44,166:168='aaa',<393>,6:19]], a3=[[@48,174:176='aaa',<393>,6:27]]}, tab2={b2=[[@11,45:47='bbb',<393>,2:23]], b3=[[@19,92:94='bbb',<393>,4:17]], b1=[[@7,37:39='bbb',<393>,2:15]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={b2=[[@13,49:50='b2',<393>,2:27], [@34,136:138='ccc',<393>,5:27]], b1=[[@9,41:42='b1',<393>,2:19], [@30,125:127='ccc',<393>,5:16]]}, delete1={a1=[[@42,162:163='a1',<393>,6:15]], a2=[[@46,170:171='a2',<393>,6:23]], a3=[[@50,178:179='a3',<393>,6:31]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_delete1={query_dictionary={a1=[[@42,162:163='a1',<393>,6:15]], a2=[[@46,170:171='a2',<393>,6:23]], a3=[[@50,178:179='a3',<393>,6:31]]}, table_dictionary={tab1={a1=[[@26,116:118='aaa',<393>,5:7], [@40,158:160='aaa',<393>,6:11]], a2=[[@44,166:168='aaa',<393>,6:19]], a3=[[@48,174:176='aaa',<393>,6:27]]}}, def_query0={query_dictionary={b2=[[@13,49:50='b2',<393>,2:27], [@34,136:138='ccc',<393>,5:27]], b1=[[@9,41:42='b1',<393>,2:19], [@30,125:127='ccc',<393>,5:16]]}, table_dictionary={tab2={b2=[[@11,45:47='bbb',<393>,2:23]], b3=[[@19,92:94='bbb',<393>,4:17]], b1=[[@7,37:39='bbb',<393>,2:15]]}}, ordered_by=[{name=b3, table_ref=bbb}], interface={b2=[{name=b2, table_ref=bbb}], b1=[{name=b1, table_ref=bbb}]}, table_alias={bbb=tab2}}, filters=[{name=a1, table_ref=aaa}, {name=b1, table_ref=ccc}, {name=b2, table_ref=ccc}], interface={a1=[{name=a1, table_ref=aaa}], a2=[{name=a2, table_ref=aaa}], a3=[{name=a3, table_ref=aaa}]}, table_alias={aaa=tab1, ccc=query0}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={a1=[[@21,101:103='eee',<393>,4:26], [@39,149:151='aaa',<393>,5:7], [@47,176:178='aaa',<393>,6:11]], a2=[[@51,184:186='aaa',<393>,6:19]], a3=[[@29,121:123='eee',<393>,4:46], [@55,192:194='aaa',<393>,6:27]]}, tab2={b2=[[@11,45:47='bbb',<393>,2:23]], b3=[[@33,130:132='bbb',<393>,4:55]], b1=[[@7,37:39='bbb',<393>,2:15], [@25,110:112='bbb',<393>,4:35]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={b2=[[@13,49:50='b2',<393>,2:27]], b1=[[@9,41:42='b1',<393>,2:19], [@43,158:160='ccc',<393>,5:16]]}, delete1={a1=[[@49,180:181='a1',<393>,6:15]], a2=[[@53,188:189='a2',<393>,6:23]], a3=[[@57,196:197='a3',<393>,6:31]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_delete1={query_dictionary={a1=[[@49,180:181='a1',<393>,6:15]], a2=[[@53,188:189='a2',<393>,6:23]], a3=[[@57,196:197='a3',<393>,6:31]]}, table_dictionary={tab1={a1=[[@39,149:151='aaa',<393>,5:7], [@47,176:178='aaa',<393>,6:11]], a2=[[@51,184:186='aaa',<393>,6:19]], a3=[[@55,192:194='aaa',<393>,6:27]]}}, def_query0={query_dictionary={b2=[[@13,49:50='b2',<393>,2:27]], b1=[[@9,41:42='b1',<393>,2:19], [@43,158:160='ccc',<393>,5:16]]}, table_dictionary={tab1={a1=[[@21,101:103='eee',<393>,4:26], [@39,149:151='aaa',<393>,5:7], [@47,176:178='aaa',<393>,6:11]], a3=[[@29,121:123='eee',<393>,4:46], [@55,192:194='aaa',<393>,6:27]]}, tab2={b2=[[@11,45:47='bbb',<393>,2:23]], b3=[[@33,130:132='bbb',<393>,4:55]], b1=[[@7,37:39='bbb',<393>,2:15], [@25,110:112='bbb',<393>,4:35]]}}, filters=[{name=a1, table_ref=eee}, {name=b1, table_ref=bbb}, {name=a3, table_ref=eee}, {name=b3, table_ref=bbb}], interface={b2=[{name=b2, table_ref=bbb}], b1=[{name=b1, table_ref=bbb}]}, table_alias={bbb=tab2, eee=tab1}}, filters=[{name=a1, table_ref=aaa}, {name=b1, table_ref=ccc}], interface={a1=[{name=a1, table_ref=aaa}], a2=[{name=a2, table_ref=aaa}], a3=[{name=a3, table_ref=aaa}]}, table_alias={aaa=tab1, ccc=query0}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={a1=[[@8,45:47='aaa',<393>,3:7], [@24,92:94='aaa',<393>,4:11]], a2=[[@16,65:67='aaa',<393>,3:27], [@28,100:102='aaa',<393>,4:19]], a3=[[@32,108:110='aaa',<393>,4:27]]}, tab2={b2=[[@20,74:76='bbb',<393>,3:36]], b1=[[@12,54:56='bbb',<393>,3:16]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{delete0={a1=[[@26,96:97='a1',<393>,4:15]], a2=[[@30,104:105='a2',<393>,4:23]], a3=[[@34,112:113='a3',<393>,4:31]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_delete0={query_dictionary={a1=[[@26,96:97='a1',<393>,4:15]], a2=[[@30,104:105='a2',<393>,4:23]], a3=[[@34,112:113='a3',<393>,4:31]]}, table_dictionary={tab1={a1=[[@8,45:47='aaa',<393>,3:7], [@24,92:94='aaa',<393>,4:11]], a2=[[@16,65:67='aaa',<393>,3:27], [@28,100:102='aaa',<393>,4:19]], a3=[[@32,108:110='aaa',<393>,4:27]]}, tab2={b2=[[@20,74:76='bbb',<393>,3:36]], b1=[[@12,54:56='bbb',<393>,3:16]]}}, filters=[{name=a1, table_ref=aaa}, {name=b1, table_ref=bbb}, {name=a2, table_ref=aaa}, {name=b2, table_ref=bbb}], interface={a1=[{name=a1, table_ref=aaa}], a2=[{name=a2, table_ref=aaa}], a3=[{name=a3, table_ref=aaa}]}, table_alias={aaa=tab1, bbb=tab2}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={a1=[[@30,120:122='aaa',<393>,5:7], [@46,167:169='aaa',<393>,6:11]], a2=[[@38,140:142='aaa',<393>,5:27], [@50,175:177='aaa',<393>,6:19]], a3=[[@54,183:185='aaa',<393>,6:27]]}, tab2={b2=[[@11,45:47='bbb',<393>,2:23]], b3=[[@15,53:55='bbb',<393>,2:31], [@22,97:99='bbb',<393>,4:14]], b1=[[@7,37:39='bbb',<393>,2:15]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={b2=[[@13,49:50='b2',<393>,2:27], [@42,149:151='ccc',<393>,5:36]], b3=[[@17,57:58='b3',<393>,2:35]], b1=[[@9,41:42='b1',<393>,2:19], [@34,129:131='ccc',<393>,5:16]]}, delete1={a1=[[@48,171:172='a1',<393>,6:15]], a2=[[@52,179:180='a2',<393>,6:23]], a3=[[@56,187:188='a3',<393>,6:31]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_delete1={query_dictionary={a1=[[@48,171:172='a1',<393>,6:15]], a2=[[@52,179:180='a2',<393>,6:23]], a3=[[@56,187:188='a3',<393>,6:31]]}, table_dictionary={tab1={a1=[[@30,120:122='aaa',<393>,5:7], [@46,167:169='aaa',<393>,6:11]], a2=[[@38,140:142='aaa',<393>,5:27], [@50,175:177='aaa',<393>,6:19]], a3=[[@54,183:185='aaa',<393>,6:27]]}}, def_query0={query_dictionary={b2=[[@13,49:50='b2',<393>,2:27], [@42,149:151='ccc',<393>,5:36]], b3=[[@17,57:58='b3',<393>,2:35]], b1=[[@9,41:42='b1',<393>,2:19], [@34,129:131='ccc',<393>,5:16]]}, table_dictionary={tab2={b2=[[@11,45:47='bbb',<393>,2:23]], b3=[[@15,53:55='bbb',<393>,2:31], [@22,97:99='bbb',<393>,4:14]], b1=[[@7,37:39='bbb',<393>,2:15]]}}, filters=[{name=b3, table_ref=bbb}], interface={b2=[{name=b2, table_ref=bbb}], b3=[{name=b3, table_ref=bbb}], b1=[{name=b1, table_ref=bbb}]}, table_alias={bbb=tab2}}, filters=[{name=a1, table_ref=aaa}, {name=b1, table_ref=ccc}, {name=a2, table_ref=aaa}, {name=b2, table_ref=ccc}], interface={a1=[{name=a1, table_ref=aaa}], a2=[{name=a2, table_ref=aaa}], a3=[{name=a3, table_ref=aaa}]}, table_alias={aaa=tab1, ccc=query0}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={a1=[[@46,195:196='a1',<393>,6:7], [@58,234:235='a1',<393>,7:11]], a2=[[@60,238:239='a2',<393>,7:15]], a3=[[@62,242:243='a3',<393>,7:19]]}, tab2={b2=[[@11,45:47='bbb',<393>,2:23], [@38,172:174='bbb',<393>,5:14]], b3=[[@27,115:117='bbb',<393>,3:62]], b1=[[@7,37:39='bbb',<393>,2:15], [@22,99:101='bbb',<393>,3:46]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={b2=[[@13,49:50='b2',<393>,2:27]], ccc=[[@33,131:133='ccc',<393>,3:78], [@52,211:213='ddd',<393>,6:23]], b1=[[@9,41:42='b1',<393>,2:19], [@48,200:202='ddd',<393>,6:12]]}, delete1={a1=[[@58,234:235='a1',<393>,7:11]], a2=[[@60,238:239='a2',<393>,7:15]], a3=[[@62,242:243='a3',<393>,7:19]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_delete1={query_dictionary={a1=[[@58,234:235='a1',<393>,7:11]], a2=[[@60,238:239='a2',<393>,7:15]], a3=[[@62,242:243='a3',<393>,7:19]]}, table_dictionary={tab1={a1=[[@46,195:196='a1',<393>,6:7], [@58,234:235='a1',<393>,7:11]], a2=[[@60,238:239='a2',<393>,7:15]], a3=[[@62,242:243='a3',<393>,7:19]]}}, def_query0={window_ordered_by=[{name=b3, table_ref=bbb}], query_dictionary={b2=[[@13,49:50='b2',<393>,2:27]], ccc=[[@33,131:133='ccc',<393>,3:78], [@52,211:213='ddd',<393>,6:23]], b1=[[@9,41:42='b1',<393>,2:19], [@48,200:202='ddd',<393>,6:12]]}, table_dictionary={tab2={b2=[[@11,45:47='bbb',<393>,2:23], [@38,172:174='bbb',<393>,5:14]], b3=[[@27,115:117='bbb',<393>,3:62]], b1=[[@7,37:39='bbb',<393>,2:15], [@22,99:101='bbb',<393>,3:46]]}}, window_partition_by=[{name=b1, table_ref=bbb}], filters=[{name=b2, table_ref=bbb}], interface={b2=[{name=b2, table_ref=bbb}], ccc=[{name=b1, table_ref=bbb}, {name=b3, table_ref=bbb}], b1=[{name=b1, table_ref=bbb}]}, table_alias={bbb=tab2}}, filters=[{name=a1, table_ref=tab1}, {name=b1, table_ref=ddd}, {name=ccc, table_ref=ddd}], interface={a1=[{name=a1, table_ref=tab1}], a2=[{name=a2, table_ref=tab1}], a3=[{name=a3, table_ref=tab1}]}, table_alias={aaa=tab1, ddd=query0}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={a1=[[@17,70:71='a1',<393>,3:7], [@35,124:125='a1',<393>,4:11]], a2=[[@23,86:87='a2',<393>,3:23], [@37,128:129='a2',<393>,4:15]], a3=[[@39,132:133='a3',<393>,4:19]]}, tab2={b2=[[@9,41:42='b2',<393>,2:19]], b3=[[@11,45:46='b3',<393>,2:23]], b1=[[@7,37:38='b1',<393>,2:15]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={b2=[[@9,41:42='b2',<393>,2:19], [@25,91:93='bbb',<393>,3:28]], b3=[[@11,45:46='b3',<393>,2:23], [@29,102:104='bbb',<393>,3:39]], b1=[[@7,37:38='b1',<393>,2:15], [@19,75:77='bbb',<393>,3:12]]}, delete1={a1=[[@35,124:125='a1',<393>,4:11]], a2=[[@37,128:129='a2',<393>,4:15]], a3=[[@39,132:133='a3',<393>,4:19]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_delete1={query_dictionary={a1=[[@35,124:125='a1',<393>,4:11]], a2=[[@37,128:129='a2',<393>,4:15]], a3=[[@39,132:133='a3',<393>,4:19]]}, table_dictionary={tab1={a1=[[@17,70:71='a1',<393>,3:7], [@35,124:125='a1',<393>,4:11]], a2=[[@23,86:87='a2',<393>,3:23], [@37,128:129='a2',<393>,4:15]], a3=[[@39,132:133='a3',<393>,4:19]]}}, def_query0={query_dictionary={b2=[[@9,41:42='b2',<393>,2:19], [@25,91:93='bbb',<393>,3:28]], b3=[[@11,45:46='b3',<393>,2:23], [@29,102:104='bbb',<393>,3:39]], b1=[[@7,37:38='b1',<393>,2:15], [@19,75:77='bbb',<393>,3:12]]}, table_dictionary={tab2={b2=[[@9,41:42='b2',<393>,2:19]], b3=[[@11,45:46='b3',<393>,2:23]], b1=[[@7,37:38='b1',<393>,2:15]]}}, interface={b2=[{name=b2, table_ref=tab2}], b3=[{name=b3, table_ref=tab2}], b1=[{name=b1, table_ref=tab2}]}}, filters=[{name=a1, table_ref=tab1}, {name=b1, table_ref=bbb}, {name=a2, table_ref=tab1}, {name=b2, table_ref=bbb}, {name=b3, table_ref=bbb}], interface={a1=[{name=a1, table_ref=tab1}], a2=[{name=a2, table_ref=tab1}], a3=[{name=a3, table_ref=tab1}]}, table_alias={aaa=tab1, bbb=query0}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={a1=[[@26,116:117='a1',<393>,5:7], [@38,154:155='a1',<393>,6:11]], a2=[[@40,158:159='a2',<393>,6:15]], a3=[[@42,162:163='a3',<393>,6:19]]}, tab2={b2=[[@11,45:47='bbb',<393>,2:23]], b3=[[@19,92:94='bbb',<393>,4:17]], b1=[[@7,37:39='bbb',<393>,2:15]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={b2=[[@13,49:50='b2',<393>,2:27], [@32,132:134='ccc',<393>,5:23]], b1=[[@9,41:42='b1',<393>,2:19], [@28,121:123='ccc',<393>,5:12]]}, delete1={a1=[[@38,154:155='a1',<393>,6:11]], a2=[[@40,158:159='a2',<393>,6:15]], a3=[[@42,162:163='a3',<393>,6:19]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_delete1={query_dictionary={a1=[[@38,154:155='a1',<393>,6:11]], a2=[[@40,158:159='a2',<393>,6:15]], a3=[[@42,162:163='a3',<393>,6:19]]}, table_dictionary={tab1={a1=[[@26,116:117='a1',<393>,5:7], [@38,154:155='a1',<393>,6:11]], a2=[[@40,158:159='a2',<393>,6:15]], a3=[[@42,162:163='a3',<393>,6:19]]}}, def_query0={query_dictionary={b2=[[@13,49:50='b2',<393>,2:27], [@32,132:134='ccc',<393>,5:23]], b1=[[@9,41:42='b1',<393>,2:19], [@28,121:123='ccc',<393>,5:12]]}, table_dictionary={tab2={b2=[[@11,45:47='bbb',<393>,2:23]], b3=[[@19,92:94='bbb',<393>,4:17]], b1=[[@7,37:39='bbb',<393>,2:15]]}}, ordered_by=[{name=b3, table_ref=bbb}], interface={b2=[{name=b2, table_ref=bbb}], b1=[{name=b1, table_ref=bbb}]}, table_alias={bbb=tab2}}, filters=[{name=a1, table_ref=tab1}, {name=b1, table_ref=ccc}, {name=b2, table_ref=ccc}], interface={a1=[{name=a1, table_ref=tab1}], a2=[{name=a2, table_ref=tab1}], a3=[{name=a3, table_ref=tab1}]}, table_alias={aaa=tab1, ccc=query0}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={a1=[[@21,101:103='eee',<393>,4:26], [@39,149:150='a1',<393>,5:7], [@45,172:173='a1',<393>,6:11]], a2=[[@47,176:177='a2',<393>,6:15]], a3=[[@29,121:123='eee',<393>,4:46], [@49,180:181='a3',<393>,6:19]]}, tab2={b2=[[@11,45:47='bbb',<393>,2:23]], b3=[[@33,130:132='bbb',<393>,4:55]], b1=[[@7,37:39='bbb',<393>,2:15], [@25,110:112='bbb',<393>,4:35]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={b2=[[@13,49:50='b2',<393>,2:27]], b1=[[@9,41:42='b1',<393>,2:19], [@41,154:156='ccc',<393>,5:12]]}, delete1={a1=[[@45,172:173='a1',<393>,6:11]], a2=[[@47,176:177='a2',<393>,6:15]], a3=[[@49,180:181='a3',<393>,6:19]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_delete1={query_dictionary={a1=[[@45,172:173='a1',<393>,6:11]], a2=[[@47,176:177='a2',<393>,6:15]], a3=[[@49,180:181='a3',<393>,6:19]]}, table_dictionary={tab1={a1=[[@39,149:150='a1',<393>,5:7], [@45,172:173='a1',<393>,6:11]], a2=[[@47,176:177='a2',<393>,6:15]], a3=[[@49,180:181='a3',<393>,6:19]]}}, def_query0={query_dictionary={b2=[[@13,49:50='b2',<393>,2:27]], b1=[[@9,41:42='b1',<393>,2:19], [@41,154:156='ccc',<393>,5:12]]}, table_dictionary={tab1={a1=[[@21,101:103='eee',<393>,4:26], [@39,149:150='a1',<393>,5:7], [@45,172:173='a1',<393>,6:11]], a3=[[@29,121:123='eee',<393>,4:46], [@49,180:181='a3',<393>,6:19]]}, tab2={b2=[[@11,45:47='bbb',<393>,2:23]], b3=[[@33,130:132='bbb',<393>,4:55]], b1=[[@7,37:39='bbb',<393>,2:15], [@25,110:112='bbb',<393>,4:35]]}}, filters=[{name=a1, table_ref=eee}, {name=b1, table_ref=bbb}, {name=a3, table_ref=eee}, {name=b3, table_ref=bbb}], interface={b2=[{name=b2, table_ref=bbb}], b1=[{name=b1, table_ref=bbb}]}, table_alias={bbb=tab2, eee=tab1}}, filters=[{name=a1, table_ref=tab1}, {name=b1, table_ref=ccc}], interface={a1=[{name=a1, table_ref=tab1}], a2=[{name=a2, table_ref=tab1}], a3=[{name=a3, table_ref=tab1}]}, table_alias={aaa=tab1, ccc=query0}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={a1=[[@8,45:46='a1',<393>,3:7], [@20,84:85='a1',<393>,4:11]], a2=[[@14,61:62='a2',<393>,3:23], [@22,88:89='a2',<393>,4:15]], a3=[[@24,92:93='a3',<393>,4:19]]}, tab2={b2=[[@16,66:68='bbb',<393>,3:28]], b1=[[@10,50:52='bbb',<393>,3:12]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{delete0={a1=[[@20,84:85='a1',<393>,4:11]], a2=[[@22,88:89='a2',<393>,4:15]], a3=[[@24,92:93='a3',<393>,4:19]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_delete0={query_dictionary={a1=[[@20,84:85='a1',<393>,4:11]], a2=[[@22,88:89='a2',<393>,4:15]], a3=[[@24,92:93='a3',<393>,4:19]]}, table_dictionary={tab1={a1=[[@8,45:46='a1',<393>,3:7], [@20,84:85='a1',<393>,4:11]], a2=[[@14,61:62='a2',<393>,3:23], [@22,88:89='a2',<393>,4:15]], a3=[[@24,92:93='a3',<393>,4:19]]}, tab2={b2=[[@16,66:68='bbb',<393>,3:28]], b1=[[@10,50:52='bbb',<393>,3:12]]}}, filters=[{name=a1, table_ref=tab1}, {name=b1, table_ref=bbb}, {name=a2, table_ref=tab1}, {name=b2, table_ref=bbb}], interface={a1=[{name=a1, table_ref=tab1}], a2=[{name=a2, table_ref=tab1}], a3=[{name=a3, table_ref=tab1}]}, table_alias={aaa=tab1, bbb=tab2}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={a1=[[@30,120:121='a1',<393>,5:7], [@42,159:160='a1',<393>,6:11]], a2=[[@36,136:137='a2',<393>,5:23], [@44,163:164='a2',<393>,6:15]], a3=[[@46,167:168='a3',<393>,6:19]]}, tab2={b2=[[@11,45:47='bbb',<393>,2:23]], b3=[[@15,53:55='bbb',<393>,2:31], [@22,97:99='bbb',<393>,4:14]], b1=[[@7,37:39='bbb',<393>,2:15]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={b2=[[@13,49:50='b2',<393>,2:27], [@38,141:143='ccc',<393>,5:28]], b3=[[@17,57:58='b3',<393>,2:35]], b1=[[@9,41:42='b1',<393>,2:19], [@32,125:127='ccc',<393>,5:12]]}, delete1={a1=[[@42,159:160='a1',<393>,6:11]], a2=[[@44,163:164='a2',<393>,6:15]], a3=[[@46,167:168='a3',<393>,6:19]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_delete1={query_dictionary={a1=[[@42,159:160='a1',<393>,6:11]], a2=[[@44,163:164='a2',<393>,6:15]], a3=[[@46,167:168='a3',<393>,6:19]]}, table_dictionary={tab1={a1=[[@30,120:121='a1',<393>,5:7], [@42,159:160='a1',<393>,6:11]], a2=[[@36,136:137='a2',<393>,5:23], [@44,163:164='a2',<393>,6:15]], a3=[[@46,167:168='a3',<393>,6:19]]}}, def_query0={query_dictionary={b2=[[@13,49:50='b2',<393>,2:27], [@38,141:143='ccc',<393>,5:28]], b3=[[@17,57:58='b3',<393>,2:35]], b1=[[@9,41:42='b1',<393>,2:19], [@32,125:127='ccc',<393>,5:12]]}, table_dictionary={tab2={b2=[[@11,45:47='bbb',<393>,2:23]], b3=[[@15,53:55='bbb',<393>,2:31], [@22,97:99='bbb',<393>,4:14]], b1=[[@7,37:39='bbb',<393>,2:15]]}}, filters=[{name=b3, table_ref=bbb}], interface={b2=[{name=b2, table_ref=bbb}], b3=[{name=b3, table_ref=bbb}], b1=[{name=b1, table_ref=bbb}]}, table_alias={bbb=tab2}}, filters=[{name=a1, table_ref=tab1}, {name=b1, table_ref=ccc}, {name=a2, table_ref=tab1}, {name=b2, table_ref=ccc}], interface={a1=[{name=a1, table_ref=tab1}], a2=[{name=a2, table_ref=tab1}], a3=[{name=a3, table_ref=tab1}]}, table_alias={aaa=tab1, ccc=query0}}}",
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
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={a1=[[@33,131:132='a1',<393>,5:7], [@53,190:191='a1',<393>,6:11]], a2=[[@39,147:148='a2',<393>,5:23], [@55,194:195='a2',<393>,6:15]], a3=[[@57,198:199='a3',<393>,6:19]]}, tab2={b2=[[@11,45:47='bbb',<393>,2:23]], b3=[[@15,53:55='bbb',<393>,2:31], [@22,97:99='bbb',<393>,4:14]], b1=[[@7,37:39='bbb',<393>,2:15]]}, users={id=[[@45,163:165='uuu',<393>,5:39]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={b2=[[@13,49:50='b2',<393>,2:27], [@41,152:154='ccc',<393>,5:28], [@63,210:212='ccc',<393>,6:31]], b3=[[@17,57:58='b3',<393>,2:35], [@49,172:174='ccc',<393>,5:48], [@67,218:220='ccc',<393>,6:39]], b1=[[@9,41:42='b1',<393>,2:19], [@35,136:138='ccc',<393>,5:12], [@59,202:204='ccc',<393>,6:23]]}, delete1={a1=[[@53,190:191='a1',<393>,6:11]], b2=[[@65,214:215='b2',<393>,6:35]], a2=[[@55,194:195='a2',<393>,6:15]], b3=[[@69,222:223='b3',<393>,6:43]], a3=[[@57,198:199='a3',<393>,6:19]], b1=[[@61,206:207='b1',<393>,6:27]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_delete1={query_dictionary={a1=[[@53,190:191='a1',<393>,6:11]], b2=[[@65,214:215='b2',<393>,6:35]], a2=[[@55,194:195='a2',<393>,6:15]], b3=[[@69,222:223='b3',<393>,6:43]], a3=[[@57,198:199='a3',<393>,6:19]], b1=[[@61,206:207='b1',<393>,6:27]]}, table_dictionary={tab1={a1=[[@33,131:132='a1',<393>,5:7], [@53,190:191='a1',<393>,6:11]], a2=[[@39,147:148='a2',<393>,5:23], [@55,194:195='a2',<393>,6:15]], a3=[[@57,198:199='a3',<393>,6:19]]}, users={id=[[@45,163:165='uuu',<393>,5:39]]}}, def_query0={query_dictionary={b2=[[@13,49:50='b2',<393>,2:27], [@41,152:154='ccc',<393>,5:28], [@63,210:212='ccc',<393>,6:31]], b3=[[@17,57:58='b3',<393>,2:35], [@49,172:174='ccc',<393>,5:48], [@67,218:220='ccc',<393>,6:39]], b1=[[@9,41:42='b1',<393>,2:19], [@35,136:138='ccc',<393>,5:12], [@59,202:204='ccc',<393>,6:23]]}, table_dictionary={tab2={b2=[[@11,45:47='bbb',<393>,2:23]], b3=[[@15,53:55='bbb',<393>,2:31], [@22,97:99='bbb',<393>,4:14]], b1=[[@7,37:39='bbb',<393>,2:15]]}}, filters=[{name=b3, table_ref=bbb}], interface={b2=[{name=b2, table_ref=bbb}], b3=[{name=b3, table_ref=bbb}], b1=[{name=b1, table_ref=bbb}]}, table_alias={bbb=tab2}}, filters=[{name=a1, table_ref=tab1}, {name=b1, table_ref=ccc}, {name=a2, table_ref=tab1}, {name=b2, table_ref=ccc}, {name=id, table_ref=uuu}, {name=b3, table_ref=ccc}], interface={a1=[{name=a1, table_ref=tab1}], b2=[{name=b2, table_ref=ccc}], a2=[{name=a2, table_ref=tab1}], b3=[{name=b3, table_ref=ccc}], a3=[{name=a3, table_ref=tab1}], b1=[{name=b1, table_ref=ccc}]}, table_alias={aaa=tab1, ccc=query0, uuu=users}}}",
				extractor.getSymbolTable().toString());
	}
// COMPLEX DML SUBSTITUTION TESTS (generated — review before commit)

	@Test
	public void insertComplexSubstitutionI1WithCteGroupByHaving() {
		final String query = "WITH staged AS ("
				+ "\n  SELECT a.emp_id, sum(a.<insert select col I1>) AS total_score"
				+ "\n  FROM <[HR Data].[Employee Accounts I1]> a"
				+ "\n  WHERE a.<insert where col I1> > 0"
				+ "\n  GROUP BY a.emp_id, a.<insert group col I1>"
				+ "\n  HAVING sum(a.<insert select col I1>) > 0)"
				+ "\nINSERT INTO employees (agg_score, rank_bucket)"
				+ "\nSELECT total_score, emp_id"
				+ "\nFROM staged s"
				+ "\nWHERE xtotal_score > 0";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		Snippet snippet = extractor.getSnippet();
		assertFatalDiagnosticAtPosition(
				snippet,
				"UNQUALIFIED_COLUMN_NOT_FOUND_IN_QUERY_ALIASES",
				"Unqualified column 'xtotal_score' at (l:10 c:6)",
				"xtotal_score",
				10,
				6);
		assertDiagnosticAtPosition(
				snippet,
				"UNRESOLVED_UNQUALIFIED_COLUMNS",
				ParseDiagnostic.Severity.ERROR,
				null,
				"xtotal_score",
				10,
				6);

		Assert.assertEquals("AST is wrong",
				"{SQL={with={1={cte={select={1={column={name=emp_id, table_ref=a}}, 2={function={function_name=sum, qualifier=null, parameters={column={substitution={name=<insert select col I1>, type=column}, table_ref=a}}}, alias=total_score}}, having={condition={left={function={function_name=sum, qualifier=null, parameters={column={substitution={name=<insert select col I1>, type=column}, table_ref=a}}}}, right={literal=0}, operator=>}}, from={table={alias=a, substitution={name=<[HR Data].[Employee Accounts I1]>, parts={1=[HR Data], 2=[Employee Accounts I1]}, type=tuple}}}, where={condition={left={column={substitution={name=<insert where col I1>, type=column}, table_ref=a}}, right={literal=0}, operator=>}}, groupby={1={column={name=emp_id, table_ref=a}}, 2={column={substitution={name=<insert group col I1>, type=column}, table_ref=a}}}}, alias=staged}}, query={insert={preamble=insert_into, from={from={table={alias=s, table=staged}}, where={condition={left={column={name=xtotal_score, table_ref=null}}, right={literal=0}, operator=>}}, select={1={column={name=total_score, table_ref=null}}, 2={column={name=emp_id, table_ref=null}}}}, target_table={table={alias=null, table=employees}}, columns={1={column={name=agg_score, table_ref=null}}, 2={column={name=rank_bucket, table_ref=null}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[rank_bucket, agg_score]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<insert where col I1>=column, <[HR Data].[Employee Accounts I1]>=tuple, <insert group col I1>=column, <insert select col I1>=column}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{<[HR Data].[Employee Accounts I1]>={<insert where col I1>=[[@21,133:133='a',<393>,4:8]], <insert group col I1>=[[@32,182:182='a',<393>,5:21]], <insert select col I1>=[[@11,40:40='a',<393>,2:23], [@38,219:219='a',<393>,6:13]], emp_id=[[@5,26:26='a',<393>,2:9], [@28,172:172='a',<393>,5:11]]}, employees={rank_bucket=[[@51,284:294='rank_bucket',<393>,7:34]], agg_score=[[@49,273:281='agg_score',<393>,7:23]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={total_score=[[@16,69:79='total_score',<393>,2:52], [@54,304:314='total_score',<393>,8:7]], emp_id=[[@7,28:33='emp_id',<393>,2:11], [@56,317:322='emp_id',<393>,8:20]]}, query1={total_score=[[@54,304:314='total_score',<393>,8:7]], emp_id=[[@56,317:322='emp_id',<393>,8:20]]}, insert2={agg_score=[[@49,273:281='agg_score',<393>,7:23]], rank_bucket=[[@51,284:294='rank_bucket',<393>,7:34]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_insert2={context_list={staged=query0}, query_dictionary={agg_score=[[@49,273:281='agg_score',<393>,7:23]], rank_bucket=[[@51,284:294='rank_bucket',<393>,7:34]]}, table_dictionary={employees={rank_bucket=[[@51,284:294='rank_bucket',<393>,7:34]], agg_score=[[@49,273:281='agg_score',<393>,7:23]]}}, def_query1={context_list={staged=query0, s=query0}, query_dictionary={total_score=[[@54,304:314='total_score',<393>,8:7]], emp_id=[[@56,317:322='emp_id',<393>,8:20]]}, filters=[{name=xtotal_score, table_ref=null}], interface={total_score=[{name=total_score, table_ref=query0}], emp_id=[{name=emp_id, table_ref=query0}]}, table_alias={s=query0, staged=query0}}, def_query0={query_dictionary={total_score=[[@16,69:79='total_score',<393>,2:52], [@54,304:314='total_score',<393>,8:7]], emp_id=[[@7,28:33='emp_id',<393>,2:11], [@56,317:322='emp_id',<393>,8:20]]}, table_dictionary={<[HR Data].[Employee Accounts I1]>={<insert where col I1>=[[@21,133:133='a',<393>,4:8]], <insert group col I1>=[[@32,182:182='a',<393>,5:21]], <insert select col I1>=[[@11,40:40='a',<393>,2:23], [@38,219:219='a',<393>,6:13]], emp_id=[[@5,26:26='a',<393>,2:9], [@28,172:172='a',<393>,5:11]]}}, grouped_by=[{name=emp_id, table_ref=a}, {substitution={name=<insert group col I1>, type=column}, table_ref=a}], filters=[{substitution={name=<insert where col I1>, type=column}, table_ref=a}, {substitution={name=<insert select col I1>, type=column}, table_ref=a}], interface={total_score=[{substitution={name=<insert select col I1>, type=column}, table_ref=a}], emp_id=[{name=emp_id, table_ref=a}]}, table_alias={a=<[HR Data].[Employee Accounts I1]>}}, interface={agg_score=[{name=total_score, table_ref=query1}], rank_bucket=[{name=emp_id, table_ref=query1}]}, table_alias={staged=query0}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void insertComplexSubstitutionI2SubqueryUnionWhereSubstitutions() {
		final String query = "INSERT INTO employees (score, rank_bucket)"
				+ "\nSELECT u.emp_id, u.metric_val"
				+ "\nFROM ("
				+ "\n  SELECT a.emp_id, a.<insert select col I2> AS metric_val"
				+ "\n  FROM <[Sales Data].[Perf Feed I2]> a"
				+ "\n  WHERE a.<insert where col I2> > 0"
				+ "\n  UNION"
				+ "\n  SELECT b.dept_id, b.<insert select col I2b> AS metric_val"
				+ "\n  FROM <[Sales Data].[Quota Feed I2]> b"
				+ "\n  WHERE b.<insert where col I2b> > 0"
				+ "\n) u"
				+ "\nWHERE u.metric_val > 0";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={insert={preamble=insert_into, from={from={table={alias=u, query={union={1={select={1={column={name=emp_id, table_ref=a}}, 2={column={substitution={name=<insert select col I2>, type=column}, table_ref=a}, alias=metric_val}}, from={table={alias=a, substitution={name=<[Sales Data].[Perf Feed I2]>, parts={1=[Sales Data], 2=[Perf Feed I2]}, type=tuple}}}, where={condition={left={column={substitution={name=<insert where col I2>, type=column}, table_ref=a}}, right={literal=0}, operator=>}}}, 2={union={qualifier=null, operator=UNION}}, 3={select={1={column={name=dept_id, table_ref=b}}, 2={column={substitution={name=<insert select col I2b>, type=column}, table_ref=b}, alias=metric_val}}, from={table={alias=b, substitution={name=<[Sales Data].[Quota Feed I2]>, parts={1=[Sales Data], 2=[Quota Feed I2]}, type=tuple}}}, where={condition={left={column={substitution={name=<insert where col I2b>, type=column}, table_ref=b}}, right={literal=0}, operator=>}}}}}}}, where={condition={left={column={name=metric_val, table_ref=u}}, right={literal=0}, operator=>}}, select={1={column={name=emp_id, table_ref=u}}, 2={column={name=metric_val, table_ref=u}}}}, target_table={table={alias=null, table=employees}}, columns={1={column={name=score, table_ref=null}}, 2={column={name=rank_bucket, table_ref=null}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[score, rank_bucket]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<[Sales Data].[Perf Feed I2]>=tuple, <[Sales Data].[Quota Feed I2]>=tuple, <insert where col I2>=column, <insert select col I2b>=column, <insert where col I2b>=column, <insert select col I2>=column}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{<[Sales Data].[Perf Feed I2]>={<insert where col I2>=[[@32,185:185='a',<393>,6:8]], emp_id=[[@19,89:89='a',<393>,4:9]], <insert select col I2>=[[@23,99:99='a',<393>,4:19]]}, <[Sales Data].[Quota Feed I2]>={<insert select col I2b>=[[@43,241:241='b',<393>,8:20]], <insert where col I2b>=[[@52,329:329='b',<393>,10:8]], dept_id=[[@39,230:230='b',<393>,8:9]]}, employees={score=[[@4,23:27='score',<393>,1:23]], rank_bucket=[[@6,30:40='rank_bucket',<393>,1:30]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{union2={metric_val=[[@13,60:60='u',<393>,2:17], [@60,368:368='u',<393>,12:6]], emp_id=[[@9,50:50='u',<393>,2:7]]}, query0={metric_val=[[@27,127:136='metric_val',<393>,4:47]], emp_id=[[@21,91:96='emp_id',<393>,4:11]]}, insert4={score=[[@4,23:27='score',<393>,1:23]], rank_bucket=[[@6,30:40='rank_bucket',<393>,1:30]]}, query1={dept_id=[[@41,232:238='dept_id',<393>,8:11]], metric_val=[[@47,270:279='metric_val',<393>,8:49]]}, query3={metric_val=[[@15,62:71='metric_val',<393>,2:19]], emp_id=[[@11,52:57='emp_id',<393>,2:9]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_insert4={query_dictionary={score=[[@4,23:27='score',<393>,1:23]], rank_bucket=[[@6,30:40='rank_bucket',<393>,1:30]]}, table_dictionary={employees={score=[[@4,23:27='score',<393>,1:23]], rank_bucket=[[@6,30:40='rank_bucket',<393>,1:30]]}}, interface={score=[{name=emp_id, table_ref=query3}], rank_bucket=[{name=metric_val, table_ref=query3}]}, def_query3={def_union2={query_dictionary={metric_val=[[@13,60:60='u',<393>,2:17], [@60,368:368='u',<393>,12:6]], emp_id=[[@9,50:50='u',<393>,2:7]]}, def_query1={query_dictionary={metric_val=[[@47,270:279='metric_val',<393>,8:49]], dept_id=[[@41,232:238='dept_id',<393>,8:11]]}, table_dictionary={<[Sales Data].[Quota Feed I2]>={<insert select col I2b>=[[@43,241:241='b',<393>,8:20]], <insert where col I2b>=[[@52,329:329='b',<393>,10:8]], dept_id=[[@39,230:230='b',<393>,8:9]]}}, setop=UNION, filters=[{substitution={name=<insert where col I2b>, type=column}, table_ref=b}], interface={metric_val=[{substitution={name=<insert select col I2b>, type=column}, table_ref=b}], dept_id=[{name=dept_id, table_ref=b}]}, table_alias={b=<[Sales Data].[Quota Feed I2]>}}, def_query0={query_dictionary={metric_val=[[@27,127:136='metric_val',<393>,4:47]], emp_id=[[@21,91:96='emp_id',<393>,4:11]]}, table_dictionary={<[Sales Data].[Perf Feed I2]>={<insert where col I2>=[[@32,185:185='a',<393>,6:8]], emp_id=[[@19,89:89='a',<393>,4:9]], <insert select col I2>=[[@23,99:99='a',<393>,4:19]]}}, filters=[{substitution={name=<insert where col I2>, type=column}, table_ref=a}], interface={metric_val=[{substitution={name=<insert select col I2>, type=column}, table_ref=a}], emp_id=[{name=emp_id, table_ref=a}]}, table_alias={a=<[Sales Data].[Perf Feed I2]>}}, interface={metric_val=query_column, emp_id=query_column}}, query_dictionary={metric_val=[[@15,62:71='metric_val',<393>,2:19]], emp_id=[[@11,52:57='emp_id',<393>,2:9]]}, filters=[{name=metric_val, table_ref=u}], interface={metric_val=[{name=metric_val, table_ref=u}], emp_id=[{name=emp_id, table_ref=u}]}, table_alias={u=union2}}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void insertComplexSubstitutionI2SubqueryExceptWhereSubstitutions(){
		final String query = "INSERT INTO employees (score, rank_bucket)"
				+ "\nSELECT u.emp_id, u.metric_val"
				+ "\nFROM ("
				+ "\n  SELECT a.emp_id, a.<insert select col I2> AS metric_val"
				+ "\n  FROM <[Sales Data].[Perf Feed I2]> a"
				+ "\n  WHERE a.<insert where col I2> > 0"
				+ "\n  EXCEPT"
				+ "\n  SELECT b.dept_id, b.<insert select col I2b> AS metric_val"
				+ "\n  FROM <[Sales Data].[Quota Feed I2]> b"
				+ "\n  WHERE b.<insert where col I2b> > 0"
				+ "\n) u"
				+ "\nWHERE u.metric_val > 0";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={insert={preamble=insert_into, from={from={table={alias=u, query={union={1={select={1={column={name=emp_id, table_ref=a}}, 2={column={substitution={name=<insert select col I2>, type=column}, table_ref=a}, alias=metric_val}}, from={table={alias=a, substitution={name=<[Sales Data].[Perf Feed I2]>, parts={1=[Sales Data], 2=[Perf Feed I2]}, type=tuple}}}, where={condition={left={column={substitution={name=<insert where col I2>, type=column}, table_ref=a}}, right={literal=0}, operator=>}}}, 2={union={qualifier=null, operator=EXCEPT}}, 3={select={1={column={name=dept_id, table_ref=b}}, 2={column={substitution={name=<insert select col I2b>, type=column}, table_ref=b}, alias=metric_val}}, from={table={alias=b, substitution={name=<[Sales Data].[Quota Feed I2]>, parts={1=[Sales Data], 2=[Quota Feed I2]}, type=tuple}}}, where={condition={left={column={substitution={name=<insert where col I2b>, type=column}, table_ref=b}}, right={literal=0}, operator=>}}}}}}}, where={condition={left={column={name=metric_val, table_ref=u}}, right={literal=0}, operator=>}}, select={1={column={name=emp_id, table_ref=u}}, 2={column={name=metric_val, table_ref=u}}}}, target_table={table={alias=null, table=employees}}, columns={1={column={name=score, table_ref=null}}, 2={column={name=rank_bucket, table_ref=null}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[score, rank_bucket]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<[Sales Data].[Perf Feed I2]>=tuple, <[Sales Data].[Quota Feed I2]>=tuple, <insert where col I2>=column, <insert select col I2b>=column, <insert where col I2b>=column, <insert select col I2>=column}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{<[Sales Data].[Perf Feed I2]>={<insert where col I2>=[[@32,185:185='a',<393>,6:8]], emp_id=[[@19,89:89='a',<393>,4:9]], <insert select col I2>=[[@23,99:99='a',<393>,4:19]]}, <[Sales Data].[Quota Feed I2]>={<insert select col I2b>=[[@43,242:242='b',<393>,8:20]], <insert where col I2b>=[[@52,330:330='b',<393>,10:8]], dept_id=[[@39,231:231='b',<393>,8:9]]}, employees={score=[[@4,23:27='score',<393>,1:23]], rank_bucket=[[@6,30:40='rank_bucket',<393>,1:30]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{union2={metric_val=[[@13,60:60='u',<393>,2:17], [@60,369:369='u',<393>,12:6]], emp_id=[[@9,50:50='u',<393>,2:7]]}, query0={metric_val=[[@27,127:136='metric_val',<393>,4:47]], emp_id=[[@21,91:96='emp_id',<393>,4:11]]}, insert4={score=[[@4,23:27='score',<393>,1:23]], rank_bucket=[[@6,30:40='rank_bucket',<393>,1:30]]}, query1={dept_id=[[@41,233:239='dept_id',<393>,8:11]], metric_val=[[@47,271:280='metric_val',<393>,8:49]]}, query3={metric_val=[[@15,62:71='metric_val',<393>,2:19]], emp_id=[[@11,52:57='emp_id',<393>,2:9]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_insert4={query_dictionary={score=[[@4,23:27='score',<393>,1:23]], rank_bucket=[[@6,30:40='rank_bucket',<393>,1:30]]}, table_dictionary={employees={score=[[@4,23:27='score',<393>,1:23]], rank_bucket=[[@6,30:40='rank_bucket',<393>,1:30]]}}, interface={score=[{name=emp_id, table_ref=query3}], rank_bucket=[{name=metric_val, table_ref=query3}]}, def_query3={def_union2={query_dictionary={metric_val=[[@13,60:60='u',<393>,2:17], [@60,369:369='u',<393>,12:6]], emp_id=[[@9,50:50='u',<393>,2:7]]}, def_query1={query_dictionary={metric_val=[[@47,271:280='metric_val',<393>,8:49]], dept_id=[[@41,233:239='dept_id',<393>,8:11]]}, table_dictionary={<[Sales Data].[Quota Feed I2]>={<insert select col I2b>=[[@43,242:242='b',<393>,8:20]], <insert where col I2b>=[[@52,330:330='b',<393>,10:8]], dept_id=[[@39,231:231='b',<393>,8:9]]}}, setop=EXCEPT, filters=[{substitution={name=<insert where col I2b>, type=column}, table_ref=b}], interface={metric_val=[{substitution={name=<insert select col I2b>, type=column}, table_ref=b}], dept_id=[{name=dept_id, table_ref=b}]}, table_alias={b=<[Sales Data].[Quota Feed I2]>}}, def_query0={query_dictionary={metric_val=[[@27,127:136='metric_val',<393>,4:47]], emp_id=[[@21,91:96='emp_id',<393>,4:11]]}, table_dictionary={<[Sales Data].[Perf Feed I2]>={<insert where col I2>=[[@32,185:185='a',<393>,6:8]], emp_id=[[@19,89:89='a',<393>,4:9]], <insert select col I2>=[[@23,99:99='a',<393>,4:19]]}}, filters=[{substitution={name=<insert where col I2>, type=column}, table_ref=a}], interface={metric_val=[{substitution={name=<insert select col I2>, type=column}, table_ref=a}], emp_id=[{name=emp_id, table_ref=a}]}, table_alias={a=<[Sales Data].[Perf Feed I2]>}}, interface={metric_val=query_column, emp_id=query_column}}, query_dictionary={metric_val=[[@15,62:71='metric_val',<393>,2:19]], emp_id=[[@11,52:57='emp_id',<393>,2:9]]}, filters=[{name=metric_val, table_ref=u}], interface={metric_val=[{name=metric_val, table_ref=u}], emp_id=[{name=emp_id, table_ref=u}]}, table_alias={u=union2}}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void insertComplexSubstitutionI3WithCteIntersectOrderBySubstitution() {
		final String query = "WITH branch_a AS ("
				+ "\n  SELECT a.emp_id, a.<insert select col I3> AS score_val"
				+ "\n  FROM <[Ops Data].[Account Ledger I3]> a"
				+ "\n  WHERE a.<insert where col I3> > 0"
				+ "\n  ORDER BY a.<insert order col I3>"
				+ "\n), branch_b AS ("
				+ "\n  SELECT b.emp_id, b.<insert select col I3> AS score_val"
				+ "\n  FROM <[Ops Data].[Audit Ledger I3]> b"
				+ "\n  WHERE b.<insert where col I3> > 0"
				+ "\n  ORDER BY b.<insert order col I3>"
				+ "\n), base AS ("
				+ "\n  SELECT * FROM branch_a"
				+ "\n  INTERSECT"
				+ "\n  SELECT * FROM branch_b"
				+ "\n)"
				+ "\nINSERT INTO employees (top_score, rank_bucket)"
				+ "\nSELECT b.score_val, b.emp_id"
				+ "\nFROM base b";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={with={1={cte={select={1={column={name=emp_id, table_ref=a}}, 2={column={substitution={name=<insert select col I3>, type=column}, table_ref=a}, alias=score_val}}, orderby={1={null_order=null, predicand={column={substitution={name=<insert order col I3>, type=column}, table_ref=a}}, sort_order=ASC}}, from={table={alias=a, substitution={name=<[Ops Data].[Account Ledger I3]>, parts={1=[Ops Data], 2=[Account Ledger I3]}, type=tuple}}}, where={condition={left={column={substitution={name=<insert where col I3>, type=column}, table_ref=a}}, right={literal=0}, operator=>}}}, alias=branch_a}, 2={cte={select={1={column={name=emp_id, table_ref=b}}, 2={column={substitution={name=<insert select col I3>, type=column}, table_ref=b}, alias=score_val}}, orderby={1={null_order=null, predicand={column={substitution={name=<insert order col I3>, type=column}, table_ref=b}}, sort_order=ASC}}, from={table={alias=b, substitution={name=<[Ops Data].[Audit Ledger I3]>, parts={1=[Ops Data], 2=[Audit Ledger I3]}, type=tuple}}}, where={condition={left={column={substitution={name=<insert where col I3>, type=column}, table_ref=b}}, right={literal=0}, operator=>}}}, alias=branch_b}, 3={cte={intersect={1={select={1={column={name=*, table_ref=*}}}, from={table={alias=null, table=branch_a}}}, 2={intersect={qualifier=null, operator=INTERSECT}}, 3={select={1={column={name=*, table_ref=*}}}, from={table={alias=null, table=branch_b}}}}}, alias=base}}, query={insert={preamble=insert_into, from={from={table={alias=b, table=base}}, select={1={column={name=score_val, table_ref=b}}, 2={column={name=emp_id, table_ref=b}}}}, target_table={table={alias=null, table=employees}}, columns={1={column={name=top_score, table_ref=null}}, 2={column={name=rank_bucket, table_ref=null}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[top_score, rank_bucket]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<[Ops Data].[Account Ledger I3]>=tuple, <insert order col I3>=column, <insert where col I3>=column, <[Ops Data].[Audit Ledger I3]>=tuple, <insert select col I3>=column}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{<[Ops Data].[Account Ledger I3]>={<insert order col I3>=[[@25,165:165='a',<393>,5:11]], <insert where col I3>=[[@18,126:126='a',<393>,4:8]], <insert select col I3>=[[@9,38:38='a',<393>,2:19]], emp_id=[[@5,28:28='a',<393>,2:9]]}, <[Ops Data].[Audit Ledger I3]>={<insert order col I3>=[[@54,350:350='b',<393>,10:11]], <insert where col I3>=[[@47,311:311='b',<393>,9:8]], <insert select col I3>=[[@38,225:225='b',<393>,7:19]], emp_id=[[@34,215:215='b',<393>,7:9]]}, employees={top_score=[[@76,474:482='top_score',<393>,16:23]], rank_bucket=[[@78,485:495='rank_bucket',<393>,16:34]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{intersect4={score_val=[[@81,505:505='b',<393>,17:7]], emp_id=[[@85,518:518='b',<393>,17:20]]}, query5={score_val=[[@83,507:515='score_val',<393>,17:9]], emp_id=[[@87,520:525='emp_id',<393>,17:22]]}, insert6={top_score=[[@76,474:482='top_score',<393>,16:23]], rank_bucket=[[@78,485:495='rank_bucket',<393>,16:34]]}, query0={score_val=[[@13,66:74='score_val',<393>,2:47]], *=[[@63,396:396='*',<291>,12:9]], emp_id=[[@7,30:35='emp_id',<393>,2:11]]}, query1={score_val=[[@42,253:261='score_val',<393>,7:47]], *=[[@63,396:396='*',<291>,12:9]], emp_id=[[@36,217:222='emp_id',<393>,7:11]]}, query2={*=[[@63,396:396='*',<291>,12:9]]}, query3={*=[[@68,433:433='*',<291>,14:9]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_insert6={context_list={branch_a=query0, branch_b=query1, base=intersect4}, query_dictionary={top_score=[[@76,474:482='top_score',<393>,16:23]], rank_bucket=[[@78,485:495='rank_bucket',<393>,16:34]]}, table_dictionary={employees={top_score=[[@76,474:482='top_score',<393>,16:23]], rank_bucket=[[@78,485:495='rank_bucket',<393>,16:34]]}}, def_intersect4={context_list={branch_a=query0, branch_b=query1}, query_dictionary={score_val=[[@81,505:505='b',<393>,17:7]], emp_id=[[@85,518:518='b',<393>,17:20]]}, interface={*=query_column}, table_alias={branch_a=query0, branch_b=query1}, def_query3={context_list={branch_a=query0, branch_b=query1}, query_dictionary={*=[[@68,433:433='*',<291>,14:9]]}, setop=INTERSECTION, interface={*=[{name=*, table_ref=*}]}, table_alias={branch_a=query0, branch_b=query1}}, def_query2={context_list={branch_a=query0, branch_b=query1}, query_dictionary={*=[[@63,396:396='*',<291>,12:9]]}, interface={*=[{name=*, table_ref=*}]}, table_alias={branch_a=query0, branch_b=query1}}}, def_query1={context_list={branch_a=query0}, query_dictionary={*=[[@63,396:396='*',<291>,12:9]], score_val=[[@42,253:261='score_val',<393>,7:47]], emp_id=[[@36,217:222='emp_id',<393>,7:11]]}, table_dictionary={<[Ops Data].[Audit Ledger I3]>={<insert order col I3>=[[@54,350:350='b',<393>,10:11]], <insert where col I3>=[[@47,311:311='b',<393>,9:8]], <insert select col I3>=[[@38,225:225='b',<393>,7:19]], emp_id=[[@34,215:215='b',<393>,7:9]]}}, ordered_by=[{substitution={name=<insert order col I3>, type=column}, table_ref=b}], filters=[{substitution={name=<insert where col I3>, type=column}, table_ref=b}], interface={score_val=[{substitution={name=<insert select col I3>, type=column}, table_ref=b}], emp_id=[{name=emp_id, table_ref=b}]}, table_alias={b=<[Ops Data].[Audit Ledger I3]>, branch_a=query0}}, def_query0={query_dictionary={*=[[@63,396:396='*',<291>,12:9]], score_val=[[@13,66:74='score_val',<393>,2:47]], emp_id=[[@7,30:35='emp_id',<393>,2:11]]}, table_dictionary={<[Ops Data].[Account Ledger I3]>={<insert order col I3>=[[@25,165:165='a',<393>,5:11]], <insert where col I3>=[[@18,126:126='a',<393>,4:8]], <insert select col I3>=[[@9,38:38='a',<393>,2:19]], emp_id=[[@5,28:28='a',<393>,2:9]]}}, ordered_by=[{substitution={name=<insert order col I3>, type=column}, table_ref=a}], filters=[{substitution={name=<insert where col I3>, type=column}, table_ref=a}], interface={score_val=[{substitution={name=<insert select col I3>, type=column}, table_ref=a}], emp_id=[{name=emp_id, table_ref=a}]}, table_alias={a=<[Ops Data].[Account Ledger I3]>}}, def_query5={context_list={branch_a=query0, branch_b=query1, base=intersect4, b=intersect4}, query_dictionary={score_val=[[@83,507:515='score_val',<393>,17:9]], emp_id=[[@87,520:525='emp_id',<393>,17:22]]}, interface={score_val=[{name=score_val, table_ref=b}], emp_id=[{name=emp_id, table_ref=b}]}, table_alias={b=intersect4, branch_a=query0, branch_b=query1, base=intersect4}}, interface={top_score=[{name=score_val, table_ref=query5}], rank_bucket=[{name=emp_id, table_ref=query5}]}, table_alias={branch_a=query0, branch_b=query1, base=intersect4}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void insertComplexSubstitutionI3WithCteExceptOrderBySubstitution() {
		final String query = "WITH branch_a AS ("
				+ "\n  SELECT a.emp_id, a.<insert select col I3> AS score_val"
				+ "\n  FROM <[Ops Data].[Account Ledger I3]> a"
				+ "\n  WHERE a.<insert where col I3> > 0"
				+ "\n  ORDER BY a.<insert order col I3>"
				+ "\n), branch_b AS ("
				+ "\n  SELECT b.emp_id, b.<insert select col I3> AS score_val"
				+ "\n  FROM <[Ops Data].[Audit Ledger I3]> b"
				+ "\n  WHERE b.<insert where col I3> > 0"
				+ "\n  ORDER BY b.<insert order col I3>"
				+ "\n), base AS ("
				+ "\n  SELECT * FROM branch_a"
				+ "\n  EXCEPT"
				+ "\n  SELECT * FROM branch_b"
				+ "\n)"
				+ "\nINSERT INTO employees (top_score, rank_bucket)"
				+ "\nSELECT b.score_val, b.emp_id"
				+ "\nFROM base b";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={with={1={cte={select={1={column={name=emp_id, table_ref=a}}, 2={column={substitution={name=<insert select col I3>, type=column}, table_ref=a}, alias=score_val}}, orderby={1={null_order=null, predicand={column={substitution={name=<insert order col I3>, type=column}, table_ref=a}}, sort_order=ASC}}, from={table={alias=a, substitution={name=<[Ops Data].[Account Ledger I3]>, parts={1=[Ops Data], 2=[Account Ledger I3]}, type=tuple}}}, where={condition={left={column={substitution={name=<insert where col I3>, type=column}, table_ref=a}}, right={literal=0}, operator=>}}}, alias=branch_a}, 2={cte={select={1={column={name=emp_id, table_ref=b}}, 2={column={substitution={name=<insert select col I3>, type=column}, table_ref=b}, alias=score_val}}, orderby={1={null_order=null, predicand={column={substitution={name=<insert order col I3>, type=column}, table_ref=b}}, sort_order=ASC}}, from={table={alias=b, substitution={name=<[Ops Data].[Audit Ledger I3]>, parts={1=[Ops Data], 2=[Audit Ledger I3]}, type=tuple}}}, where={condition={left={column={substitution={name=<insert where col I3>, type=column}, table_ref=b}}, right={literal=0}, operator=>}}}, alias=branch_b}, 3={cte={union={1={select={1={column={name=*, table_ref=*}}}, from={table={alias=null, table=branch_a}}}, 2={union={qualifier=null, operator=EXCEPT}}, 3={select={1={column={name=*, table_ref=*}}}, from={table={alias=null, table=branch_b}}}}}, alias=base}}, query={insert={preamble=insert_into, from={from={table={alias=b, table=base}}, select={1={column={name=score_val, table_ref=b}}, 2={column={name=emp_id, table_ref=b}}}}, target_table={table={alias=null, table=employees}}, columns={1={column={name=top_score, table_ref=null}}, 2={column={name=rank_bucket, table_ref=null}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[top_score, rank_bucket]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<[Ops Data].[Account Ledger I3]>=tuple, <insert order col I3>=column, <insert where col I3>=column, <[Ops Data].[Audit Ledger I3]>=tuple, <insert select col I3>=column}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{<[Ops Data].[Account Ledger I3]>={<insert order col I3>=[[@25,165:165='a',<393>,5:11]], <insert where col I3>=[[@18,126:126='a',<393>,4:8]], <insert select col I3>=[[@9,38:38='a',<393>,2:19]], emp_id=[[@5,28:28='a',<393>,2:9]]}, <[Ops Data].[Audit Ledger I3]>={<insert order col I3>=[[@54,350:350='b',<393>,10:11]], <insert where col I3>=[[@47,311:311='b',<393>,9:8]], <insert select col I3>=[[@38,225:225='b',<393>,7:19]], emp_id=[[@34,215:215='b',<393>,7:9]]}, employees={top_score=[[@76,471:479='top_score',<393>,16:23]], rank_bucket=[[@78,482:492='rank_bucket',<393>,16:34]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{union4={score_val=[[@81,502:502='b',<393>,17:7]], emp_id=[[@85,515:515='b',<393>,17:20]]}, query5={score_val=[[@83,504:512='score_val',<393>,17:9]], emp_id=[[@87,517:522='emp_id',<393>,17:22]]}, insert6={top_score=[[@76,471:479='top_score',<393>,16:23]], rank_bucket=[[@78,482:492='rank_bucket',<393>,16:34]]}, query0={score_val=[[@13,66:74='score_val',<393>,2:47]], *=[[@63,396:396='*',<291>,12:9]], emp_id=[[@7,30:35='emp_id',<393>,2:11]]}, query1={score_val=[[@42,253:261='score_val',<393>,7:47]], *=[[@63,396:396='*',<291>,12:9]], emp_id=[[@36,217:222='emp_id',<393>,7:11]]}, query2={*=[[@63,396:396='*',<291>,12:9]]}, query3={*=[[@68,430:430='*',<291>,14:9]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_insert6={context_list={branch_a=query0, branch_b=query1, base=union4}, def_union4={context_list={branch_a=query0, branch_b=query1}, query_dictionary={score_val=[[@81,502:502='b',<393>,17:7]], emp_id=[[@85,515:515='b',<393>,17:20]]}, interface={*=query_column}, table_alias={branch_a=query0, branch_b=query1}, def_query3={context_list={branch_a=query0, branch_b=query1}, query_dictionary={*=[[@68,430:430='*',<291>,14:9]]}, setop=EXCEPT, interface={*=[{name=*, table_ref=*}]}, table_alias={branch_a=query0, branch_b=query1}}, def_query2={context_list={branch_a=query0, branch_b=query1}, query_dictionary={*=[[@63,396:396='*',<291>,12:9]]}, interface={*=[{name=*, table_ref=*}]}, table_alias={branch_a=query0, branch_b=query1}}}, query_dictionary={top_score=[[@76,471:479='top_score',<393>,16:23]], rank_bucket=[[@78,482:492='rank_bucket',<393>,16:34]]}, table_dictionary={employees={top_score=[[@76,471:479='top_score',<393>,16:23]], rank_bucket=[[@78,482:492='rank_bucket',<393>,16:34]]}}, def_query1={context_list={branch_a=query0}, query_dictionary={*=[[@63,396:396='*',<291>,12:9]], score_val=[[@42,253:261='score_val',<393>,7:47]], emp_id=[[@36,217:222='emp_id',<393>,7:11]]}, table_dictionary={<[Ops Data].[Audit Ledger I3]>={<insert order col I3>=[[@54,350:350='b',<393>,10:11]], <insert where col I3>=[[@47,311:311='b',<393>,9:8]], <insert select col I3>=[[@38,225:225='b',<393>,7:19]], emp_id=[[@34,215:215='b',<393>,7:9]]}}, ordered_by=[{substitution={name=<insert order col I3>, type=column}, table_ref=b}], filters=[{substitution={name=<insert where col I3>, type=column}, table_ref=b}], interface={score_val=[{substitution={name=<insert select col I3>, type=column}, table_ref=b}], emp_id=[{name=emp_id, table_ref=b}]}, table_alias={b=<[Ops Data].[Audit Ledger I3]>, branch_a=query0}}, def_query0={query_dictionary={*=[[@63,396:396='*',<291>,12:9]], score_val=[[@13,66:74='score_val',<393>,2:47]], emp_id=[[@7,30:35='emp_id',<393>,2:11]]}, table_dictionary={<[Ops Data].[Account Ledger I3]>={<insert order col I3>=[[@25,165:165='a',<393>,5:11]], <insert where col I3>=[[@18,126:126='a',<393>,4:8]], <insert select col I3>=[[@9,38:38='a',<393>,2:19]], emp_id=[[@5,28:28='a',<393>,2:9]]}}, ordered_by=[{substitution={name=<insert order col I3>, type=column}, table_ref=a}], filters=[{substitution={name=<insert where col I3>, type=column}, table_ref=a}], interface={score_val=[{substitution={name=<insert select col I3>, type=column}, table_ref=a}], emp_id=[{name=emp_id, table_ref=a}]}, table_alias={a=<[Ops Data].[Account Ledger I3]>}}, def_query5={context_list={branch_a=query0, branch_b=query1, base=union4, b=union4}, query_dictionary={score_val=[[@83,504:512='score_val',<393>,17:9]], emp_id=[[@87,517:522='emp_id',<393>,17:22]]}, interface={score_val=[{name=score_val, table_ref=b}], emp_id=[{name=emp_id, table_ref=b}]}, table_alias={b=union4, branch_a=query0, branch_b=query1, base=union4}}, interface={top_score=[{name=score_val, table_ref=query5}], rank_bucket=[{name=emp_id, table_ref=query5}]}, table_alias={branch_a=query0, branch_b=query1, base=union4}}}",
				extractor.getSymbolTable().toString());
	}



	@Test
	public void insertComplexSubstitutionI4NestedWithInCteBody() {
		final String query = "WITH outer_cte AS ("
				+ "\n  WITH inner_cte AS ("
				+ "\n    SELECT a.emp_id, a.<insert select col I4> AS metric_val"
				+ "\n    FROM <[Finance].[Revenue Feed I4]> a"
				+ "\n    WHERE a.<insert where col I4> > 0"
				+ "\n  )"
				+ "\n  SELECT i.emp_id, i.metric_val"
				+ "\n  FROM inner_cte i"
				+ "\n  WHERE i.metric_val > 0"
				+ "\n)"
				+ "\nINSERT INTO employees (score, rank_bucket)"
				+ "\nSELECT o.metric_val, o.emp_id"
				+ "\nFROM outer_cte o";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={with={1={cte={with={1={cte={select={1={column={name=emp_id, table_ref=a}}, 2={column={substitution={name=<insert select col I4>, type=column}, table_ref=a}, alias=metric_val}}, from={table={alias=a, substitution={name=<[Finance].[Revenue Feed I4]>, parts={1=[Finance], 2=[Revenue Feed I4]}, type=tuple}}}, where={condition={left={column={substitution={name=<insert where col I4>, type=column}, table_ref=a}}, right={literal=0}, operator=>}}}, alias=inner_cte}}, query={select={1={column={name=emp_id, table_ref=i}}, 2={column={name=metric_val, table_ref=i}}}, from={table={alias=i, table=inner_cte}}, where={condition={left={column={name=metric_val, table_ref=i}}, right={literal=0}, operator=>}}}}, alias=outer_cte}}, query={insert={preamble=insert_into, from={from={table={alias=o, table=outer_cte}}, select={1={column={name=metric_val, table_ref=o}}, 2={column={name=emp_id, table_ref=o}}}}, target_table={table={alias=null, table=employees}}, columns={1={column={name=score, table_ref=null}}, 2={column={name=rank_bucket, table_ref=null}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[score, rank_bucket]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<[Finance].[Revenue Feed I4]>=tuple, <insert select col I4>=column, <insert where col I4>=column}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{<[Finance].[Revenue Feed I4]>={<insert select col I4>=[[@13,63:63='a',<393>,3:21]], <insert where col I4>=[[@22,153:153='a',<393>,5:10]], emp_id=[[@9,53:53='a',<393>,3:11]]}, employees={score=[[@50,286:290='score',<393>,11:23]], rank_bucket=[[@52,293:303='rank_bucket',<393>,11:30]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={metric_val=[[@17,91:100='metric_val',<393>,3:49], [@33,204:204='i',<393>,7:19], [@40,244:244='i',<393>,9:8]], emp_id=[[@11,55:60='emp_id',<393>,3:13], [@29,194:194='i',<393>,7:9]]}, query1={metric_val=[[@35,206:215='metric_val',<393>,7:21], [@55,313:313='o',<393>,12:7]], emp_id=[[@31,196:201='emp_id',<393>,7:11], [@59,327:327='o',<393>,12:21]]}, insert3={score=[[@50,286:290='score',<393>,11:23]], rank_bucket=[[@52,293:303='rank_bucket',<393>,11:30]]}, query2={metric_val=[[@57,315:324='metric_val',<393>,12:9]], emp_id=[[@61,329:334='emp_id',<393>,12:23]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_insert3={context_list={outer_cte=query1}, query_dictionary={score=[[@50,286:290='score',<393>,11:23]], rank_bucket=[[@52,293:303='rank_bucket',<393>,11:30]]}, table_dictionary={employees={score=[[@50,286:290='score',<393>,11:23]], rank_bucket=[[@52,293:303='rank_bucket',<393>,11:30]]}}, def_query1={context_list={inner_cte=query0, i=query0}, query_dictionary={metric_val=[[@35,206:215='metric_val',<393>,7:21], [@55,313:313='o',<393>,12:7]], emp_id=[[@31,196:201='emp_id',<393>,7:11], [@59,327:327='o',<393>,12:21]]}, def_query0={query_dictionary={metric_val=[[@17,91:100='metric_val',<393>,3:49], [@33,204:204='i',<393>,7:19], [@40,244:244='i',<393>,9:8]], emp_id=[[@11,55:60='emp_id',<393>,3:13], [@29,194:194='i',<393>,7:9]]}, table_dictionary={<[Finance].[Revenue Feed I4]>={<insert select col I4>=[[@13,63:63='a',<393>,3:21]], <insert where col I4>=[[@22,153:153='a',<393>,5:10]], emp_id=[[@9,53:53='a',<393>,3:11]]}}, filters=[{substitution={name=<insert where col I4>, type=column}, table_ref=a}], interface={metric_val=[{substitution={name=<insert select col I4>, type=column}, table_ref=a}], emp_id=[{name=emp_id, table_ref=a}]}, table_alias={a=<[Finance].[Revenue Feed I4]>}}, filters=[{name=metric_val, table_ref=i}], interface={metric_val=[{name=metric_val, table_ref=i}], emp_id=[{name=emp_id, table_ref=i}]}, table_alias={inner_cte=query0, i=query0}}, interface={score=[{name=metric_val, table_ref=query2}], rank_bucket=[{name=emp_id, table_ref=query2}]}, table_alias={outer_cte=query1}, def_query2={context_list={outer_cte=query1, o=query1}, query_dictionary={metric_val=[[@57,315:324='metric_val',<393>,12:9]], emp_id=[[@61,329:334='emp_id',<393>,12:23]]}, interface={metric_val=[{name=metric_val, table_ref=o}], emp_id=[{name=emp_id, table_ref=o}]}, table_alias={outer_cte=query1, o=query1}}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void insertComplexSubstitutionI5WithCteQualifyWindowSubstitution() {
		final String query = "WITH ranked AS ("
				+ "\n  SELECT a.emp_id, a.<insert select col I5> AS score_val,"
				+ "\n         row_number() OVER (PARTITION BY a.emp_id ORDER BY a.<insert order col I5> DESC) AS rn"
				+ "\n  FROM <[Metrics].[Score Feed I5]> a"
				+ "\n  WHERE a.<insert where col I5> > 0"
				+ "\n  QUALIFY rn = 1"
				+ "\n)"
				+ "\nINSERT INTO employees (top_score, rank_bucket)"
				+ "\nSELECT r.score_val, r.emp_id"
				+ "\nFROM ranked r";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={with={1={cte={select={1={column={name=emp_id, table_ref=a}}, 2={column={substitution={name=<insert select col I5>, type=column}, table_ref=a}, alias=score_val}, 3={alias=rn, window_function={over={partition_by={1={column={name=emp_id, table_ref=a}}}, orderby={1={null_order=null, predicand={column={substitution={name=<insert order col I5>, type=column}, table_ref=a}}, sort_order=DESC}}}, function={function_name=row_number, parameters=null}}}}, from={table={alias=a, substitution={name=<[Metrics].[Score Feed I5]>, parts={1=[Metrics], 2=[Score Feed I5]}, type=tuple}}}, where={condition={left={column={substitution={name=<insert where col I5>, type=column}, table_ref=a}}, right={literal=0}, operator=>}}, qualify={condition={left={column={name=rn, table_ref=null}}, right={literal=1}, operator==}}}, alias=ranked}}, query={insert={preamble=insert_into, from={from={table={alias=r, table=ranked}}, select={1={column={name=score_val, table_ref=r}}, 2={column={name=emp_id, table_ref=r}}}}, target_table={table={alias=null, table=employees}}, columns={1={column={name=top_score, table_ref=null}}, 2={column={name=rank_bucket, table_ref=null}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[top_score, rank_bucket]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<insert order col I5>=column, <[Metrics].[Score Feed I5]>=tuple, <insert select col I5>=column, <insert where col I5>=column}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{<[Metrics].[Score Feed I5]>={<insert order col I5>=[[@27,134:134='a',<393>,3:59]], <insert select col I5>=[[@9,36:36='a',<393>,2:19]], <insert where col I5>=[[@38,215:215='a',<393>,5:8]], emp_id=[[@5,26:26='a',<393>,2:9], [@22,116:116='a',<393>,3:41]]}, employees={top_score=[[@52,285:293='top_score',<393>,8:23]], rank_bucket=[[@54,296:306='rank_bucket',<393>,8:34]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={rn=[[@33,167:168='rn',<393>,3:92], [@44,253:254='rn',<393>,6:10]], score_val=[[@13,64:72='score_val',<393>,2:47], [@57,316:316='r',<393>,9:7]], emp_id=[[@7,28:33='emp_id',<393>,2:11], [@61,329:329='r',<393>,9:20]]}, query1={score_val=[[@59,318:326='score_val',<393>,9:9]], emp_id=[[@63,331:336='emp_id',<393>,9:22]]}, insert2={top_score=[[@52,285:293='top_score',<393>,8:23]], rank_bucket=[[@54,296:306='rank_bucket',<393>,8:34]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_insert2={context_list={ranked=query0}, query_dictionary={top_score=[[@52,285:293='top_score',<393>,8:23]], rank_bucket=[[@54,296:306='rank_bucket',<393>,8:34]]}, table_dictionary={employees={top_score=[[@52,285:293='top_score',<393>,8:23]], rank_bucket=[[@54,296:306='rank_bucket',<393>,8:34]]}}, def_query1={context_list={ranked=query0, r=query0}, query_dictionary={score_val=[[@59,318:326='score_val',<393>,9:9]], emp_id=[[@63,331:336='emp_id',<393>,9:22]]}, interface={score_val=[{name=score_val, table_ref=r}], emp_id=[{name=emp_id, table_ref=r}]}, table_alias={r=query0, ranked=query0}}, def_query0={window_ordered_by=[{substitution={name=<insert order col I5>, type=column}, table_ref=a}], query_dictionary={rn=[[@33,167:168='rn',<393>,3:92], [@44,253:254='rn',<393>,6:10]], score_val=[[@13,64:72='score_val',<393>,2:47], [@57,316:316='r',<393>,9:7]], emp_id=[[@7,28:33='emp_id',<393>,2:11], [@61,329:329='r',<393>,9:20]]}, table_dictionary={<[Metrics].[Score Feed I5]>={<insert order col I5>=[[@27,134:134='a',<393>,3:59]], <insert select col I5>=[[@9,36:36='a',<393>,2:19]], <insert where col I5>=[[@38,215:215='a',<393>,5:8]], emp_id=[[@5,26:26='a',<393>,2:9], [@22,116:116='a',<393>,3:41]]}}, window_partition_by=[{name=emp_id, table_ref=a}], filters=[{substitution={name=<insert where col I5>, type=column}, table_ref=a}, {name=rn, table_ref=query0}], interface={rn=[{name=emp_id, table_ref=a}, {substitution={name=<insert order col I5>, type=column}, table_ref=a}], score_val=[{substitution={name=<insert select col I5>, type=column}, table_ref=a}], emp_id=[{name=emp_id, table_ref=a}]}, table_alias={a=<[Metrics].[Score Feed I5]>}}, interface={top_score=[{name=score_val, table_ref=query1}], rank_bucket=[{name=emp_id, table_ref=query1}]}, table_alias={ranked=query0}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void insertComplexSubstitutionI6SubqueryJoinOnColumnSubstitution() {
		final String query = "INSERT INTO employees (score, rank_bucket)"
				+ "\nSELECT j.metric_val, j.emp_id"
				+ "\nFROM ("
				+ "\n  SELECT a.emp_id, a.<insert select col I6> AS metric_val"
				+ "\n  FROM <[Join Data].[Left Feed I6]> a"
				+ "\n  JOIN <[Join Data].[Right Feed I6]> b"
				+ "\n    ON a.<insert join col I6> = b.<insert join col I6b>"
				+ "\n  WHERE a.<insert where col I6> > 0"
				+ "\n) j";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={insert={preamble=insert_into, from={from={table={alias=j, query={select={1={column={name=emp_id, table_ref=a}}, 2={column={substitution={name=<insert select col I6>, type=column}, table_ref=a}, alias=metric_val}}, from={join={1={table={alias=a, substitution={name=<[Join Data].[Left Feed I6]>, parts={1=[Join Data], 2=[Left Feed I6]}, type=tuple}}}, 2={join=JOIN, on={condition={left={column={substitution={name=<insert join col I6>, type=column}, table_ref=a}}, right={column={substitution={name=<insert join col I6b>, type=column}, table_ref=b}}, operator==}}}, 3={table={alias=b, substitution={name=<[Join Data].[Right Feed I6]>, parts={1=[Join Data], 2=[Right Feed I6]}, type=tuple}}}}}, where={condition={left={column={substitution={name=<insert where col I6>, type=column}, table_ref=a}}, right={literal=0}, operator=>}}}}}, select={1={column={name=metric_val, table_ref=j}}, 2={column={name=emp_id, table_ref=j}}}}, target_table={table={alias=null, table=employees}}, columns={1={column={name=score, table_ref=null}}, 2={column={name=rank_bucket, table_ref=null}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[score, rank_bucket]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<[Join Data].[Left Feed I6]>=tuple, <insert join col I6b>=column, <[Join Data].[Right Feed I6]>=tuple, <insert select col I6>=column, <insert where col I6>=column, <insert join col I6>=column}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{<[Join Data].[Left Feed I6]>={<insert select col I6>=[[@23,99:99='a',<393>,4:19]], <insert where col I6>=[[@43,279:279='a',<393>,8:8]], emp_id=[[@19,89:89='a',<393>,4:9]], <insert join col I6>=[[@35,222:222='a',<393>,7:7]]}, <[Join Data].[Right Feed I6]>={<insert join col I6b>=[[@39,247:247='b',<393>,7:32]]}, employees={score=[[@4,23:27='score',<393>,1:23]], rank_bucket=[[@6,30:40='rank_bucket',<393>,1:30]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={metric_val=[[@27,127:136='metric_val',<393>,4:47], [@9,50:50='j',<393>,2:7]], emp_id=[[@21,91:96='emp_id',<393>,4:11], [@13,64:64='j',<393>,2:21]]}, query1={metric_val=[[@11,52:61='metric_val',<393>,2:9]], emp_id=[[@15,66:71='emp_id',<393>,2:23]]}, insert2={score=[[@4,23:27='score',<393>,1:23]], rank_bucket=[[@6,30:40='rank_bucket',<393>,1:30]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_insert2={query_dictionary={score=[[@4,23:27='score',<393>,1:23]], rank_bucket=[[@6,30:40='rank_bucket',<393>,1:30]]}, table_dictionary={employees={score=[[@4,23:27='score',<393>,1:23]], rank_bucket=[[@6,30:40='rank_bucket',<393>,1:30]]}}, def_query1={query_dictionary={metric_val=[[@11,52:61='metric_val',<393>,2:9]], emp_id=[[@15,66:71='emp_id',<393>,2:23]]}, def_query0={query_dictionary={metric_val=[[@27,127:136='metric_val',<393>,4:47], [@9,50:50='j',<393>,2:7]], emp_id=[[@21,91:96='emp_id',<393>,4:11], [@13,64:64='j',<393>,2:21]]}, table_dictionary={<[Join Data].[Left Feed I6]>={<insert select col I6>=[[@23,99:99='a',<393>,4:19]], <insert where col I6>=[[@43,279:279='a',<393>,8:8]], emp_id=[[@19,89:89='a',<393>,4:9]], <insert join col I6>=[[@35,222:222='a',<393>,7:7]]}, <[Join Data].[Right Feed I6]>={<insert join col I6b>=[[@39,247:247='b',<393>,7:32]]}}, filters=[{substitution={name=<insert join col I6>, type=column}, table_ref=a}, {substitution={name=<insert join col I6b>, type=column}, table_ref=b}, {substitution={name=<insert where col I6>, type=column}, table_ref=a}], interface={metric_val=[{substitution={name=<insert select col I6>, type=column}, table_ref=a}], emp_id=[{name=emp_id, table_ref=a}]}, table_alias={a=<[Join Data].[Left Feed I6]>, b=<[Join Data].[Right Feed I6]>}}, interface={metric_val=[{name=metric_val, table_ref=j}], emp_id=[{name=emp_id, table_ref=j}]}, table_alias={j=query0}}, interface={score=[{name=metric_val, table_ref=query1}], rank_bucket=[{name=emp_id, table_ref=query1}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void insertComplexSubstitutionI7ChainedCteReferences() {
		final String query = "WITH step1 AS ("
				+ "\n  SELECT a.emp_id, a.<insert select col I7> AS raw_val"
				+ "\n  FROM <[Pipeline].[Stage One I7]> a"
				+ "\n  WHERE a.<insert where col I7> > 0"
				+ "\n), step2 AS ("
				+ "\n  SELECT s.emp_id, s.raw_val"
				+ "\n  FROM step1 s"
				+ "\n  WHERE s.raw_val > 0"
				+ "\n)"
				+ "\nINSERT INTO employees (score, rank_bucket)"
				+ "\nSELECT t.raw_val, t.emp_id"
				+ "\nFROM step2 t";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={with={1={cte={select={1={column={name=emp_id, table_ref=a}}, 2={column={substitution={name=<insert select col I7>, type=column}, table_ref=a}, alias=raw_val}}, from={table={alias=a, substitution={name=<[Pipeline].[Stage One I7]>, parts={1=[Pipeline], 2=[Stage One I7]}, type=tuple}}}, where={condition={left={column={substitution={name=<insert where col I7>, type=column}, table_ref=a}}, right={literal=0}, operator=>}}}, alias=step1}, 2={cte={select={1={column={name=emp_id, table_ref=s}}, 2={column={name=raw_val, table_ref=s}}}, from={table={alias=s, table=step1}}, where={condition={left={column={name=raw_val, table_ref=s}}, right={literal=0}, operator=>}}}, alias=step2}}, query={insert={preamble=insert_into, from={from={table={alias=t, table=step2}}, select={1={column={name=raw_val, table_ref=t}}, 2={column={name=emp_id, table_ref=t}}}}, target_table={table={alias=null, table=employees}}, columns={1={column={name=score, table_ref=null}}, 2={column={name=rank_bucket, table_ref=null}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[score, rank_bucket]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<insert select col I7>=column, <[Pipeline].[Stage One I7]>=tuple, <insert where col I7>=column}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{<[Pipeline].[Stage One I7]>={<insert select col I7>=[[@9,35:35='a',<393>,2:19]], emp_id=[[@5,25:25='a',<393>,2:9]], <insert where col I7>=[[@18,116:116='a',<393>,4:8]]}, employees={score=[[@50,249:253='score',<393>,10:23]], rank_bucket=[[@52,256:266='rank_bucket',<393>,10:30]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={raw_val=[[@13,63:69='raw_val',<393>,2:47], [@33,177:177='s',<393>,6:19], [@40,210:210='s',<393>,8:8]], emp_id=[[@7,27:32='emp_id',<393>,2:11], [@29,167:167='s',<393>,6:9]]}, query1={raw_val=[[@35,179:185='raw_val',<393>,6:21], [@55,276:276='t',<393>,11:7]], emp_id=[[@31,169:174='emp_id',<393>,6:11], [@59,287:287='t',<393>,11:18]]}, insert3={score=[[@50,249:253='score',<393>,10:23]], rank_bucket=[[@52,256:266='rank_bucket',<393>,10:30]]}, query2={raw_val=[[@57,278:284='raw_val',<393>,11:9]], emp_id=[[@61,289:294='emp_id',<393>,11:20]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_insert3={context_list={step1=query0, step2=query1}, query_dictionary={score=[[@50,249:253='score',<393>,10:23]], rank_bucket=[[@52,256:266='rank_bucket',<393>,10:30]]}, table_dictionary={employees={score=[[@50,249:253='score',<393>,10:23]], rank_bucket=[[@52,256:266='rank_bucket',<393>,10:30]]}}, def_query1={context_list={step1=query0, s=query0}, query_dictionary={raw_val=[[@35,179:185='raw_val',<393>,6:21], [@55,276:276='t',<393>,11:7]], emp_id=[[@31,169:174='emp_id',<393>,6:11], [@59,287:287='t',<393>,11:18]]}, filters=[{name=raw_val, table_ref=s}], interface={raw_val=[{name=raw_val, table_ref=s}], emp_id=[{name=emp_id, table_ref=s}]}, table_alias={s=query0, step1=query0}}, def_query0={query_dictionary={raw_val=[[@13,63:69='raw_val',<393>,2:47], [@33,177:177='s',<393>,6:19], [@40,210:210='s',<393>,8:8]], emp_id=[[@7,27:32='emp_id',<393>,2:11], [@29,167:167='s',<393>,6:9]]}, table_dictionary={<[Pipeline].[Stage One I7]>={<insert select col I7>=[[@9,35:35='a',<393>,2:19]], emp_id=[[@5,25:25='a',<393>,2:9]], <insert where col I7>=[[@18,116:116='a',<393>,4:8]]}}, filters=[{substitution={name=<insert where col I7>, type=column}, table_ref=a}], interface={raw_val=[{substitution={name=<insert select col I7>, type=column}, table_ref=a}], emp_id=[{name=emp_id, table_ref=a}]}, table_alias={a=<[Pipeline].[Stage One I7]>}}, interface={score=[{name=raw_val, table_ref=query2}], rank_bucket=[{name=emp_id, table_ref=query2}]}, table_alias={step2=query1, step1=query0}, def_query2={context_list={step1=query0, step2=query1, t=query1}, query_dictionary={raw_val=[[@57,278:284='raw_val',<393>,11:9]], emp_id=[[@61,289:294='emp_id',<393>,11:20]]}, interface={raw_val=[{name=raw_val, table_ref=t}], emp_id=[{name=emp_id, table_ref=t}]}, table_alias={t=query1, step2=query1, step1=query0}}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void insertComplexSubstitutionI8UnionIntersectNestedSubquery() {
		final String query = "INSERT INTO employees (score, rank_bucket)"
				+ "\nSELECT x.metric_val, x.emp_id"
				+ "\nFROM ("
				+ "\n  SELECT u.emp_id, u.metric_val"
				+ "\n  FROM ("
				+ "\n    SELECT a.emp_id, a.<insert select col I8> AS metric_val"
				+ "\n    FROM <[Blend Data].[Branch A I8]> a"
				+ "\n    WHERE a.<insert where col I8> > 0"
				+ "\n    UNION"
				+ "\n    SELECT b.emp_id, b.<insert select col I8> AS metric_val"
				+ "\n    FROM <[Blend Data].[Branch B I8]> b"
				+ "\n    WHERE b.<insert where col I8> > 0"
				+ "\n  ) u"
				+ "\n  INTERSECT"
				+ "\n  SELECT c.emp_id, c.<insert select col I8> AS metric_val"
				+ "\n  FROM <[Blend Data].[Branch C I8]> c"
				+ "\n  WHERE c.<insert where col I8> > 0"
				+ "\n) x";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={insert={preamble=insert_into, from={from={table={alias=x, query={intersect={1={select={1={column={name=emp_id, table_ref=u}}, 2={column={name=metric_val, table_ref=u}}}, from={table={alias=u, query={union={1={select={1={column={name=emp_id, table_ref=a}}, 2={column={substitution={name=<insert select col I8>, type=column}, table_ref=a}, alias=metric_val}}, from={table={alias=a, substitution={name=<[Blend Data].[Branch A I8]>, parts={1=[Blend Data], 2=[Branch A I8]}, type=tuple}}}, where={condition={left={column={substitution={name=<insert where col I8>, type=column}, table_ref=a}}, right={literal=0}, operator=>}}}, 2={union={qualifier=null, operator=UNION}}, 3={select={1={column={name=emp_id, table_ref=b}}, 2={column={substitution={name=<insert select col I8>, type=column}, table_ref=b}, alias=metric_val}}, from={table={alias=b, substitution={name=<[Blend Data].[Branch B I8]>, parts={1=[Blend Data], 2=[Branch B I8]}, type=tuple}}}, where={condition={left={column={substitution={name=<insert where col I8>, type=column}, table_ref=b}}, right={literal=0}, operator=>}}}}}}}}, 2={intersect={qualifier=null, operator=INTERSECT}}, 3={select={1={column={name=emp_id, table_ref=c}}, 2={column={substitution={name=<insert select col I8>, type=column}, table_ref=c}, alias=metric_val}}, from={table={alias=c, substitution={name=<[Blend Data].[Branch C I8]>, parts={1=[Blend Data], 2=[Branch C I8]}, type=tuple}}}, where={condition={left={column={substitution={name=<insert where col I8>, type=column}, table_ref=c}}, right={literal=0}, operator=>}}}}}}}, select={1={column={name=metric_val, table_ref=x}}, 2={column={name=emp_id, table_ref=x}}}}, target_table={table={alias=null, table=employees}}, columns={1={column={name=score, table_ref=null}}, 2={column={name=rank_bucket, table_ref=null}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[score, rank_bucket]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<[Blend Data].[Branch B I8]>=tuple, <insert select col I8>=column, <insert where col I8>=column, <[Blend Data].[Branch C I8]>=tuple, <[Blend Data].[Branch A I8]>=tuple}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{<[Blend Data].[Branch B I8]>={<insert select col I8>=[[@53,290:290='b',<393>,10:21]], <insert where col I8>=[[@62,379:379='b',<393>,12:10]], emp_id=[[@49,280:280='b',<393>,10:11]]}, <[Blend Data].[Branch C I8]>={<insert select col I8>=[[@75,444:444='c',<393>,15:19]], <insert where col I8>=[[@84,529:529='c',<393>,17:8]], emp_id=[[@71,434:434='c',<393>,15:9]]}, employees={score=[[@4,23:27='score',<393>,1:23]], rank_bucket=[[@6,30:40='rank_bucket',<393>,1:30]]}, <[Blend Data].[Branch A I8]>={<insert select col I8>=[[@33,142:142='a',<393>,6:21]], <insert where col I8>=[[@42,231:231='a',<393>,8:10]], emp_id=[[@29,132:132='a',<393>,6:11]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{union2={metric_val=[[@23,99:99='u',<393>,4:19]], emp_id=[[@19,89:89='u',<393>,4:9]]}, intersect5={metric_val=[[@9,50:50='x',<393>,2:7]], emp_id=[[@13,64:64='x',<393>,2:21]]}, query4={metric_val=[[@79,472:481='metric_val',<393>,15:47]], emp_id=[[@73,436:441='emp_id',<393>,15:11]]}, insert7={score=[[@4,23:27='score',<393>,1:23]], rank_bucket=[[@6,30:40='rank_bucket',<393>,1:30]]}, query6={metric_val=[[@11,52:61='metric_val',<393>,2:9]], emp_id=[[@15,66:71='emp_id',<393>,2:23]]}, query0={metric_val=[[@37,170:179='metric_val',<393>,6:49]], emp_id=[[@31,134:139='emp_id',<393>,6:13]]}, query1={metric_val=[[@57,318:327='metric_val',<393>,10:49]], emp_id=[[@51,282:287='emp_id',<393>,10:13]]}, query3={metric_val=[[@25,101:110='metric_val',<393>,4:21]], emp_id=[[@21,91:96='emp_id',<393>,4:11]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_insert7={query_dictionary={score=[[@4,23:27='score',<393>,1:23]], rank_bucket=[[@6,30:40='rank_bucket',<393>,1:30]]}, def_query6={query_dictionary={metric_val=[[@11,52:61='metric_val',<393>,2:9]], emp_id=[[@15,66:71='emp_id',<393>,2:23]]}, def_intersect5={query_dictionary={metric_val=[[@9,50:50='x',<393>,2:7]], emp_id=[[@13,64:64='x',<393>,2:21]]}, interface={metric_val=query_column, emp_id=query_column}, def_query4={query_dictionary={metric_val=[[@79,472:481='metric_val',<393>,15:47]], emp_id=[[@73,436:441='emp_id',<393>,15:11]]}, table_dictionary={<[Blend Data].[Branch C I8]>={<insert select col I8>=[[@75,444:444='c',<393>,15:19]], <insert where col I8>=[[@84,529:529='c',<393>,17:8]], emp_id=[[@71,434:434='c',<393>,15:9]]}}, setop=INTERSECTION, filters=[{substitution={name=<insert where col I8>, type=column}, table_ref=c}], interface={metric_val=[{substitution={name=<insert select col I8>, type=column}, table_ref=c}], emp_id=[{name=emp_id, table_ref=c}]}, table_alias={c=<[Blend Data].[Branch C I8]>}}, def_query3={def_union2={query_dictionary={metric_val=[[@23,99:99='u',<393>,4:19]], emp_id=[[@19,89:89='u',<393>,4:9]]}, def_query1={query_dictionary={metric_val=[[@57,318:327='metric_val',<393>,10:49]], emp_id=[[@51,282:287='emp_id',<393>,10:13]]}, table_dictionary={<[Blend Data].[Branch B I8]>={<insert select col I8>=[[@53,290:290='b',<393>,10:21]], <insert where col I8>=[[@62,379:379='b',<393>,12:10]], emp_id=[[@49,280:280='b',<393>,10:11]]}}, setop=UNION, filters=[{substitution={name=<insert where col I8>, type=column}, table_ref=b}], interface={metric_val=[{substitution={name=<insert select col I8>, type=column}, table_ref=b}], emp_id=[{name=emp_id, table_ref=b}]}, table_alias={b=<[Blend Data].[Branch B I8]>}}, def_query0={query_dictionary={metric_val=[[@37,170:179='metric_val',<393>,6:49]], emp_id=[[@31,134:139='emp_id',<393>,6:13]]}, table_dictionary={<[Blend Data].[Branch A I8]>={<insert select col I8>=[[@33,142:142='a',<393>,6:21]], <insert where col I8>=[[@42,231:231='a',<393>,8:10]], emp_id=[[@29,132:132='a',<393>,6:11]]}}, filters=[{substitution={name=<insert where col I8>, type=column}, table_ref=a}], interface={metric_val=[{substitution={name=<insert select col I8>, type=column}, table_ref=a}], emp_id=[{name=emp_id, table_ref=a}]}, table_alias={a=<[Blend Data].[Branch A I8]>}}, interface={metric_val=query_column, emp_id=query_column}}, query_dictionary={metric_val=[[@25,101:110='metric_val',<393>,4:21]], emp_id=[[@21,91:96='emp_id',<393>,4:11]]}, interface={metric_val=[{name=metric_val, table_ref=u}], emp_id=[{name=emp_id, table_ref=u}]}, table_alias={u=union2}}}, interface={metric_val=[{name=metric_val, table_ref=x}], emp_id=[{name=emp_id, table_ref=x}]}, table_alias={x=intersect5}}, table_dictionary={employees={score=[[@4,23:27='score',<393>,1:23]], rank_bucket=[[@6,30:40='rank_bucket',<393>,1:30]]}}, interface={score=[{name=metric_val, table_ref=query6}], rank_bucket=[{name=emp_id, table_ref=query6}]}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void insertComplexSubstitutionI8UnionExceptNestedSubquery() {
		final String query = "INSERT INTO employees (score, rank_bucket)"
				+ "\nSELECT x.metric_val, x.emp_id"
				+ "\nFROM ("
				+ "\n  SELECT u.emp_id, u.metric_val"
				+ "\n  FROM ("
				+ "\n    SELECT a.emp_id, a.<insert select col I8> AS metric_val"
				+ "\n    FROM <[Blend Data].[Branch A I8]> a"
				+ "\n    WHERE a.<insert where col I8> > 0"
				+ "\n    UNION"
				+ "\n    SELECT b.emp_id, b.<insert select col I8> AS metric_val"
				+ "\n    FROM <[Blend Data].[Branch B I8]> b"
				+ "\n    WHERE b.<insert where col I8> > 0"
				+ "\n  ) u"
				+ "\n  EXCEPT"
				+ "\n  SELECT c.emp_id, c.<insert select col I8> AS metric_val"
				+ "\n  FROM <[Blend Data].[Branch C I8]> c"
				+ "\n  WHERE c.<insert where col I8> > 0"
				+ "\n) x";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={insert={preamble=insert_into, from={from={table={alias=x, query={union={1={select={1={column={name=emp_id, table_ref=u}}, 2={column={name=metric_val, table_ref=u}}}, from={table={alias=u, query={union={1={select={1={column={name=emp_id, table_ref=a}}, 2={column={substitution={name=<insert select col I8>, type=column}, table_ref=a}, alias=metric_val}}, from={table={alias=a, substitution={name=<[Blend Data].[Branch A I8]>, parts={1=[Blend Data], 2=[Branch A I8]}, type=tuple}}}, where={condition={left={column={substitution={name=<insert where col I8>, type=column}, table_ref=a}}, right={literal=0}, operator=>}}}, 2={union={qualifier=null, operator=UNION}}, 3={select={1={column={name=emp_id, table_ref=b}}, 2={column={substitution={name=<insert select col I8>, type=column}, table_ref=b}, alias=metric_val}}, from={table={alias=b, substitution={name=<[Blend Data].[Branch B I8]>, parts={1=[Blend Data], 2=[Branch B I8]}, type=tuple}}}, where={condition={left={column={substitution={name=<insert where col I8>, type=column}, table_ref=b}}, right={literal=0}, operator=>}}}}}}}}, 2={union={qualifier=null, operator=EXCEPT}}, 3={select={1={column={name=emp_id, table_ref=c}}, 2={column={substitution={name=<insert select col I8>, type=column}, table_ref=c}, alias=metric_val}}, from={table={alias=c, substitution={name=<[Blend Data].[Branch C I8]>, parts={1=[Blend Data], 2=[Branch C I8]}, type=tuple}}}, where={condition={left={column={substitution={name=<insert where col I8>, type=column}, table_ref=c}}, right={literal=0}, operator=>}}}}}}}, select={1={column={name=metric_val, table_ref=x}}, 2={column={name=emp_id, table_ref=x}}}}, target_table={table={alias=null, table=employees}}, columns={1={column={name=score, table_ref=null}}, 2={column={name=rank_bucket, table_ref=null}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[score, rank_bucket]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<[Blend Data].[Branch B I8]>=tuple, <insert select col I8>=column, <insert where col I8>=column, <[Blend Data].[Branch C I8]>=tuple, <[Blend Data].[Branch A I8]>=tuple}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{<[Blend Data].[Branch B I8]>={<insert select col I8>=[[@53,290:290='b',<393>,10:21]], <insert where col I8>=[[@62,379:379='b',<393>,12:10]], emp_id=[[@49,280:280='b',<393>,10:11]]}, <[Blend Data].[Branch C I8]>={<insert select col I8>=[[@75,441:441='c',<393>,15:19]], <insert where col I8>=[[@84,526:526='c',<393>,17:8]], emp_id=[[@71,431:431='c',<393>,15:9]]}, employees={score=[[@4,23:27='score',<393>,1:23]], rank_bucket=[[@6,30:40='rank_bucket',<393>,1:30]]}, <[Blend Data].[Branch A I8]>={<insert select col I8>=[[@33,142:142='a',<393>,6:21]], <insert where col I8>=[[@42,231:231='a',<393>,8:10]], emp_id=[[@29,132:132='a',<393>,6:11]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{union5={metric_val=[[@9,50:50='x',<393>,2:7]], emp_id=[[@13,64:64='x',<393>,2:21]]}, union2={metric_val=[[@23,99:99='u',<393>,4:19]], emp_id=[[@19,89:89='u',<393>,4:9]]}, query4={metric_val=[[@79,469:478='metric_val',<393>,15:47]], emp_id=[[@73,433:438='emp_id',<393>,15:11]]}, insert7={score=[[@4,23:27='score',<393>,1:23]], rank_bucket=[[@6,30:40='rank_bucket',<393>,1:30]]}, query6={metric_val=[[@11,52:61='metric_val',<393>,2:9]], emp_id=[[@15,66:71='emp_id',<393>,2:23]]}, query0={metric_val=[[@37,170:179='metric_val',<393>,6:49]], emp_id=[[@31,134:139='emp_id',<393>,6:13]]}, query1={metric_val=[[@57,318:327='metric_val',<393>,10:49]], emp_id=[[@51,282:287='emp_id',<393>,10:13]]}, query3={metric_val=[[@25,101:110='metric_val',<393>,4:21]], emp_id=[[@21,91:96='emp_id',<393>,4:11]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_insert7={query_dictionary={score=[[@4,23:27='score',<393>,1:23]], rank_bucket=[[@6,30:40='rank_bucket',<393>,1:30]]}, def_query6={query_dictionary={metric_val=[[@11,52:61='metric_val',<393>,2:9]], emp_id=[[@15,66:71='emp_id',<393>,2:23]]}, interface={metric_val=[{name=metric_val, table_ref=x}], emp_id=[{name=emp_id, table_ref=x}]}, def_union5={query_dictionary={metric_val=[[@9,50:50='x',<393>,2:7]], emp_id=[[@13,64:64='x',<393>,2:21]]}, interface={metric_val=query_column, emp_id=query_column}, def_query4={query_dictionary={metric_val=[[@79,469:478='metric_val',<393>,15:47]], emp_id=[[@73,433:438='emp_id',<393>,15:11]]}, table_dictionary={<[Blend Data].[Branch C I8]>={<insert select col I8>=[[@75,441:441='c',<393>,15:19]], <insert where col I8>=[[@84,526:526='c',<393>,17:8]], emp_id=[[@71,431:431='c',<393>,15:9]]}}, setop=EXCEPT, filters=[{substitution={name=<insert where col I8>, type=column}, table_ref=c}], interface={metric_val=[{substitution={name=<insert select col I8>, type=column}, table_ref=c}], emp_id=[{name=emp_id, table_ref=c}]}, table_alias={c=<[Blend Data].[Branch C I8]>}}, def_query3={def_union2={query_dictionary={metric_val=[[@23,99:99='u',<393>,4:19]], emp_id=[[@19,89:89='u',<393>,4:9]]}, def_query1={query_dictionary={metric_val=[[@57,318:327='metric_val',<393>,10:49]], emp_id=[[@51,282:287='emp_id',<393>,10:13]]}, table_dictionary={<[Blend Data].[Branch B I8]>={<insert select col I8>=[[@53,290:290='b',<393>,10:21]], <insert where col I8>=[[@62,379:379='b',<393>,12:10]], emp_id=[[@49,280:280='b',<393>,10:11]]}}, setop=UNION, filters=[{substitution={name=<insert where col I8>, type=column}, table_ref=b}], interface={metric_val=[{substitution={name=<insert select col I8>, type=column}, table_ref=b}], emp_id=[{name=emp_id, table_ref=b}]}, table_alias={b=<[Blend Data].[Branch B I8]>}}, def_query0={query_dictionary={metric_val=[[@37,170:179='metric_val',<393>,6:49]], emp_id=[[@31,134:139='emp_id',<393>,6:13]]}, table_dictionary={<[Blend Data].[Branch A I8]>={<insert select col I8>=[[@33,142:142='a',<393>,6:21]], <insert where col I8>=[[@42,231:231='a',<393>,8:10]], emp_id=[[@29,132:132='a',<393>,6:11]]}}, filters=[{substitution={name=<insert where col I8>, type=column}, table_ref=a}], interface={metric_val=[{substitution={name=<insert select col I8>, type=column}, table_ref=a}], emp_id=[{name=emp_id, table_ref=a}]}, table_alias={a=<[Blend Data].[Branch A I8]>}}, interface={metric_val=query_column, emp_id=query_column}}, query_dictionary={metric_val=[[@25,101:110='metric_val',<393>,4:21]], emp_id=[[@21,91:96='emp_id',<393>,4:11]]}, interface={metric_val=[{name=metric_val, table_ref=u}], emp_id=[{name=emp_id, table_ref=u}]}, table_alias={u=union2}}}, table_alias={x=union5}}, table_dictionary={employees={score=[[@4,23:27='score',<393>,1:23]], rank_bucket=[[@6,30:40='rank_bucket',<393>,1:30]]}}, interface={score=[{name=metric_val, table_ref=query6}], rank_bucket=[{name=emp_id, table_ref=query6}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void insertComplexSubstitutionI8ExceptIntersectNestedSubquery(){
		final String query = "INSERT INTO employees (score, rank_bucket)"
				+ "\nSELECT x.metric_val, x.emp_id"
				+ "\nFROM ("
				+ "\n  SELECT u.emp_id, u.metric_val"
				+ "\n  FROM ("
				+ "\n    SELECT a.emp_id, a.<insert select col I8> AS metric_val"
				+ "\n    FROM <[Blend Data].[Branch A I8]> a"
				+ "\n    WHERE a.<insert where col I8> > 0"
				+ "\n    EXCEPT"
				+ "\n    SELECT b.emp_id, b.<insert select col I8> AS metric_val"
				+ "\n    FROM <[Blend Data].[Branch B I8]> b"
				+ "\n    WHERE b.<insert where col I8> > 0"
				+ "\n  ) u"
				+ "\n  INTERSECT"
				+ "\n  SELECT c.emp_id, c.<insert select col I8> AS metric_val"
				+ "\n  FROM <[Blend Data].[Branch C I8]> c"
				+ "\n  WHERE c.<insert where col I8> > 0"
				+ "\n) x";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={insert={preamble=insert_into, from={from={table={alias=x, query={intersect={1={select={1={column={name=emp_id, table_ref=u}}, 2={column={name=metric_val, table_ref=u}}}, from={table={alias=u, query={union={1={select={1={column={name=emp_id, table_ref=a}}, 2={column={substitution={name=<insert select col I8>, type=column}, table_ref=a}, alias=metric_val}}, from={table={alias=a, substitution={name=<[Blend Data].[Branch A I8]>, parts={1=[Blend Data], 2=[Branch A I8]}, type=tuple}}}, where={condition={left={column={substitution={name=<insert where col I8>, type=column}, table_ref=a}}, right={literal=0}, operator=>}}}, 2={union={qualifier=null, operator=EXCEPT}}, 3={select={1={column={name=emp_id, table_ref=b}}, 2={column={substitution={name=<insert select col I8>, type=column}, table_ref=b}, alias=metric_val}}, from={table={alias=b, substitution={name=<[Blend Data].[Branch B I8]>, parts={1=[Blend Data], 2=[Branch B I8]}, type=tuple}}}, where={condition={left={column={substitution={name=<insert where col I8>, type=column}, table_ref=b}}, right={literal=0}, operator=>}}}}}}}}, 2={intersect={qualifier=null, operator=INTERSECT}}, 3={select={1={column={name=emp_id, table_ref=c}}, 2={column={substitution={name=<insert select col I8>, type=column}, table_ref=c}, alias=metric_val}}, from={table={alias=c, substitution={name=<[Blend Data].[Branch C I8]>, parts={1=[Blend Data], 2=[Branch C I8]}, type=tuple}}}, where={condition={left={column={substitution={name=<insert where col I8>, type=column}, table_ref=c}}, right={literal=0}, operator=>}}}}}}}, select={1={column={name=metric_val, table_ref=x}}, 2={column={name=emp_id, table_ref=x}}}}, target_table={table={alias=null, table=employees}}, columns={1={column={name=score, table_ref=null}}, 2={column={name=rank_bucket, table_ref=null}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[score, rank_bucket]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<[Blend Data].[Branch B I8]>=tuple, <insert select col I8>=column, <insert where col I8>=column, <[Blend Data].[Branch C I8]>=tuple, <[Blend Data].[Branch A I8]>=tuple}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{<[Blend Data].[Branch B I8]>={<insert select col I8>=[[@53,291:291='b',<393>,10:21]], <insert where col I8>=[[@62,380:380='b',<393>,12:10]], emp_id=[[@49,281:281='b',<393>,10:11]]}, <[Blend Data].[Branch C I8]>={<insert select col I8>=[[@75,445:445='c',<393>,15:19]], <insert where col I8>=[[@84,530:530='c',<393>,17:8]], emp_id=[[@71,435:435='c',<393>,15:9]]}, employees={score=[[@4,23:27='score',<393>,1:23]], rank_bucket=[[@6,30:40='rank_bucket',<393>,1:30]]}, <[Blend Data].[Branch A I8]>={<insert select col I8>=[[@33,142:142='a',<393>,6:21]], <insert where col I8>=[[@42,231:231='a',<393>,8:10]], emp_id=[[@29,132:132='a',<393>,6:11]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{union2={metric_val=[[@23,99:99='u',<393>,4:19]], emp_id=[[@19,89:89='u',<393>,4:9]]}, intersect5={metric_val=[[@9,50:50='x',<393>,2:7]], emp_id=[[@13,64:64='x',<393>,2:21]]}, query4={metric_val=[[@79,473:482='metric_val',<393>,15:47]], emp_id=[[@73,437:442='emp_id',<393>,15:11]]}, insert7={score=[[@4,23:27='score',<393>,1:23]], rank_bucket=[[@6,30:40='rank_bucket',<393>,1:30]]}, query6={metric_val=[[@11,52:61='metric_val',<393>,2:9]], emp_id=[[@15,66:71='emp_id',<393>,2:23]]}, query0={metric_val=[[@37,170:179='metric_val',<393>,6:49]], emp_id=[[@31,134:139='emp_id',<393>,6:13]]}, query1={metric_val=[[@57,319:328='metric_val',<393>,10:49]], emp_id=[[@51,283:288='emp_id',<393>,10:13]]}, query3={metric_val=[[@25,101:110='metric_val',<393>,4:21]], emp_id=[[@21,91:96='emp_id',<393>,4:11]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_insert7={query_dictionary={score=[[@4,23:27='score',<393>,1:23]], rank_bucket=[[@6,30:40='rank_bucket',<393>,1:30]]}, def_query6={query_dictionary={metric_val=[[@11,52:61='metric_val',<393>,2:9]], emp_id=[[@15,66:71='emp_id',<393>,2:23]]}, def_intersect5={query_dictionary={metric_val=[[@9,50:50='x',<393>,2:7]], emp_id=[[@13,64:64='x',<393>,2:21]]}, interface={metric_val=query_column, emp_id=query_column}, def_query4={query_dictionary={metric_val=[[@79,473:482='metric_val',<393>,15:47]], emp_id=[[@73,437:442='emp_id',<393>,15:11]]}, table_dictionary={<[Blend Data].[Branch C I8]>={<insert select col I8>=[[@75,445:445='c',<393>,15:19]], <insert where col I8>=[[@84,530:530='c',<393>,17:8]], emp_id=[[@71,435:435='c',<393>,15:9]]}}, setop=INTERSECTION, filters=[{substitution={name=<insert where col I8>, type=column}, table_ref=c}], interface={metric_val=[{substitution={name=<insert select col I8>, type=column}, table_ref=c}], emp_id=[{name=emp_id, table_ref=c}]}, table_alias={c=<[Blend Data].[Branch C I8]>}}, def_query3={def_union2={query_dictionary={metric_val=[[@23,99:99='u',<393>,4:19]], emp_id=[[@19,89:89='u',<393>,4:9]]}, def_query1={query_dictionary={metric_val=[[@57,319:328='metric_val',<393>,10:49]], emp_id=[[@51,283:288='emp_id',<393>,10:13]]}, table_dictionary={<[Blend Data].[Branch B I8]>={<insert select col I8>=[[@53,291:291='b',<393>,10:21]], <insert where col I8>=[[@62,380:380='b',<393>,12:10]], emp_id=[[@49,281:281='b',<393>,10:11]]}}, setop=EXCEPT, filters=[{substitution={name=<insert where col I8>, type=column}, table_ref=b}], interface={metric_val=[{substitution={name=<insert select col I8>, type=column}, table_ref=b}], emp_id=[{name=emp_id, table_ref=b}]}, table_alias={b=<[Blend Data].[Branch B I8]>}}, def_query0={query_dictionary={metric_val=[[@37,170:179='metric_val',<393>,6:49]], emp_id=[[@31,134:139='emp_id',<393>,6:13]]}, table_dictionary={<[Blend Data].[Branch A I8]>={<insert select col I8>=[[@33,142:142='a',<393>,6:21]], <insert where col I8>=[[@42,231:231='a',<393>,8:10]], emp_id=[[@29,132:132='a',<393>,6:11]]}}, filters=[{substitution={name=<insert where col I8>, type=column}, table_ref=a}], interface={metric_val=[{substitution={name=<insert select col I8>, type=column}, table_ref=a}], emp_id=[{name=emp_id, table_ref=a}]}, table_alias={a=<[Blend Data].[Branch A I8]>}}, interface={metric_val=query_column, emp_id=query_column}}, query_dictionary={metric_val=[[@25,101:110='metric_val',<393>,4:21]], emp_id=[[@21,91:96='emp_id',<393>,4:11]]}, interface={metric_val=[{name=metric_val, table_ref=u}], emp_id=[{name=emp_id, table_ref=u}]}, table_alias={u=union2}}}, interface={metric_val=[{name=metric_val, table_ref=x}], emp_id=[{name=emp_id, table_ref=x}]}, table_alias={x=intersect5}}, table_dictionary={employees={score=[[@4,23:27='score',<393>,1:23]], rank_bucket=[[@6,30:40='rank_bucket',<393>,1:30]]}}, interface={score=[{name=metric_val, table_ref=query6}], rank_bucket=[{name=emp_id, table_ref=query6}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void insertComplexSubstitutionI9WithCteSelfUnionBranches() {
		final String query = "WITH blended AS ("
				+ "\n  SELECT a.emp_id, a.<insert select col I9a> AS metric_val"
				+ "\n  FROM <[Union Data].[Feed Alpha I9]> a"
				+ "\n  WHERE a.<insert where col I9a> > 0"
				+ "\n  UNION"
				+ "\n  SELECT b.emp_id, b.<insert select col I9b> AS metric_val"
				+ "\n  FROM <[Union Data].[Feed Beta I9]> b"
				+ "\n  WHERE b.<insert where col I9b> > 0"
				+ "\n)"
				+ "\nINSERT INTO employees (score, rank_bucket)"
				+ "\nSELECT bl.metric_val, bl.emp_id"
				+ "\nFROM blended bl"
				+ "\nWHERE bl.metric_val > 0";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={with={1={cte={union={1={select={1={column={name=emp_id, table_ref=a}}, 2={column={substitution={name=<insert select col I9a>, type=column}, table_ref=a}, alias=metric_val}}, from={table={alias=a, substitution={name=<[Union Data].[Feed Alpha I9]>, parts={1=[Union Data], 2=[Feed Alpha I9]}, type=tuple}}}, where={condition={left={column={substitution={name=<insert where col I9a>, type=column}, table_ref=a}}, right={literal=0}, operator=>}}}, 2={union={qualifier=null, operator=UNION}}, 3={select={1={column={name=emp_id, table_ref=b}}, 2={column={substitution={name=<insert select col I9b>, type=column}, table_ref=b}, alias=metric_val}}, from={table={alias=b, substitution={name=<[Union Data].[Feed Beta I9]>, parts={1=[Union Data], 2=[Feed Beta I9]}, type=tuple}}}, where={condition={left={column={substitution={name=<insert where col I9b>, type=column}, table_ref=b}}, right={literal=0}, operator=>}}}}}, alias=blended}}, query={insert={preamble=insert_into, from={from={table={alias=bl, table=blended}}, where={condition={left={column={name=metric_val, table_ref=bl}}, right={literal=0}, operator=>}}, select={1={column={name=metric_val, table_ref=bl}}, 2={column={name=emp_id, table_ref=bl}}}}, target_table={table={alias=null, table=employees}}, columns={1={column={name=score, table_ref=null}}, 2={column={name=rank_bucket, table_ref=null}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[score, rank_bucket]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<insert select col I9a>=column, <insert where col I9b>=column, <insert where col I9a>=column, <insert select col I9b>=column, <[Union Data].[Feed Beta I9]>=tuple, <[Union Data].[Feed Alpha I9]>=tuple}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{<[Union Data].[Feed Beta I9]>={<insert where col I9b>=[[@38,268:268='b',<393>,8:8]], <insert select col I9b>=[[@29,181:181='b',<393>,6:19]], emp_id=[[@25,171:171='b',<393>,6:9]]}, <[Union Data].[Feed Alpha I9]>={<insert select col I9a>=[[@9,37:37='a',<393>,2:19]], <insert where col I9a>=[[@18,125:125='a',<393>,4:8]], emp_id=[[@5,27:27='a',<393>,2:9]]}, employees={score=[[@48,322:326='score',<393>,10:23]], rank_bucket=[[@50,329:339='rank_bucket',<393>,10:30]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{union2={metric_val=[[@53,349:350='bl',<393>,11:7], [@64,396:397='bl',<393>,13:6]], emp_id=[[@57,364:365='bl',<393>,11:22]]}, query0={metric_val=[[@13,66:75='metric_val',<393>,2:48]], emp_id=[[@7,29:34='emp_id',<393>,2:11]]}, insert4={score=[[@48,322:326='score',<393>,10:23]], rank_bucket=[[@50,329:339='rank_bucket',<393>,10:30]]}, query1={metric_val=[[@33,210:219='metric_val',<393>,6:48]], emp_id=[[@27,173:178='emp_id',<393>,6:11]]}, query3={metric_val=[[@55,352:361='metric_val',<393>,11:10]], emp_id=[[@59,367:372='emp_id',<393>,11:25]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_insert4={context_list={blended=union2}, query_dictionary={score=[[@48,322:326='score',<393>,10:23]], rank_bucket=[[@50,329:339='rank_bucket',<393>,10:30]]}, def_union2={query_dictionary={metric_val=[[@53,349:350='bl',<393>,11:7], [@64,396:397='bl',<393>,13:6]], emp_id=[[@57,364:365='bl',<393>,11:22]]}, def_query1={query_dictionary={metric_val=[[@33,210:219='metric_val',<393>,6:48]], emp_id=[[@27,173:178='emp_id',<393>,6:11]]}, table_dictionary={<[Union Data].[Feed Beta I9]>={<insert where col I9b>=[[@38,268:268='b',<393>,8:8]], <insert select col I9b>=[[@29,181:181='b',<393>,6:19]], emp_id=[[@25,171:171='b',<393>,6:9]]}}, setop=UNION, filters=[{substitution={name=<insert where col I9b>, type=column}, table_ref=b}], interface={metric_val=[{substitution={name=<insert select col I9b>, type=column}, table_ref=b}], emp_id=[{name=emp_id, table_ref=b}]}, table_alias={b=<[Union Data].[Feed Beta I9]>}}, def_query0={query_dictionary={metric_val=[[@13,66:75='metric_val',<393>,2:48]], emp_id=[[@7,29:34='emp_id',<393>,2:11]]}, table_dictionary={<[Union Data].[Feed Alpha I9]>={<insert select col I9a>=[[@9,37:37='a',<393>,2:19]], <insert where col I9a>=[[@18,125:125='a',<393>,4:8]], emp_id=[[@5,27:27='a',<393>,2:9]]}}, filters=[{substitution={name=<insert where col I9a>, type=column}, table_ref=a}], interface={metric_val=[{substitution={name=<insert select col I9a>, type=column}, table_ref=a}], emp_id=[{name=emp_id, table_ref=a}]}, table_alias={a=<[Union Data].[Feed Alpha I9]>}}, interface={metric_val=query_column, emp_id=query_column}}, table_dictionary={employees={score=[[@48,322:326='score',<393>,10:23]], rank_bucket=[[@50,329:339='rank_bucket',<393>,10:30]]}}, interface={score=[{name=metric_val, table_ref=query3}], rank_bucket=[{name=emp_id, table_ref=query3}]}, def_query3={context_list={blended=union2, bl=union2}, query_dictionary={metric_val=[[@55,352:361='metric_val',<393>,11:10]], emp_id=[[@59,367:372='emp_id',<393>,11:25]]}, filters=[{name=metric_val, table_ref=bl}], interface={metric_val=[{name=metric_val, table_ref=bl}], emp_id=[{name=emp_id, table_ref=bl}]}, table_alias={blended=union2, bl=union2}}, table_alias={blended=union2}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void insertComplexSubstitutionI9WithCteSelfExceptBranches(){
		final String query = "WITH blended AS ("
				+ "\n  SELECT a.emp_id, a.<insert select col I9a> AS metric_val"
				+ "\n  FROM <[except Data].[Feed Alpha I9]> a"
				+ "\n  WHERE a.<insert where col I9a> > 0"
				+ "\n  EXCEPT"
				+ "\n  SELECT b.emp_id, b.<insert select col I9b> AS metric_val"
				+ "\n  FROM <[except Data].[Feed Beta I9]> b"
				+ "\n  WHERE b.<insert where col I9b> > 0"
				+ "\n)"
				+ "\nINSERT INTO employees (score, rank_bucket)"
				+ "\nSELECT bl.metric_val, bl.emp_id"
				+ "\nFROM blended bl"
				+ "\nWHERE bl.metric_val > 0";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={with={1={cte={union={1={select={1={column={name=emp_id, table_ref=a}}, 2={column={substitution={name=<insert select col I9a>, type=column}, table_ref=a}, alias=metric_val}}, from={table={alias=a, substitution={name=<[except Data].[Feed Alpha I9]>, parts={1=[except Data], 2=[Feed Alpha I9]}, type=tuple}}}, where={condition={left={column={substitution={name=<insert where col I9a>, type=column}, table_ref=a}}, right={literal=0}, operator=>}}}, 2={union={qualifier=null, operator=EXCEPT}}, 3={select={1={column={name=emp_id, table_ref=b}}, 2={column={substitution={name=<insert select col I9b>, type=column}, table_ref=b}, alias=metric_val}}, from={table={alias=b, substitution={name=<[except Data].[Feed Beta I9]>, parts={1=[except Data], 2=[Feed Beta I9]}, type=tuple}}}, where={condition={left={column={substitution={name=<insert where col I9b>, type=column}, table_ref=b}}, right={literal=0}, operator=>}}}}}, alias=blended}}, query={insert={preamble=insert_into, from={from={table={alias=bl, table=blended}}, where={condition={left={column={name=metric_val, table_ref=bl}}, right={literal=0}, operator=>}}, select={1={column={name=metric_val, table_ref=bl}}, 2={column={name=emp_id, table_ref=bl}}}}, target_table={table={alias=null, table=employees}}, columns={1={column={name=score, table_ref=null}}, 2={column={name=rank_bucket, table_ref=null}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[score, rank_bucket]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<insert select col I9a>=column, <insert where col I9b>=column, <insert where col I9a>=column, <insert select col I9b>=column, <[except Data].[Feed Beta I9]>=tuple, <[except Data].[Feed Alpha I9]>=tuple}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{<[except Data].[Feed Beta I9]>={<insert where col I9b>=[[@38,271:271='b',<393>,8:8]], <insert select col I9b>=[[@29,183:183='b',<393>,6:19]], emp_id=[[@25,173:173='b',<393>,6:9]]}, <[except Data].[Feed Alpha I9]>={<insert select col I9a>=[[@9,37:37='a',<393>,2:19]], <insert where col I9a>=[[@18,126:126='a',<393>,4:8]], emp_id=[[@5,27:27='a',<393>,2:9]]}, employees={score=[[@48,325:329='score',<393>,10:23]], rank_bucket=[[@50,332:342='rank_bucket',<393>,10:30]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{union2={metric_val=[[@53,352:353='bl',<393>,11:7], [@64,399:400='bl',<393>,13:6]], emp_id=[[@57,367:368='bl',<393>,11:22]]}, query0={metric_val=[[@13,66:75='metric_val',<393>,2:48]], emp_id=[[@7,29:34='emp_id',<393>,2:11]]}, insert4={score=[[@48,325:329='score',<393>,10:23]], rank_bucket=[[@50,332:342='rank_bucket',<393>,10:30]]}, query1={metric_val=[[@33,212:221='metric_val',<393>,6:48]], emp_id=[[@27,175:180='emp_id',<393>,6:11]]}, query3={metric_val=[[@55,355:364='metric_val',<393>,11:10]], emp_id=[[@59,370:375='emp_id',<393>,11:25]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_insert4={context_list={blended=union2}, query_dictionary={score=[[@48,325:329='score',<393>,10:23]], rank_bucket=[[@50,332:342='rank_bucket',<393>,10:30]]}, def_union2={query_dictionary={metric_val=[[@53,352:353='bl',<393>,11:7], [@64,399:400='bl',<393>,13:6]], emp_id=[[@57,367:368='bl',<393>,11:22]]}, def_query1={query_dictionary={metric_val=[[@33,212:221='metric_val',<393>,6:48]], emp_id=[[@27,175:180='emp_id',<393>,6:11]]}, table_dictionary={<[except Data].[Feed Beta I9]>={<insert where col I9b>=[[@38,271:271='b',<393>,8:8]], <insert select col I9b>=[[@29,183:183='b',<393>,6:19]], emp_id=[[@25,173:173='b',<393>,6:9]]}}, setop=EXCEPT, filters=[{substitution={name=<insert where col I9b>, type=column}, table_ref=b}], interface={metric_val=[{substitution={name=<insert select col I9b>, type=column}, table_ref=b}], emp_id=[{name=emp_id, table_ref=b}]}, table_alias={b=<[except Data].[Feed Beta I9]>}}, def_query0={query_dictionary={metric_val=[[@13,66:75='metric_val',<393>,2:48]], emp_id=[[@7,29:34='emp_id',<393>,2:11]]}, table_dictionary={<[except Data].[Feed Alpha I9]>={<insert select col I9a>=[[@9,37:37='a',<393>,2:19]], <insert where col I9a>=[[@18,126:126='a',<393>,4:8]], emp_id=[[@5,27:27='a',<393>,2:9]]}}, filters=[{substitution={name=<insert where col I9a>, type=column}, table_ref=a}], interface={metric_val=[{substitution={name=<insert select col I9a>, type=column}, table_ref=a}], emp_id=[{name=emp_id, table_ref=a}]}, table_alias={a=<[except Data].[Feed Alpha I9]>}}, interface={metric_val=query_column, emp_id=query_column}}, table_dictionary={employees={score=[[@48,325:329='score',<393>,10:23]], rank_bucket=[[@50,332:342='rank_bucket',<393>,10:30]]}}, interface={score=[{name=metric_val, table_ref=query3}], rank_bucket=[{name=emp_id, table_ref=query3}]}, def_query3={context_list={blended=union2, bl=union2}, query_dictionary={metric_val=[[@55,355:364='metric_val',<393>,11:10]], emp_id=[[@59,370:375='emp_id',<393>,11:25]]}, filters=[{name=metric_val, table_ref=bl}], interface={metric_val=[{name=metric_val, table_ref=bl}], emp_id=[{name=emp_id, table_ref=bl}]}, table_alias={blended=union2, bl=union2}}, table_alias={blended=union2}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void insertComplexSubstitutionI10SubqueryGroupByHavingQualifyCombined() {
		final String query = "INSERT INTO employees (agg_score, rank_bucket)"
				+ "\nSELECT src.total_score, src.emp_id"
				+ "\nFROM ("
				+ "\n  SELECT a.emp_id, sum(a.<insert select col I10>) AS total_score,"
				+ "\n         row_number() OVER (PARTITION BY a.emp_id ORDER BY a.<insert order col I10> DESC) AS rn"
				+ "\n  FROM <[Agg Data].[Fact Table I10]> a"
				+ "\n  WHERE a.<insert where col I10> > 0"
				+ "\n  GROUP BY a.emp_id, a.<insert group col I10>"
				+ "\n  HAVING sum(a.<insert select col I10>) > 0"
				+ "\n  QUALIFY rn = 1"
				+ "\n) src";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={insert={preamble=insert_into, from={from={table={alias=src, query={select={1={column={name=emp_id, table_ref=a}}, 2={function={function_name=sum, qualifier=null, parameters={column={substitution={name=<insert select col I10>, type=column}, table_ref=a}}}, alias=total_score}, 3={alias=rn, window_function={over={partition_by={1={column={name=emp_id, table_ref=a}}}, orderby={1={null_order=null, predicand={column={substitution={name=<insert order col I10>, type=column}, table_ref=a}}, sort_order=DESC}}}, function={function_name=row_number, parameters=null}}}}, having={condition={left={function={function_name=sum, qualifier=null, parameters={column={substitution={name=<insert select col I10>, type=column}, table_ref=a}}}}, right={literal=0}, operator=>}}, from={table={alias=a, substitution={name=<[Agg Data].[Fact Table I10]>, parts={1=[Agg Data], 2=[Fact Table I10]}, type=tuple}}}, where={condition={left={column={substitution={name=<insert where col I10>, type=column}, table_ref=a}}, right={literal=0}, operator=>}}, groupby={1={column={name=emp_id, table_ref=a}}, 2={column={substitution={name=<insert group col I10>, type=column}, table_ref=a}}}, qualify={condition={left={column={name=rn, table_ref=null}}, right={literal=1}, operator==}}}}}, select={1={column={name=total_score, table_ref=src}}, 2={column={name=emp_id, table_ref=src}}}}, target_table={table={alias=null, table=employees}}, columns={1={column={name=agg_score, table_ref=null}}, 2={column={name=rank_bucket, table_ref=null}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[rank_bucket, agg_score]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<[Agg Data].[Fact Table I10]>=tuple, <insert where col I10>=column, <insert group col I10>=column, <insert select col I10>=column, <insert order col I10>=column}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{<[Agg Data].[Fact Table I10]>={<insert where col I10>=[[@55,298:298='a',<393>,7:8]], <insert group col I10>=[[@66,348:348='a',<393>,8:21]], <insert select col I10>=[[@25,112:112='a',<393>,4:23], [@72,386:386='a',<393>,9:13]], <insert order col I10>=[[@44,214:214='a',<393>,5:59]], emp_id=[[@19,98:98='a',<393>,4:9], [@39,196:196='a',<393>,5:41], [@62,338:338='a',<393>,8:11]]}, employees={rank_bucket=[[@6,34:44='rank_bucket',<393>,1:34]], agg_score=[[@4,23:31='agg_score',<393>,1:23]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={total_score=[[@30,142:152='total_score',<393>,4:53], [@9,54:56='src',<393>,2:7]], rn=[[@50,248:249='rn',<393>,5:93], [@79,427:428='rn',<393>,10:10]], emp_id=[[@21,100:105='emp_id',<393>,4:11], [@13,71:73='src',<393>,2:24]]}, query1={total_score=[[@11,58:68='total_score',<393>,2:11]], emp_id=[[@15,75:80='emp_id',<393>,2:28]]}, insert2={agg_score=[[@4,23:31='agg_score',<393>,1:23]], rank_bucket=[[@6,34:44='rank_bucket',<393>,1:34]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_insert2={query_dictionary={agg_score=[[@4,23:31='agg_score',<393>,1:23]], rank_bucket=[[@6,34:44='rank_bucket',<393>,1:34]]}, table_dictionary={employees={rank_bucket=[[@6,34:44='rank_bucket',<393>,1:34]], agg_score=[[@4,23:31='agg_score',<393>,1:23]]}}, def_query1={query_dictionary={total_score=[[@11,58:68='total_score',<393>,2:11]], emp_id=[[@15,75:80='emp_id',<393>,2:28]]}, def_query0={window_ordered_by=[{substitution={name=<insert order col I10>, type=column}, table_ref=a}], query_dictionary={total_score=[[@30,142:152='total_score',<393>,4:53], [@9,54:56='src',<393>,2:7]], rn=[[@50,248:249='rn',<393>,5:93], [@79,427:428='rn',<393>,10:10]], emp_id=[[@21,100:105='emp_id',<393>,4:11], [@13,71:73='src',<393>,2:24]]}, table_dictionary={<[Agg Data].[Fact Table I10]>={<insert where col I10>=[[@55,298:298='a',<393>,7:8]], <insert group col I10>=[[@66,348:348='a',<393>,8:21]], <insert select col I10>=[[@25,112:112='a',<393>,4:23], [@72,386:386='a',<393>,9:13]], <insert order col I10>=[[@44,214:214='a',<393>,5:59]], emp_id=[[@19,98:98='a',<393>,4:9], [@39,196:196='a',<393>,5:41], [@62,338:338='a',<393>,8:11]]}}, grouped_by=[{name=emp_id, table_ref=a}, {substitution={name=<insert group col I10>, type=column}, table_ref=a}], window_partition_by=[{name=emp_id, table_ref=a}], filters=[{substitution={name=<insert where col I10>, type=column}, table_ref=a}, {substitution={name=<insert select col I10>, type=column}, table_ref=a}, {name=rn, table_ref=query0}], interface={total_score=[{substitution={name=<insert select col I10>, type=column}, table_ref=a}], rn=[{name=emp_id, table_ref=a}, {substitution={name=<insert order col I10>, type=column}, table_ref=a}], emp_id=[{name=emp_id, table_ref=a}]}, table_alias={a=<[Agg Data].[Fact Table I10]>}}, interface={total_score=[{name=total_score, table_ref=src}], emp_id=[{name=emp_id, table_ref=src}]}, table_alias={src=query0}}, interface={agg_score=[{name=total_score, table_ref=query1}], rank_bucket=[{name=emp_id, table_ref=query1}]}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void updateComplexSubstitutionU1WithCteGroupByHaving() {
		final String query = "WITH staged AS ("
				+ "\n  SELECT a.emp_id, sum(a.<update select col U1>) AS total_score"
				+ "\n  FROM <[HR Data].[Employee Accounts U1]> a"
				+ "\n  WHERE a.<update where col U1> > 0"
				+ "\n  GROUP BY a.emp_id, a.<update group col U1>"
				+ "\n  HAVING sum(a.<update select col U1>) > 0)"
				+ "\nUPDATE employees e"
				+ "\nSET score = s.total_score"
				+ "\nFROM staged s"
				+ "\nWHERE e.emp_id = s.emp_id AND s.total_score > 0";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={with={1={cte={select={1={column={name=emp_id, table_ref=a}}, 2={function={function_name=sum, qualifier=null, parameters={column={substitution={name=<update select col U1>, type=column}, table_ref=a}}}, alias=total_score}}, having={condition={left={function={function_name=sum, qualifier=null, parameters={column={substitution={name=<update select col U1>, type=column}, table_ref=a}}}}, right={literal=0}, operator=>}}, from={table={alias=a, substitution={name=<[HR Data].[Employee Accounts U1]>, parts={1=[HR Data], 2=[Employee Accounts U1]}, type=tuple}}}, where={condition={left={column={substitution={name=<update where col U1>, type=column}, table_ref=a}}, right={literal=0}, operator=>}}, groupby={1={column={name=emp_id, table_ref=a}}, 2={column={substitution={name=<update group col U1>, type=column}, table_ref=a}}}}, alias=staged}}, query={update={from={table={alias=s, table=staged}}, where={and={1={condition={left={column={name=emp_id, table_ref=e}}, right={column={name=emp_id, table_ref=s}}, operator==}}, 2={condition={left={column={name=total_score, table_ref=s}}, right={literal=0}, operator=>}}}}, assignments={1={set={column={name=score, table_ref=null}}, to={column={name=total_score, table_ref=s}}}}, table={alias=e, table=employees}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[score]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<[HR Data].[Employee Accounts U1]>=tuple, <update group col U1>=column, <update where col U1>=column, <update select col U1>=column}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{<[HR Data].[Employee Accounts U1]>={<update group col U1>=[[@32,182:182='a',<393>,5:21]], <update where col U1>=[[@21,133:133='a',<393>,4:8]], <update select col U1>=[[@11,40:40='a',<393>,2:23], [@38,219:219='a',<393>,6:13]], emp_id=[[@5,26:26='a',<393>,2:9], [@28,172:172='a',<393>,5:11]]}, employees={score=[[@49,273:277='score',<393>,8:4]], emp_id=[[@58,315:315='e',<393>,10:6]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={total_score=[[@16,69:79='total_score',<393>,2:52], [@51,281:281='s',<393>,8:12], [@66,339:339='s',<393>,10:30]], emp_id=[[@7,28:33='emp_id',<393>,2:11], [@62,326:326='s',<393>,10:17]]}, update1={score=[[@49,273:277='score',<393>,8:4]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_update1={context_list={s=query0, staged=query0}, assignments={score=[{name=total_score, table_ref=s}]}, table_dictionary={employees={score=[[@49,273:277='score',<393>,8:4]], emp_id=[[@58,315:315='e',<393>,10:6]]}}, update_dictionary={score=[[@49,273:277='score',<393>,8:4]]}, def_query0={query_dictionary={total_score=[[@16,69:79='total_score',<393>,2:52], [@51,281:281='s',<393>,8:12], [@66,339:339='s',<393>,10:30]], emp_id=[[@7,28:33='emp_id',<393>,2:11], [@62,326:326='s',<393>,10:17]]}, table_dictionary={<[HR Data].[Employee Accounts U1]>={<update group col U1>=[[@32,182:182='a',<393>,5:21]], <update where col U1>=[[@21,133:133='a',<393>,4:8]], <update select col U1>=[[@11,40:40='a',<393>,2:23], [@38,219:219='a',<393>,6:13]], emp_id=[[@5,26:26='a',<393>,2:9], [@28,172:172='a',<393>,5:11]]}}, grouped_by=[{name=emp_id, table_ref=a}, {substitution={name=<update group col U1>, type=column}, table_ref=a}], filters=[{substitution={name=<update where col U1>, type=column}, table_ref=a}, {substitution={name=<update select col U1>, type=column}, table_ref=a}], interface={total_score=[{substitution={name=<update select col U1>, type=column}, table_ref=a}], emp_id=[{name=emp_id, table_ref=a}]}, table_alias={a=<[HR Data].[Employee Accounts U1]>}}, filters=[{name=emp_id, table_ref=e}, {name=emp_id, table_ref=s}, {name=total_score, table_ref=s}], table_alias={s=query0, e=employees, staged=query0}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void updateComplexSubstitutionU2SubqueryUnionWhereSubstitutions() {
		final String query = "UPDATE employees e"
				+ "\nSET score = src.metric_val, rank_bucket = src.emp_id"
				+ "\nFROM ("
				+ "\n  SELECT a.emp_id, a.<update select col U2> AS metric_val"
				+ "\n  FROM <[Sales Data].[Perf Feed U2]> a"
				+ "\n  WHERE a.<update where col U2> > 0"
				+ "\n  UNION"
				+ "\n  SELECT b.dept_id, b.<update select col U2b> AS metric_val"
				+ "\n  FROM <[Sales Data].[Quota Feed U2]> b"
				+ "\n  WHERE b.<update where col U2b> > 0"
				+ "\n) src"
				+ "\nWHERE e.emp_id = src.emp_id";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={update={from={table={alias=src, query={union={1={select={1={column={name=emp_id, table_ref=a}}, 2={column={substitution={name=<update select col U2>, type=column}, table_ref=a}, alias=metric_val}}, from={table={alias=a, substitution={name=<[Sales Data].[Perf Feed U2]>, parts={1=[Sales Data], 2=[Perf Feed U2]}, type=tuple}}}, where={condition={left={column={substitution={name=<update where col U2>, type=column}, table_ref=a}}, right={literal=0}, operator=>}}}, 2={union={qualifier=null, operator=UNION}}, 3={select={1={column={name=dept_id, table_ref=b}}, 2={column={substitution={name=<update select col U2b>, type=column}, table_ref=b}, alias=metric_val}}, from={table={alias=b, substitution={name=<[Sales Data].[Quota Feed U2]>, parts={1=[Sales Data], 2=[Quota Feed U2]}, type=tuple}}}, where={condition={left={column={substitution={name=<update where col U2b>, type=column}, table_ref=b}}, right={literal=0}, operator=>}}}}}}}, where={condition={left={column={name=emp_id, table_ref=e}}, right={column={name=emp_id, table_ref=src}}, operator==}}, assignments={1={set={column={name=score, table_ref=null}}, to={column={name=metric_val, table_ref=src}}}, 2={set={column={name=rank_bucket, table_ref=null}}, to={column={name=emp_id, table_ref=src}}}}, table={alias=e, table=employees}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[score, rank_bucket]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<update where col U2b>=column, <[Sales Data].[Quota Feed U2]>=tuple, <[Sales Data].[Perf Feed U2]>=tuple, <update where col U2>=column, <update select col U2>=column, <update select col U2b>=column}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{<[Sales Data].[Quota Feed U2]>={<update where col U2b>=[[@51,328:328='b',<393>,10:8]], dept_id=[[@38,229:229='b',<393>,8:9]], <update select col U2b>=[[@42,240:240='b',<393>,8:20]]}, employees={score=[[@4,23:27='score',<393>,2:4]], rank_bucket=[[@10,47:57='rank_bucket',<393>,2:28]], emp_id=[[@59,369:369='e',<393>,12:6]]}, <[Sales Data].[Perf Feed U2]>={<update where col U2>=[[@31,184:184='a',<393>,6:8]], emp_id=[[@18,88:88='a',<393>,4:9]], <update select col U2>=[[@22,98:98='a',<393>,4:19]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{union2={metric_val=[[@6,31:33='src',<393>,2:12]], emp_id=[[@12,61:63='src',<393>,2:42], [@63,380:382='src',<393>,12:17]]}, query0={metric_val=[[@26,126:135='metric_val',<393>,4:47]], emp_id=[[@20,90:95='emp_id',<393>,4:11]]}, update3={score=[[@4,23:27='score',<393>,2:4]], rank_bucket=[[@10,47:57='rank_bucket',<393>,2:28]]}, query1={dept_id=[[@40,231:237='dept_id',<393>,8:11]], metric_val=[[@46,269:278='metric_val',<393>,8:49]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_update3={assignments={score=[{name=metric_val, table_ref=src}], rank_bucket=[{name=emp_id, table_ref=src}]}, def_union2={query_dictionary={metric_val=[[@6,31:33='src',<393>,2:12]], emp_id=[[@12,61:63='src',<393>,2:42], [@63,380:382='src',<393>,12:17]]}, def_query1={query_dictionary={metric_val=[[@46,269:278='metric_val',<393>,8:49]], dept_id=[[@40,231:237='dept_id',<393>,8:11]]}, table_dictionary={<[Sales Data].[Quota Feed U2]>={<update where col U2b>=[[@51,328:328='b',<393>,10:8]], dept_id=[[@38,229:229='b',<393>,8:9]], <update select col U2b>=[[@42,240:240='b',<393>,8:20]]}}, setop=UNION, filters=[{substitution={name=<update where col U2b>, type=column}, table_ref=b}], interface={metric_val=[{substitution={name=<update select col U2b>, type=column}, table_ref=b}], dept_id=[{name=dept_id, table_ref=b}]}, table_alias={b=<[Sales Data].[Quota Feed U2]>}}, def_query0={query_dictionary={metric_val=[[@26,126:135='metric_val',<393>,4:47]], emp_id=[[@20,90:95='emp_id',<393>,4:11]]}, table_dictionary={<[Sales Data].[Perf Feed U2]>={<update where col U2>=[[@31,184:184='a',<393>,6:8]], emp_id=[[@18,88:88='a',<393>,4:9]], <update select col U2>=[[@22,98:98='a',<393>,4:19]]}}, filters=[{substitution={name=<update where col U2>, type=column}, table_ref=a}], interface={metric_val=[{substitution={name=<update select col U2>, type=column}, table_ref=a}], emp_id=[{name=emp_id, table_ref=a}]}, table_alias={a=<[Sales Data].[Perf Feed U2]>}}, interface={metric_val=query_column, emp_id=query_column}}, table_dictionary={employees={score=[[@4,23:27='score',<393>,2:4]], rank_bucket=[[@10,47:57='rank_bucket',<393>,2:28]], emp_id=[[@59,369:369='e',<393>,12:6]]}}, update_dictionary={score=[[@4,23:27='score',<393>,2:4]], rank_bucket=[[@10,47:57='rank_bucket',<393>,2:28]]}, filters=[{name=emp_id, table_ref=e}, {name=emp_id, table_ref=src}], table_alias={e=employees, src=union2}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void updateComplexSubstitutionU2SubqueryExceptWhereSubstitutions(){
		final String query = "UPDATE employees e"
				+ "\nSET score = src.metric_val, rank_bucket = src.emp_id"
				+ "\nFROM ("
				+ "\n  SELECT a.emp_id, a.<update select col U2> AS metric_val"
				+ "\n  FROM <[Sales Data].[Perf Feed U2]> a"
				+ "\n  WHERE a.<update where col U2> > 0"
				+ "\n  EXCEPT"
				+ "\n  SELECT b.dept_id, b.<update select col U2b> AS metric_val"
				+ "\n  FROM <[Sales Data].[Quota Feed U2]> b"
				+ "\n  WHERE b.<update where col U2b> > 0"
				+ "\n) src"
				+ "\nWHERE e.emp_id = src.emp_id";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={update={from={table={alias=src, query={union={1={select={1={column={name=emp_id, table_ref=a}}, 2={column={substitution={name=<update select col U2>, type=column}, table_ref=a}, alias=metric_val}}, from={table={alias=a, substitution={name=<[Sales Data].[Perf Feed U2]>, parts={1=[Sales Data], 2=[Perf Feed U2]}, type=tuple}}}, where={condition={left={column={substitution={name=<update where col U2>, type=column}, table_ref=a}}, right={literal=0}, operator=>}}}, 2={union={qualifier=null, operator=EXCEPT}}, 3={select={1={column={name=dept_id, table_ref=b}}, 2={column={substitution={name=<update select col U2b>, type=column}, table_ref=b}, alias=metric_val}}, from={table={alias=b, substitution={name=<[Sales Data].[Quota Feed U2]>, parts={1=[Sales Data], 2=[Quota Feed U2]}, type=tuple}}}, where={condition={left={column={substitution={name=<update where col U2b>, type=column}, table_ref=b}}, right={literal=0}, operator=>}}}}}}}, where={condition={left={column={name=emp_id, table_ref=e}}, right={column={name=emp_id, table_ref=src}}, operator==}}, assignments={1={set={column={name=score, table_ref=null}}, to={column={name=metric_val, table_ref=src}}}, 2={set={column={name=rank_bucket, table_ref=null}}, to={column={name=emp_id, table_ref=src}}}}, table={alias=e, table=employees}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[score, rank_bucket]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<update where col U2b>=column, <[Sales Data].[Quota Feed U2]>=tuple, <[Sales Data].[Perf Feed U2]>=tuple, <update where col U2>=column, <update select col U2>=column, <update select col U2b>=column}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{<[Sales Data].[Quota Feed U2]>={<update where col U2b>=[[@51,329:329='b',<393>,10:8]], dept_id=[[@38,230:230='b',<393>,8:9]], <update select col U2b>=[[@42,241:241='b',<393>,8:20]]}, employees={score=[[@4,23:27='score',<393>,2:4]], rank_bucket=[[@10,47:57='rank_bucket',<393>,2:28]], emp_id=[[@59,370:370='e',<393>,12:6]]}, <[Sales Data].[Perf Feed U2]>={<update where col U2>=[[@31,184:184='a',<393>,6:8]], emp_id=[[@18,88:88='a',<393>,4:9]], <update select col U2>=[[@22,98:98='a',<393>,4:19]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{union2={metric_val=[[@6,31:33='src',<393>,2:12]], emp_id=[[@12,61:63='src',<393>,2:42], [@63,381:383='src',<393>,12:17]]}, query0={metric_val=[[@26,126:135='metric_val',<393>,4:47]], emp_id=[[@20,90:95='emp_id',<393>,4:11]]}, update3={score=[[@4,23:27='score',<393>,2:4]], rank_bucket=[[@10,47:57='rank_bucket',<393>,2:28]]}, query1={dept_id=[[@40,232:238='dept_id',<393>,8:11]], metric_val=[[@46,270:279='metric_val',<393>,8:49]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_update3={assignments={score=[{name=metric_val, table_ref=src}], rank_bucket=[{name=emp_id, table_ref=src}]}, def_union2={query_dictionary={metric_val=[[@6,31:33='src',<393>,2:12]], emp_id=[[@12,61:63='src',<393>,2:42], [@63,381:383='src',<393>,12:17]]}, def_query1={query_dictionary={metric_val=[[@46,270:279='metric_val',<393>,8:49]], dept_id=[[@40,232:238='dept_id',<393>,8:11]]}, table_dictionary={<[Sales Data].[Quota Feed U2]>={<update where col U2b>=[[@51,329:329='b',<393>,10:8]], dept_id=[[@38,230:230='b',<393>,8:9]], <update select col U2b>=[[@42,241:241='b',<393>,8:20]]}}, setop=EXCEPT, filters=[{substitution={name=<update where col U2b>, type=column}, table_ref=b}], interface={metric_val=[{substitution={name=<update select col U2b>, type=column}, table_ref=b}], dept_id=[{name=dept_id, table_ref=b}]}, table_alias={b=<[Sales Data].[Quota Feed U2]>}}, def_query0={query_dictionary={metric_val=[[@26,126:135='metric_val',<393>,4:47]], emp_id=[[@20,90:95='emp_id',<393>,4:11]]}, table_dictionary={<[Sales Data].[Perf Feed U2]>={<update where col U2>=[[@31,184:184='a',<393>,6:8]], emp_id=[[@18,88:88='a',<393>,4:9]], <update select col U2>=[[@22,98:98='a',<393>,4:19]]}}, filters=[{substitution={name=<update where col U2>, type=column}, table_ref=a}], interface={metric_val=[{substitution={name=<update select col U2>, type=column}, table_ref=a}], emp_id=[{name=emp_id, table_ref=a}]}, table_alias={a=<[Sales Data].[Perf Feed U2]>}}, interface={metric_val=query_column, emp_id=query_column}}, table_dictionary={employees={score=[[@4,23:27='score',<393>,2:4]], rank_bucket=[[@10,47:57='rank_bucket',<393>,2:28]], emp_id=[[@59,370:370='e',<393>,12:6]]}}, update_dictionary={score=[[@4,23:27='score',<393>,2:4]], rank_bucket=[[@10,47:57='rank_bucket',<393>,2:28]]}, filters=[{name=emp_id, table_ref=e}, {name=emp_id, table_ref=src}], table_alias={e=employees, src=union2}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void updateComplexSubstitutionU3WithCteIntersectOrderBySubstitution() {
		final String query = "WITH base AS ("
				+ "\n  SELECT a.emp_id, a.<update select col U3> AS score_val"
				+ "\n  FROM <[Ops Data].[Account Ledger U3]> a"
				+ "\n  WHERE a.<update where col U3> > 0"
				+ "\n  INTERSECT"
				+ "\n  SELECT b.emp_id, b.<update select col U3> AS score_val"
				+ "\n  FROM <[Ops Data].[Audit Ledger U3]> b"
				+ "\n  WHERE b.<update where col U3> > 0"
				+ "\n)"
				+ "\nUPDATE employees e"
				+ "\nSET score = b.score_val"
				+ "\nFROM base b"
				+ "\nWHERE e.emp_id = b.emp_id";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={with={1={cte={intersect={1={select={1={column={name=emp_id, table_ref=a}}, 2={column={substitution={name=<update select col U3>, type=column}, table_ref=a}, alias=score_val}}, from={table={alias=a, substitution={name=<[Ops Data].[Account Ledger U3]>, parts={1=[Ops Data], 2=[Account Ledger U3]}, type=tuple}}}, where={condition={left={column={substitution={name=<update where col U3>, type=column}, table_ref=a}}, right={literal=0}, operator=>}}}, 2={intersect={qualifier=null, operator=INTERSECT}}, 3={select={1={column={name=emp_id, table_ref=b}}, 2={column={substitution={name=<update select col U3>, type=column}, table_ref=b}, alias=score_val}}, from={table={alias=b, substitution={name=<[Ops Data].[Audit Ledger U3]>, parts={1=[Ops Data], 2=[Audit Ledger U3]}, type=tuple}}}, where={condition={left={column={substitution={name=<update where col U3>, type=column}, table_ref=b}}, right={literal=0}, operator=>}}}}}, alias=base}}, query={update={from={table={alias=b, table=base}}, where={condition={left={column={name=emp_id, table_ref=e}}, right={column={name=emp_id, table_ref=b}}, operator==}}, assignments={1={set={column={name=score, table_ref=null}}, to={column={name=score_val, table_ref=b}}}}, table={alias=e, table=employees}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[score]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<update select col U3>=column, <[Ops Data].[Audit Ledger U3]>=tuple, <[Ops Data].[Account Ledger U3]>=tuple, <update where col U3>=column}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{<[Ops Data].[Audit Ledger U3]>={<update select col U3>=[[@29,181:181='b',<393>,6:19]], emp_id=[[@25,171:171='b',<393>,6:9]], <update where col U3>=[[@38,267:267='b',<393>,8:8]]}, <[Ops Data].[Account Ledger U3]>={<update select col U3>=[[@9,34:34='a',<393>,2:19]], emp_id=[[@5,24:24='a',<393>,2:9]], <update where col U3>=[[@18,122:122='a',<393>,4:8]]}, employees={score=[[@48,320:324='score',<393>,11:4]], emp_id=[[@57,358:358='e',<393>,13:6]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{intersect2={score_val=[[@50,328:328='b',<393>,11:12]], emp_id=[[@61,369:369='b',<393>,13:17]]}, query0={score_val=[[@13,62:70='score_val',<393>,2:47]], emp_id=[[@7,26:31='emp_id',<393>,2:11]]}, update3={score=[[@48,320:324='score',<393>,11:4]]}, query1={score_val=[[@33,209:217='score_val',<393>,6:47]], emp_id=[[@27,173:178='emp_id',<393>,6:11]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_update3={context_list={b=intersect2, base=intersect2}, assignments={score=[{name=score_val, table_ref=b}]}, table_dictionary={employees={score=[[@48,320:324='score',<393>,11:4]], emp_id=[[@57,358:358='e',<393>,13:6]]}}, def_intersect2={query_dictionary={score_val=[[@50,328:328='b',<393>,11:12]], emp_id=[[@61,369:369='b',<393>,13:17]]}, def_query1={query_dictionary={score_val=[[@33,209:217='score_val',<393>,6:47]], emp_id=[[@27,173:178='emp_id',<393>,6:11]]}, table_dictionary={<[Ops Data].[Audit Ledger U3]>={<update select col U3>=[[@29,181:181='b',<393>,6:19]], emp_id=[[@25,171:171='b',<393>,6:9]], <update where col U3>=[[@38,267:267='b',<393>,8:8]]}}, setop=INTERSECTION, filters=[{substitution={name=<update where col U3>, type=column}, table_ref=b}], interface={score_val=[{substitution={name=<update select col U3>, type=column}, table_ref=b}], emp_id=[{name=emp_id, table_ref=b}]}, table_alias={b=<[Ops Data].[Audit Ledger U3]>}}, def_query0={query_dictionary={score_val=[[@13,62:70='score_val',<393>,2:47]], emp_id=[[@7,26:31='emp_id',<393>,2:11]]}, table_dictionary={<[Ops Data].[Account Ledger U3]>={<update select col U3>=[[@9,34:34='a',<393>,2:19]], emp_id=[[@5,24:24='a',<393>,2:9]], <update where col U3>=[[@18,122:122='a',<393>,4:8]]}}, filters=[{substitution={name=<update where col U3>, type=column}, table_ref=a}], interface={score_val=[{substitution={name=<update select col U3>, type=column}, table_ref=a}], emp_id=[{name=emp_id, table_ref=a}]}, table_alias={a=<[Ops Data].[Account Ledger U3]>}}, interface={score_val=query_column, emp_id=query_column}}, update_dictionary={score=[[@48,320:324='score',<393>,11:4]]}, filters=[{name=emp_id, table_ref=e}, {name=emp_id, table_ref=b}], table_alias={b=intersect2, e=employees, base=intersect2}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void updateComplexSubstitutionU3WithCteExceptOrderBySubstitution() {
		final String query = "WITH base AS ("
				+ "\n  SELECT a.emp_id, a.<update select col U3> AS score_val"
				+ "\n  FROM <[Ops Data].[Account Ledger U3]> a"
				+ "\n  WHERE a.<update where col U3> > 0"
				+ "\n  EXCEPT"
				+ "\n  SELECT b.emp_id, b.<update select col U3> AS score_val"
				+ "\n  FROM <[Ops Data].[Audit Ledger U3]> b"
				+ "\n  WHERE b.<update where col U3> > 0"
				+ "\n)"
				+ "\nUPDATE employees e"
				+ "\nSET score = b.score_val"
				+ "\nFROM base b"
				+ "\nWHERE e.emp_id = b.emp_id";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={with={1={cte={union={1={select={1={column={name=emp_id, table_ref=a}}, 2={column={substitution={name=<update select col U3>, type=column}, table_ref=a}, alias=score_val}}, from={table={alias=a, substitution={name=<[Ops Data].[Account Ledger U3]>, parts={1=[Ops Data], 2=[Account Ledger U3]}, type=tuple}}}, where={condition={left={column={substitution={name=<update where col U3>, type=column}, table_ref=a}}, right={literal=0}, operator=>}}}, 2={union={qualifier=null, operator=EXCEPT}}, 3={select={1={column={name=emp_id, table_ref=b}}, 2={column={substitution={name=<update select col U3>, type=column}, table_ref=b}, alias=score_val}}, from={table={alias=b, substitution={name=<[Ops Data].[Audit Ledger U3]>, parts={1=[Ops Data], 2=[Audit Ledger U3]}, type=tuple}}}, where={condition={left={column={substitution={name=<update where col U3>, type=column}, table_ref=b}}, right={literal=0}, operator=>}}}}}, alias=base}}, query={update={from={table={alias=b, table=base}}, where={condition={left={column={name=emp_id, table_ref=e}}, right={column={name=emp_id, table_ref=b}}, operator==}}, assignments={1={set={column={name=score, table_ref=null}}, to={column={name=score_val, table_ref=b}}}}, table={alias=e, table=employees}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[score]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<update select col U3>=column, <[Ops Data].[Audit Ledger U3]>=tuple, <[Ops Data].[Account Ledger U3]>=tuple, <update where col U3>=column}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{<[Ops Data].[Audit Ledger U3]>={<update select col U3>=[[@29,178:178='b',<393>,6:19]], emp_id=[[@25,168:168='b',<393>,6:9]], <update where col U3>=[[@38,264:264='b',<393>,8:8]]}, <[Ops Data].[Account Ledger U3]>={<update select col U3>=[[@9,34:34='a',<393>,2:19]], emp_id=[[@5,24:24='a',<393>,2:9]], <update where col U3>=[[@18,122:122='a',<393>,4:8]]}, employees={score=[[@48,317:321='score',<393>,11:4]], emp_id=[[@57,355:355='e',<393>,13:6]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{union2={score_val=[[@50,325:325='b',<393>,11:12]], emp_id=[[@61,366:366='b',<393>,13:17]]}, query0={score_val=[[@13,62:70='score_val',<393>,2:47]], emp_id=[[@7,26:31='emp_id',<393>,2:11]]}, update3={score=[[@48,317:321='score',<393>,11:4]]}, query1={score_val=[[@33,206:214='score_val',<393>,6:47]], emp_id=[[@27,170:175='emp_id',<393>,6:11]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_update3={context_list={b=union2, base=union2}, assignments={score=[{name=score_val, table_ref=b}]}, def_union2={query_dictionary={score_val=[[@50,325:325='b',<393>,11:12]], emp_id=[[@61,366:366='b',<393>,13:17]]}, def_query1={query_dictionary={score_val=[[@33,206:214='score_val',<393>,6:47]], emp_id=[[@27,170:175='emp_id',<393>,6:11]]}, table_dictionary={<[Ops Data].[Audit Ledger U3]>={<update select col U3>=[[@29,178:178='b',<393>,6:19]], emp_id=[[@25,168:168='b',<393>,6:9]], <update where col U3>=[[@38,264:264='b',<393>,8:8]]}}, setop=EXCEPT, filters=[{substitution={name=<update where col U3>, type=column}, table_ref=b}], interface={score_val=[{substitution={name=<update select col U3>, type=column}, table_ref=b}], emp_id=[{name=emp_id, table_ref=b}]}, table_alias={b=<[Ops Data].[Audit Ledger U3]>}}, def_query0={query_dictionary={score_val=[[@13,62:70='score_val',<393>,2:47]], emp_id=[[@7,26:31='emp_id',<393>,2:11]]}, table_dictionary={<[Ops Data].[Account Ledger U3]>={<update select col U3>=[[@9,34:34='a',<393>,2:19]], emp_id=[[@5,24:24='a',<393>,2:9]], <update where col U3>=[[@18,122:122='a',<393>,4:8]]}}, filters=[{substitution={name=<update where col U3>, type=column}, table_ref=a}], interface={score_val=[{substitution={name=<update select col U3>, type=column}, table_ref=a}], emp_id=[{name=emp_id, table_ref=a}]}, table_alias={a=<[Ops Data].[Account Ledger U3]>}}, interface={score_val=query_column, emp_id=query_column}}, table_dictionary={employees={score=[[@48,317:321='score',<393>,11:4]], emp_id=[[@57,355:355='e',<393>,13:6]]}}, update_dictionary={score=[[@48,317:321='score',<393>,11:4]]}, filters=[{name=emp_id, table_ref=e}, {name=emp_id, table_ref=b}], table_alias={b=union2, e=employees, base=union2}}}",
				extractor.getSymbolTable().toString());
	}



	@Test
	public void updateComplexSubstitutionU4NestedWithInCteBody() {
		final String query = "WITH outer_cte AS ("
				+ "\n  WITH inner_cte AS ("
				+ "\n    SELECT a.emp_id, a.<update select col U4> AS metric_val"
				+ "\n    FROM <[Finance].[Revenue Feed U4]> a"
				+ "\n    WHERE a.<update where col U4> > 0"
				+ "\n  )"
				+ "\n  SELECT i.emp_id, i.metric_val"
				+ "\n  FROM inner_cte i"
				+ "\n)"
				+ "\nUPDATE employees e"
				+ "\nSET score = o.metric_val"
				+ "\nFROM outer_cte o"
				+ "\nWHERE e.emp_id = o.emp_id";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={with={1={cte={with={1={cte={select={1={column={name=emp_id, table_ref=a}}, 2={column={substitution={name=<update select col U4>, type=column}, table_ref=a}, alias=metric_val}}, from={table={alias=a, substitution={name=<[Finance].[Revenue Feed U4]>, parts={1=[Finance], 2=[Revenue Feed U4]}, type=tuple}}}, where={condition={left={column={substitution={name=<update where col U4>, type=column}, table_ref=a}}, right={literal=0}, operator=>}}}, alias=inner_cte}}, query={select={1={column={name=emp_id, table_ref=i}}, 2={column={name=metric_val, table_ref=i}}}, from={table={alias=i, table=inner_cte}}}}, alias=outer_cte}}, query={update={from={table={alias=o, table=outer_cte}}, where={condition={left={column={name=emp_id, table_ref=e}}, right={column={name=emp_id, table_ref=o}}, operator==}}, assignments={1={set={column={name=score, table_ref=null}}, to={column={name=metric_val, table_ref=o}}}}, table={alias=e, table=employees}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[score]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<update select col U4>=column, <update where col U4>=column, <[Finance].[Revenue Feed U4]>=tuple}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{employees={score=[[@44,261:265='score',<393>,11:4]], emp_id=[[@53,305:305='e',<393>,13:6]]}, <[Finance].[Revenue Feed U4]>={<update select col U4>=[[@13,63:63='a',<393>,3:21]], <update where col U4>=[[@22,153:153='a',<393>,5:10]], emp_id=[[@9,53:53='a',<393>,3:11]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={metric_val=[[@17,91:100='metric_val',<393>,3:49], [@33,204:204='i',<393>,7:19]], emp_id=[[@11,55:60='emp_id',<393>,3:13], [@29,194:194='i',<393>,7:9]]}, query1={metric_val=[[@35,206:215='metric_val',<393>,7:21], [@46,269:269='o',<393>,11:12]], emp_id=[[@31,196:201='emp_id',<393>,7:11], [@57,316:316='o',<393>,13:17]]}, update2={score=[[@44,261:265='score',<393>,11:4]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_update2={context_list={o=query1, outer_cte=query1}, assignments={score=[{name=metric_val, table_ref=o}]}, table_dictionary={employees={score=[[@44,261:265='score',<393>,11:4]], emp_id=[[@53,305:305='e',<393>,13:6]]}}, update_dictionary={score=[[@44,261:265='score',<393>,11:4]]}, def_query1={context_list={inner_cte=query0, i=query0}, query_dictionary={metric_val=[[@35,206:215='metric_val',<393>,7:21], [@46,269:269='o',<393>,11:12]], emp_id=[[@31,196:201='emp_id',<393>,7:11], [@57,316:316='o',<393>,13:17]]}, def_query0={query_dictionary={metric_val=[[@17,91:100='metric_val',<393>,3:49], [@33,204:204='i',<393>,7:19]], emp_id=[[@11,55:60='emp_id',<393>,3:13], [@29,194:194='i',<393>,7:9]]}, table_dictionary={<[Finance].[Revenue Feed U4]>={<update select col U4>=[[@13,63:63='a',<393>,3:21]], <update where col U4>=[[@22,153:153='a',<393>,5:10]], emp_id=[[@9,53:53='a',<393>,3:11]]}}, filters=[{substitution={name=<update where col U4>, type=column}, table_ref=a}], interface={metric_val=[{substitution={name=<update select col U4>, type=column}, table_ref=a}], emp_id=[{name=emp_id, table_ref=a}]}, table_alias={a=<[Finance].[Revenue Feed U4]>}}, interface={metric_val=[{name=metric_val, table_ref=i}], emp_id=[{name=emp_id, table_ref=i}]}, table_alias={inner_cte=query0, i=query0}}, filters=[{name=emp_id, table_ref=e}, {name=emp_id, table_ref=o}], table_alias={e=employees, outer_cte=query1, o=query1}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void updateComplexSubstitutionU5WithCteQualifyWindowSubstitution() {
		final String query = "WITH ranked AS ("
				+ "\n  SELECT a.emp_id, a.<update select col U5> AS score_val,"
				+ "\n         row_number() OVER (PARTITION BY a.emp_id ORDER BY a.<update order col U5> DESC) AS rn"
				+ "\n  FROM <[Metrics].[Score Feed U5]> a"
				+ "\n  WHERE a.<update where col U5> > 0"
				+ "\n  QUALIFY rn = 1"
				+ "\n)"
				+ "\nUPDATE employees e"
				+ "\nSET score = r.score_val"
				+ "\nFROM ranked r"
				+ "\nWHERE e.emp_id = r.emp_id";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={with={1={cte={select={1={column={name=emp_id, table_ref=a}}, 2={column={substitution={name=<update select col U5>, type=column}, table_ref=a}, alias=score_val}, 3={alias=rn, window_function={over={partition_by={1={column={name=emp_id, table_ref=a}}}, orderby={1={null_order=null, predicand={column={substitution={name=<update order col U5>, type=column}, table_ref=a}}, sort_order=DESC}}}, function={function_name=row_number, parameters=null}}}}, from={table={alias=a, substitution={name=<[Metrics].[Score Feed U5]>, parts={1=[Metrics], 2=[Score Feed U5]}, type=tuple}}}, where={condition={left={column={substitution={name=<update where col U5>, type=column}, table_ref=a}}, right={literal=0}, operator=>}}, qualify={condition={left={column={name=rn, table_ref=null}}, right={literal=1}, operator==}}}, alias=ranked}}, query={update={from={table={alias=r, table=ranked}}, where={condition={left={column={name=emp_id, table_ref=e}}, right={column={name=emp_id, table_ref=r}}, operator==}}, assignments={1={set={column={name=score, table_ref=null}}, to={column={name=score_val, table_ref=r}}}}, table={alias=e, table=employees}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[score]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<update select col U5>=column, <update order col U5>=column, <update where col U5>=column, <[Metrics].[Score Feed U5]>=tuple}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{<[Metrics].[Score Feed U5]>={<update select col U5>=[[@9,36:36='a',<393>,2:19]], <update order col U5>=[[@27,134:134='a',<393>,3:59]], <update where col U5>=[[@38,215:215='a',<393>,5:8]], emp_id=[[@5,26:26='a',<393>,2:9], [@22,116:116='a',<393>,3:41]]}, employees={score=[[@52,285:289='score',<393>,9:4]], emp_id=[[@61,325:325='e',<393>,11:6]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={rn=[[@33,167:168='rn',<393>,3:92], [@44,253:254='rn',<393>,6:10]], score_val=[[@13,64:72='score_val',<393>,2:47], [@54,293:293='r',<393>,9:12]], emp_id=[[@7,28:33='emp_id',<393>,2:11], [@65,336:336='r',<393>,11:17]]}, update1={score=[[@52,285:289='score',<393>,9:4]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_update1={context_list={r=query0, ranked=query0}, assignments={score=[{name=score_val, table_ref=r}]}, table_dictionary={employees={score=[[@52,285:289='score',<393>,9:4]], emp_id=[[@61,325:325='e',<393>,11:6]]}}, update_dictionary={score=[[@52,285:289='score',<393>,9:4]]}, def_query0={window_ordered_by=[{substitution={name=<update order col U5>, type=column}, table_ref=a}], query_dictionary={rn=[[@33,167:168='rn',<393>,3:92], [@44,253:254='rn',<393>,6:10]], score_val=[[@13,64:72='score_val',<393>,2:47], [@54,293:293='r',<393>,9:12]], emp_id=[[@7,28:33='emp_id',<393>,2:11], [@65,336:336='r',<393>,11:17]]}, table_dictionary={<[Metrics].[Score Feed U5]>={<update select col U5>=[[@9,36:36='a',<393>,2:19]], <update order col U5>=[[@27,134:134='a',<393>,3:59]], <update where col U5>=[[@38,215:215='a',<393>,5:8]], emp_id=[[@5,26:26='a',<393>,2:9], [@22,116:116='a',<393>,3:41]]}}, window_partition_by=[{name=emp_id, table_ref=a}], filters=[{substitution={name=<update where col U5>, type=column}, table_ref=a}, {name=rn, table_ref=query0}], interface={rn=[{name=emp_id, table_ref=a}, {substitution={name=<update order col U5>, type=column}, table_ref=a}], score_val=[{substitution={name=<update select col U5>, type=column}, table_ref=a}], emp_id=[{name=emp_id, table_ref=a}]}, table_alias={a=<[Metrics].[Score Feed U5]>}}, filters=[{name=emp_id, table_ref=e}, {name=emp_id, table_ref=r}], table_alias={r=query0, e=employees, ranked=query0}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void updateComplexSubstitutionU6SubqueryJoinOnColumnSubstitution() {
		final String query = "UPDATE employees e"
				+ "\nSET score = j.metric_val"
				+ "\nFROM ("
				+ "\n  SELECT a.emp_id, a.<update select col U6> AS metric_val"
				+ "\n  FROM <[Join Data].[Left Feed U6]> a"
				+ "\n  JOIN <[Join Data].[Right Feed U6]> b"
				+ "\n    ON a.<update join col U6> = b.<update join col U6b>"
				+ "\n  WHERE a.<update where col U6> > 0"
				+ "\n) j"
				+ "\nWHERE e.emp_id = j.emp_id";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={update={from={table={alias=j, query={select={1={column={name=emp_id, table_ref=a}}, 2={column={substitution={name=<update select col U6>, type=column}, table_ref=a}, alias=metric_val}}, from={join={1={table={alias=a, substitution={name=<[Join Data].[Left Feed U6]>, parts={1=[Join Data], 2=[Left Feed U6]}, type=tuple}}}, 2={join=JOIN, on={condition={left={column={substitution={name=<update join col U6>, type=column}, table_ref=a}}, right={column={substitution={name=<update join col U6b>, type=column}, table_ref=b}}, operator==}}}, 3={table={alias=b, substitution={name=<[Join Data].[Right Feed U6]>, parts={1=[Join Data], 2=[Right Feed U6]}, type=tuple}}}}}, where={condition={left={column={substitution={name=<update where col U6>, type=column}, table_ref=a}}, right={literal=0}, operator=>}}}}}, where={condition={left={column={name=emp_id, table_ref=e}}, right={column={name=emp_id, table_ref=j}}, operator==}}, assignments={1={set={column={name=score, table_ref=null}}, to={column={name=metric_val, table_ref=j}}}}, table={alias=e, table=employees}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[score]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<update select col U6>=column, <[Join Data].[Left Feed U6]>=tuple, <update join col U6>=column, <[Join Data].[Right Feed U6]>=tuple, <update where col U6>=column, <update join col U6b>=column}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{<[Join Data].[Left Feed U6]>={<update select col U6>=[[@16,70:70='a',<393>,4:19]], <update join col U6>=[[@28,193:193='a',<393>,7:7]], <update where col U6>=[[@36,250:250='a',<393>,8:8]], emp_id=[[@12,60:60='a',<393>,4:9]]}, <[Join Data].[Right Feed U6]>={<update join col U6b>=[[@32,218:218='b',<393>,7:32]]}, employees={score=[[@4,23:27='score',<393>,2:4]], emp_id=[[@44,288:288='e',<393>,10:6]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={metric_val=[[@20,98:107='metric_val',<393>,4:47], [@6,31:31='j',<393>,2:12]], emp_id=[[@14,62:67='emp_id',<393>,4:11], [@48,299:299='j',<393>,10:17]]}, update1={score=[[@4,23:27='score',<393>,2:4]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_update1={assignments={score=[{name=metric_val, table_ref=j}]}, table_dictionary={employees={score=[[@4,23:27='score',<393>,2:4]], emp_id=[[@44,288:288='e',<393>,10:6]]}}, update_dictionary={score=[[@4,23:27='score',<393>,2:4]]}, def_query0={query_dictionary={metric_val=[[@20,98:107='metric_val',<393>,4:47], [@6,31:31='j',<393>,2:12]], emp_id=[[@14,62:67='emp_id',<393>,4:11], [@48,299:299='j',<393>,10:17]]}, table_dictionary={<[Join Data].[Left Feed U6]>={<update select col U6>=[[@16,70:70='a',<393>,4:19]], <update join col U6>=[[@28,193:193='a',<393>,7:7]], <update where col U6>=[[@36,250:250='a',<393>,8:8]], emp_id=[[@12,60:60='a',<393>,4:9]]}, <[Join Data].[Right Feed U6]>={<update join col U6b>=[[@32,218:218='b',<393>,7:32]]}}, filters=[{substitution={name=<update join col U6>, type=column}, table_ref=a}, {substitution={name=<update join col U6b>, type=column}, table_ref=b}, {substitution={name=<update where col U6>, type=column}, table_ref=a}], interface={metric_val=[{substitution={name=<update select col U6>, type=column}, table_ref=a}], emp_id=[{name=emp_id, table_ref=a}]}, table_alias={a=<[Join Data].[Left Feed U6]>, b=<[Join Data].[Right Feed U6]>}}, filters=[{name=emp_id, table_ref=e}, {name=emp_id, table_ref=j}], table_alias={e=employees, j=query0}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void updateComplexSubstitutionU7ChainedCteReferences() {
		final String query = "WITH step1 AS ("
				+ "\n  SELECT a.emp_id, a.<update select col U7> AS raw_val"
				+ "\n  FROM <[Pipeline].[Stage One U7]> a"
				+ "\n  WHERE a.<update where col U7> > 0"
				+ "\n), step2 AS ("
				+ "\n  SELECT s.emp_id, s.raw_val"
				+ "\n  FROM step1 s"
				+ "\n)"
				+ "\nUPDATE employees e"
				+ "\nSET score = t.raw_val"
				+ "\nFROM step2 t"
				+ "\nWHERE e.emp_id = t.emp_id";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={with={1={cte={select={1={column={name=emp_id, table_ref=a}}, 2={column={substitution={name=<update select col U7>, type=column}, table_ref=a}, alias=raw_val}}, from={table={alias=a, substitution={name=<[Pipeline].[Stage One U7]>, parts={1=[Pipeline], 2=[Stage One U7]}, type=tuple}}}, where={condition={left={column={substitution={name=<update where col U7>, type=column}, table_ref=a}}, right={literal=0}, operator=>}}}, alias=step1}, 2={cte={select={1={column={name=emp_id, table_ref=s}}, 2={column={name=raw_val, table_ref=s}}}, from={table={alias=s, table=step1}}}, alias=step2}}, query={update={from={table={alias=t, table=step2}}, where={condition={left={column={name=emp_id, table_ref=e}}, right={column={name=emp_id, table_ref=t}}, operator==}}, assignments={1={set={column={name=score, table_ref=null}}, to={column={name=raw_val, table_ref=t}}}}, table={alias=e, table=employees}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[score]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<[Pipeline].[Stage One U7]>=tuple, <update select col U7>=column, <update where col U7>=column}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{<[Pipeline].[Stage One U7]>={<update select col U7>=[[@9,35:35='a',<393>,2:19]], <update where col U7>=[[@18,116:116='a',<393>,4:8]], emp_id=[[@5,25:25='a',<393>,2:9]]}, employees={score=[[@44,227:231='score',<393>,10:4]], emp_id=[[@53,264:264='e',<393>,12:6]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={raw_val=[[@13,63:69='raw_val',<393>,2:47], [@33,177:177='s',<393>,6:19]], emp_id=[[@7,27:32='emp_id',<393>,2:11], [@29,167:167='s',<393>,6:9]]}, query1={raw_val=[[@35,179:185='raw_val',<393>,6:21], [@46,235:235='t',<393>,10:12]], emp_id=[[@31,169:174='emp_id',<393>,6:11], [@57,275:275='t',<393>,12:17]]}, update2={score=[[@44,227:231='score',<393>,10:4]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_update2={context_list={t=query1, step1=query0, step2=query1}, assignments={score=[{name=raw_val, table_ref=t}]}, table_dictionary={employees={score=[[@44,227:231='score',<393>,10:4]], emp_id=[[@53,264:264='e',<393>,12:6]]}}, update_dictionary={score=[[@44,227:231='score',<393>,10:4]]}, def_query1={context_list={step1=query0, s=query0}, query_dictionary={raw_val=[[@35,179:185='raw_val',<393>,6:21], [@46,235:235='t',<393>,10:12]], emp_id=[[@31,169:174='emp_id',<393>,6:11], [@57,275:275='t',<393>,12:17]]}, interface={raw_val=[{name=raw_val, table_ref=s}], emp_id=[{name=emp_id, table_ref=s}]}, table_alias={s=query0, step1=query0}}, def_query0={query_dictionary={raw_val=[[@13,63:69='raw_val',<393>,2:47], [@33,177:177='s',<393>,6:19]], emp_id=[[@7,27:32='emp_id',<393>,2:11], [@29,167:167='s',<393>,6:9]]}, table_dictionary={<[Pipeline].[Stage One U7]>={<update select col U7>=[[@9,35:35='a',<393>,2:19]], <update where col U7>=[[@18,116:116='a',<393>,4:8]], emp_id=[[@5,25:25='a',<393>,2:9]]}}, filters=[{substitution={name=<update where col U7>, type=column}, table_ref=a}], interface={raw_val=[{substitution={name=<update select col U7>, type=column}, table_ref=a}], emp_id=[{name=emp_id, table_ref=a}]}, table_alias={a=<[Pipeline].[Stage One U7]>}}, filters=[{name=emp_id, table_ref=e}, {name=emp_id, table_ref=t}], table_alias={t=query1, e=employees, step2=query1, step1=query0}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void updateComplexSubstitutionU8UnionIntersectNestedSubquery() {
		final String query = "UPDATE employees e"
				+ "\nSET score = x.metric_val"
				+ "\nFROM ("
				+ "\n  SELECT u.emp_id, u.metric_val"
				+ "\n  FROM ("
				+ "\n    SELECT a.emp_id, a.<update select col U8> AS metric_val"
				+ "\n    FROM <[Blend Data].[Branch A U8]> a"
				+ "\n    WHERE a.<update where col U8> > 0"
				+ "\n    UNION"
				+ "\n    SELECT b.emp_id, b.<update select col U8> AS metric_val"
				+ "\n    FROM <[Blend Data].[Branch B U8]> b"
				+ "\n    WHERE b.<update where col U8> > 0"
				+ "\n  ) u"
				+ "\n  INTERSECT"
				+ "\n  SELECT c.emp_id, c.<update select col U8> AS metric_val"
				+ "\n  FROM <[Blend Data].[Branch C U8]> c"
				+ "\n  WHERE c.<update where col U8> > 0"
				+ "\n) x"
				+ "\nWHERE e.emp_id = x.emp_id";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={update={from={table={alias=x, query={intersect={1={select={1={column={name=emp_id, table_ref=u}}, 2={column={name=metric_val, table_ref=u}}}, from={table={alias=u, query={union={1={select={1={column={name=emp_id, table_ref=a}}, 2={column={substitution={name=<update select col U8>, type=column}, table_ref=a}, alias=metric_val}}, from={table={alias=a, substitution={name=<[Blend Data].[Branch A U8]>, parts={1=[Blend Data], 2=[Branch A U8]}, type=tuple}}}, where={condition={left={column={substitution={name=<update where col U8>, type=column}, table_ref=a}}, right={literal=0}, operator=>}}}, 2={union={qualifier=null, operator=UNION}}, 3={select={1={column={name=emp_id, table_ref=b}}, 2={column={substitution={name=<update select col U8>, type=column}, table_ref=b}, alias=metric_val}}, from={table={alias=b, substitution={name=<[Blend Data].[Branch B U8]>, parts={1=[Blend Data], 2=[Branch B U8]}, type=tuple}}}, where={condition={left={column={substitution={name=<update where col U8>, type=column}, table_ref=b}}, right={literal=0}, operator=>}}}}}}}}, 2={intersect={qualifier=null, operator=INTERSECT}}, 3={select={1={column={name=emp_id, table_ref=c}}, 2={column={substitution={name=<update select col U8>, type=column}, table_ref=c}, alias=metric_val}}, from={table={alias=c, substitution={name=<[Blend Data].[Branch C U8]>, parts={1=[Blend Data], 2=[Branch C U8]}, type=tuple}}}, where={condition={left={column={substitution={name=<update where col U8>, type=column}, table_ref=c}}, right={literal=0}, operator=>}}}}}}}, where={condition={left={column={name=emp_id, table_ref=e}}, right={column={name=emp_id, table_ref=x}}, operator==}}, assignments={1={set={column={name=score, table_ref=null}}, to={column={name=metric_val, table_ref=x}}}}, table={alias=e, table=employees}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[score]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<[Blend Data].[Branch A U8]>=tuple, <update where col U8>=column, <update select col U8>=column, <[Blend Data].[Branch C U8]>=tuple, <[Blend Data].[Branch B U8]>=tuple}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{<[Blend Data].[Branch A U8]>={<update where col U8>=[[@35,202:202='a',<393>,8:10]], <update select col U8>=[[@26,113:113='a',<393>,6:21]], emp_id=[[@22,103:103='a',<393>,6:11]]}, <[Blend Data].[Branch C U8]>={<update where col U8>=[[@77,500:500='c',<393>,17:8]], <update select col U8>=[[@68,415:415='c',<393>,15:19]], emp_id=[[@64,405:405='c',<393>,15:9]]}, employees={score=[[@4,23:27='score',<393>,2:4]], emp_id=[[@85,538:538='e',<393>,19:6]]}, <[Blend Data].[Branch B U8]>={<update where col U8>=[[@55,350:350='b',<393>,12:10]], <update select col U8>=[[@46,261:261='b',<393>,10:21]], emp_id=[[@42,251:251='b',<393>,10:11]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{union2={metric_val=[[@16,70:70='u',<393>,4:19]], emp_id=[[@12,60:60='u',<393>,4:9]]}, intersect5={metric_val=[[@6,31:31='x',<393>,2:12]], emp_id=[[@89,549:549='x',<393>,19:17]]}, query4={metric_val=[[@72,443:452='metric_val',<393>,15:47]], emp_id=[[@66,407:412='emp_id',<393>,15:11]]}, update6={score=[[@4,23:27='score',<393>,2:4]]}, query0={metric_val=[[@30,141:150='metric_val',<393>,6:49]], emp_id=[[@24,105:110='emp_id',<393>,6:13]]}, query1={metric_val=[[@50,289:298='metric_val',<393>,10:49]], emp_id=[[@44,253:258='emp_id',<393>,10:13]]}, query3={metric_val=[[@18,72:81='metric_val',<393>,4:21]], emp_id=[[@14,62:67='emp_id',<393>,4:11]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_update6={assignments={score=[{name=metric_val, table_ref=x}]}, table_dictionary={employees={score=[[@4,23:27='score',<393>,2:4]], emp_id=[[@85,538:538='e',<393>,19:6]]}}, update_dictionary={score=[[@4,23:27='score',<393>,2:4]]}, def_intersect5={query_dictionary={metric_val=[[@6,31:31='x',<393>,2:12]], emp_id=[[@89,549:549='x',<393>,19:17]]}, interface={metric_val=query_column, emp_id=query_column}, def_query4={query_dictionary={metric_val=[[@72,443:452='metric_val',<393>,15:47]], emp_id=[[@66,407:412='emp_id',<393>,15:11]]}, table_dictionary={<[Blend Data].[Branch C U8]>={<update where col U8>=[[@77,500:500='c',<393>,17:8]], <update select col U8>=[[@68,415:415='c',<393>,15:19]], emp_id=[[@64,405:405='c',<393>,15:9]]}}, setop=INTERSECTION, filters=[{substitution={name=<update where col U8>, type=column}, table_ref=c}], interface={metric_val=[{substitution={name=<update select col U8>, type=column}, table_ref=c}], emp_id=[{name=emp_id, table_ref=c}]}, table_alias={c=<[Blend Data].[Branch C U8]>}}, def_query3={def_union2={query_dictionary={metric_val=[[@16,70:70='u',<393>,4:19]], emp_id=[[@12,60:60='u',<393>,4:9]]}, def_query1={query_dictionary={metric_val=[[@50,289:298='metric_val',<393>,10:49]], emp_id=[[@44,253:258='emp_id',<393>,10:13]]}, table_dictionary={<[Blend Data].[Branch B U8]>={<update where col U8>=[[@55,350:350='b',<393>,12:10]], <update select col U8>=[[@46,261:261='b',<393>,10:21]], emp_id=[[@42,251:251='b',<393>,10:11]]}}, setop=UNION, filters=[{substitution={name=<update where col U8>, type=column}, table_ref=b}], interface={metric_val=[{substitution={name=<update select col U8>, type=column}, table_ref=b}], emp_id=[{name=emp_id, table_ref=b}]}, table_alias={b=<[Blend Data].[Branch B U8]>}}, def_query0={query_dictionary={metric_val=[[@30,141:150='metric_val',<393>,6:49]], emp_id=[[@24,105:110='emp_id',<393>,6:13]]}, table_dictionary={<[Blend Data].[Branch A U8]>={<update where col U8>=[[@35,202:202='a',<393>,8:10]], <update select col U8>=[[@26,113:113='a',<393>,6:21]], emp_id=[[@22,103:103='a',<393>,6:11]]}}, filters=[{substitution={name=<update where col U8>, type=column}, table_ref=a}], interface={metric_val=[{substitution={name=<update select col U8>, type=column}, table_ref=a}], emp_id=[{name=emp_id, table_ref=a}]}, table_alias={a=<[Blend Data].[Branch A U8]>}}, interface={metric_val=query_column, emp_id=query_column}}, query_dictionary={metric_val=[[@18,72:81='metric_val',<393>,4:21]], emp_id=[[@14,62:67='emp_id',<393>,4:11]]}, interface={metric_val=[{name=metric_val, table_ref=u}], emp_id=[{name=emp_id, table_ref=u}]}, table_alias={u=union2}}}, filters=[{name=emp_id, table_ref=e}, {name=emp_id, table_ref=x}], table_alias={e=employees, x=intersect5}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void updateComplexSubstitutionU8UnionExceptNestedSubquery() {
		final String query = "UPDATE employees e"
				+ "\nSET score = x.metric_val"
				+ "\nFROM ("
				+ "\n  SELECT u.emp_id, u.metric_val"
				+ "\n  FROM ("
				+ "\n    SELECT a.emp_id, a.<update select col U8> AS metric_val"
				+ "\n    FROM <[Blend Data].[Branch A U8]> a"
				+ "\n    WHERE a.<update where col U8> > 0"
				+ "\n    UNION"
				+ "\n    SELECT b.emp_id, b.<update select col U8> AS metric_val"
				+ "\n    FROM <[Blend Data].[Branch B U8]> b"
				+ "\n    WHERE b.<update where col U8> > 0"
				+ "\n  ) u"
				+ "\n  EXCEPT"
				+ "\n  SELECT c.emp_id, c.<update select col U8> AS metric_val"
				+ "\n  FROM <[Blend Data].[Branch C U8]> c"
				+ "\n  WHERE c.<update where col U8> > 0"
				+ "\n) x"
				+ "\nWHERE e.emp_id = x.emp_id";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={update={from={table={alias=x, query={union={1={select={1={column={name=emp_id, table_ref=u}}, 2={column={name=metric_val, table_ref=u}}}, from={table={alias=u, query={union={1={select={1={column={name=emp_id, table_ref=a}}, 2={column={substitution={name=<update select col U8>, type=column}, table_ref=a}, alias=metric_val}}, from={table={alias=a, substitution={name=<[Blend Data].[Branch A U8]>, parts={1=[Blend Data], 2=[Branch A U8]}, type=tuple}}}, where={condition={left={column={substitution={name=<update where col U8>, type=column}, table_ref=a}}, right={literal=0}, operator=>}}}, 2={union={qualifier=null, operator=UNION}}, 3={select={1={column={name=emp_id, table_ref=b}}, 2={column={substitution={name=<update select col U8>, type=column}, table_ref=b}, alias=metric_val}}, from={table={alias=b, substitution={name=<[Blend Data].[Branch B U8]>, parts={1=[Blend Data], 2=[Branch B U8]}, type=tuple}}}, where={condition={left={column={substitution={name=<update where col U8>, type=column}, table_ref=b}}, right={literal=0}, operator=>}}}}}}}}, 2={union={qualifier=null, operator=EXCEPT}}, 3={select={1={column={name=emp_id, table_ref=c}}, 2={column={substitution={name=<update select col U8>, type=column}, table_ref=c}, alias=metric_val}}, from={table={alias=c, substitution={name=<[Blend Data].[Branch C U8]>, parts={1=[Blend Data], 2=[Branch C U8]}, type=tuple}}}, where={condition={left={column={substitution={name=<update where col U8>, type=column}, table_ref=c}}, right={literal=0}, operator=>}}}}}}}, where={condition={left={column={name=emp_id, table_ref=e}}, right={column={name=emp_id, table_ref=x}}, operator==}}, assignments={1={set={column={name=score, table_ref=null}}, to={column={name=metric_val, table_ref=x}}}}, table={alias=e, table=employees}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[score]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<[Blend Data].[Branch A U8]>=tuple, <update where col U8>=column, <update select col U8>=column, <[Blend Data].[Branch C U8]>=tuple, <[Blend Data].[Branch B U8]>=tuple}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{<[Blend Data].[Branch A U8]>={<update where col U8>=[[@35,202:202='a',<393>,8:10]], <update select col U8>=[[@26,113:113='a',<393>,6:21]], emp_id=[[@22,103:103='a',<393>,6:11]]}, <[Blend Data].[Branch C U8]>={<update where col U8>=[[@77,497:497='c',<393>,17:8]], <update select col U8>=[[@68,412:412='c',<393>,15:19]], emp_id=[[@64,402:402='c',<393>,15:9]]}, employees={score=[[@4,23:27='score',<393>,2:4]], emp_id=[[@85,535:535='e',<393>,19:6]]}, <[Blend Data].[Branch B U8]>={<update where col U8>=[[@55,350:350='b',<393>,12:10]], <update select col U8>=[[@46,261:261='b',<393>,10:21]], emp_id=[[@42,251:251='b',<393>,10:11]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{union5={metric_val=[[@6,31:31='x',<393>,2:12]], emp_id=[[@89,546:546='x',<393>,19:17]]}, union2={metric_val=[[@16,70:70='u',<393>,4:19]], emp_id=[[@12,60:60='u',<393>,4:9]]}, query4={metric_val=[[@72,440:449='metric_val',<393>,15:47]], emp_id=[[@66,404:409='emp_id',<393>,15:11]]}, update6={score=[[@4,23:27='score',<393>,2:4]]}, query0={metric_val=[[@30,141:150='metric_val',<393>,6:49]], emp_id=[[@24,105:110='emp_id',<393>,6:13]]}, query1={metric_val=[[@50,289:298='metric_val',<393>,10:49]], emp_id=[[@44,253:258='emp_id',<393>,10:13]]}, query3={metric_val=[[@18,72:81='metric_val',<393>,4:21]], emp_id=[[@14,62:67='emp_id',<393>,4:11]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_update6={assignments={score=[{name=metric_val, table_ref=x}]}, table_dictionary={employees={score=[[@4,23:27='score',<393>,2:4]], emp_id=[[@85,535:535='e',<393>,19:6]]}}, update_dictionary={score=[[@4,23:27='score',<393>,2:4]]}, filters=[{name=emp_id, table_ref=e}, {name=emp_id, table_ref=x}], def_union5={query_dictionary={metric_val=[[@6,31:31='x',<393>,2:12]], emp_id=[[@89,546:546='x',<393>,19:17]]}, interface={metric_val=query_column, emp_id=query_column}, def_query4={query_dictionary={metric_val=[[@72,440:449='metric_val',<393>,15:47]], emp_id=[[@66,404:409='emp_id',<393>,15:11]]}, table_dictionary={<[Blend Data].[Branch C U8]>={<update where col U8>=[[@77,497:497='c',<393>,17:8]], <update select col U8>=[[@68,412:412='c',<393>,15:19]], emp_id=[[@64,402:402='c',<393>,15:9]]}}, setop=EXCEPT, filters=[{substitution={name=<update where col U8>, type=column}, table_ref=c}], interface={metric_val=[{substitution={name=<update select col U8>, type=column}, table_ref=c}], emp_id=[{name=emp_id, table_ref=c}]}, table_alias={c=<[Blend Data].[Branch C U8]>}}, def_query3={def_union2={query_dictionary={metric_val=[[@16,70:70='u',<393>,4:19]], emp_id=[[@12,60:60='u',<393>,4:9]]}, def_query1={query_dictionary={metric_val=[[@50,289:298='metric_val',<393>,10:49]], emp_id=[[@44,253:258='emp_id',<393>,10:13]]}, table_dictionary={<[Blend Data].[Branch B U8]>={<update where col U8>=[[@55,350:350='b',<393>,12:10]], <update select col U8>=[[@46,261:261='b',<393>,10:21]], emp_id=[[@42,251:251='b',<393>,10:11]]}}, setop=UNION, filters=[{substitution={name=<update where col U8>, type=column}, table_ref=b}], interface={metric_val=[{substitution={name=<update select col U8>, type=column}, table_ref=b}], emp_id=[{name=emp_id, table_ref=b}]}, table_alias={b=<[Blend Data].[Branch B U8]>}}, def_query0={query_dictionary={metric_val=[[@30,141:150='metric_val',<393>,6:49]], emp_id=[[@24,105:110='emp_id',<393>,6:13]]}, table_dictionary={<[Blend Data].[Branch A U8]>={<update where col U8>=[[@35,202:202='a',<393>,8:10]], <update select col U8>=[[@26,113:113='a',<393>,6:21]], emp_id=[[@22,103:103='a',<393>,6:11]]}}, filters=[{substitution={name=<update where col U8>, type=column}, table_ref=a}], interface={metric_val=[{substitution={name=<update select col U8>, type=column}, table_ref=a}], emp_id=[{name=emp_id, table_ref=a}]}, table_alias={a=<[Blend Data].[Branch A U8]>}}, interface={metric_val=query_column, emp_id=query_column}}, query_dictionary={metric_val=[[@18,72:81='metric_val',<393>,4:21]], emp_id=[[@14,62:67='emp_id',<393>,4:11]]}, interface={metric_val=[{name=metric_val, table_ref=u}], emp_id=[{name=emp_id, table_ref=u}]}, table_alias={u=union2}}}, table_alias={e=employees, x=union5}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void updateComplexSubstitutionU8ExceptIntersectNestedSubquery(){
		final String query = "UPDATE employees e"
				+ "\nSET score = x.metric_val"
				+ "\nFROM ("
				+ "\n  SELECT u.emp_id, u.metric_val"
				+ "\n  FROM ("
				+ "\n    SELECT a.emp_id, a.<update select col U8> AS metric_val"
				+ "\n    FROM <[Blend Data].[Branch A U8]> a"
				+ "\n    WHERE a.<update where col U8> > 0"
				+ "\n    EXCEPT"
				+ "\n    SELECT b.emp_id, b.<update select col U8> AS metric_val"
				+ "\n    FROM <[Blend Data].[Branch B U8]> b"
				+ "\n    WHERE b.<update where col U8> > 0"
				+ "\n  ) u"
				+ "\n  INTERSECT"
				+ "\n  SELECT c.emp_id, c.<update select col U8> AS metric_val"
				+ "\n  FROM <[Blend Data].[Branch C U8]> c"
				+ "\n  WHERE c.<update where col U8> > 0"
				+ "\n) x"
				+ "\nWHERE e.emp_id = x.emp_id";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={update={from={table={alias=x, query={intersect={1={select={1={column={name=emp_id, table_ref=u}}, 2={column={name=metric_val, table_ref=u}}}, from={table={alias=u, query={union={1={select={1={column={name=emp_id, table_ref=a}}, 2={column={substitution={name=<update select col U8>, type=column}, table_ref=a}, alias=metric_val}}, from={table={alias=a, substitution={name=<[Blend Data].[Branch A U8]>, parts={1=[Blend Data], 2=[Branch A U8]}, type=tuple}}}, where={condition={left={column={substitution={name=<update where col U8>, type=column}, table_ref=a}}, right={literal=0}, operator=>}}}, 2={union={qualifier=null, operator=EXCEPT}}, 3={select={1={column={name=emp_id, table_ref=b}}, 2={column={substitution={name=<update select col U8>, type=column}, table_ref=b}, alias=metric_val}}, from={table={alias=b, substitution={name=<[Blend Data].[Branch B U8]>, parts={1=[Blend Data], 2=[Branch B U8]}, type=tuple}}}, where={condition={left={column={substitution={name=<update where col U8>, type=column}, table_ref=b}}, right={literal=0}, operator=>}}}}}}}}, 2={intersect={qualifier=null, operator=INTERSECT}}, 3={select={1={column={name=emp_id, table_ref=c}}, 2={column={substitution={name=<update select col U8>, type=column}, table_ref=c}, alias=metric_val}}, from={table={alias=c, substitution={name=<[Blend Data].[Branch C U8]>, parts={1=[Blend Data], 2=[Branch C U8]}, type=tuple}}}, where={condition={left={column={substitution={name=<update where col U8>, type=column}, table_ref=c}}, right={literal=0}, operator=>}}}}}}}, where={condition={left={column={name=emp_id, table_ref=e}}, right={column={name=emp_id, table_ref=x}}, operator==}}, assignments={1={set={column={name=score, table_ref=null}}, to={column={name=metric_val, table_ref=x}}}}, table={alias=e, table=employees}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[score]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<[Blend Data].[Branch A U8]>=tuple, <update where col U8>=column, <update select col U8>=column, <[Blend Data].[Branch C U8]>=tuple, <[Blend Data].[Branch B U8]>=tuple}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{<[Blend Data].[Branch A U8]>={<update where col U8>=[[@35,202:202='a',<393>,8:10]], <update select col U8>=[[@26,113:113='a',<393>,6:21]], emp_id=[[@22,103:103='a',<393>,6:11]]}, <[Blend Data].[Branch C U8]>={<update where col U8>=[[@77,501:501='c',<393>,17:8]], <update select col U8>=[[@68,416:416='c',<393>,15:19]], emp_id=[[@64,406:406='c',<393>,15:9]]}, employees={score=[[@4,23:27='score',<393>,2:4]], emp_id=[[@85,539:539='e',<393>,19:6]]}, <[Blend Data].[Branch B U8]>={<update where col U8>=[[@55,351:351='b',<393>,12:10]], <update select col U8>=[[@46,262:262='b',<393>,10:21]], emp_id=[[@42,252:252='b',<393>,10:11]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{union2={metric_val=[[@16,70:70='u',<393>,4:19]], emp_id=[[@12,60:60='u',<393>,4:9]]}, intersect5={metric_val=[[@6,31:31='x',<393>,2:12]], emp_id=[[@89,550:550='x',<393>,19:17]]}, query4={metric_val=[[@72,444:453='metric_val',<393>,15:47]], emp_id=[[@66,408:413='emp_id',<393>,15:11]]}, update6={score=[[@4,23:27='score',<393>,2:4]]}, query0={metric_val=[[@30,141:150='metric_val',<393>,6:49]], emp_id=[[@24,105:110='emp_id',<393>,6:13]]}, query1={metric_val=[[@50,290:299='metric_val',<393>,10:49]], emp_id=[[@44,254:259='emp_id',<393>,10:13]]}, query3={metric_val=[[@18,72:81='metric_val',<393>,4:21]], emp_id=[[@14,62:67='emp_id',<393>,4:11]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_update6={assignments={score=[{name=metric_val, table_ref=x}]}, table_dictionary={employees={score=[[@4,23:27='score',<393>,2:4]], emp_id=[[@85,539:539='e',<393>,19:6]]}}, update_dictionary={score=[[@4,23:27='score',<393>,2:4]]}, def_intersect5={query_dictionary={metric_val=[[@6,31:31='x',<393>,2:12]], emp_id=[[@89,550:550='x',<393>,19:17]]}, interface={metric_val=query_column, emp_id=query_column}, def_query4={query_dictionary={metric_val=[[@72,444:453='metric_val',<393>,15:47]], emp_id=[[@66,408:413='emp_id',<393>,15:11]]}, table_dictionary={<[Blend Data].[Branch C U8]>={<update where col U8>=[[@77,501:501='c',<393>,17:8]], <update select col U8>=[[@68,416:416='c',<393>,15:19]], emp_id=[[@64,406:406='c',<393>,15:9]]}}, setop=INTERSECTION, filters=[{substitution={name=<update where col U8>, type=column}, table_ref=c}], interface={metric_val=[{substitution={name=<update select col U8>, type=column}, table_ref=c}], emp_id=[{name=emp_id, table_ref=c}]}, table_alias={c=<[Blend Data].[Branch C U8]>}}, def_query3={def_union2={query_dictionary={metric_val=[[@16,70:70='u',<393>,4:19]], emp_id=[[@12,60:60='u',<393>,4:9]]}, def_query1={query_dictionary={metric_val=[[@50,290:299='metric_val',<393>,10:49]], emp_id=[[@44,254:259='emp_id',<393>,10:13]]}, table_dictionary={<[Blend Data].[Branch B U8]>={<update where col U8>=[[@55,351:351='b',<393>,12:10]], <update select col U8>=[[@46,262:262='b',<393>,10:21]], emp_id=[[@42,252:252='b',<393>,10:11]]}}, setop=EXCEPT, filters=[{substitution={name=<update where col U8>, type=column}, table_ref=b}], interface={metric_val=[{substitution={name=<update select col U8>, type=column}, table_ref=b}], emp_id=[{name=emp_id, table_ref=b}]}, table_alias={b=<[Blend Data].[Branch B U8]>}}, def_query0={query_dictionary={metric_val=[[@30,141:150='metric_val',<393>,6:49]], emp_id=[[@24,105:110='emp_id',<393>,6:13]]}, table_dictionary={<[Blend Data].[Branch A U8]>={<update where col U8>=[[@35,202:202='a',<393>,8:10]], <update select col U8>=[[@26,113:113='a',<393>,6:21]], emp_id=[[@22,103:103='a',<393>,6:11]]}}, filters=[{substitution={name=<update where col U8>, type=column}, table_ref=a}], interface={metric_val=[{substitution={name=<update select col U8>, type=column}, table_ref=a}], emp_id=[{name=emp_id, table_ref=a}]}, table_alias={a=<[Blend Data].[Branch A U8]>}}, interface={metric_val=query_column, emp_id=query_column}}, query_dictionary={metric_val=[[@18,72:81='metric_val',<393>,4:21]], emp_id=[[@14,62:67='emp_id',<393>,4:11]]}, interface={metric_val=[{name=metric_val, table_ref=u}], emp_id=[{name=emp_id, table_ref=u}]}, table_alias={u=union2}}}, filters=[{name=emp_id, table_ref=e}, {name=emp_id, table_ref=x}], table_alias={e=employees, x=intersect5}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void updateComplexSubstitutionU9WithCteSelfUnionBranches() {
		final String query = "WITH blended AS ("
				+ "\n  SELECT a.emp_id, a.<update select col U9a> AS metric_val"
				+ "\n  FROM <[Union Data].[Feed Alpha U9]> a"
				+ "\n  WHERE a.<update where col U9a> > 0"
				+ "\n  UNION"
				+ "\n  SELECT b.emp_id, b.<update select col U9b> AS metric_val"
				+ "\n  FROM <[Union Data].[Feed Beta U9]> b"
				+ "\n  WHERE b.<update where col U9b> > 0"
				+ "\n)"
				+ "\nUPDATE employees e"
				+ "\nSET score = bl.metric_val"
				+ "\nFROM blended bl"
				+ "\nWHERE e.emp_id = bl.emp_id";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={with={1={cte={union={1={select={1={column={name=emp_id, table_ref=a}}, 2={column={substitution={name=<update select col U9a>, type=column}, table_ref=a}, alias=metric_val}}, from={table={alias=a, substitution={name=<[Union Data].[Feed Alpha U9]>, parts={1=[Union Data], 2=[Feed Alpha U9]}, type=tuple}}}, where={condition={left={column={substitution={name=<update where col U9a>, type=column}, table_ref=a}}, right={literal=0}, operator=>}}}, 2={union={qualifier=null, operator=UNION}}, 3={select={1={column={name=emp_id, table_ref=b}}, 2={column={substitution={name=<update select col U9b>, type=column}, table_ref=b}, alias=metric_val}}, from={table={alias=b, substitution={name=<[Union Data].[Feed Beta U9]>, parts={1=[Union Data], 2=[Feed Beta U9]}, type=tuple}}}, where={condition={left={column={substitution={name=<update where col U9b>, type=column}, table_ref=b}}, right={literal=0}, operator=>}}}}}, alias=blended}}, query={update={from={table={alias=bl, table=blended}}, where={condition={left={column={name=emp_id, table_ref=e}}, right={column={name=emp_id, table_ref=bl}}, operator==}}, assignments={1={set={column={name=score, table_ref=null}}, to={column={name=metric_val, table_ref=bl}}}}, table={alias=e, table=employees}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[score]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<[Union Data].[Feed Alpha U9]>=tuple, <update select col U9b>=column, <update select col U9a>=column, <[Union Data].[Feed Beta U9]>=tuple, <update where col U9a>=column, <update where col U9b>=column}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{<[Union Data].[Feed Alpha U9]>={<update select col U9a>=[[@9,37:37='a',<393>,2:19]], <update where col U9a>=[[@18,125:125='a',<393>,4:8]], emp_id=[[@5,27:27='a',<393>,2:9]]}, <[Union Data].[Feed Beta U9]>={<update select col U9b>=[[@29,181:181='b',<393>,6:19]], emp_id=[[@25,171:171='b',<393>,6:9]], <update where col U9b>=[[@38,268:268='b',<393>,8:8]]}, employees={score=[[@48,322:326='score',<393>,11:4]], emp_id=[[@57,366:366='e',<393>,13:6]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{union2={metric_val=[[@50,330:331='bl',<393>,11:12]], emp_id=[[@61,377:378='bl',<393>,13:17]]}, query0={metric_val=[[@13,66:75='metric_val',<393>,2:48]], emp_id=[[@7,29:34='emp_id',<393>,2:11]]}, update3={score=[[@48,322:326='score',<393>,11:4]]}, query1={metric_val=[[@33,210:219='metric_val',<393>,6:48]], emp_id=[[@27,173:178='emp_id',<393>,6:11]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_update3={context_list={bl=union2, blended=union2}, assignments={score=[{name=metric_val, table_ref=bl}]}, def_union2={query_dictionary={metric_val=[[@50,330:331='bl',<393>,11:12]], emp_id=[[@61,377:378='bl',<393>,13:17]]}, def_query1={query_dictionary={metric_val=[[@33,210:219='metric_val',<393>,6:48]], emp_id=[[@27,173:178='emp_id',<393>,6:11]]}, table_dictionary={<[Union Data].[Feed Beta U9]>={<update select col U9b>=[[@29,181:181='b',<393>,6:19]], emp_id=[[@25,171:171='b',<393>,6:9]], <update where col U9b>=[[@38,268:268='b',<393>,8:8]]}}, setop=UNION, filters=[{substitution={name=<update where col U9b>, type=column}, table_ref=b}], interface={metric_val=[{substitution={name=<update select col U9b>, type=column}, table_ref=b}], emp_id=[{name=emp_id, table_ref=b}]}, table_alias={b=<[Union Data].[Feed Beta U9]>}}, def_query0={query_dictionary={metric_val=[[@13,66:75='metric_val',<393>,2:48]], emp_id=[[@7,29:34='emp_id',<393>,2:11]]}, table_dictionary={<[Union Data].[Feed Alpha U9]>={<update select col U9a>=[[@9,37:37='a',<393>,2:19]], <update where col U9a>=[[@18,125:125='a',<393>,4:8]], emp_id=[[@5,27:27='a',<393>,2:9]]}}, filters=[{substitution={name=<update where col U9a>, type=column}, table_ref=a}], interface={metric_val=[{substitution={name=<update select col U9a>, type=column}, table_ref=a}], emp_id=[{name=emp_id, table_ref=a}]}, table_alias={a=<[Union Data].[Feed Alpha U9]>}}, interface={metric_val=query_column, emp_id=query_column}}, table_dictionary={employees={score=[[@48,322:326='score',<393>,11:4]], emp_id=[[@57,366:366='e',<393>,13:6]]}}, update_dictionary={score=[[@48,322:326='score',<393>,11:4]]}, filters=[{name=emp_id, table_ref=e}, {name=emp_id, table_ref=bl}], table_alias={blended=union2, e=employees, bl=union2}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void updateComplexSubstitutionU9WithCteSelfExceptBranches(){
		final String query = "WITH blended AS ("
				+ "\n  SELECT a.emp_id, a.<update select col U9a> AS metric_val"
				+ "\n  FROM <[except Data].[Feed Alpha U9]> a"
				+ "\n  WHERE a.<update where col U9a> > 0"
				+ "\n  EXCEPT"
				+ "\n  SELECT b.emp_id, b.<update select col U9b> AS metric_val"
				+ "\n  FROM <[except Data].[Feed Beta U9]> b"
				+ "\n  WHERE b.<update where col U9b> > 0"
				+ "\n)"
				+ "\nUPDATE employees e"
				+ "\nSET score = bl.metric_val"
				+ "\nFROM blended bl"
				+ "\nWHERE e.emp_id = bl.emp_id";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={with={1={cte={union={1={select={1={column={name=emp_id, table_ref=a}}, 2={column={substitution={name=<update select col U9a>, type=column}, table_ref=a}, alias=metric_val}}, from={table={alias=a, substitution={name=<[except Data].[Feed Alpha U9]>, parts={1=[except Data], 2=[Feed Alpha U9]}, type=tuple}}}, where={condition={left={column={substitution={name=<update where col U9a>, type=column}, table_ref=a}}, right={literal=0}, operator=>}}}, 2={union={qualifier=null, operator=EXCEPT}}, 3={select={1={column={name=emp_id, table_ref=b}}, 2={column={substitution={name=<update select col U9b>, type=column}, table_ref=b}, alias=metric_val}}, from={table={alias=b, substitution={name=<[except Data].[Feed Beta U9]>, parts={1=[except Data], 2=[Feed Beta U9]}, type=tuple}}}, where={condition={left={column={substitution={name=<update where col U9b>, type=column}, table_ref=b}}, right={literal=0}, operator=>}}}}}, alias=blended}}, query={update={from={table={alias=bl, table=blended}}, where={condition={left={column={name=emp_id, table_ref=e}}, right={column={name=emp_id, table_ref=bl}}, operator==}}, assignments={1={set={column={name=score, table_ref=null}}, to={column={name=metric_val, table_ref=bl}}}}, table={alias=e, table=employees}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[score]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<update select col U9b>=column, <update select col U9a>=column, <[except Data].[Feed Alpha U9]>=tuple, <[except Data].[Feed Beta U9]>=tuple, <update where col U9a>=column, <update where col U9b>=column}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{<[except Data].[Feed Alpha U9]>={<update select col U9a>=[[@9,37:37='a',<393>,2:19]], <update where col U9a>=[[@18,126:126='a',<393>,4:8]], emp_id=[[@5,27:27='a',<393>,2:9]]}, <[except Data].[Feed Beta U9]>={<update select col U9b>=[[@29,183:183='b',<393>,6:19]], emp_id=[[@25,173:173='b',<393>,6:9]], <update where col U9b>=[[@38,271:271='b',<393>,8:8]]}, employees={score=[[@48,325:329='score',<393>,11:4]], emp_id=[[@57,369:369='e',<393>,13:6]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{union2={metric_val=[[@50,333:334='bl',<393>,11:12]], emp_id=[[@61,380:381='bl',<393>,13:17]]}, query0={metric_val=[[@13,66:75='metric_val',<393>,2:48]], emp_id=[[@7,29:34='emp_id',<393>,2:11]]}, update3={score=[[@48,325:329='score',<393>,11:4]]}, query1={metric_val=[[@33,212:221='metric_val',<393>,6:48]], emp_id=[[@27,175:180='emp_id',<393>,6:11]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_update3={context_list={bl=union2, blended=union2}, assignments={score=[{name=metric_val, table_ref=bl}]}, def_union2={query_dictionary={metric_val=[[@50,333:334='bl',<393>,11:12]], emp_id=[[@61,380:381='bl',<393>,13:17]]}, def_query1={query_dictionary={metric_val=[[@33,212:221='metric_val',<393>,6:48]], emp_id=[[@27,175:180='emp_id',<393>,6:11]]}, table_dictionary={<[except Data].[Feed Beta U9]>={<update select col U9b>=[[@29,183:183='b',<393>,6:19]], emp_id=[[@25,173:173='b',<393>,6:9]], <update where col U9b>=[[@38,271:271='b',<393>,8:8]]}}, setop=EXCEPT, filters=[{substitution={name=<update where col U9b>, type=column}, table_ref=b}], interface={metric_val=[{substitution={name=<update select col U9b>, type=column}, table_ref=b}], emp_id=[{name=emp_id, table_ref=b}]}, table_alias={b=<[except Data].[Feed Beta U9]>}}, def_query0={query_dictionary={metric_val=[[@13,66:75='metric_val',<393>,2:48]], emp_id=[[@7,29:34='emp_id',<393>,2:11]]}, table_dictionary={<[except Data].[Feed Alpha U9]>={<update select col U9a>=[[@9,37:37='a',<393>,2:19]], <update where col U9a>=[[@18,126:126='a',<393>,4:8]], emp_id=[[@5,27:27='a',<393>,2:9]]}}, filters=[{substitution={name=<update where col U9a>, type=column}, table_ref=a}], interface={metric_val=[{substitution={name=<update select col U9a>, type=column}, table_ref=a}], emp_id=[{name=emp_id, table_ref=a}]}, table_alias={a=<[except Data].[Feed Alpha U9]>}}, interface={metric_val=query_column, emp_id=query_column}}, table_dictionary={employees={score=[[@48,325:329='score',<393>,11:4]], emp_id=[[@57,369:369='e',<393>,13:6]]}}, update_dictionary={score=[[@48,325:329='score',<393>,11:4]]}, filters=[{name=emp_id, table_ref=e}, {name=emp_id, table_ref=bl}], table_alias={blended=union2, e=employees, bl=union2}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void updateComplexSubstitutionU10SubqueryGroupByHavingQualifyCombined() {
		final String query = "UPDATE employees e"
				+ "\nSET score = src.total_score"
				+ "\nFROM ("
				+ "\n  SELECT a.emp_id, sum(a.<update select col U10>) AS total_score,"
				+ "\n         row_number() OVER (PARTITION BY a.emp_id ORDER BY a.<update order col U10> DESC) AS rn"
				+ "\n  FROM <[Agg Data].[Fact Table U10]> a"
				+ "\n  WHERE a.<update where col U10> > 0"
				+ "\n  GROUP BY a.emp_id, a.<update group col U10>"
				+ "\n  HAVING sum(a.<update select col U10>) > 0"
				+ "\n  QUALIFY rn = 1"
				+ "\n) src"
				+ "\nWHERE e.emp_id = src.emp_id";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={update={from={table={alias=src, query={select={1={column={name=emp_id, table_ref=a}}, 2={function={function_name=sum, qualifier=null, parameters={column={substitution={name=<update select col U10>, type=column}, table_ref=a}}}, alias=total_score}, 3={alias=rn, window_function={over={partition_by={1={column={name=emp_id, table_ref=a}}}, orderby={1={null_order=null, predicand={column={substitution={name=<update order col U10>, type=column}, table_ref=a}}, sort_order=DESC}}}, function={function_name=row_number, parameters=null}}}}, having={condition={left={function={function_name=sum, qualifier=null, parameters={column={substitution={name=<update select col U10>, type=column}, table_ref=a}}}}, right={literal=0}, operator=>}}, from={table={alias=a, substitution={name=<[Agg Data].[Fact Table U10]>, parts={1=[Agg Data], 2=[Fact Table U10]}, type=tuple}}}, where={condition={left={column={substitution={name=<update where col U10>, type=column}, table_ref=a}}, right={literal=0}, operator=>}}, groupby={1={column={name=emp_id, table_ref=a}}, 2={column={substitution={name=<update group col U10>, type=column}, table_ref=a}}}, qualify={condition={left={column={name=rn, table_ref=null}}, right={literal=1}, operator==}}}}}, where={condition={left={column={name=emp_id, table_ref=e}}, right={column={name=emp_id, table_ref=src}}, operator==}}, assignments={1={set={column={name=score, table_ref=null}}, to={column={name=total_score, table_ref=src}}}}, table={alias=e, table=employees}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[score]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<update order col U10>=column, <update where col U10>=column, <update group col U10>=column, <[Agg Data].[Fact Table U10]>=tuple, <update select col U10>=column}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{<[Agg Data].[Fact Table U10]>={<update order col U10>=[[@37,179:179='a',<393>,5:59]], <update where col U10>=[[@48,263:263='a',<393>,7:8]], <update group col U10>=[[@59,313:313='a',<393>,8:21]], <update select col U10>=[[@18,77:77='a',<393>,4:23], [@65,351:351='a',<393>,9:13]], emp_id=[[@12,63:63='a',<393>,4:9], [@32,161:161='a',<393>,5:41], [@55,303:303='a',<393>,8:11]]}, employees={score=[[@4,23:27='score',<393>,2:4]], emp_id=[[@78,411:411='e',<393>,12:6]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={total_score=[[@23,107:117='total_score',<393>,4:53], [@6,31:33='src',<393>,2:12]], rn=[[@43,213:214='rn',<393>,5:93], [@72,392:393='rn',<393>,10:10]], emp_id=[[@14,65:70='emp_id',<393>,4:11], [@82,422:424='src',<393>,12:17]]}, update1={score=[[@4,23:27='score',<393>,2:4]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_update1={assignments={score=[{name=total_score, table_ref=src}]}, table_dictionary={employees={score=[[@4,23:27='score',<393>,2:4]], emp_id=[[@78,411:411='e',<393>,12:6]]}}, update_dictionary={score=[[@4,23:27='score',<393>,2:4]]}, def_query0={window_ordered_by=[{substitution={name=<update order col U10>, type=column}, table_ref=a}], query_dictionary={total_score=[[@23,107:117='total_score',<393>,4:53], [@6,31:33='src',<393>,2:12]], rn=[[@43,213:214='rn',<393>,5:93], [@72,392:393='rn',<393>,10:10]], emp_id=[[@14,65:70='emp_id',<393>,4:11], [@82,422:424='src',<393>,12:17]]}, table_dictionary={<[Agg Data].[Fact Table U10]>={<update order col U10>=[[@37,179:179='a',<393>,5:59]], <update where col U10>=[[@48,263:263='a',<393>,7:8]], <update group col U10>=[[@59,313:313='a',<393>,8:21]], <update select col U10>=[[@18,77:77='a',<393>,4:23], [@65,351:351='a',<393>,9:13]], emp_id=[[@12,63:63='a',<393>,4:9], [@32,161:161='a',<393>,5:41], [@55,303:303='a',<393>,8:11]]}}, grouped_by=[{name=emp_id, table_ref=a}, {substitution={name=<update group col U10>, type=column}, table_ref=a}], window_partition_by=[{name=emp_id, table_ref=a}], filters=[{substitution={name=<update where col U10>, type=column}, table_ref=a}, {substitution={name=<update select col U10>, type=column}, table_ref=a}, {name=rn, table_ref=query0}], interface={total_score=[{substitution={name=<update select col U10>, type=column}, table_ref=a}], rn=[{name=emp_id, table_ref=a}, {substitution={name=<update order col U10>, type=column}, table_ref=a}], emp_id=[{name=emp_id, table_ref=a}]}, table_alias={a=<[Agg Data].[Fact Table U10]>}}, filters=[{name=emp_id, table_ref=e}, {name=emp_id, table_ref=src}], table_alias={e=employees, src=query0}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void deleteComplexSubstitutionD1WithCteGroupByHaving() {
		final String query = "WITH staged AS ("
				+ "\n  SELECT a.emp_id, sum(a.<delete select col D1>) AS total_score"
				+ "\n  FROM <[HR Data].[Employee Accounts D1]> a"
				+ "\n  WHERE a.<delete where col D1> > 0"
				+ "\n  GROUP BY a.emp_id, a.<delete group col D1>"
				+ "\n  HAVING sum(a.<delete select col D1>) > 0)"
				+ "\nDELETE FROM employees e"
				+ "\nUSING staged s"
				+ "\nWHERE e.emp_id = s.emp_id AND s.total_score > 0";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={with={1={cte={select={1={column={name=emp_id, table_ref=a}}, 2={function={function_name=sum, qualifier=null, parameters={column={substitution={name=<delete select col D1>, type=column}, table_ref=a}}}, alias=total_score}}, having={condition={left={function={function_name=sum, qualifier=null, parameters={column={substitution={name=<delete select col D1>, type=column}, table_ref=a}}}}, right={literal=0}, operator=>}}, from={table={alias=a, substitution={name=<[HR Data].[Employee Accounts D1]>, parts={1=[HR Data], 2=[Employee Accounts D1]}, type=tuple}}}, where={condition={left={column={substitution={name=<delete where col D1>, type=column}, table_ref=a}}, right={literal=0}, operator=>}}, groupby={1={column={name=emp_id, table_ref=a}}, 2={column={substitution={name=<delete group col D1>, type=column}, table_ref=a}}}}, alias=staged}}, query={delete={table={alias=e, table=employees}, using={1={table={alias=s, table=staged}}}, where={and={1={condition={left={column={name=emp_id, table_ref=e}}, right={column={name=emp_id, table_ref=s}}, operator==}}, 2={condition={left={column={name=total_score, table_ref=s}}, right={literal=0}, operator=>}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<delete select col D1>=column, <delete group col D1>=column, <delete where col D1>=column, <[HR Data].[Employee Accounts D1]>=tuple}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{<[HR Data].[Employee Accounts D1]>={<delete select col D1>=[[@11,40:40='a',<393>,2:23], [@38,219:219='a',<393>,6:13]], <delete group col D1>=[[@32,182:182='a',<393>,5:21]], <delete where col D1>=[[@21,133:133='a',<393>,4:8]], emp_id=[[@5,26:26='a',<393>,2:9], [@28,172:172='a',<393>,5:11]]}, employees={emp_id=[[@53,295:295='e',<393>,9:6]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={total_score=[[@16,69:79='total_score',<393>,2:52], [@61,319:319='s',<393>,9:30]], emp_id=[[@7,28:33='emp_id',<393>,2:11], [@57,306:306='s',<393>,9:17]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_delete1={context_list={staged=query0}, query_dictionary={}, table_dictionary={employees={emp_id=[[@53,295:295='e',<393>,9:6]]}}, def_query0={query_dictionary={total_score=[[@16,69:79='total_score',<393>,2:52], [@61,319:319='s',<393>,9:30]], emp_id=[[@7,28:33='emp_id',<393>,2:11], [@57,306:306='s',<393>,9:17]]}, table_dictionary={<[HR Data].[Employee Accounts D1]>={<delete select col D1>=[[@11,40:40='a',<393>,2:23], [@38,219:219='a',<393>,6:13]], <delete group col D1>=[[@32,182:182='a',<393>,5:21]], <delete where col D1>=[[@21,133:133='a',<393>,4:8]], emp_id=[[@5,26:26='a',<393>,2:9], [@28,172:172='a',<393>,5:11]]}}, grouped_by=[{name=emp_id, table_ref=a}, {substitution={name=<delete group col D1>, type=column}, table_ref=a}], filters=[{substitution={name=<delete where col D1>, type=column}, table_ref=a}, {substitution={name=<delete select col D1>, type=column}, table_ref=a}], interface={total_score=[{substitution={name=<delete select col D1>, type=column}, table_ref=a}], emp_id=[{name=emp_id, table_ref=a}]}, table_alias={a=<[HR Data].[Employee Accounts D1]>}}, filters=[{name=emp_id, table_ref=e}, {name=emp_id, table_ref=s}, {name=total_score, table_ref=s}], interface=null, table_alias={s=query0, e=employees, staged=query0}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void deleteComplexSubstitutionD2SubqueryUnionWhereSubstitutions() {
		final String query = "DELETE FROM employees e"
				+ "\nUSING ("
				+ "\n  SELECT a.emp_id, a.<delete select col D2> AS metric_val"
				+ "\n  FROM <[Sales Data].[Perf Feed D2]> a"
				+ "\n  WHERE a.<delete where col D2> > 0"
				+ "\n  UNION"
				+ "\n  SELECT b.dept_id, b.<delete select col D2b> AS metric_val"
				+ "\n  FROM <[Sales Data].[Quota Feed D2]> b"
				+ "\n  WHERE b.<delete where col D2b> > 0"
				+ "\n) src"
				+ "\nWHERE e.emp_id = src.emp_id";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={delete={table={alias=e, table=employees}, using={1={table={alias=src, query={union={1={select={1={column={name=emp_id, table_ref=a}}, 2={column={substitution={name=<delete select col D2>, type=column}, table_ref=a}, alias=metric_val}}, from={table={alias=a, substitution={name=<[Sales Data].[Perf Feed D2]>, parts={1=[Sales Data], 2=[Perf Feed D2]}, type=tuple}}}, where={condition={left={column={substitution={name=<delete where col D2>, type=column}, table_ref=a}}, right={literal=0}, operator=>}}}, 2={union={qualifier=null, operator=UNION}}, 3={select={1={column={name=dept_id, table_ref=b}}, 2={column={substitution={name=<delete select col D2b>, type=column}, table_ref=b}, alias=metric_val}}, from={table={alias=b, substitution={name=<[Sales Data].[Quota Feed D2]>, parts={1=[Sales Data], 2=[Quota Feed D2]}, type=tuple}}}, where={condition={left={column={substitution={name=<delete where col D2b>, type=column}, table_ref=b}}, right={literal=0}, operator=>}}}}}}}}, where={condition={left={column={name=emp_id, table_ref=e}}, right={column={name=emp_id, table_ref=src}}, operator==}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<delete select col D2>=column, <[Sales Data].[Perf Feed D2]>=tuple, <delete where col D2>=column, <[Sales Data].[Quota Feed D2]>=tuple, <delete where col D2b>=column, <delete select col D2b>=column}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{<[Sales Data].[Perf Feed D2]>={<delete select col D2>=[[@11,51:51='a',<393>,3:19]], <delete where col D2>=[[@20,137:137='a',<393>,5:8]], emp_id=[[@7,41:41='a',<393>,3:9]]}, <[Sales Data].[Quota Feed D2]>={dept_id=[[@27,182:182='b',<393>,7:9]], <delete where col D2b>=[[@40,281:281='b',<393>,9:8]], <delete select col D2b>=[[@31,193:193='b',<393>,7:20]]}, employees={emp_id=[[@48,322:322='e',<393>,11:6]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{union2={emp_id=[[@52,333:335='src',<393>,11:17]]}, query0={metric_val=[[@15,79:88='metric_val',<393>,3:47]], emp_id=[[@9,43:48='emp_id',<393>,3:11]]}, query1={dept_id=[[@29,184:190='dept_id',<393>,7:11]], metric_val=[[@35,222:231='metric_val',<393>,7:49]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_delete3={def_union2={query_dictionary={emp_id=[[@52,333:335='src',<393>,11:17]]}, def_query1={query_dictionary={metric_val=[[@35,222:231='metric_val',<393>,7:49]], dept_id=[[@29,184:190='dept_id',<393>,7:11]]}, table_dictionary={<[Sales Data].[Quota Feed D2]>={dept_id=[[@27,182:182='b',<393>,7:9]], <delete where col D2b>=[[@40,281:281='b',<393>,9:8]], <delete select col D2b>=[[@31,193:193='b',<393>,7:20]]}}, setop=UNION, filters=[{substitution={name=<delete where col D2b>, type=column}, table_ref=b}], interface={metric_val=[{substitution={name=<delete select col D2b>, type=column}, table_ref=b}], dept_id=[{name=dept_id, table_ref=b}]}, table_alias={b=<[Sales Data].[Quota Feed D2]>}}, def_query0={query_dictionary={metric_val=[[@15,79:88='metric_val',<393>,3:47]], emp_id=[[@9,43:48='emp_id',<393>,3:11]]}, table_dictionary={<[Sales Data].[Perf Feed D2]>={<delete select col D2>=[[@11,51:51='a',<393>,3:19]], <delete where col D2>=[[@20,137:137='a',<393>,5:8]], emp_id=[[@7,41:41='a',<393>,3:9]]}}, filters=[{substitution={name=<delete where col D2>, type=column}, table_ref=a}], interface={metric_val=[{substitution={name=<delete select col D2>, type=column}, table_ref=a}], emp_id=[{name=emp_id, table_ref=a}]}, table_alias={a=<[Sales Data].[Perf Feed D2]>}}, interface={metric_val=query_column, emp_id=query_column}}, query_dictionary={}, table_dictionary={employees={emp_id=[[@48,322:322='e',<393>,11:6]]}}, filters=[{name=emp_id, table_ref=e}, {name=emp_id, table_ref=src}], interface=null, table_alias={e=employees, src=union2}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void deleteComplexSubstitutionD2SubqueryExceptWhereSubstitutions(){
		final String query = "DELETE FROM employees e"
				+ "\nUSING ("
				+ "\n  SELECT a.emp_id, a.<delete select col D2> AS metric_val"
				+ "\n  FROM <[Sales Data].[Perf Feed D2]> a"
				+ "\n  WHERE a.<delete where col D2> > 0"
				+ "\n  EXCEPT"
				+ "\n  SELECT b.dept_id, b.<delete select col D2b> AS metric_val"
				+ "\n  FROM <[Sales Data].[Quota Feed D2]> b"
				+ "\n  WHERE b.<delete where col D2b> > 0"
				+ "\n) src"
				+ "\nWHERE e.emp_id = src.emp_id";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={delete={table={alias=e, table=employees}, using={1={table={alias=src, query={union={1={select={1={column={name=emp_id, table_ref=a}}, 2={column={substitution={name=<delete select col D2>, type=column}, table_ref=a}, alias=metric_val}}, from={table={alias=a, substitution={name=<[Sales Data].[Perf Feed D2]>, parts={1=[Sales Data], 2=[Perf Feed D2]}, type=tuple}}}, where={condition={left={column={substitution={name=<delete where col D2>, type=column}, table_ref=a}}, right={literal=0}, operator=>}}}, 2={union={qualifier=null, operator=EXCEPT}}, 3={select={1={column={name=dept_id, table_ref=b}}, 2={column={substitution={name=<delete select col D2b>, type=column}, table_ref=b}, alias=metric_val}}, from={table={alias=b, substitution={name=<[Sales Data].[Quota Feed D2]>, parts={1=[Sales Data], 2=[Quota Feed D2]}, type=tuple}}}, where={condition={left={column={substitution={name=<delete where col D2b>, type=column}, table_ref=b}}, right={literal=0}, operator=>}}}}}}}}, where={condition={left={column={name=emp_id, table_ref=e}}, right={column={name=emp_id, table_ref=src}}, operator==}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<delete select col D2>=column, <[Sales Data].[Perf Feed D2]>=tuple, <delete where col D2>=column, <[Sales Data].[Quota Feed D2]>=tuple, <delete where col D2b>=column, <delete select col D2b>=column}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{<[Sales Data].[Perf Feed D2]>={<delete select col D2>=[[@11,51:51='a',<393>,3:19]], <delete where col D2>=[[@20,137:137='a',<393>,5:8]], emp_id=[[@7,41:41='a',<393>,3:9]]}, <[Sales Data].[Quota Feed D2]>={dept_id=[[@27,183:183='b',<393>,7:9]], <delete where col D2b>=[[@40,282:282='b',<393>,9:8]], <delete select col D2b>=[[@31,194:194='b',<393>,7:20]]}, employees={emp_id=[[@48,323:323='e',<393>,11:6]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{union2={emp_id=[[@52,334:336='src',<393>,11:17]]}, query0={metric_val=[[@15,79:88='metric_val',<393>,3:47]], emp_id=[[@9,43:48='emp_id',<393>,3:11]]}, query1={dept_id=[[@29,185:191='dept_id',<393>,7:11]], metric_val=[[@35,223:232='metric_val',<393>,7:49]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_delete3={def_union2={query_dictionary={emp_id=[[@52,334:336='src',<393>,11:17]]}, def_query1={query_dictionary={metric_val=[[@35,223:232='metric_val',<393>,7:49]], dept_id=[[@29,185:191='dept_id',<393>,7:11]]}, table_dictionary={<[Sales Data].[Quota Feed D2]>={dept_id=[[@27,183:183='b',<393>,7:9]], <delete where col D2b>=[[@40,282:282='b',<393>,9:8]], <delete select col D2b>=[[@31,194:194='b',<393>,7:20]]}}, setop=EXCEPT, filters=[{substitution={name=<delete where col D2b>, type=column}, table_ref=b}], interface={metric_val=[{substitution={name=<delete select col D2b>, type=column}, table_ref=b}], dept_id=[{name=dept_id, table_ref=b}]}, table_alias={b=<[Sales Data].[Quota Feed D2]>}}, def_query0={query_dictionary={metric_val=[[@15,79:88='metric_val',<393>,3:47]], emp_id=[[@9,43:48='emp_id',<393>,3:11]]}, table_dictionary={<[Sales Data].[Perf Feed D2]>={<delete select col D2>=[[@11,51:51='a',<393>,3:19]], <delete where col D2>=[[@20,137:137='a',<393>,5:8]], emp_id=[[@7,41:41='a',<393>,3:9]]}}, filters=[{substitution={name=<delete where col D2>, type=column}, table_ref=a}], interface={metric_val=[{substitution={name=<delete select col D2>, type=column}, table_ref=a}], emp_id=[{name=emp_id, table_ref=a}]}, table_alias={a=<[Sales Data].[Perf Feed D2]>}}, interface={metric_val=query_column, emp_id=query_column}}, query_dictionary={}, table_dictionary={employees={emp_id=[[@48,323:323='e',<393>,11:6]]}}, filters=[{name=emp_id, table_ref=e}, {name=emp_id, table_ref=src}], interface=null, table_alias={e=employees, src=union2}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void deleteComplexSubstitutionD3WithCteIntersectOrderBySubstitution() {
		final String query = "WITH base AS ("
				+ "\n  SELECT a.emp_id, a.<delete select col D3> AS score_val"
				+ "\n  FROM <[Ops Data].[Account Ledger D3]> a"
				+ "\n  WHERE a.<delete where col D3> > 0"
				+ "\n  INTERSECT"
				+ "\n  SELECT b.emp_id, b.<delete select col D3> AS score_val"
				+ "\n  FROM <[Ops Data].[Audit Ledger D3]> b"
				+ "\n  WHERE b.<delete where col D3> > 0"
				+ "\n)"
				+ "\nDELETE FROM employees e"
				+ "\nUSING base b"
				+ "\nWHERE e.emp_id = b.emp_id";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={with={1={cte={intersect={1={select={1={column={name=emp_id, table_ref=a}}, 2={column={substitution={name=<delete select col D3>, type=column}, table_ref=a}, alias=score_val}}, from={table={alias=a, substitution={name=<[Ops Data].[Account Ledger D3]>, parts={1=[Ops Data], 2=[Account Ledger D3]}, type=tuple}}}, where={condition={left={column={substitution={name=<delete where col D3>, type=column}, table_ref=a}}, right={literal=0}, operator=>}}}, 2={intersect={qualifier=null, operator=INTERSECT}}, 3={select={1={column={name=emp_id, table_ref=b}}, 2={column={substitution={name=<delete select col D3>, type=column}, table_ref=b}, alias=score_val}}, from={table={alias=b, substitution={name=<[Ops Data].[Audit Ledger D3]>, parts={1=[Ops Data], 2=[Audit Ledger D3]}, type=tuple}}}, where={condition={left={column={substitution={name=<delete where col D3>, type=column}, table_ref=b}}, right={literal=0}, operator=>}}}}}, alias=base}}, query={delete={table={alias=e, table=employees}, using={1={table={alias=b, table=base}}}, where={condition={left={column={name=emp_id, table_ref=e}}, right={column={name=emp_id, table_ref=b}}, operator==}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<delete select col D3>=column, <delete where col D3>=column, <[Ops Data].[Audit Ledger D3]>=tuple, <[Ops Data].[Account Ledger D3]>=tuple}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{<[Ops Data].[Audit Ledger D3]>={<delete select col D3>=[[@29,181:181='b',<393>,6:19]], <delete where col D3>=[[@38,267:267='b',<393>,8:8]], emp_id=[[@25,171:171='b',<393>,6:9]]}, <[Ops Data].[Account Ledger D3]>={<delete select col D3>=[[@9,34:34='a',<393>,2:19]], <delete where col D3>=[[@18,122:122='a',<393>,4:8]], emp_id=[[@5,24:24='a',<393>,2:9]]}, employees={emp_id=[[@52,340:340='e',<393>,12:6]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{intersect2={emp_id=[[@56,351:351='b',<393>,12:17]]}, query0={score_val=[[@13,62:70='score_val',<393>,2:47]], emp_id=[[@7,26:31='emp_id',<393>,2:11]]}, query1={score_val=[[@33,209:217='score_val',<393>,6:47]], emp_id=[[@27,173:178='emp_id',<393>,6:11]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_delete3={context_list={base=intersect2}, query_dictionary={}, table_dictionary={employees={emp_id=[[@52,340:340='e',<393>,12:6]]}}, def_intersect2={query_dictionary={emp_id=[[@56,351:351='b',<393>,12:17]]}, def_query1={query_dictionary={score_val=[[@33,209:217='score_val',<393>,6:47]], emp_id=[[@27,173:178='emp_id',<393>,6:11]]}, table_dictionary={<[Ops Data].[Audit Ledger D3]>={<delete select col D3>=[[@29,181:181='b',<393>,6:19]], <delete where col D3>=[[@38,267:267='b',<393>,8:8]], emp_id=[[@25,171:171='b',<393>,6:9]]}}, setop=INTERSECTION, filters=[{substitution={name=<delete where col D3>, type=column}, table_ref=b}], interface={score_val=[{substitution={name=<delete select col D3>, type=column}, table_ref=b}], emp_id=[{name=emp_id, table_ref=b}]}, table_alias={b=<[Ops Data].[Audit Ledger D3]>}}, def_query0={query_dictionary={score_val=[[@13,62:70='score_val',<393>,2:47]], emp_id=[[@7,26:31='emp_id',<393>,2:11]]}, table_dictionary={<[Ops Data].[Account Ledger D3]>={<delete select col D3>=[[@9,34:34='a',<393>,2:19]], <delete where col D3>=[[@18,122:122='a',<393>,4:8]], emp_id=[[@5,24:24='a',<393>,2:9]]}}, filters=[{substitution={name=<delete where col D3>, type=column}, table_ref=a}], interface={score_val=[{substitution={name=<delete select col D3>, type=column}, table_ref=a}], emp_id=[{name=emp_id, table_ref=a}]}, table_alias={a=<[Ops Data].[Account Ledger D3]>}}, interface={score_val=query_column, emp_id=query_column}}, filters=[{name=emp_id, table_ref=e}, {name=emp_id, table_ref=b}], interface=null, table_alias={b=intersect2, e=employees, base=intersect2}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void deleteComplexSubstitutionD3WithCteExceptOrderBySubstitution() {
		final String query = "WITH base AS ("
				+ "\n  SELECT a.emp_id, a.<delete select col D3> AS score_val"
				+ "\n  FROM <[Ops Data].[Account Ledger D3]> a"
				+ "\n  WHERE a.<delete where col D3> > 0"
				+ "\n  EXCEPT"
				+ "\n  SELECT b.emp_id, b.<delete select col D3> AS score_val"
				+ "\n  FROM <[Ops Data].[Audit Ledger D3]> b"
				+ "\n  WHERE b.<delete where col D3> > 0"
				+ "\n)"
				+ "\nDELETE FROM employees e"
				+ "\nUSING base b"
				+ "\nWHERE e.emp_id = b.emp_id";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={with={1={cte={union={1={select={1={column={name=emp_id, table_ref=a}}, 2={column={substitution={name=<delete select col D3>, type=column}, table_ref=a}, alias=score_val}}, from={table={alias=a, substitution={name=<[Ops Data].[Account Ledger D3]>, parts={1=[Ops Data], 2=[Account Ledger D3]}, type=tuple}}}, where={condition={left={column={substitution={name=<delete where col D3>, type=column}, table_ref=a}}, right={literal=0}, operator=>}}}, 2={union={qualifier=null, operator=EXCEPT}}, 3={select={1={column={name=emp_id, table_ref=b}}, 2={column={substitution={name=<delete select col D3>, type=column}, table_ref=b}, alias=score_val}}, from={table={alias=b, substitution={name=<[Ops Data].[Audit Ledger D3]>, parts={1=[Ops Data], 2=[Audit Ledger D3]}, type=tuple}}}, where={condition={left={column={substitution={name=<delete where col D3>, type=column}, table_ref=b}}, right={literal=0}, operator=>}}}}}, alias=base}}, query={delete={table={alias=e, table=employees}, using={1={table={alias=b, table=base}}}, where={condition={left={column={name=emp_id, table_ref=e}}, right={column={name=emp_id, table_ref=b}}, operator==}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<delete select col D3>=column, <delete where col D3>=column, <[Ops Data].[Audit Ledger D3]>=tuple, <[Ops Data].[Account Ledger D3]>=tuple}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{<[Ops Data].[Audit Ledger D3]>={<delete select col D3>=[[@29,178:178='b',<393>,6:19]], <delete where col D3>=[[@38,264:264='b',<393>,8:8]], emp_id=[[@25,168:168='b',<393>,6:9]]}, <[Ops Data].[Account Ledger D3]>={<delete select col D3>=[[@9,34:34='a',<393>,2:19]], <delete where col D3>=[[@18,122:122='a',<393>,4:8]], emp_id=[[@5,24:24='a',<393>,2:9]]}, employees={emp_id=[[@52,337:337='e',<393>,12:6]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{union2={emp_id=[[@56,348:348='b',<393>,12:17]]}, query0={score_val=[[@13,62:70='score_val',<393>,2:47]], emp_id=[[@7,26:31='emp_id',<393>,2:11]]}, query1={score_val=[[@33,206:214='score_val',<393>,6:47]], emp_id=[[@27,170:175='emp_id',<393>,6:11]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_delete3={context_list={base=union2}, query_dictionary={}, def_union2={query_dictionary={emp_id=[[@56,348:348='b',<393>,12:17]]}, def_query1={query_dictionary={score_val=[[@33,206:214='score_val',<393>,6:47]], emp_id=[[@27,170:175='emp_id',<393>,6:11]]}, table_dictionary={<[Ops Data].[Audit Ledger D3]>={<delete select col D3>=[[@29,178:178='b',<393>,6:19]], <delete where col D3>=[[@38,264:264='b',<393>,8:8]], emp_id=[[@25,168:168='b',<393>,6:9]]}}, setop=EXCEPT, filters=[{substitution={name=<delete where col D3>, type=column}, table_ref=b}], interface={score_val=[{substitution={name=<delete select col D3>, type=column}, table_ref=b}], emp_id=[{name=emp_id, table_ref=b}]}, table_alias={b=<[Ops Data].[Audit Ledger D3]>}}, def_query0={query_dictionary={score_val=[[@13,62:70='score_val',<393>,2:47]], emp_id=[[@7,26:31='emp_id',<393>,2:11]]}, table_dictionary={<[Ops Data].[Account Ledger D3]>={<delete select col D3>=[[@9,34:34='a',<393>,2:19]], <delete where col D3>=[[@18,122:122='a',<393>,4:8]], emp_id=[[@5,24:24='a',<393>,2:9]]}}, filters=[{substitution={name=<delete where col D3>, type=column}, table_ref=a}], interface={score_val=[{substitution={name=<delete select col D3>, type=column}, table_ref=a}], emp_id=[{name=emp_id, table_ref=a}]}, table_alias={a=<[Ops Data].[Account Ledger D3]>}}, interface={score_val=query_column, emp_id=query_column}}, table_dictionary={employees={emp_id=[[@52,337:337='e',<393>,12:6]]}}, filters=[{name=emp_id, table_ref=e}, {name=emp_id, table_ref=b}], interface=null, table_alias={b=union2, e=employees, base=union2}}}",
				extractor.getSymbolTable().toString());
	}



	@Test
	public void deleteComplexSubstitutionD4NestedWithInCteBody() {
		final String query = "WITH outer_cte AS ("
				+ "\n  WITH inner_cte AS ("
				+ "\n    SELECT a.emp_id, a.<delete select col D4> AS metric_val"
				+ "\n    FROM <[Finance].[Revenue Feed D4]> a"
				+ "\n    WHERE a.<delete where col D4> > 0"
				+ "\n  )"
				+ "\n  SELECT i.emp_id, i.metric_val"
				+ "\n  FROM inner_cte i"
				+ "\n)"
				+ "\nDELETE FROM employees e"
				+ "\nUSING outer_cte o"
				+ "\nWHERE e.emp_id = o.emp_id";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={with={1={cte={with={1={cte={select={1={column={name=emp_id, table_ref=a}}, 2={column={substitution={name=<delete select col D4>, type=column}, table_ref=a}, alias=metric_val}}, from={table={alias=a, substitution={name=<[Finance].[Revenue Feed D4]>, parts={1=[Finance], 2=[Revenue Feed D4]}, type=tuple}}}, where={condition={left={column={substitution={name=<delete where col D4>, type=column}, table_ref=a}}, right={literal=0}, operator=>}}}, alias=inner_cte}}, query={select={1={column={name=emp_id, table_ref=i}}, 2={column={name=metric_val, table_ref=i}}}, from={table={alias=i, table=inner_cte}}}}, alias=outer_cte}}, query={delete={table={alias=e, table=employees}, using={1={table={alias=o, table=outer_cte}}}, where={condition={left={column={name=emp_id, table_ref=e}}, right={column={name=emp_id, table_ref=o}}, operator==}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<delete where col D4>=column, <delete select col D4>=column, <[Finance].[Revenue Feed D4]>=tuple}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{<[Finance].[Revenue Feed D4]>={<delete where col D4>=[[@22,153:153='a',<393>,5:10]], <delete select col D4>=[[@13,63:63='a',<393>,3:21]], emp_id=[[@9,53:53='a',<393>,3:11]]}, employees={emp_id=[[@48,286:286='e',<393>,12:6]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={metric_val=[[@17,91:100='metric_val',<393>,3:49], [@33,204:204='i',<393>,7:19]], emp_id=[[@11,55:60='emp_id',<393>,3:13], [@29,194:194='i',<393>,7:9]]}, query1={metric_val=[[@35,206:215='metric_val',<393>,7:21]], emp_id=[[@31,196:201='emp_id',<393>,7:11], [@52,297:297='o',<393>,12:17]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_delete2={context_list={outer_cte=query1}, query_dictionary={}, table_dictionary={employees={emp_id=[[@48,286:286='e',<393>,12:6]]}}, def_query1={context_list={inner_cte=query0, i=query0}, query_dictionary={metric_val=[[@35,206:215='metric_val',<393>,7:21]], emp_id=[[@31,196:201='emp_id',<393>,7:11], [@52,297:297='o',<393>,12:17]]}, def_query0={query_dictionary={metric_val=[[@17,91:100='metric_val',<393>,3:49], [@33,204:204='i',<393>,7:19]], emp_id=[[@11,55:60='emp_id',<393>,3:13], [@29,194:194='i',<393>,7:9]]}, table_dictionary={<[Finance].[Revenue Feed D4]>={<delete where col D4>=[[@22,153:153='a',<393>,5:10]], <delete select col D4>=[[@13,63:63='a',<393>,3:21]], emp_id=[[@9,53:53='a',<393>,3:11]]}}, filters=[{substitution={name=<delete where col D4>, type=column}, table_ref=a}], interface={metric_val=[{substitution={name=<delete select col D4>, type=column}, table_ref=a}], emp_id=[{name=emp_id, table_ref=a}]}, table_alias={a=<[Finance].[Revenue Feed D4]>}}, interface={metric_val=[{name=metric_val, table_ref=i}], emp_id=[{name=emp_id, table_ref=i}]}, table_alias={inner_cte=query0, i=query0}}, filters=[{name=emp_id, table_ref=e}, {name=emp_id, table_ref=o}], interface=null, table_alias={e=employees, outer_cte=query1, o=query1}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void deleteComplexSubstitutionD5WithCteQualifyWindowSubstitution() {
		final String query = "WITH ranked AS ("
				+ "\n  SELECT a.emp_id, a.<delete select col D5> AS score_val,"
				+ "\n         row_number() OVER (PARTITION BY a.emp_id ORDER BY a.<delete order col D5> DESC) AS rn"
				+ "\n  FROM <[Metrics].[Score Feed D5]> a"
				+ "\n  WHERE a.<delete where col D5> > 0"
				+ "\n  QUALIFY rn = 1"
				+ "\n)"
				+ "\nDELETE FROM employees e"
				+ "\nUSING ranked r"
				+ "\nWHERE e.emp_id = r.emp_id";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={with={1={cte={select={1={column={name=emp_id, table_ref=a}}, 2={column={substitution={name=<delete select col D5>, type=column}, table_ref=a}, alias=score_val}, 3={alias=rn, window_function={over={partition_by={1={column={name=emp_id, table_ref=a}}}, orderby={1={null_order=null, predicand={column={substitution={name=<delete order col D5>, type=column}, table_ref=a}}, sort_order=DESC}}}, function={function_name=row_number, parameters=null}}}}, from={table={alias=a, substitution={name=<[Metrics].[Score Feed D5]>, parts={1=[Metrics], 2=[Score Feed D5]}, type=tuple}}}, where={condition={left={column={substitution={name=<delete where col D5>, type=column}, table_ref=a}}, right={literal=0}, operator=>}}, qualify={condition={left={column={name=rn, table_ref=null}}, right={literal=1}, operator==}}}, alias=ranked}}, query={delete={table={alias=e, table=employees}, using={1={table={alias=r, table=ranked}}}, where={condition={left={column={name=emp_id, table_ref=e}}, right={column={name=emp_id, table_ref=r}}, operator==}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<delete where col D5>=column, <delete select col D5>=column, <delete order col D5>=column, <[Metrics].[Score Feed D5]>=tuple}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{employees={emp_id=[[@56,307:307='e',<393>,10:6]]}, <[Metrics].[Score Feed D5]>={<delete where col D5>=[[@38,215:215='a',<393>,5:8]], <delete select col D5>=[[@9,36:36='a',<393>,2:19]], <delete order col D5>=[[@27,134:134='a',<393>,3:59]], emp_id=[[@5,26:26='a',<393>,2:9], [@22,116:116='a',<393>,3:41]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={rn=[[@33,167:168='rn',<393>,3:92], [@44,253:254='rn',<393>,6:10]], score_val=[[@13,64:72='score_val',<393>,2:47]], emp_id=[[@7,28:33='emp_id',<393>,2:11], [@60,318:318='r',<393>,10:17]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_delete1={context_list={ranked=query0}, query_dictionary={}, table_dictionary={employees={emp_id=[[@56,307:307='e',<393>,10:6]]}}, def_query0={window_ordered_by=[{substitution={name=<delete order col D5>, type=column}, table_ref=a}], query_dictionary={rn=[[@33,167:168='rn',<393>,3:92], [@44,253:254='rn',<393>,6:10]], score_val=[[@13,64:72='score_val',<393>,2:47]], emp_id=[[@7,28:33='emp_id',<393>,2:11], [@60,318:318='r',<393>,10:17]]}, table_dictionary={<[Metrics].[Score Feed D5]>={<delete where col D5>=[[@38,215:215='a',<393>,5:8]], <delete select col D5>=[[@9,36:36='a',<393>,2:19]], <delete order col D5>=[[@27,134:134='a',<393>,3:59]], emp_id=[[@5,26:26='a',<393>,2:9], [@22,116:116='a',<393>,3:41]]}}, window_partition_by=[{name=emp_id, table_ref=a}], filters=[{substitution={name=<delete where col D5>, type=column}, table_ref=a}, {name=rn, table_ref=query0}], interface={rn=[{name=emp_id, table_ref=a}, {substitution={name=<delete order col D5>, type=column}, table_ref=a}], score_val=[{substitution={name=<delete select col D5>, type=column}, table_ref=a}], emp_id=[{name=emp_id, table_ref=a}]}, table_alias={a=<[Metrics].[Score Feed D5]>}}, filters=[{name=emp_id, table_ref=e}, {name=emp_id, table_ref=r}], interface=null, table_alias={r=query0, e=employees, ranked=query0}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void deleteComplexSubstitutionD6SubqueryJoinOnColumnSubstitution() {
		final String query = "DELETE FROM employees e"
				+ "\nUSING ("
				+ "\n  SELECT a.emp_id, a.<delete select col D6> AS metric_val"
				+ "\n  FROM <[Join Data].[Left Feed D6]> a"
				+ "\n  JOIN <[Join Data].[Right Feed D6]> b"
				+ "\n    ON a.<delete join col D6> = b.<delete join col D6b>"
				+ "\n  WHERE a.<delete where col D6> > 0"
				+ "\n) j"
				+ "\nWHERE e.emp_id = j.emp_id";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={delete={table={alias=e, table=employees}, using={1={table={alias=j, query={select={1={column={name=emp_id, table_ref=a}}, 2={column={substitution={name=<delete select col D6>, type=column}, table_ref=a}, alias=metric_val}}, from={join={1={table={alias=a, substitution={name=<[Join Data].[Left Feed D6]>, parts={1=[Join Data], 2=[Left Feed D6]}, type=tuple}}}, 2={join=JOIN, on={condition={left={column={substitution={name=<delete join col D6>, type=column}, table_ref=a}}, right={column={substitution={name=<delete join col D6b>, type=column}, table_ref=b}}, operator==}}}, 3={table={alias=b, substitution={name=<[Join Data].[Right Feed D6]>, parts={1=[Join Data], 2=[Right Feed D6]}, type=tuple}}}}}, where={condition={left={column={substitution={name=<delete where col D6>, type=column}, table_ref=a}}, right={literal=0}, operator=>}}}}}}, where={condition={left={column={name=emp_id, table_ref=e}}, right={column={name=emp_id, table_ref=j}}, operator==}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<[Join Data].[Right Feed D6]>=tuple, <delete where col D6>=column, <delete select col D6>=column, <delete join col D6>=column, <delete join col D6b>=column, <[Join Data].[Left Feed D6]>=tuple}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{<[Join Data].[Right Feed D6]>={<delete join col D6b>=[[@27,199:199='b',<393>,6:32]]}, <[Join Data].[Left Feed D6]>={<delete where col D6>=[[@31,231:231='a',<393>,7:8]], <delete select col D6>=[[@11,51:51='a',<393>,3:19]], <delete join col D6>=[[@23,174:174='a',<393>,6:7]], emp_id=[[@7,41:41='a',<393>,3:9]]}, employees={emp_id=[[@39,269:269='e',<393>,9:6]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={metric_val=[[@15,79:88='metric_val',<393>,3:47]], emp_id=[[@9,43:48='emp_id',<393>,3:11], [@43,280:280='j',<393>,9:17]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_delete1={query_dictionary={}, table_dictionary={employees={emp_id=[[@39,269:269='e',<393>,9:6]]}}, def_query0={query_dictionary={metric_val=[[@15,79:88='metric_val',<393>,3:47]], emp_id=[[@9,43:48='emp_id',<393>,3:11], [@43,280:280='j',<393>,9:17]]}, table_dictionary={<[Join Data].[Right Feed D6]>={<delete join col D6b>=[[@27,199:199='b',<393>,6:32]]}, <[Join Data].[Left Feed D6]>={<delete where col D6>=[[@31,231:231='a',<393>,7:8]], <delete select col D6>=[[@11,51:51='a',<393>,3:19]], <delete join col D6>=[[@23,174:174='a',<393>,6:7]], emp_id=[[@7,41:41='a',<393>,3:9]]}}, filters=[{substitution={name=<delete join col D6>, type=column}, table_ref=a}, {substitution={name=<delete join col D6b>, type=column}, table_ref=b}, {substitution={name=<delete where col D6>, type=column}, table_ref=a}], interface={metric_val=[{substitution={name=<delete select col D6>, type=column}, table_ref=a}], emp_id=[{name=emp_id, table_ref=a}]}, table_alias={a=<[Join Data].[Left Feed D6]>, b=<[Join Data].[Right Feed D6]>}}, filters=[{name=emp_id, table_ref=e}, {name=emp_id, table_ref=j}], interface=null, table_alias={e=employees, j=query0}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void deleteComplexSubstitutionD7ChainedCteReferences() {
		final String query = "WITH step1 AS ("
				+ "\n  SELECT a.emp_id, a.<delete select col D7> AS raw_val"
				+ "\n  FROM <[Pipeline].[Stage One D7]> a"
				+ "\n  WHERE a.<delete where col D7> > 0"
				+ "\n), step2 AS ("
				+ "\n  SELECT s.emp_id, s.raw_val"
				+ "\n  FROM step1 s"
				+ "\n)"
				+ "\nDELETE FROM employees e"
				+ "\nUSING step2 t"
				+ "\nWHERE e.emp_id = t.emp_id";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={with={1={cte={select={1={column={name=emp_id, table_ref=a}}, 2={column={substitution={name=<delete select col D7>, type=column}, table_ref=a}, alias=raw_val}}, from={table={alias=a, substitution={name=<[Pipeline].[Stage One D7]>, parts={1=[Pipeline], 2=[Stage One D7]}, type=tuple}}}, where={condition={left={column={substitution={name=<delete where col D7>, type=column}, table_ref=a}}, right={literal=0}, operator=>}}}, alias=step1}, 2={cte={select={1={column={name=emp_id, table_ref=s}}, 2={column={name=raw_val, table_ref=s}}}, from={table={alias=s, table=step1}}}, alias=step2}}, query={delete={table={alias=e, table=employees}, using={1={table={alias=t, table=step2}}}, where={condition={left={column={name=emp_id, table_ref=e}}, right={column={name=emp_id, table_ref=t}}, operator==}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<delete where col D7>=column, <delete select col D7>=column, <[Pipeline].[Stage One D7]>=tuple}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{<[Pipeline].[Stage One D7]>={<delete where col D7>=[[@18,116:116='a',<393>,4:8]], <delete select col D7>=[[@9,35:35='a',<393>,2:19]], emp_id=[[@5,25:25='a',<393>,2:9]]}, employees={emp_id=[[@48,248:248='e',<393>,11:6]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={raw_val=[[@13,63:69='raw_val',<393>,2:47], [@33,177:177='s',<393>,6:19]], emp_id=[[@7,27:32='emp_id',<393>,2:11], [@29,167:167='s',<393>,6:9]]}, query1={raw_val=[[@35,179:185='raw_val',<393>,6:21]], emp_id=[[@31,169:174='emp_id',<393>,6:11], [@52,259:259='t',<393>,11:17]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_delete2={context_list={step1=query0, step2=query1}, query_dictionary={}, table_dictionary={employees={emp_id=[[@48,248:248='e',<393>,11:6]]}}, def_query1={context_list={step1=query0, s=query0}, query_dictionary={raw_val=[[@35,179:185='raw_val',<393>,6:21]], emp_id=[[@31,169:174='emp_id',<393>,6:11], [@52,259:259='t',<393>,11:17]]}, interface={raw_val=[{name=raw_val, table_ref=s}], emp_id=[{name=emp_id, table_ref=s}]}, table_alias={s=query0, step1=query0}}, def_query0={query_dictionary={raw_val=[[@13,63:69='raw_val',<393>,2:47], [@33,177:177='s',<393>,6:19]], emp_id=[[@7,27:32='emp_id',<393>,2:11], [@29,167:167='s',<393>,6:9]]}, table_dictionary={<[Pipeline].[Stage One D7]>={<delete where col D7>=[[@18,116:116='a',<393>,4:8]], <delete select col D7>=[[@9,35:35='a',<393>,2:19]], emp_id=[[@5,25:25='a',<393>,2:9]]}}, filters=[{substitution={name=<delete where col D7>, type=column}, table_ref=a}], interface={raw_val=[{substitution={name=<delete select col D7>, type=column}, table_ref=a}], emp_id=[{name=emp_id, table_ref=a}]}, table_alias={a=<[Pipeline].[Stage One D7]>}}, filters=[{name=emp_id, table_ref=e}, {name=emp_id, table_ref=t}], interface=null, table_alias={t=query1, e=employees, step2=query1, step1=query0}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void deleteComplexSubstitutionD8UnionIntersectNestedSubquery() {
		final String query = "DELETE FROM employees e"
				+ "\nUSING ("
				+ "\n  SELECT u.emp_id, u.metric_val"
				+ "\n  FROM ("
				+ "\n    SELECT a.emp_id, a.<delete select col D8> AS metric_val"
				+ "\n    FROM <[Blend Data].[Branch A D8]> a"
				+ "\n    WHERE a.<delete where col D8> > 0"
				+ "\n    UNION"
				+ "\n    SELECT b.emp_id, b.<delete select col D8> AS metric_val"
				+ "\n    FROM <[Blend Data].[Branch B D8]> b"
				+ "\n    WHERE b.<delete where col D8> > 0"
				+ "\n  ) u"
				+ "\n  INTERSECT"
				+ "\n  SELECT c.emp_id, c.<delete select col D8> AS metric_val"
				+ "\n  FROM <[Blend Data].[Branch C D8]> c"
				+ "\n  WHERE c.<delete where col D8> > 0"
				+ "\n) x"
				+ "\nWHERE e.emp_id = x.emp_id";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={delete={table={alias=e, table=employees}, using={1={table={alias=x, query={intersect={1={select={1={column={name=emp_id, table_ref=u}}, 2={column={name=metric_val, table_ref=u}}}, from={table={alias=u, query={union={1={select={1={column={name=emp_id, table_ref=a}}, 2={column={substitution={name=<delete select col D8>, type=column}, table_ref=a}, alias=metric_val}}, from={table={alias=a, substitution={name=<[Blend Data].[Branch A D8]>, parts={1=[Blend Data], 2=[Branch A D8]}, type=tuple}}}, where={condition={left={column={substitution={name=<delete where col D8>, type=column}, table_ref=a}}, right={literal=0}, operator=>}}}, 2={union={qualifier=null, operator=UNION}}, 3={select={1={column={name=emp_id, table_ref=b}}, 2={column={substitution={name=<delete select col D8>, type=column}, table_ref=b}, alias=metric_val}}, from={table={alias=b, substitution={name=<[Blend Data].[Branch B D8]>, parts={1=[Blend Data], 2=[Branch B D8]}, type=tuple}}}, where={condition={left={column={substitution={name=<delete where col D8>, type=column}, table_ref=b}}, right={literal=0}, operator=>}}}}}}}}, 2={intersect={qualifier=null, operator=INTERSECT}}, 3={select={1={column={name=emp_id, table_ref=c}}, 2={column={substitution={name=<delete select col D8>, type=column}, table_ref=c}, alias=metric_val}}, from={table={alias=c, substitution={name=<[Blend Data].[Branch C D8]>, parts={1=[Blend Data], 2=[Branch C D8]}, type=tuple}}}, where={condition={left={column={substitution={name=<delete where col D8>, type=column}, table_ref=c}}, right={literal=0}, operator=>}}}}}}}}, where={condition={left={column={name=emp_id, table_ref=e}}, right={column={name=emp_id, table_ref=x}}, operator==}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<delete where col D8>=column, <[Blend Data].[Branch C D8]>=tuple, <[Blend Data].[Branch A D8]>=tuple, <[Blend Data].[Branch B D8]>=tuple, <delete select col D8>=column}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{<[Blend Data].[Branch C D8]>={<delete where col D8>=[[@72,481:481='c',<393>,16:8]], <delete select col D8>=[[@63,396:396='c',<393>,14:19]], emp_id=[[@59,386:386='c',<393>,14:9]]}, <[Blend Data].[Branch A D8]>={<delete where col D8>=[[@30,183:183='a',<393>,7:10]], <delete select col D8>=[[@21,94:94='a',<393>,5:21]], emp_id=[[@17,84:84='a',<393>,5:11]]}, <[Blend Data].[Branch B D8]>={<delete where col D8>=[[@50,331:331='b',<393>,11:10]], <delete select col D8>=[[@41,242:242='b',<393>,9:21]], emp_id=[[@37,232:232='b',<393>,9:11]]}, employees={emp_id=[[@80,519:519='e',<393>,18:6]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{union2={metric_val=[[@11,51:51='u',<393>,3:19]], emp_id=[[@7,41:41='u',<393>,3:9]]}, intersect5={emp_id=[[@84,530:530='x',<393>,18:17]]}, query4={metric_val=[[@67,424:433='metric_val',<393>,14:47]], emp_id=[[@61,388:393='emp_id',<393>,14:11]]}, query0={metric_val=[[@25,122:131='metric_val',<393>,5:49]], emp_id=[[@19,86:91='emp_id',<393>,5:13]]}, query1={metric_val=[[@45,270:279='metric_val',<393>,9:49]], emp_id=[[@39,234:239='emp_id',<393>,9:13]]}, query3={metric_val=[[@13,53:62='metric_val',<393>,3:21]], emp_id=[[@9,43:48='emp_id',<393>,3:11]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_delete6={query_dictionary={}, table_dictionary={employees={emp_id=[[@80,519:519='e',<393>,18:6]]}}, def_intersect5={query_dictionary={emp_id=[[@84,530:530='x',<393>,18:17]]}, interface={metric_val=query_column, emp_id=query_column}, def_query4={query_dictionary={metric_val=[[@67,424:433='metric_val',<393>,14:47]], emp_id=[[@61,388:393='emp_id',<393>,14:11]]}, table_dictionary={<[Blend Data].[Branch C D8]>={<delete where col D8>=[[@72,481:481='c',<393>,16:8]], <delete select col D8>=[[@63,396:396='c',<393>,14:19]], emp_id=[[@59,386:386='c',<393>,14:9]]}}, setop=INTERSECTION, filters=[{substitution={name=<delete where col D8>, type=column}, table_ref=c}], interface={metric_val=[{substitution={name=<delete select col D8>, type=column}, table_ref=c}], emp_id=[{name=emp_id, table_ref=c}]}, table_alias={c=<[Blend Data].[Branch C D8]>}}, def_query3={def_union2={query_dictionary={metric_val=[[@11,51:51='u',<393>,3:19]], emp_id=[[@7,41:41='u',<393>,3:9]]}, def_query1={query_dictionary={metric_val=[[@45,270:279='metric_val',<393>,9:49]], emp_id=[[@39,234:239='emp_id',<393>,9:13]]}, table_dictionary={<[Blend Data].[Branch B D8]>={<delete where col D8>=[[@50,331:331='b',<393>,11:10]], <delete select col D8>=[[@41,242:242='b',<393>,9:21]], emp_id=[[@37,232:232='b',<393>,9:11]]}}, setop=UNION, filters=[{substitution={name=<delete where col D8>, type=column}, table_ref=b}], interface={metric_val=[{substitution={name=<delete select col D8>, type=column}, table_ref=b}], emp_id=[{name=emp_id, table_ref=b}]}, table_alias={b=<[Blend Data].[Branch B D8]>}}, def_query0={query_dictionary={metric_val=[[@25,122:131='metric_val',<393>,5:49]], emp_id=[[@19,86:91='emp_id',<393>,5:13]]}, table_dictionary={<[Blend Data].[Branch A D8]>={<delete where col D8>=[[@30,183:183='a',<393>,7:10]], <delete select col D8>=[[@21,94:94='a',<393>,5:21]], emp_id=[[@17,84:84='a',<393>,5:11]]}}, filters=[{substitution={name=<delete where col D8>, type=column}, table_ref=a}], interface={metric_val=[{substitution={name=<delete select col D8>, type=column}, table_ref=a}], emp_id=[{name=emp_id, table_ref=a}]}, table_alias={a=<[Blend Data].[Branch A D8]>}}, interface={metric_val=query_column, emp_id=query_column}}, query_dictionary={metric_val=[[@13,53:62='metric_val',<393>,3:21]], emp_id=[[@9,43:48='emp_id',<393>,3:11]]}, interface={metric_val=[{name=metric_val, table_ref=u}], emp_id=[{name=emp_id, table_ref=u}]}, table_alias={u=union2}}}, filters=[{name=emp_id, table_ref=e}, {name=emp_id, table_ref=x}], interface=null, table_alias={e=employees, x=intersect5}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void deleteComplexSubstitutionD8UnionExceptNestedSubquery() {
		final String query = "DELETE FROM employees e"
				+ "\nUSING ("
				+ "\n  SELECT u.emp_id, u.metric_val"
				+ "\n  FROM ("
				+ "\n    SELECT a.emp_id, a.<delete select col D8> AS metric_val"
				+ "\n    FROM <[Blend Data].[Branch A D8]> a"
				+ "\n    WHERE a.<delete where col D8> > 0"
				+ "\n    UNION"
				+ "\n    SELECT b.emp_id, b.<delete select col D8> AS metric_val"
				+ "\n    FROM <[Blend Data].[Branch B D8]> b"
				+ "\n    WHERE b.<delete where col D8> > 0"
				+ "\n  ) u"
				+ "\n  EXCEPT"
				+ "\n  SELECT c.emp_id, c.<delete select col D8> AS metric_val"
				+ "\n  FROM <[Blend Data].[Branch C D8]> c"
				+ "\n  WHERE c.<delete where col D8> > 0"
				+ "\n) x"
				+ "\nWHERE e.emp_id = x.emp_id";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={delete={table={alias=e, table=employees}, using={1={table={alias=x, query={union={1={select={1={column={name=emp_id, table_ref=u}}, 2={column={name=metric_val, table_ref=u}}}, from={table={alias=u, query={union={1={select={1={column={name=emp_id, table_ref=a}}, 2={column={substitution={name=<delete select col D8>, type=column}, table_ref=a}, alias=metric_val}}, from={table={alias=a, substitution={name=<[Blend Data].[Branch A D8]>, parts={1=[Blend Data], 2=[Branch A D8]}, type=tuple}}}, where={condition={left={column={substitution={name=<delete where col D8>, type=column}, table_ref=a}}, right={literal=0}, operator=>}}}, 2={union={qualifier=null, operator=UNION}}, 3={select={1={column={name=emp_id, table_ref=b}}, 2={column={substitution={name=<delete select col D8>, type=column}, table_ref=b}, alias=metric_val}}, from={table={alias=b, substitution={name=<[Blend Data].[Branch B D8]>, parts={1=[Blend Data], 2=[Branch B D8]}, type=tuple}}}, where={condition={left={column={substitution={name=<delete where col D8>, type=column}, table_ref=b}}, right={literal=0}, operator=>}}}}}}}}, 2={union={qualifier=null, operator=EXCEPT}}, 3={select={1={column={name=emp_id, table_ref=c}}, 2={column={substitution={name=<delete select col D8>, type=column}, table_ref=c}, alias=metric_val}}, from={table={alias=c, substitution={name=<[Blend Data].[Branch C D8]>, parts={1=[Blend Data], 2=[Branch C D8]}, type=tuple}}}, where={condition={left={column={substitution={name=<delete where col D8>, type=column}, table_ref=c}}, right={literal=0}, operator=>}}}}}}}}, where={condition={left={column={name=emp_id, table_ref=e}}, right={column={name=emp_id, table_ref=x}}, operator==}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<delete where col D8>=column, <[Blend Data].[Branch C D8]>=tuple, <[Blend Data].[Branch A D8]>=tuple, <[Blend Data].[Branch B D8]>=tuple, <delete select col D8>=column}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{<[Blend Data].[Branch C D8]>={<delete where col D8>=[[@72,478:478='c',<393>,16:8]], <delete select col D8>=[[@63,393:393='c',<393>,14:19]], emp_id=[[@59,383:383='c',<393>,14:9]]}, <[Blend Data].[Branch A D8]>={<delete where col D8>=[[@30,183:183='a',<393>,7:10]], <delete select col D8>=[[@21,94:94='a',<393>,5:21]], emp_id=[[@17,84:84='a',<393>,5:11]]}, <[Blend Data].[Branch B D8]>={<delete where col D8>=[[@50,331:331='b',<393>,11:10]], <delete select col D8>=[[@41,242:242='b',<393>,9:21]], emp_id=[[@37,232:232='b',<393>,9:11]]}, employees={emp_id=[[@80,516:516='e',<393>,18:6]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{union5={emp_id=[[@84,527:527='x',<393>,18:17]]}, union2={metric_val=[[@11,51:51='u',<393>,3:19]], emp_id=[[@7,41:41='u',<393>,3:9]]}, query4={metric_val=[[@67,421:430='metric_val',<393>,14:47]], emp_id=[[@61,385:390='emp_id',<393>,14:11]]}, query0={metric_val=[[@25,122:131='metric_val',<393>,5:49]], emp_id=[[@19,86:91='emp_id',<393>,5:13]]}, query1={metric_val=[[@45,270:279='metric_val',<393>,9:49]], emp_id=[[@39,234:239='emp_id',<393>,9:13]]}, query3={metric_val=[[@13,53:62='metric_val',<393>,3:21]], emp_id=[[@9,43:48='emp_id',<393>,3:11]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_delete6={query_dictionary={}, table_dictionary={employees={emp_id=[[@80,516:516='e',<393>,18:6]]}}, filters=[{name=emp_id, table_ref=e}, {name=emp_id, table_ref=x}], interface=null, def_union5={query_dictionary={emp_id=[[@84,527:527='x',<393>,18:17]]}, interface={metric_val=query_column, emp_id=query_column}, def_query4={query_dictionary={metric_val=[[@67,421:430='metric_val',<393>,14:47]], emp_id=[[@61,385:390='emp_id',<393>,14:11]]}, table_dictionary={<[Blend Data].[Branch C D8]>={<delete where col D8>=[[@72,478:478='c',<393>,16:8]], <delete select col D8>=[[@63,393:393='c',<393>,14:19]], emp_id=[[@59,383:383='c',<393>,14:9]]}}, setop=EXCEPT, filters=[{substitution={name=<delete where col D8>, type=column}, table_ref=c}], interface={metric_val=[{substitution={name=<delete select col D8>, type=column}, table_ref=c}], emp_id=[{name=emp_id, table_ref=c}]}, table_alias={c=<[Blend Data].[Branch C D8]>}}, def_query3={def_union2={query_dictionary={metric_val=[[@11,51:51='u',<393>,3:19]], emp_id=[[@7,41:41='u',<393>,3:9]]}, def_query1={query_dictionary={metric_val=[[@45,270:279='metric_val',<393>,9:49]], emp_id=[[@39,234:239='emp_id',<393>,9:13]]}, table_dictionary={<[Blend Data].[Branch B D8]>={<delete where col D8>=[[@50,331:331='b',<393>,11:10]], <delete select col D8>=[[@41,242:242='b',<393>,9:21]], emp_id=[[@37,232:232='b',<393>,9:11]]}}, setop=UNION, filters=[{substitution={name=<delete where col D8>, type=column}, table_ref=b}], interface={metric_val=[{substitution={name=<delete select col D8>, type=column}, table_ref=b}], emp_id=[{name=emp_id, table_ref=b}]}, table_alias={b=<[Blend Data].[Branch B D8]>}}, def_query0={query_dictionary={metric_val=[[@25,122:131='metric_val',<393>,5:49]], emp_id=[[@19,86:91='emp_id',<393>,5:13]]}, table_dictionary={<[Blend Data].[Branch A D8]>={<delete where col D8>=[[@30,183:183='a',<393>,7:10]], <delete select col D8>=[[@21,94:94='a',<393>,5:21]], emp_id=[[@17,84:84='a',<393>,5:11]]}}, filters=[{substitution={name=<delete where col D8>, type=column}, table_ref=a}], interface={metric_val=[{substitution={name=<delete select col D8>, type=column}, table_ref=a}], emp_id=[{name=emp_id, table_ref=a}]}, table_alias={a=<[Blend Data].[Branch A D8]>}}, interface={metric_val=query_column, emp_id=query_column}}, query_dictionary={metric_val=[[@13,53:62='metric_val',<393>,3:21]], emp_id=[[@9,43:48='emp_id',<393>,3:11]]}, interface={metric_val=[{name=metric_val, table_ref=u}], emp_id=[{name=emp_id, table_ref=u}]}, table_alias={u=union2}}}, table_alias={e=employees, x=union5}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void deleteComplexSubstitutionD8ExceptIntersectNestedSubquery(){
		final String query = "DELETE FROM employees e"
				+ "\nUSING ("
				+ "\n  SELECT u.emp_id, u.metric_val"
				+ "\n  FROM ("
				+ "\n    SELECT a.emp_id, a.<delete select col D8> AS metric_val"
				+ "\n    FROM <[Blend Data].[Branch A D8]> a"
				+ "\n    WHERE a.<delete where col D8> > 0"
				+ "\n    EXCEPT"
				+ "\n    SELECT b.emp_id, b.<delete select col D8> AS metric_val"
				+ "\n    FROM <[Blend Data].[Branch B D8]> b"
				+ "\n    WHERE b.<delete where col D8> > 0"
				+ "\n  ) u"
				+ "\n  INTERSECT"
				+ "\n  SELECT c.emp_id, c.<delete select col D8> AS metric_val"
				+ "\n  FROM <[Blend Data].[Branch C D8]> c"
				+ "\n  WHERE c.<delete where col D8> > 0"
				+ "\n) x"
				+ "\nWHERE e.emp_id = x.emp_id";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={delete={table={alias=e, table=employees}, using={1={table={alias=x, query={intersect={1={select={1={column={name=emp_id, table_ref=u}}, 2={column={name=metric_val, table_ref=u}}}, from={table={alias=u, query={union={1={select={1={column={name=emp_id, table_ref=a}}, 2={column={substitution={name=<delete select col D8>, type=column}, table_ref=a}, alias=metric_val}}, from={table={alias=a, substitution={name=<[Blend Data].[Branch A D8]>, parts={1=[Blend Data], 2=[Branch A D8]}, type=tuple}}}, where={condition={left={column={substitution={name=<delete where col D8>, type=column}, table_ref=a}}, right={literal=0}, operator=>}}}, 2={union={qualifier=null, operator=EXCEPT}}, 3={select={1={column={name=emp_id, table_ref=b}}, 2={column={substitution={name=<delete select col D8>, type=column}, table_ref=b}, alias=metric_val}}, from={table={alias=b, substitution={name=<[Blend Data].[Branch B D8]>, parts={1=[Blend Data], 2=[Branch B D8]}, type=tuple}}}, where={condition={left={column={substitution={name=<delete where col D8>, type=column}, table_ref=b}}, right={literal=0}, operator=>}}}}}}}}, 2={intersect={qualifier=null, operator=INTERSECT}}, 3={select={1={column={name=emp_id, table_ref=c}}, 2={column={substitution={name=<delete select col D8>, type=column}, table_ref=c}, alias=metric_val}}, from={table={alias=c, substitution={name=<[Blend Data].[Branch C D8]>, parts={1=[Blend Data], 2=[Branch C D8]}, type=tuple}}}, where={condition={left={column={substitution={name=<delete where col D8>, type=column}, table_ref=c}}, right={literal=0}, operator=>}}}}}}}}, where={condition={left={column={name=emp_id, table_ref=e}}, right={column={name=emp_id, table_ref=x}}, operator==}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<delete where col D8>=column, <[Blend Data].[Branch C D8]>=tuple, <[Blend Data].[Branch A D8]>=tuple, <[Blend Data].[Branch B D8]>=tuple, <delete select col D8>=column}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{<[Blend Data].[Branch C D8]>={<delete where col D8>=[[@72,482:482='c',<393>,16:8]], <delete select col D8>=[[@63,397:397='c',<393>,14:19]], emp_id=[[@59,387:387='c',<393>,14:9]]}, <[Blend Data].[Branch A D8]>={<delete where col D8>=[[@30,183:183='a',<393>,7:10]], <delete select col D8>=[[@21,94:94='a',<393>,5:21]], emp_id=[[@17,84:84='a',<393>,5:11]]}, <[Blend Data].[Branch B D8]>={<delete where col D8>=[[@50,332:332='b',<393>,11:10]], <delete select col D8>=[[@41,243:243='b',<393>,9:21]], emp_id=[[@37,233:233='b',<393>,9:11]]}, employees={emp_id=[[@80,520:520='e',<393>,18:6]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{union2={metric_val=[[@11,51:51='u',<393>,3:19]], emp_id=[[@7,41:41='u',<393>,3:9]]}, intersect5={emp_id=[[@84,531:531='x',<393>,18:17]]}, query4={metric_val=[[@67,425:434='metric_val',<393>,14:47]], emp_id=[[@61,389:394='emp_id',<393>,14:11]]}, query0={metric_val=[[@25,122:131='metric_val',<393>,5:49]], emp_id=[[@19,86:91='emp_id',<393>,5:13]]}, query1={metric_val=[[@45,271:280='metric_val',<393>,9:49]], emp_id=[[@39,235:240='emp_id',<393>,9:13]]}, query3={metric_val=[[@13,53:62='metric_val',<393>,3:21]], emp_id=[[@9,43:48='emp_id',<393>,3:11]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_delete6={query_dictionary={}, table_dictionary={employees={emp_id=[[@80,520:520='e',<393>,18:6]]}}, def_intersect5={query_dictionary={emp_id=[[@84,531:531='x',<393>,18:17]]}, interface={metric_val=query_column, emp_id=query_column}, def_query4={query_dictionary={metric_val=[[@67,425:434='metric_val',<393>,14:47]], emp_id=[[@61,389:394='emp_id',<393>,14:11]]}, table_dictionary={<[Blend Data].[Branch C D8]>={<delete where col D8>=[[@72,482:482='c',<393>,16:8]], <delete select col D8>=[[@63,397:397='c',<393>,14:19]], emp_id=[[@59,387:387='c',<393>,14:9]]}}, setop=INTERSECTION, filters=[{substitution={name=<delete where col D8>, type=column}, table_ref=c}], interface={metric_val=[{substitution={name=<delete select col D8>, type=column}, table_ref=c}], emp_id=[{name=emp_id, table_ref=c}]}, table_alias={c=<[Blend Data].[Branch C D8]>}}, def_query3={def_union2={query_dictionary={metric_val=[[@11,51:51='u',<393>,3:19]], emp_id=[[@7,41:41='u',<393>,3:9]]}, def_query1={query_dictionary={metric_val=[[@45,271:280='metric_val',<393>,9:49]], emp_id=[[@39,235:240='emp_id',<393>,9:13]]}, table_dictionary={<[Blend Data].[Branch B D8]>={<delete where col D8>=[[@50,332:332='b',<393>,11:10]], <delete select col D8>=[[@41,243:243='b',<393>,9:21]], emp_id=[[@37,233:233='b',<393>,9:11]]}}, setop=EXCEPT, filters=[{substitution={name=<delete where col D8>, type=column}, table_ref=b}], interface={metric_val=[{substitution={name=<delete select col D8>, type=column}, table_ref=b}], emp_id=[{name=emp_id, table_ref=b}]}, table_alias={b=<[Blend Data].[Branch B D8]>}}, def_query0={query_dictionary={metric_val=[[@25,122:131='metric_val',<393>,5:49]], emp_id=[[@19,86:91='emp_id',<393>,5:13]]}, table_dictionary={<[Blend Data].[Branch A D8]>={<delete where col D8>=[[@30,183:183='a',<393>,7:10]], <delete select col D8>=[[@21,94:94='a',<393>,5:21]], emp_id=[[@17,84:84='a',<393>,5:11]]}}, filters=[{substitution={name=<delete where col D8>, type=column}, table_ref=a}], interface={metric_val=[{substitution={name=<delete select col D8>, type=column}, table_ref=a}], emp_id=[{name=emp_id, table_ref=a}]}, table_alias={a=<[Blend Data].[Branch A D8]>}}, interface={metric_val=query_column, emp_id=query_column}}, query_dictionary={metric_val=[[@13,53:62='metric_val',<393>,3:21]], emp_id=[[@9,43:48='emp_id',<393>,3:11]]}, interface={metric_val=[{name=metric_val, table_ref=u}], emp_id=[{name=emp_id, table_ref=u}]}, table_alias={u=union2}}}, filters=[{name=emp_id, table_ref=e}, {name=emp_id, table_ref=x}], interface=null, table_alias={e=employees, x=intersect5}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void deleteComplexSubstitutionD9WithCteSelfUnionBranches() {
		final String query = "WITH blended AS ("
				+ "\n  SELECT a.emp_id, a.<delete select col D9a> AS metric_val"
				+ "\n  FROM <[Union Data].[Feed Alpha D9]> a"
				+ "\n  WHERE a.<delete where col D9a> > 0"
				+ "\n  UNION"
				+ "\n  SELECT b.emp_id, b.<delete select col D9b> AS metric_val"
				+ "\n  FROM <[Union Data].[Feed Beta D9]> b"
				+ "\n  WHERE b.<delete where col D9b> > 0"
				+ "\n)"
				+ "\nDELETE FROM employees e"
				+ "\nUSING blended bl"
				+ "\nWHERE e.emp_id = bl.emp_id";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={with={1={cte={union={1={select={1={column={name=emp_id, table_ref=a}}, 2={column={substitution={name=<delete select col D9a>, type=column}, table_ref=a}, alias=metric_val}}, from={table={alias=a, substitution={name=<[Union Data].[Feed Alpha D9]>, parts={1=[Union Data], 2=[Feed Alpha D9]}, type=tuple}}}, where={condition={left={column={substitution={name=<delete where col D9a>, type=column}, table_ref=a}}, right={literal=0}, operator=>}}}, 2={union={qualifier=null, operator=UNION}}, 3={select={1={column={name=emp_id, table_ref=b}}, 2={column={substitution={name=<delete select col D9b>, type=column}, table_ref=b}, alias=metric_val}}, from={table={alias=b, substitution={name=<[Union Data].[Feed Beta D9]>, parts={1=[Union Data], 2=[Feed Beta D9]}, type=tuple}}}, where={condition={left={column={substitution={name=<delete where col D9b>, type=column}, table_ref=b}}, right={literal=0}, operator=>}}}}}, alias=blended}}, query={delete={table={alias=e, table=employees}, using={1={table={alias=bl, table=blended}}}, where={condition={left={column={name=emp_id, table_ref=e}}, right={column={name=emp_id, table_ref=bl}}, operator==}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<delete where col D9b>=column, <[Union Data].[Feed Beta D9]>=tuple, <delete where col D9a>=column, <delete select col D9b>=column, <delete select col D9a>=column, <[Union Data].[Feed Alpha D9]>=tuple}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{<[Union Data].[Feed Beta D9]>={<delete where col D9b>=[[@38,268:268='b',<393>,8:8]], <delete select col D9b>=[[@29,181:181='b',<393>,6:19]], emp_id=[[@25,171:171='b',<393>,6:9]]}, <[Union Data].[Feed Alpha D9]>={<delete where col D9a>=[[@18,125:125='a',<393>,4:8]], <delete select col D9a>=[[@9,37:37='a',<393>,2:19]], emp_id=[[@5,27:27='a',<393>,2:9]]}, employees={emp_id=[[@52,346:346='e',<393>,12:6]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{union2={emp_id=[[@56,357:358='bl',<393>,12:17]]}, query0={metric_val=[[@13,66:75='metric_val',<393>,2:48]], emp_id=[[@7,29:34='emp_id',<393>,2:11]]}, query1={metric_val=[[@33,210:219='metric_val',<393>,6:48]], emp_id=[[@27,173:178='emp_id',<393>,6:11]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_delete3={context_list={blended=union2}, query_dictionary={}, def_union2={query_dictionary={emp_id=[[@56,357:358='bl',<393>,12:17]]}, def_query1={query_dictionary={metric_val=[[@33,210:219='metric_val',<393>,6:48]], emp_id=[[@27,173:178='emp_id',<393>,6:11]]}, table_dictionary={<[Union Data].[Feed Beta D9]>={<delete where col D9b>=[[@38,268:268='b',<393>,8:8]], <delete select col D9b>=[[@29,181:181='b',<393>,6:19]], emp_id=[[@25,171:171='b',<393>,6:9]]}}, setop=UNION, filters=[{substitution={name=<delete where col D9b>, type=column}, table_ref=b}], interface={metric_val=[{substitution={name=<delete select col D9b>, type=column}, table_ref=b}], emp_id=[{name=emp_id, table_ref=b}]}, table_alias={b=<[Union Data].[Feed Beta D9]>}}, def_query0={query_dictionary={metric_val=[[@13,66:75='metric_val',<393>,2:48]], emp_id=[[@7,29:34='emp_id',<393>,2:11]]}, table_dictionary={<[Union Data].[Feed Alpha D9]>={<delete where col D9a>=[[@18,125:125='a',<393>,4:8]], <delete select col D9a>=[[@9,37:37='a',<393>,2:19]], emp_id=[[@5,27:27='a',<393>,2:9]]}}, filters=[{substitution={name=<delete where col D9a>, type=column}, table_ref=a}], interface={metric_val=[{substitution={name=<delete select col D9a>, type=column}, table_ref=a}], emp_id=[{name=emp_id, table_ref=a}]}, table_alias={a=<[Union Data].[Feed Alpha D9]>}}, interface={metric_val=query_column, emp_id=query_column}}, table_dictionary={employees={emp_id=[[@52,346:346='e',<393>,12:6]]}}, filters=[{name=emp_id, table_ref=e}, {name=emp_id, table_ref=bl}], interface=null, table_alias={blended=union2, e=employees, bl=union2}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void deleteComplexSubstitutionD9WithCteSelfExceptBranches(){
		final String query = "WITH blended AS ("
				+ "\n  SELECT a.emp_id, a.<delete select col D9a> AS metric_val"
				+ "\n  FROM <[except Data].[Feed Alpha D9]> a"
				+ "\n  WHERE a.<delete where col D9a> > 0"
				+ "\n  EXCEPT"
				+ "\n  SELECT b.emp_id, b.<delete select col D9b> AS metric_val"
				+ "\n  FROM <[except Data].[Feed Beta D9]> b"
				+ "\n  WHERE b.<delete where col D9b> > 0"
				+ "\n)"
				+ "\nDELETE FROM employees e"
				+ "\nUSING blended bl"
				+ "\nWHERE e.emp_id = bl.emp_id";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={with={1={cte={union={1={select={1={column={name=emp_id, table_ref=a}}, 2={column={substitution={name=<delete select col D9a>, type=column}, table_ref=a}, alias=metric_val}}, from={table={alias=a, substitution={name=<[except Data].[Feed Alpha D9]>, parts={1=[except Data], 2=[Feed Alpha D9]}, type=tuple}}}, where={condition={left={column={substitution={name=<delete where col D9a>, type=column}, table_ref=a}}, right={literal=0}, operator=>}}}, 2={union={qualifier=null, operator=EXCEPT}}, 3={select={1={column={name=emp_id, table_ref=b}}, 2={column={substitution={name=<delete select col D9b>, type=column}, table_ref=b}, alias=metric_val}}, from={table={alias=b, substitution={name=<[except Data].[Feed Beta D9]>, parts={1=[except Data], 2=[Feed Beta D9]}, type=tuple}}}, where={condition={left={column={substitution={name=<delete where col D9b>, type=column}, table_ref=b}}, right={literal=0}, operator=>}}}}}, alias=blended}}, query={delete={table={alias=e, table=employees}, using={1={table={alias=bl, table=blended}}}, where={condition={left={column={name=emp_id, table_ref=e}}, right={column={name=emp_id, table_ref=bl}}, operator==}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<delete where col D9b>=column, <[except Data].[Feed Beta D9]>=tuple, <delete where col D9a>=column, <[except Data].[Feed Alpha D9]>=tuple, <delete select col D9b>=column, <delete select col D9a>=column}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{<[except Data].[Feed Beta D9]>={<delete where col D9b>=[[@38,271:271='b',<393>,8:8]], <delete select col D9b>=[[@29,183:183='b',<393>,6:19]], emp_id=[[@25,173:173='b',<393>,6:9]]}, <[except Data].[Feed Alpha D9]>={<delete where col D9a>=[[@18,126:126='a',<393>,4:8]], <delete select col D9a>=[[@9,37:37='a',<393>,2:19]], emp_id=[[@5,27:27='a',<393>,2:9]]}, employees={emp_id=[[@52,349:349='e',<393>,12:6]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{union2={emp_id=[[@56,360:361='bl',<393>,12:17]]}, query0={metric_val=[[@13,66:75='metric_val',<393>,2:48]], emp_id=[[@7,29:34='emp_id',<393>,2:11]]}, query1={metric_val=[[@33,212:221='metric_val',<393>,6:48]], emp_id=[[@27,175:180='emp_id',<393>,6:11]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_delete3={context_list={blended=union2}, query_dictionary={}, def_union2={query_dictionary={emp_id=[[@56,360:361='bl',<393>,12:17]]}, def_query1={query_dictionary={metric_val=[[@33,212:221='metric_val',<393>,6:48]], emp_id=[[@27,175:180='emp_id',<393>,6:11]]}, table_dictionary={<[except Data].[Feed Beta D9]>={<delete where col D9b>=[[@38,271:271='b',<393>,8:8]], <delete select col D9b>=[[@29,183:183='b',<393>,6:19]], emp_id=[[@25,173:173='b',<393>,6:9]]}}, setop=EXCEPT, filters=[{substitution={name=<delete where col D9b>, type=column}, table_ref=b}], interface={metric_val=[{substitution={name=<delete select col D9b>, type=column}, table_ref=b}], emp_id=[{name=emp_id, table_ref=b}]}, table_alias={b=<[except Data].[Feed Beta D9]>}}, def_query0={query_dictionary={metric_val=[[@13,66:75='metric_val',<393>,2:48]], emp_id=[[@7,29:34='emp_id',<393>,2:11]]}, table_dictionary={<[except Data].[Feed Alpha D9]>={<delete where col D9a>=[[@18,126:126='a',<393>,4:8]], <delete select col D9a>=[[@9,37:37='a',<393>,2:19]], emp_id=[[@5,27:27='a',<393>,2:9]]}}, filters=[{substitution={name=<delete where col D9a>, type=column}, table_ref=a}], interface={metric_val=[{substitution={name=<delete select col D9a>, type=column}, table_ref=a}], emp_id=[{name=emp_id, table_ref=a}]}, table_alias={a=<[except Data].[Feed Alpha D9]>}}, interface={metric_val=query_column, emp_id=query_column}}, table_dictionary={employees={emp_id=[[@52,349:349='e',<393>,12:6]]}}, filters=[{name=emp_id, table_ref=e}, {name=emp_id, table_ref=bl}], interface=null, table_alias={blended=union2, e=employees, bl=union2}}}",
				extractor.getSymbolTable().toString());
	}


	@Test
	public void deleteComplexSubstitutionD10SubqueryGroupByHavingQualifyCombined() {
		final String query = "DELETE FROM employees e"
				+ "\nUSING ("
				+ "\n  SELECT a.emp_id, sum(a.<delete select col D10>) AS total_score,"
				+ "\n         row_number() OVER (PARTITION BY a.emp_id ORDER BY a.<delete order col D10> DESC) AS rn"
				+ "\n  FROM <[Agg Data].[Fact Table D10]> a"
				+ "\n  WHERE a.<delete where col D10> > 0"
				+ "\n  GROUP BY a.emp_id, a.<delete group col D10>"
				+ "\n  HAVING sum(a.<delete select col D10>) > 0"
				+ "\n  QUALIFY rn = 1"
				+ "\n) src"
				+ "\nWHERE e.emp_id = src.emp_id";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={delete={table={alias=e, table=employees}, using={1={table={alias=src, query={select={1={column={name=emp_id, table_ref=a}}, 2={function={function_name=sum, qualifier=null, parameters={column={substitution={name=<delete select col D10>, type=column}, table_ref=a}}}, alias=total_score}, 3={alias=rn, window_function={over={partition_by={1={column={name=emp_id, table_ref=a}}}, orderby={1={null_order=null, predicand={column={substitution={name=<delete order col D10>, type=column}, table_ref=a}}, sort_order=DESC}}}, function={function_name=row_number, parameters=null}}}}, having={condition={left={function={function_name=sum, qualifier=null, parameters={column={substitution={name=<delete select col D10>, type=column}, table_ref=a}}}}, right={literal=0}, operator=>}}, from={table={alias=a, substitution={name=<[Agg Data].[Fact Table D10]>, parts={1=[Agg Data], 2=[Fact Table D10]}, type=tuple}}}, where={condition={left={column={substitution={name=<delete where col D10>, type=column}, table_ref=a}}, right={literal=0}, operator=>}}, groupby={1={column={name=emp_id, table_ref=a}}, 2={column={substitution={name=<delete group col D10>, type=column}, table_ref=a}}}, qualify={condition={left={column={name=rn, table_ref=null}}, right={literal=1}, operator==}}}}}}, where={condition={left={column={name=emp_id, table_ref=e}}, right={column={name=emp_id, table_ref=src}}, operator==}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<[Agg Data].[Fact Table D10]>=tuple, <delete order col D10>=column, <delete select col D10>=column, <delete where col D10>=column, <delete group col D10>=column}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{<[Agg Data].[Fact Table D10]>={<delete order col D10>=[[@32,157:157='a',<393>,4:59]], <delete select col D10>=[[@13,55:55='a',<393>,3:23], [@60,329:329='a',<393>,8:13]], <delete where col D10>=[[@43,241:241='a',<393>,6:8]], <delete group col D10>=[[@54,291:291='a',<393>,7:21]], emp_id=[[@7,41:41='a',<393>,3:9], [@27,139:139='a',<393>,4:41], [@50,281:281='a',<393>,7:11]]}, employees={emp_id=[[@73,389:389='e',<393>,11:6]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={total_score=[[@18,85:95='total_score',<393>,3:53]], rn=[[@38,191:192='rn',<393>,4:93], [@67,370:371='rn',<393>,9:10]], emp_id=[[@9,43:48='emp_id',<393>,3:11], [@77,400:402='src',<393>,11:17]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_delete1={query_dictionary={}, table_dictionary={employees={emp_id=[[@73,389:389='e',<393>,11:6]]}}, def_query0={window_ordered_by=[{substitution={name=<delete order col D10>, type=column}, table_ref=a}], query_dictionary={total_score=[[@18,85:95='total_score',<393>,3:53]], rn=[[@38,191:192='rn',<393>,4:93], [@67,370:371='rn',<393>,9:10]], emp_id=[[@9,43:48='emp_id',<393>,3:11], [@77,400:402='src',<393>,11:17]]}, table_dictionary={<[Agg Data].[Fact Table D10]>={<delete order col D10>=[[@32,157:157='a',<393>,4:59]], <delete select col D10>=[[@13,55:55='a',<393>,3:23], [@60,329:329='a',<393>,8:13]], <delete where col D10>=[[@43,241:241='a',<393>,6:8]], <delete group col D10>=[[@54,291:291='a',<393>,7:21]], emp_id=[[@7,41:41='a',<393>,3:9], [@27,139:139='a',<393>,4:41], [@50,281:281='a',<393>,7:11]]}}, grouped_by=[{name=emp_id, table_ref=a}, {substitution={name=<delete group col D10>, type=column}, table_ref=a}], window_partition_by=[{name=emp_id, table_ref=a}], filters=[{substitution={name=<delete where col D10>, type=column}, table_ref=a}, {substitution={name=<delete select col D10>, type=column}, table_ref=a}, {name=rn, table_ref=query0}], interface={total_score=[{substitution={name=<delete select col D10>, type=column}, table_ref=a}], rn=[{name=emp_id, table_ref=a}, {substitution={name=<delete order col D10>, type=column}, table_ref=a}], emp_id=[{name=emp_id, table_ref=a}]}, table_alias={a=<[Agg Data].[Fact Table D10]>}}, filters=[{name=emp_id, table_ref=e}, {name=emp_id, table_ref=src}], interface=null, table_alias={e=employees, src=query0}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void updateFromSelectValuesWithExplicitColumnNamesV1() {
		final String query = " update employees e set score = src.col1, rank_bucket = src.col2"
				+ "\n from (select col1, col2 from (values (100, 1)) as value_src (col1, col2)) src"
				+ "\n where e.emp_id = 1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong",
		"{SQL={update={from={table={alias=src, query={select={1={column={name=col1, table_ref=null}}, 2={column={name=col2, table_ref=null}}}, from={values={columns={1={column={name=col1, table_ref=null}}, 2={column={name=col2, table_ref=null}}}, alias=value_src, matrix={1={row={1={literal=100}, 2={literal=1}}}}}}}}}, where={condition={left={column={name=emp_id, table_ref=e}}, right={literal=1}, operator==}}, assignments={1={set={column={name=score, table_ref=null}}, to={column={name=col1, table_ref=src}}}, 2={set={column={name=rank_bucket, table_ref=null}}, to={column={name=col2, table_ref=src}}}}, table={alias=e, table=employees}}}}",
			extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[score, rank_bucket]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{employees={score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@10,42:52='rank_bucket',<393>,1:42]], emp_id=[[@40,151:151='e',<393>,3:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{values0={col2=[[@35,133:136='col2',<393>,2:68], [@20,85:88='col2',<393>,2:20]], col1=[[@33,127:130='col1',<393>,2:62], [@18,79:82='col1',<393>,2:14]]}, query1={col2=[[@20,85:88='col2',<393>,2:20], [@12,56:58='src',<393>,1:56]], col1=[[@18,79:82='col1',<393>,2:14], [@6,32:34='src',<393>,1:32]]}, update2={score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@10,42:52='rank_bucket',<393>,1:42]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_update2={assignments={score=[{name=col1, table_ref=src}], rank_bucket=[{name=col2, table_ref=src}]}, table_dictionary={employees={score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@10,42:52='rank_bucket',<393>,1:42]], emp_id=[[@40,151:151='e',<393>,3:7]]}}, update_dictionary={score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@10,42:52='rank_bucket',<393>,1:42]]}, def_query1={query_dictionary={col2=[[@20,85:88='col2',<393>,2:20], [@12,56:58='src',<393>,1:56]], col1=[[@18,79:82='col1',<393>,2:14], [@6,32:34='src',<393>,1:32]]}, def_values0={query_dictionary={col2=[[@35,133:136='col2',<393>,2:68], [@20,85:88='col2',<393>,2:20]], col1=[[@33,127:130='col1',<393>,2:62], [@18,79:82='col1',<393>,2:14]]}, interface={col2=[], col1=[]}}, interface={col2=[{name=col2, table_ref=values0}], col1=[{name=col1, table_ref=values0}]}, table_alias={value_src=values0}}, filters=[{name=emp_id, table_ref=e}], table_alias={e=employees, src=query1}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void updateFromSelectValuesWithImplicitColumnNamesV2() {
		final String query = " update employees e set score = src.col1, rank_bucket = src.col2"
				+ "\n from (select $1 as col1, $2 as col2 from (values (100, 1)) as value_src) src"
				+ "\n where e.emp_id = 1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={update={from={table={alias=src, query={select={1={alias=col1, literal=1}, 2={alias=col2, literal=2}}, from={values={alias=value_src, matrix={1={row={1={literal=100}, 2={literal=1}}}}}}}}}, where={condition={left={column={name=emp_id, table_ref=e}}, right={literal=1}, operator==}}, assignments={1={set={column={name=score, table_ref=null}}, to={column={name=col1, table_ref=src}}}, 2={set={column={name=rank_bucket, table_ref=null}}, to={column={name=col2, table_ref=src}}}}, table={alias=e, table=employees}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[score, rank_bucket]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{employees={score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@10,42:52='rank_bucket',<393>,1:42]], emp_id=[[@39,150:150='e',<393>,3:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{values0={$1=[[@28,115:115='(',<287>,2:50]], $2=[[@28,115:115='(',<287>,2:50]]}, query1={col2=[[@24,97:100='col2',<393>,2:32], [@12,56:58='src',<393>,1:56]], col1=[[@20,85:88='col1',<393>,2:20], [@6,32:34='src',<393>,1:32]]}, update2={score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@10,42:52='rank_bucket',<393>,1:42]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_update2={assignments={score=[{name=col1, table_ref=src}], rank_bucket=[{name=col2, table_ref=src}]}, table_dictionary={employees={score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@10,42:52='rank_bucket',<393>,1:42]], emp_id=[[@39,150:150='e',<393>,3:7]]}}, update_dictionary={score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@10,42:52='rank_bucket',<393>,1:42]]}, def_query1={query_dictionary={col2=[[@24,97:100='col2',<393>,2:32], [@12,56:58='src',<393>,1:56]], col1=[[@20,85:88='col1',<393>,2:20], [@6,32:34='src',<393>,1:32]]}, def_values0={query_dictionary={$1=[[@28,115:115='(',<287>,2:50]], $2=[[@28,115:115='(',<287>,2:50]]}, interface={$1=[], $2=[]}}, interface={col2=[], col1=[]}, table_alias={value_src=values0}}, filters=[{name=emp_id, table_ref=e}], table_alias={e=employees, src=query1}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void updateFromSelectValuesWithImplicitColumnNamesV3() {
		final String query = " update employees e set score = src.col1, rank_bucket = src.col2"
				+ "\n from (select $1 as col1, $2 as col2 from (values (100, 1))) src"
				+ "\n where e.emp_id = 1";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={update={from={table={alias=src, query={select={1={alias=col1, literal=1}, 2={alias=col2, literal=2}}, from={values={matrix={1={row={1={literal=100}, 2={literal=1}}}}}}}}}, where={condition={left={column={name=emp_id, table_ref=e}}, right={literal=1}, operator==}}, assignments={1={set={column={name=score, table_ref=null}}, to={column={name=col1, table_ref=src}}}, 2={set={column={name=rank_bucket, table_ref=null}}, to={column={name=col2, table_ref=src}}}}, table={alias=e, table=employees}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[score, rank_bucket]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{employees={score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@10,42:52='rank_bucket',<393>,1:42]], emp_id=[[@37,137:137='e',<393>,3:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{values0={$1=[[@28,115:115='(',<287>,2:50]], $2=[[@28,115:115='(',<287>,2:50]]}, query1={col2=[[@24,97:100='col2',<393>,2:32], [@12,56:58='src',<393>,1:56]], col1=[[@20,85:88='col1',<393>,2:20], [@6,32:34='src',<393>,1:32]]}, update2={score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@10,42:52='rank_bucket',<393>,1:42]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_update2={assignments={score=[{name=col1, table_ref=src}], rank_bucket=[{name=col2, table_ref=src}]}, table_dictionary={employees={score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@10,42:52='rank_bucket',<393>,1:42]], emp_id=[[@37,137:137='e',<393>,3:7]]}}, update_dictionary={score=[[@4,24:28='score',<393>,1:24]], rank_bucket=[[@10,42:52='rank_bucket',<393>,1:42]]}, def_query1={query_dictionary={col2=[[@24,97:100='col2',<393>,2:32], [@12,56:58='src',<393>,1:56]], col1=[[@20,85:88='col1',<393>,2:20], [@6,32:34='src',<393>,1:32]]}, def_values0={query_dictionary={$1=[[@28,115:115='(',<287>,2:50]], $2=[[@28,115:115='(',<287>,2:50]]}, interface={$1=[], $2=[]}}, interface={col2=[], col1=[]}, table_alias={values0=values0}}, filters=[{name=emp_id, table_ref=e}], table_alias={e=employees, src=query1}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void deleteFromSelectValuesWithExplicitColumnNamesV1() {
		final String query = " delete from employees e"
				+ "\n where e.emp_id in ("
				+ "\n   select col1 from (values (100), (200)) as value_src (col1)"
				+ "\n )";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={delete={table={alias=e, table=employees}, where={in={item={column={name=emp_id, table_ref=e}}, in_list={select={1={column={name=col1, table_ref=null}}}, from={values={columns={1={column={name=col1, table_ref=null}}}, alias=value_src, matrix={1={row={1={literal=100}}}, 2={row={1={literal=200}}}}}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{employees={emp_id=[[@5,32:32='e',<393>,2:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{values0={col1=[[@26,102:105='col1',<393>,3:56], [@11,56:59='col1',<393>,3:10]]}, query1={col1=[[@11,56:59='col1',<393>,3:10]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_delete3={query_dictionary={}, table_dictionary={employees={emp_id=[[@5,32:32='e',<393>,2:7]]}}, def_query1={query_dictionary={col1=[[@11,56:59='col1',<393>,3:10]]}, def_values0={query_dictionary={col1=[[@26,102:105='col1',<393>,3:56], [@11,56:59='col1',<393>,3:10]]}, interface={col1=[]}}, interface={col1=[{name=col1, table_ref=values0}]}, table_alias={value_src=values0}}, dependent_queries={in_list2={query=query1, type=filters}}, filters=[{name=emp_id, table_ref=e}], interface=null, table_alias={e=employees}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void deleteFromSelectValuesWithImlicitColumnNamesV2() {
		final String query = " delete from employees e"
				+ "\n where e.emp_id in ("
				+ "\n   select $1 as col1 from (values (100), (200)) as value_src"
				+ "\n )";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={delete={table={alias=e, table=employees}, where={in={item={column={name=emp_id, table_ref=e}}, in_list={select={1={alias=col1, literal=1}}, from={values={alias=value_src, matrix={1={row={1={literal=100}}}, 2={row={1={literal=200}}}}}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{employees={emp_id=[[@5,32:32='e',<393>,2:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{values0={$1=[[@17,80:80='(',<287>,3:34], [@21,87:87='(',<287>,3:41]]}, query1={col1=[[@13,62:65='col1',<393>,3:16]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_delete3={query_dictionary={}, table_dictionary={employees={emp_id=[[@5,32:32='e',<393>,2:7]]}}, def_query1={query_dictionary={col1=[[@13,62:65='col1',<393>,3:16]]}, def_values0={query_dictionary={$1=[[@17,80:80='(',<287>,3:34], [@21,87:87='(',<287>,3:41]]}, interface={$1=[]}}, interface={col1=[]}, table_alias={value_src=values0}}, dependent_queries={in_list2={query=query1, type=filters}}, filters=[{name=emp_id, table_ref=e}], interface=null, table_alias={e=employees}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void deleteFromSelectValuesWithImlicitColumnNamesV3() {
		final String query = " delete from employees e"
				+ "\n where e.emp_id in ("
				+ "\n   select $1 as col1 from (values (100), (200))"
				+ "\n )";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={delete={table={alias=e, table=employees}, where={in={item={column={name=emp_id, table_ref=e}}, in_list={select={1={alias=col1, literal=1}}, from={values={matrix={1={row={1={literal=100}}}, 2={row={1={literal=200}}}}}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{employees={emp_id=[[@5,32:32='e',<393>,2:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{values0={$1=[[@17,80:80='(',<287>,3:34], [@21,87:87='(',<287>,3:41]]}, query1={col1=[[@13,62:65='col1',<393>,3:16]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_delete3={query_dictionary={}, table_dictionary={employees={emp_id=[[@5,32:32='e',<393>,2:7]]}}, def_query1={query_dictionary={col1=[[@13,62:65='col1',<393>,3:16]]}, def_values0={query_dictionary={$1=[[@17,80:80='(',<287>,3:34], [@21,87:87='(',<287>,3:41]]}, interface={$1=[]}}, interface={col1=[]}, table_alias={values0=values0}}, dependent_queries={in_list2={query=query1, type=filters}}, filters=[{name=emp_id, table_ref=e}], interface=null, table_alias={e=employees}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void updateSetQualifiedColumnLhsPredicandRhsTest() {
		final String query = "UPDATE employees e SET e.<target col> = <source predicand>";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={update={table={alias=e, table=employees}, assignments={1={set={column={substitution={name=<target col>, type=column}, table_ref=e}}, to={substitution={name=<source predicand>, type=predicand}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Substitution List is wrong", "{<target col>=column, <source predicand>=predicand}",
				extractor.getSubstitutionsMap().toString());
	}

	@Test
	public void updateSetLiteralLhsPredicandRhsTest() {
		final String query = "UPDATE employees SET score = <source predicand>";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={update={table={alias=null, table=employees}, assignments={1={set={column={name=score, table_ref=null}}, to={substitution={name=<source predicand>, type=predicand}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Substitution List is wrong", "{<source predicand>=predicand}",
				extractor.getSubstitutionsMap().toString());
	}

	@Test
	public void updateSetPredicandRhsParenthesizedTest() {
		final String query = "UPDATE employees SET score = (<source predicand>)";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={update={table={alias=null, table=employees}, assignments={1={set={column={name=score, table_ref=null}}, to={substitution={name=<source predicand>, type=predicand}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Substitution List is wrong", "{<source predicand>=predicand}",
				extractor.getSubstitutionsMap().toString());
	}

	@Test
	public void updateWhereBareConditionSubstitutionTest() {
		final String query = "UPDATE employees SET score = 1 WHERE <filter>";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={update={table={alias=null, table=employees}, where={substitution={name=<filter>, type=condition}}, assignments={1={set={column={name=score, table_ref=null}}, to={literal=1}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Substitution List is wrong", "{<filter>=condition}",
				extractor.getSubstitutionsMap().toString());
	}

	@Test
	public void deleteWhereBareConditionSubstitutionTest() {
		final String query = "DELETE FROM employees WHERE <filter>";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={delete={table={alias=null, table=employees}, where={substitution={name=<filter>, type=condition}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Substitution List is wrong", "{<filter>=condition}",
				extractor.getSubstitutionsMap().toString());
	}

	@Test
	public void updateFromBareQueryVariableTest() {
		// table_source_primary variable_identifier in FROM is typed tuple per grammar (requires alias).
		final String query = "UPDATE t SET a = 1 FROM <query variable> src";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={update={table={alias=null, table=t}, from={table={alias=src, substitution={name=<query variable>, type=tuple}}}, assignments={1={set={column={name=a, table_ref=null}}, to={literal=1}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Substitution List is wrong", "{<query variable>=tuple}",
				extractor.getSubstitutionsMap().toString());
	}

	@Test
	public void deleteUsingBareQueryVariableTest() {
		final String query = "DELETE FROM t USING <query variable> src";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={delete={table={alias=null, table=t}, using={1={table={alias=src, substitution={name=<query variable>, type=tuple}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Substitution List is wrong", "{<query variable>=tuple}",
				extractor.getSubstitutionsMap().toString());
	}

	@Test
	public void updateReturningStarInterfaceTest() {
		final String query = "UPDATE employees e SET score = 1 RETURNING *";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={update={table={alias=e, table=employees}, returning={1={column={name=*, table_ref=*}}}, assignments={1={set={column={name=score, table_ref=null}}, to={literal=1}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[score, *]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_update0={assignments={score=[]}, query_dictionary={*=[[@8,43:43='*',<291>,1:43]]}, table_dictionary={employees={*=[[@8,43:43='*',<291>,1:43]]}}, update_dictionary={score=[[@4,23:27='score',<393>,1:23]]}, target_table={employees={score=[[@4,23:27='score',<393>,1:23]]}}, interface={score=[], *=[{name=*, table_ref=*}]}, table_alias={e=employees}, lhs_unresolved_columns={score={column={name=score, table_ref=null}, locations=[[@4,23:27='score',<393>,1:23]]}}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void updateReturningQualifiedColumnsTest() {
		final String query = "UPDATE employees e SET score = 1 RETURNING e.emp_id AS updated_id, e.score";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={update={table={alias=e, table=employees}, returning={1={column={name=emp_id, table_ref=e}, alias=updated_id}, 2={column={name=score, table_ref=e}}}, assignments={1={set={column={name=score, table_ref=null}}, to={literal=1}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[score, updated_id]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_update0={assignments={score=[]}, query_dictionary={score=[[@16,69:73='score',<393>,1:69]], updated_id=[[@12,55:64='updated_id',<393>,1:55]]}, table_dictionary={employees={score=[[@14,67:67='e',<393>,1:67]], emp_id=[[@8,43:43='e',<393>,1:43]]}}, update_dictionary={score=[[@4,23:27='score',<393>,1:23]]}, target_table={employees={score=[[@4,23:27='score',<393>,1:23]]}}, interface={score=[], updated_id=[{name=emp_id, table_ref=e}]}, table_alias={e=employees}, lhs_unresolved_columns={score={column={name=score, table_ref=null}, locations=[[@4,23:27='score',<393>,1:23]]}}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void updateReturningWithFromSubqueryTest() {
		final String query = "UPDATE employees e SET score = 1 FROM (SELECT emp_id, row_number() OVER () AS rn FROM employees) src WHERE e.emp_id = src.emp_id RETURNING src.rn, e.emp_id";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={update={from={table={alias=src, query={select={1={column={name=emp_id, table_ref=null}}, 2={alias=rn, window_function={over=null, function={function_name=row_number, parameters=null}}}}, from={table={alias=null, table=employees}}}}}, where={condition={left={column={name=emp_id, table_ref=e}}, right={column={name=emp_id, table_ref=src}}, operator==}}, assignments={1={set={column={name=score, table_ref=null}}, to={literal=1}}}, table={alias=e, table=employees}, returning={1={column={name=rn, table_ref=src}}, 2={column={name=emp_id, table_ref=e}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[score, rn, emp_id]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_update1={assignments={score=[]}, table_dictionary={employees={score=[[@4,23:27='score',<393>,1:23]], emp_id=[[@25,107:107='e',<393>,1:107], [@37,147:147='e',<393>,1:147]]}}, update_dictionary={score=[[@4,23:27='score',<393>,1:23]]}, def_query0={query_dictionary={rn=[[@19,78:79='rn',<393>,1:78], [@33,139:141='src',<393>,1:139]], emp_id=[[@10,46:51='emp_id',<393>,1:46], [@29,118:120='src',<393>,1:118]]}, table_dictionary={employees={emp_id=[[@10,46:51='emp_id',<393>,1:46], [@25,107:107='e',<393>,1:107], [@37,147:147='e',<393>,1:147]]}}, interface={rn=[], emp_id=[{name=emp_id, table_ref=employees}]}}, filters=[{name=emp_id, table_ref=e}, {name=emp_id, table_ref=src}], interface={score=[], rn=[{name=rn, table_ref=src}], emp_id=[{name=emp_id, table_ref=e}]}, query_dictionary={rn=[[@35,143:144='rn',<393>,1:143]], emp_id=[[@39,149:154='emp_id',<393>,1:149]]}, table_alias={e=employees, src=query0}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void updateReturningPredicandSubstitutionTest() {
		final String query = "UPDATE employees e SET score = 1 RETURNING e.emp_id, <returning predicand>";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={update={table={alias=e, table=employees}, returning={1={column={name=emp_id, table_ref=e}}, 2={substitution={name=<returning predicand>, type=predicand}}}, assignments={1={set={column={name=score, table_ref=null}}, to={literal=1}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[score, <returning predicand>, emp_id]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<returning predicand>=predicand}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_update0={assignments={score=[]}, query_dictionary={<returning predicand>=[[@12,53:73='<returning predicand>',<327>,1:53]], emp_id=[[@10,45:50='emp_id',<393>,1:45]]}, table_dictionary={employees={emp_id=[[@8,43:43='e',<393>,1:43]]}}, update_dictionary={score=[[@4,23:27='score',<393>,1:23]]}, target_table={employees={score=[[@4,23:27='score',<393>,1:23]]}}, interface={score=[], <returning predicand>=[{name=<returning predicand>, type=predicand}], emp_id=[{name=emp_id, table_ref=e}]}, table_alias={e=employees}, lhs_unresolved_columns={score={column={name=score, table_ref=null}, locations=[[@4,23:27='score',<393>,1:23]]}}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void postgresInsertReturningSelectListInterfaceTest() {
		final String query = "INSERT INTO employees (score, rank_bucket) VALUES (100, 1) RETURNING score, emp_id AS updated_id";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={insert={preamble=insert_into, from={values={matrix={1={row={1={literal=100}, 2={literal=1}}}}}}, target_table={table={alias=null, table=employees}}, columns={1={column={name=score, table_ref=null}}, 2={column={name=rank_bucket, table_ref=null}}}, returning={1={column={name=score, table_ref=employees}}, 2={column={name=emp_id, table_ref=employees}, alias=updated_id}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[score, rank_bucket, updated_id]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_insert1={query_dictionary={score=[[@4,23:27='score',<393>,1:23], [@15,69:73='score',<393>,1:69], [@15,69:73='score',<393>,1:69]], updated_id=[[@19,86:95='updated_id',<393>,1:86]], rank_bucket=[[@6,30:40='rank_bucket',<393>,1:30]], emp_id=[[@17,76:81='emp_id',<393>,1:76]]}, table_dictionary={employees={score=[[@4,23:27='score',<393>,1:23], [@15,69:73='score',<393>,1:69], [@15,69:73='score',<393>,1:69]], rank_bucket=[[@6,30:40='rank_bucket',<393>,1:30]], emp_id=[[@17,76:81='emp_id',<393>,1:76]]}}, def_values0={query_dictionary={$1=[[@9,50:50='(',<287>,1:50]], $2=[[@9,50:50='(',<287>,1:50]]}, interface={$1=[], $2=[]}}, _tmp_insert_source_select_sequence=[score, updated_id], interface={score=[{name=$1, table_ref=values0}], rank_bucket=[{name=$2, table_ref=values0}], updated_id=[{name=emp_id, table_ref=employees}]}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void postgresInsertOnConflictDoNothingTest() {
		final String query = "INSERT INTO employees (emp_id, score) VALUES (1, 100) ON CONFLICT (emp_id) DO NOTHING";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={insert={preamble=insert_into, from={values={matrix={1={row={1={literal=1}, 2={literal=100}}}}}}, target_table={table={alias=null, table=employees}}, columns={1={column={name=emp_id, table_ref=null}}, 2={column={name=score, table_ref=null}}}, on_conflict={target={1={column={name=emp_id, table_ref=employees}}}, action={do=NOTHING}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[score, emp_id]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_insert1={query_dictionary={score=[[@6,31:35='score',<393>,1:31]], emp_id=[[@4,23:28='emp_id',<393>,1:23], [@17,67:72='emp_id',<393>,1:67]]}, table_dictionary={employees={score=[[@6,31:35='score',<393>,1:31]], emp_id=[[@4,23:28='emp_id',<393>,1:23], [@17,67:72='emp_id',<393>,1:67]]}}, def_values0={query_dictionary={$1=[[@9,45:45='(',<287>,1:45]], $2=[[@9,45:45='(',<287>,1:45]]}, interface={$1=[], $2=[]}}, interface={emp_id=[{name=$1, table_ref=values0}], score=[{name=$2, table_ref=values0}]}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void postgresInsertOnConflictDoNothingWithoutTargetTest() {
		final String query = "INSERT INTO employees (emp_id, score) VALUES (1, 100) ON CONFLICT DO NOTHING";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={insert={preamble=insert_into, from={values={matrix={1={row={1={literal=1}, 2={literal=100}}}}}}, target_table={table={alias=null, table=employees}}, columns={1={column={name=emp_id, table_ref=null}}, 2={column={name=score, table_ref=null}}}, on_conflict={action={do=NOTHING}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[score, emp_id]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_insert1={query_dictionary={score=[[@6,31:35='score',<393>,1:31]], emp_id=[[@4,23:28='emp_id',<393>,1:23]]}, table_dictionary={employees={score=[[@6,31:35='score',<393>,1:31]], emp_id=[[@4,23:28='emp_id',<393>,1:23]]}}, def_values0={query_dictionary={$1=[[@9,45:45='(',<287>,1:45]], $2=[[@9,45:45='(',<287>,1:45]]}, interface={$1=[], $2=[]}}, interface={emp_id=[{name=$1, table_ref=values0}], score=[{name=$2, table_ref=values0}]}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void postgresInsertOnConflictDoUpdateTest() {
		final String query = "INSERT INTO employees (emp_id, score) VALUES (1, 100)"
				+ " ON CONFLICT (emp_id) DO UPDATE SET score = EXCLUDED.score";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={insert={preamble=insert_into, from={values={matrix={1={row={1={literal=1}, 2={literal=100}}}}}}, target_table={table={alias=null, table=employees}}, columns={1={column={name=emp_id, table_ref=null}}, 2={column={name=score, table_ref=null}}}, on_conflict={target={1={column={name=emp_id, table_ref=employees}}}, action={do=UPDATE, assignments={1={set={column={name=score, table_ref=employees}}, to={column={name=score, table_ref=EXCLUDED}}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[score, emp_id]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_insert2={query_dictionary={score=[[@6,31:35='score',<393>,1:31], [@22,89:93='score',<393>,1:89]], emp_id=[[@4,23:28='emp_id',<393>,1:23], [@17,67:72='emp_id',<393>,1:67]]}, table_dictionary={employees={score=[[@6,31:35='score',<393>,1:31], [@22,89:93='score',<393>,1:89]], emp_id=[[@4,23:28='emp_id',<393>,1:23], [@17,67:72='emp_id',<393>,1:67]]}}, def_update1={assignments={score=[{name=score, table_ref=EXCLUDED}]}, query_dictionary={score=[[@22,89:93='score',<393>,1:89]]}, table_dictionary={employees={score=[[@6,31:35='score',<393>,1:31], [@22,89:93='score',<393>,1:89]], emp_id=[[@4,23:28='emp_id',<393>,1:23], [@17,67:72='emp_id',<393>,1:67]]}}, update_dictionary={score=[[@22,89:93='score',<393>,1:89]]}, target_table={employees={}}, lhs_unresolved_columns={employees.score={name=score, table_ref=employees}}}, def_values0={query_dictionary={$1=[[@9,45:45='(',<287>,1:45]], $2=[[@9,45:45='(',<287>,1:45]]}, interface={$1=[], $2=[]}}, interface={emp_id=[{name=$1, table_ref=values0}], score=[{name=$2, table_ref=values0}]}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void postgresInsertOnConflictDoUpdateWithWhereTest() {
		final String query = "INSERT INTO employees (emp_id, score) VALUES (1, 100)"
				+ " ON CONFLICT (emp_id) DO UPDATE SET score = EXCLUDED.score WHERE employees.score < 50";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={insert={preamble=insert_into, from={values={matrix={1={row={1={literal=1}, 2={literal=100}}}}}}, target_table={table={alias=null, table=employees}}, columns={1={column={name=emp_id, table_ref=null}}, 2={column={name=score, table_ref=null}}}, on_conflict={target={1={column={name=emp_id, table_ref=employees}}}, action={do=UPDATE, assignments={1={set={column={name=score, table_ref=employees}}, to={column={name=score, table_ref=EXCLUDED}}}}, where={condition={left={column={name=score, table_ref=employees}}, right={literal=50}, operator=<}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[score, emp_id]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_insert2={query_dictionary={score=[[@6,31:35='score',<393>,1:31], [@22,89:93='score',<393>,1:89]], emp_id=[[@4,23:28='emp_id',<393>,1:23], [@17,67:72='emp_id',<393>,1:67]]}, table_dictionary={employees={score=[[@6,31:35='score',<393>,1:31], [@22,89:93='score',<393>,1:89]], emp_id=[[@4,23:28='emp_id',<393>,1:23], [@17,67:72='emp_id',<393>,1:67]]}}, def_update1={assignments={score=[{name=score, table_ref=EXCLUDED}]}, query_dictionary={score=[[@22,89:93='score',<393>,1:89]]}, table_dictionary={employees={score=[[@6,31:35='score',<393>,1:31], [@22,89:93='score',<393>,1:89]], emp_id=[[@4,23:28='emp_id',<393>,1:23], [@17,67:72='emp_id',<393>,1:67]]}}, update_dictionary={score=[[@22,89:93='score',<393>,1:89]]}, target_table={employees={}}, filters=[{name=score, table_ref=employees}], lhs_unresolved_columns={employees.score={name=score, table_ref=employees}}}, def_values0={query_dictionary={$1=[[@9,45:45='(',<287>,1:45]], $2=[[@9,45:45='(',<287>,1:45]]}, interface={$1=[], $2=[]}}, interface={emp_id=[{name=$1, table_ref=values0}], score=[{name=$2, table_ref=values0}]}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void postgresInsertDefaultValuesTest() {
		final String query = "INSERT INTO employees DEFAULT VALUES";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={insert={preamble=insert_into, from={default_values=true}, target_table={table={alias=null, table=employees}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_insert0={table_dictionary={employees={}}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void postgresInsertWithCteBodyTest() {
		final String query = "WITH ins AS (INSERT INTO employees (score, rank_bucket) VALUES (100, 1) RETURNING score, emp_id) SELECT score FROM ins";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={with={1={cte={insert={preamble=insert_into, from={values={matrix={1={row={1={literal=100}, 2={literal=1}}}}}}, target_table={table={alias=null, table=employees}}, columns={1={column={name=score, table_ref=null}}, 2={column={name=rank_bucket, table_ref=null}}}, returning={1={column={name=score, table_ref=employees}}, 2={column={name=emp_id, table_ref=employees}}}}}, alias=ins}}, query={select={1={column={name=score, table_ref=null}}}, from={table={alias=null, table=ins}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[score]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query2={context_list={ins=insert1}, query_dictionary={score=[[@24,104:108='score',<393>,1:104]]}, def_insert1={query_dictionary={score=[[@8,36:40='score',<393>,1:36], [@19,82:86='score',<393>,1:82], [@19,82:86='score',<393>,1:82]], rank_bucket=[[@10,43:53='rank_bucket',<393>,1:43]], emp_id=[[@21,89:94='emp_id',<393>,1:89], [@21,89:94='emp_id',<393>,1:89]]}, table_dictionary={employees={score=[[@8,36:40='score',<393>,1:36], [@19,82:86='score',<393>,1:82], [@19,82:86='score',<393>,1:82]], rank_bucket=[[@10,43:53='rank_bucket',<393>,1:43]], emp_id=[[@21,89:94='emp_id',<393>,1:89], [@21,89:94='emp_id',<393>,1:89]]}}, def_values0={query_dictionary={$1=[[@13,63:63='(',<287>,1:63]], $2=[[@13,63:63='(',<287>,1:63]]}, interface={$1=[], $2=[]}}, _tmp_insert_source_select_sequence=[score, emp_id], interface={score=[{name=$1, table_ref=values0}], rank_bucket=[{name=$2, table_ref=values0}], emp_id=[{name=emp_id, table_ref=employees}]}}, _tmp_insert_source_select_sequence=[score], interface={score=[{name=score, table_ref=insert1}]}, table_alias={ins=insert1}}}",
				extractor.getSymbolTable().toString());
	}

	/** Walker coverage T2.2 — {@code insert_target_table_primary} substitution variable target. */
	@Test
	public void insertTargetSubstitutionVariableT2_2Test() {
		final String query = "INSERT INTO <staging_dest> (score, rank_bucket) VALUES (1, 2)";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={insert={preamble=insert_into, from={values={matrix={1={row={1={literal=1}, 2={literal=2}}}}}}, target_table={table={alias=null, substitution={name=<staging_dest>, type=tuple}}}, columns={1={column={name=score, table_ref=null}}, 2={column={name=rank_bucket, table_ref=null}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[score, rank_bucket]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<staging_dest>=tuple}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{<staging_dest>={score=[[@4,28:32='score',<393>,1:28]], rank_bucket=[[@6,35:45='rank_bucket',<393>,1:35]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{values0={$1=[[@9,55:55='(',<287>,1:55]], $2=[[@9,55:55='(',<287>,1:55]]}, insert1={score=[[@4,28:32='score',<393>,1:28]], rank_bucket=[[@6,35:45='rank_bucket',<393>,1:35]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_insert1={query_dictionary={score=[[@4,28:32='score',<393>,1:28]], rank_bucket=[[@6,35:45='rank_bucket',<393>,1:35]]}, table_dictionary={<staging_dest>={score=[[@4,28:32='score',<393>,1:28]], rank_bucket=[[@6,35:45='rank_bucket',<393>,1:35]]}}, def_values0={query_dictionary={$1=[[@9,55:55='(',<287>,1:55]], $2=[[@9,55:55='(',<287>,1:55]]}, interface={$1=[], $2=[]}}, interface={score=[{name=$1, table_ref=values0}], rank_bucket=[{name=$2, table_ref=values0}]}}}",
				extractor.getSymbolTable().toString());
	}

	/** Walker coverage T2.2 — {@code insert_target_table_primary} Jinja {@code ref(...)} target. */
	@Test
	public void insertTargetJinjaRefT2_2Test() {
		final String query = "INSERT INTO {{ ref('employees') }} (score) VALUES (100)";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={insert={preamble=insert_into, from={values={matrix={1={row={1={literal=100}}}}}}, target_table={table={alias=null, substitution={name={{ ref('employees') }}, parts={jinja_table={function_name=ref, parameters={1={literal='employees'}}}}, type=tuple}}}, columns={1={column={name=score, table_ref=null}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[score]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{{{ ref('employees') }}=tuple}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{{{ ref('employees') }}={score=[[@9,36:40='score',<393>,1:36]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{values0={$1=[[@12,50:50='(',<287>,1:50]]}, insert1={score=[[@9,36:40='score',<393>,1:36]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_insert1={query_dictionary={score=[[@9,36:40='score',<393>,1:36]]}, table_dictionary={{{ ref('employees') }}={score=[[@9,36:40='score',<393>,1:36]]}}, def_values0={query_dictionary={$1=[[@12,50:50='(',<287>,1:50]]}, interface={$1=[]}}, interface={score=[{name=$1, table_ref=values0}]}}}",
				extractor.getSymbolTable().toString());
	}

	/** Walker coverage T2.2 — {@code insert_target_table_primary} target {@code AS} alias (no column list). */
	@Test
	public void insertTargetRelationAliasT2_2Test() {
		final String query = "INSERT INTO employees AS tgt SELECT score, emp_id FROM perf_feed";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={insert={preamble=insert_into, from={from={table={alias=null, table=perf_feed}}, select={1={column={name=score, table_ref=null}}, 2={column={name=emp_id, table_ref=null}}}}, target_table={table={alias=tgt, table=employees}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[score, emp_id]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{perf_feed={score=[[@6,36:40='score',<393>,1:36]], emp_id=[[@8,43:48='emp_id',<393>,1:43]]}, employees={score=[[@6,36:40='score',<393>,1:36]], emp_id=[[@8,43:48='emp_id',<393>,1:43]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={score=[[@6,36:40='score',<393>,1:36]], emp_id=[[@8,43:48='emp_id',<393>,1:43]]}, insert1={score=[[@6,36:40='score',<393>,1:36]], emp_id=[[@8,43:48='emp_id',<393>,1:43]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_insert1={query_dictionary={score=[[@6,36:40='score',<393>,1:36]], emp_id=[[@8,43:48='emp_id',<393>,1:43]]}, table_dictionary={employees={score=[[@6,36:40='score',<393>,1:36]], emp_id=[[@8,43:48='emp_id',<393>,1:43]]}}, def_query0={query_dictionary={score=[[@6,36:40='score',<393>,1:36]], emp_id=[[@8,43:48='emp_id',<393>,1:43]]}, table_dictionary={perf_feed={score=[[@6,36:40='score',<393>,1:36]], emp_id=[[@8,43:48='emp_id',<393>,1:43]]}}, interface={score=[{name=score, table_ref=perf_feed}], emp_id=[{name=emp_id, table_ref=perf_feed}]}}, interface={score=[{name=score, table_ref=query0}], emp_id=[{name=emp_id, table_ref=query0}]}, table_alias={tgt=employees}}}",
				extractor.getSymbolTable().toString());
	}

	/** Walker coverage T2.2 — {@code insert_target_table_primary} qualified table, no target column list. */
	@Test
	public void insertTargetNoColumnListT2_2Test() {
		final String query = "INSERT INTO hr.employees SELECT score, emp_id FROM perf_feed";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={insert={preamble=insert_into, from={from={table={alias=null, table=perf_feed}}, select={1={column={name=score, table_ref=null}}, 2={column={name=emp_id, table_ref=null}}}}, target_table={table={schema=hr, alias=null, table=employees}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[score, emp_id]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{perf_feed={score=[[@6,32:36='score',<393>,1:32]], emp_id=[[@8,39:44='emp_id',<393>,1:39]]}, hr.employees={score=[[@6,32:36='score',<393>,1:32]], emp_id=[[@8,39:44='emp_id',<393>,1:39]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={score=[[@6,32:36='score',<393>,1:32]], emp_id=[[@8,39:44='emp_id',<393>,1:39]]}, insert1={score=[[@6,32:36='score',<393>,1:32]], emp_id=[[@8,39:44='emp_id',<393>,1:39]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_insert1={query_dictionary={score=[[@6,32:36='score',<393>,1:32]], emp_id=[[@8,39:44='emp_id',<393>,1:39]]}, table_dictionary={hr.employees={score=[[@6,32:36='score',<393>,1:32]], emp_id=[[@8,39:44='emp_id',<393>,1:39]]}}, def_query0={query_dictionary={score=[[@6,32:36='score',<393>,1:32]], emp_id=[[@8,39:44='emp_id',<393>,1:39]]}, table_dictionary={perf_feed={score=[[@6,32:36='score',<393>,1:32]], emp_id=[[@8,39:44='emp_id',<393>,1:39]]}}, interface={score=[{name=score, table_ref=perf_feed}], emp_id=[{name=emp_id, table_ref=perf_feed}]}}, interface={score=[{name=score, table_ref=query0}], emp_id=[{name=emp_id, table_ref=query0}]}}}",
				extractor.getSymbolTable().toString());
	}
}
