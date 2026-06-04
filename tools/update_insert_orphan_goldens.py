#!/usr/bin/env python3
import re
from pathlib import Path

GOLDEN_FILE = Path('/tmp/insert_orphan_goldens.txt')
TEST_FILE = Path(__file__).resolve().parents[1] / 'parse/src/test/java/sql/walker/SqlEventWalkerDmlUpdateInsertDeleteTruncateTests.java'

ORPHAN_COLUMNS = {
    'V1': 'orphan_marker',
    'V2': 'orphan_marker',
    'V3': 'missing_flag',
    'V4': 'missing_flag',
    'V5': 'orphan_marker',
    'V6': 'shadow_col',
    'V7': 'unqualified_note',
    'V8': 'orphan_marker',
    'V9': 'orphan_marker',
    'V10': 'orphan_marker',
    'V11': 'orphan_marker',
    'V12': 'orphan_marker',
}

METHODS = {
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

java = TEST_FILE.read_text()

diagnostic_block = '''\t\tSnippet snippet = extractor.getSnippet();
\t\tassertNoFatalErrors(extractor);
\t\tassertDiagnosticCountBySeverity(
\t\t\t\tsnippet,
\t\t\t\t"UNRESOLVED_UNQUALIFIED_COLUMNS",
\t\t\t\tParseDiagnostic.Severity.ERROR,
\t\t\t\tnull,
\t\t\t\t"{orphan}",
\t\t\t\t1);
'''

for version, method in METHODS.items():
    g = goldens[version]
    orphan = ORPHAN_COLUMNS[version]

    pattern = rf'(public void {re.escape(method)}\(\) \{{.*?SqlParseEventWalker extractor = runParsertest\(query, parser\);\n)(.*?)(\n\t\tAssert\.assertEquals\("AST is wrong")'
    match = re.search(pattern, java, re.DOTALL)
    if not match:
        raise SystemExit(f'Could not find method {method}')

    replacement = match.group(1) + diagnostic_block.format(orphan=orphan) + match.group(3)
    java = java[:match.start()] + replacement + java[match.end():]

    java = re.sub(
        rf'(public void {re.escape(method)}\(\) \{{.*?Assert\.assertEquals\("Table Dictionary is wrong", ")[^"]*(")',
        rf'\1{g["table"]}\2',
        java,
        count=1,
        flags=re.DOTALL,
    )
    java = re.sub(
        rf'(public void {re.escape(method)}\(\) \{{.*?Assert\.assertEquals\("Query Column Dictionary is wrong", ")[^"]*(")',
        rf'\1{g["query"]}\2',
        java,
        count=1,
        flags=re.DOTALL,
    )
    symbol_key = 'Symbol Table is wrong'
    if version == 'V3' or version == 'V4':
        # V3 uses Symbol Tree in one place historically - check
        pass
    java = re.sub(
        rf'(public void {re.escape(method)}\(\) \{{.*?Assert\.assertEquals\("(Symbol Table is wrong|Symbol Tree is wrong)", ")[^"]*(")',
        rf'\1{g["symbol"]}\2',
        java,
        count=1,
        flags=re.DOTALL,
    )

TEST_FILE.write_text(java)
print('Updated', TEST_FILE)
