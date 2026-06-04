#!/usr/bin/env python3
"""Update insert orphan tests and add update/delete orphan parity tests."""
import re
from pathlib import Path

GOLDEN_FILE = Path('/tmp/insert_orphan_goldens.txt')
TEST_FILE = Path(__file__).resolve().parents[1] / 'parse/src/test/java/sql/walker/SqlEventWalkerDmlUpdateInsertDeleteTruncateTests.java'

ORPHAN_COLUMNS = {
    'V1': 'orphan_marker', 'V2': 'orphan_marker', 'V3': 'missing_flag', 'V4': 'missing_flag',
    'V5': 'orphan_marker', 'V6': 'shadow_col', 'V7': 'unqualified_note', 'V8': 'orphan_marker',
    'V9': 'orphan_marker', 'V10': 'orphan_marker', 'V11': 'orphan_marker', 'V12': 'orphan_marker',
}

INSERT_METHODS = {
    'V1': 'insertDictionaryHandlingQualifiedColumnsFromWindowedSubqueryAndOrphanRhsV1',
    'V2': 'insertDictionaryHandlingQualifiedColumnsAcrossWhereSubclausesAndOrphanRhsV2',
    'V3': 'insertDictionaryHandlingUnqualifiedFallsBackToTargetTableV3',
    'V4': 'insertDictionaryHandlingUnqualifiedWithAdditionalPhysicalTableStillResolvesV4',
    'V5': 'insertDictionaryHandlingGroupByHavingSubqueryAndUnqualifiedRhsV5',
    'V6': 'insertDictionaryHandlingOrderBySubqueryAndUnqualifiedRhsV6',
    'V7': 'insertDictionaryHandlingQualifySubqueryAndUnqualifiedRhsV7',
    'V8': 'insertDictionaryHandlingWhereInSubqueryWithOrphanRhsV8',
    'V9': 'insertDictionaryHandlingJoinOnInSubqueryWithOrphanRhsV9',
    'V10': 'insertDictionaryHandlingQualifyInSubqueryWithOrphanRhsV10',
    'V11': 'insertDictionaryHandlingOrderByInSubqueryWithOrphanRhsV11',
    'V12': 'insertDictionaryHandlingNoQualifiedSubqueryBodyWithQualifiedSelectAndOrphanRhsV12',
}

