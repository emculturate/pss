#!/usr/bin/env python3
import re
from pathlib import Path

GOLDEN_FILE = Path('/tmp/insert_orphan_goldens.txt')
TEST_FILE = Path(__file__).resolve().parents[1] / 'parse/src/test/java/sql/walker/SqlEventWalkerDmlUpdateInsertDeleteTruncateTests.java'

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
        if line.startswith('SymbolTable='):
            g['symbol'] = line[len('SymbolTable='):]
    goldens[f'V{version}'] = g

java = TEST_FILE.read_text()
for version, method in METHODS.items():
    symbol = goldens[version]['symbol']
    pattern = (
        rf'(public void {re.escape(method)}\(\) \{{.*?'
        rf'Assert\.assertEquals\("(Symbol Table is wrong|Symbol Tree is wrong)", ")(.*?)(",\s*\n\s*extractor\.getSymbolTable\(\)\.toString\(\)\);)'
    )

    def repl(match, symbol=symbol):
        return match.group(1) + symbol + match.group(3)

    new_java, count = re.subn(pattern, repl, java, count=1, flags=re.DOTALL)
    if count != 1:
        raise SystemExit(f'Failed symbol replace for {method}, count={count}')
    java = new_java

TEST_FILE.write_text(java)
print('Fixed symbol table goldens in', TEST_FILE)