UPDATE_TEST = '''\t@Test
\tpublic void updateDictionaryHandlingQualifiedColumnsFromWindowedSubqueryAndOrphanRhsV1() {
\t\tfinal String query = " update employees e set score = src.acct_sales_count, rank_bucket = src.rn, orphan_sink = orphan_marker"
\t\t\t\t+ "\\n from (select a.emp_id, a.acct_sales_count,"
\t\t\t\t+ "\\n              row_number() over (partition by a.emp_id order by a.last_update desc) as rn"
\t\t\t\t+ "\\n         from accounts a"
\t\t\t\t+ "\\n        where a.acct_sales_count > 0) src"
\t\t\t\t+ "\\n where e.emp_id = src.emp_id";

\t\tfinal SQLSelectParserParser parser = parse(query);
\t\tSqlParseEventWalker extractor = runParsertest(query, parser);
\t\tassertNoWalkerDiagnostics(extractor);

\t\tAssert.assertEquals("AST is wrong", "{SQL={update={from={table={alias=src, query={select={1={column={name=emp_id, table_ref=a}}, 2={column={name=acct_sales_count, table_ref=a}}, 3={alias=rn, window_function={over={partition_by={1={column={name=emp_id, table_ref=a}}}, orderby={1={null_order=null, predicand={column={name=last_update, table_ref=a}}, sort_order=desc}}}, function={function_name=row_number, parameters=null}}}}, from={table={alias=a, table=accounts}}, where={condition={left={column={name=acct_sales_count, table_ref=a}}, right={literal=0}, operator=>}}}}}, where={condition={left={column={name=emp_id, table_ref=e}}, right={column={name=emp_id, table_ref=src}}, operator==}}, assignments={1={set={column={name=score, table_ref=null}}, to={column={name=acct_sales_count, table_ref=src}}}, 2={set={column={name=rank_bucket, table_ref=null}}, to={column={name=rn, table_ref=src}}}, 3={set={column={name=orphan_sink, table_ref=null}}, to={column={name=orphan_marker, table_ref=null}}}}, table={alias=e, table=employees}}}}",
\t\t\t\textractor.getAsTree().toString());
\t\tAssert.assertEquals("Interface is wrong", "[orphan_sink, score, rank_bucket]",
\t\t\t\textractor.getInterface().toString());
\t\tAssert.assertEquals("Substitution List is wrong", "{}",
\t\t\t\textractor.getSubstitutionsMap().toString());
\t\tAssert.assertEquals("Table Dictionary is wrong", "{accounts={acct_sales_count=[[@26,128:128='a',<381>,2:24], [@53,277:277='a',<381>,5:14]], last_update=[[@42,212:212='a',<381>,3:64]], emp_id=[[@22,118:118='a',<381>,2:14], [@37,194:194='a',<381>,3:46]]}, employees={orphan_sink=[[@16,76:86='orphan_sink',<381>,1:76]], score=[[@4,24:28='score',<381>,1:24]], rank_bucket=[[@10,54:64='rank_bucket',<381>,1:54]], orphan_marker=[[@18,90:102='orphan_marker',<381>,1:90]], emp_id=[[@61,312:312='e',<381>,6:7]]}}",
\t\t\t\textractor.getTableColumnDictionaryMap().toString());
\t\tAssert.assertEquals("Query Column Dictionary is wrong", "{query0={acct_sales_count=[[@28,130:145='acct_sales_count',<381>,2:26]], rn=[[@48,235:236='rn',<381>,3:87]], emp_id=[[@24,120:125='emp_id',<381>,2:16], [@65,323:325='src',<381>,6:18]]}, update1={orphan_sink=[[@16,76:86='orphan_sink',<381>,1:76]], score=[[@4,24:28='score',<381>,1:24]], rank_bucket=[[@10,54:64='rank_bucket',<381>,1:54]]}}",
\t\t\t\textractor.getQueryColumnDictionaryMap().toString());
\t\tAssert.assertEquals("Symbol Table is wrong", "{update1={assignments={orphan_sink=[{name=orphan_marker, table_ref=null}], score=[{name=acct_sales_count, table_ref=src}], rank_bucket=[{name=rn, table_ref=src}]}, table_dictionary={employees={orphan_sink=[[@16,76:86='orphan_sink',<381>,1:76]], score=[[@4,24:28='score',<381>,1:24]], orphan_marker=[[@18,90:102='orphan_marker',<381>,1:90]], rank_bucket=[[@10,54:64='rank_bucket',<381>,1:54]], emp_id=[[@61,312:312='e',<381>,6:7]]}}, unresolved_column={src.acct_sales_count={column={name=acct_sales_count, table_ref=src}, locations=[[@6,32:34='src',<381>,1:32]]}, src.rn={column={name=rn, table_ref=src}, locations=[[@12,68:70='src',<381>,1:68]]}}, update_dictionary={orphan_sink=[[@16,76:86='orphan_sink',<381>,1:76]], score=[[@4,24:28='score',<381>,1:24]], rank_bucket=[[@10,54:64='rank_bucket',<381>,1:54]]}, def_query0={query_dictionary={acct_sales_count=[[@28,130:145='acct_sales_count',<381>,2:26]], rn=[[@48,235:236='rn',<381>,3:87]], emp_id=[[@24,120:125='emp_id',<381>,2:16], [@65,323:325='src',<381>,6:18]]}, table_dictionary={accounts={acct_sales_count=[[@26,128:128='a',<381>,2:24], [@53,277:277='a',<381>,5:14]], last_update=[[@42,212:212='a',<381>,3:64]], emp_id=[[@22,118:118='a',<381>,2:14], [@37,194:194='a',<381>,3:46]]}}, filters=[{name=acct_sales_count, table_ref=a}], interface={acct_sales_count=[{name=acct_sales_count, table_ref=a}], rn=[{name=emp_id, table_ref=a}, {name=last_update, table_ref=a}], emp_id=[{name=emp_id, table_ref=a}]}, table_alias={a=accounts}}, filters=[{name=emp_id, table_ref=e}, {name=emp_id, table_ref=src}], table_alias={e=employees, src=query0}}}",
\t\t\t\textractor.getSymbolTable().toString());
\t}


'''

DELETE_TEST = '''\t@Test
\tpublic void deleteDictionaryHandlingPostgresUsingWindowedSubqueryAndOrphanRhsV1() {
\t\tfinal String query = " delete from employees e"
\t\t\t\t+ "\\n using (select a.emp_id, a.acct_sales_count,"
\t\t\t\t+ "\\n              row_number() over (partition by a.emp_id order by a.last_update desc) as rn"
\t\t\t\t+ "\\n         from accounts a"
\t\t\t\t+ "\\n        where a.acct_sales_count > 0) src"
\t\t\t\t+ "\\n where e.emp_id = src.emp_id and orphan_marker = 1";

\t\tfinal SQLSelectParserParser parser = parse(query);
\t\tSqlParseEventWalker extractor = runParsertest(query, parser);
\t\tassertNoWalkerDiagnostics(extractor);

\t\tAssert.assertEquals("AST is wrong", "{SQL={delete={table={alias=e, table=employees}, using={1={table={alias=src, query={select={1={column={name=emp_id, table_ref=a}}, 2={column={name=acct_sales_count, table_ref=a}}, 3={alias=rn, window_function={over={partition_by={1={column={name=emp_id, table_ref=a}}}, orderby={1={null_order=null, predicand={column={name=last_update, table_ref=a}}, sort_order=desc}}}, function={function_name=row_number, parameters=null}}}}, from={table={alias=a, table=accounts}}, where={condition={left={column={name=acct_sales_count, table_ref=a}}, right={literal=0}, operator=>}}}}}}, where={and={1={condition={left={column={name=emp_id, table_ref=e}}, right={column={name=emp_id, table_ref=src}}, operator==}}, 2={condition={left={column={name=orphan_marker, table_ref=null}}, right={literal=1}, operator==}}}}}}",
\t\t\t\textractor.getAsTree().toString());
\t\tAssert.assertEquals("Interface is wrong", "[]",
\t\t\t\textractor.getInterface().toString());
\t\tAssert.assertEquals("Substitution List is wrong", "{}",
\t\t\t\textractor.getSubstitutionsMap().toString());
\t\tAssert.assertEquals("Table Dictionary is wrong", "{accounts={acct_sales_count=[[@11,50:50='a',<381>,2:25], [@38,199:199='a',<381>,5:14]], last_update=[[@27,134:134='a',<381>,3:64]], emp_id=[[@7,40:40='a',<381>,2:15], [@22,116:116='a',<381>,3:46]]}, employees={orphan_marker=[[@54,260:272='orphan_marker',<381>,6:33]], emp_id=[[@46,234:234='e',<381>,6:7]]}}",
\t\t\t\textractor.getTableColumnDictionaryMap().toString());
\t\tAssert.assertEquals("Query Column Dictionary is wrong", "{query0={acct_sales_count=[[@13,52:67='acct_sales_count',<381>,2:27]], rn=[[@33,157:158='rn',<381>,3:87]], emp_id=[[@9,42:47='emp_id',<381>,2:17], [@50,245:247='src',<381>,6:18]]}}",
\t\t\t\textractor.getQueryColumnDictionaryMap().toString());
\t\tAssert.assertEquals("Symbol Table is wrong", "{delete1={query_dictionary={}, table_dictionary={employees={orphan_marker=[[@54,260:272='orphan_marker',<381>,6:33]], emp_id=[[@46,234:234='e',<381>,6:7]]}}, def_query0={query_dictionary={acct_sales_count=[[@13,52:67='acct_sales_count',<381>,2:27]], rn=[[@33,157:158='rn',<381>,3:87]], emp_id=[[@9,42:47='emp_id',<381>,2:17], [@50,245:247='src',<381>,6:18]]}, table_dictionary={accounts={acct_sales_count=[[@11,50:50='a',<381>,2:25], [@38,199:199='a',<381>,5:14]], last_update=[[@27,134:134='a',<381>,3:64]], emp_id=[[@7,40:40='a',<381>,2:15], [@22,116:116='a',<381>,3:46]]}}, filters=[{name=acct_sales_count, table_ref=a}], interface={acct_sales_count=[{name=acct_sales_count, table_ref=a}], rn=[{name=emp_id, table_ref=a}, {name=last_update, table_ref=a}], emp_id=[{name=emp_id, table_ref=a}]}, table_alias={a=accounts}}, filters=[{name=emp_id, table_ref=e}, {name=emp_id, table_ref=src}, {name=orphan_marker, table_ref=employees}], interface=null, table_alias={e=employees, src=query0}}}",
\t\t\t\textractor.getSymbolTable().toString());
\t}


'''

DIAGNOSTIC_BLOCK = '''\t\tSnippet snippet = extractor.getSnippet();
\t\tassertNoFatalErrors(extractor);
\t\tassertDiagnosticCountBySeverity(
\t\t\t\tsnippet,
\t\t\t\t"UNRESOLVED_UNQUALIFIED_COLUMNS",
\t\t\t\tParseDiagnostic.Severity.ERROR,
\t\t\t\tnull,
\t\t\t\t"{orphan}",
\t\t\t\t1);
'''


def load_goldens():
    content = GOLDEN_FILE.read_text()
    goldens = {}
    for block in content.split('===== V')[1:]:
        version = block.split(' =====', 1)[0]
        g = {}
        for line in block.splitlines():
            if line.startswith('TableDict='):
                g['table'] = line[len('TableDict='):]
            elif line.startswith('QueryDict='):
                g['query'] = line[len('QueryDict='):]
            elif line.startswith('SymbolTable='):
                g['symbol'] = line[len('SymbolTable='):]
        goldens[f'V{version}'] = g
    return goldens


def patch_insert_method(java: str, method: str, orphan: str, golden: dict, symbols_only: bool = False) -> str:
    if not symbols_only:
        pattern = rf'(public void {re.escape(method)}\(\) \{{.*?runParsertest\(query, parser\);\n)(.*?)(\n\t\tAssert\.assertEquals\("AST is wrong")'
        match = re.search(pattern, java, re.DOTALL)
        if not match:
            raise SystemExit(f'Could not locate method body start: {method}')
        if 'assertDiagnosticCountBySeverity' not in match.group(2):
            java = java[:match.start(2)] + DIAGNOSTIC_BLOCK.format(orphan=orphan) + java[match.end(2):]

    labels = [('Symbol Table is wrong', 'symbol'), ('Symbol Tree is wrong', 'symbol')] if symbols_only else [
        ('Table Dictionary is wrong', 'table'),
        ('Query Column Dictionary is wrong', 'query'),
        ('Symbol Table is wrong', 'symbol'),
        ('Symbol Tree is wrong', 'symbol'),
    ]
    patched = 0
    for label, key in labels:
        pattern = (
            rf'(public void {re.escape(method)}\(\) \{{.*?'
            rf'Assert\.assertEquals\("{re.escape(label)}", ")(?:.*?)(",\s*\n\s*extractor\.get(?:TableColumnDictionaryMap|QueryColumnDictionaryMap|SymbolTable)\(\)\.toString\(\)\);)'
        )

        def repl(m, val=golden[key]):
            return m.group(1) + val + m.group(2)

        java, count = re.subn(pattern, repl, java, count=1, flags=re.DOTALL)
        patched += count

    if patched == 0:
        raise SystemExit(f'Could not patch goldens for {method}')

    return java


def main():
    import sys
    symbols_only = '--symbols-only' in sys.argv
    goldens = load_goldens()
    java = TEST_FILE.read_text()

    if not symbols_only:
        if 'updateDictionaryHandlingQualifiedColumnsFromWindowedSubqueryAndOrphanRhsV1' not in java:
            java = java.replace(
                '\t@Test\n\tpublic void insertDictionaryHandlingQualifiedColumnsFromWindowedSubqueryAndOrphanRhsV1() {',
                UPDATE_TEST + '\t@Test\n\tpublic void insertDictionaryHandlingQualifiedColumnsFromWindowedSubqueryAndOrphanRhsV1() {',
            )

        if 'deleteDictionaryHandlingPostgresUsingWindowedSubqueryAndOrphanRhsV1' not in java:
            java = java.replace(
                '// DELETE TESTS\n\n\t@Test\n\tpublic void deleteDictionaryHandlingPostgresReturningWindowedSubqueryV1() {',
                '// DELETE TESTS\n\n' + DELETE_TEST + '\t@Test\n\tpublic void deleteDictionaryHandlingPostgresReturningWindowedSubqueryV1() {',
            )

    for version, method in INSERT_METHODS.items():
        java = patch_insert_method(
            java, method, ORPHAN_COLUMNS[version], goldens[version], symbols_only=symbols_only)

    TEST_FILE.write_text(java)
    print('Patched', TEST_FILE)


if __name__ == '__main__':
    main()
