#!/usr/bin/env python3
"""
Refactoring script: Extract symbol-tree helper methods from SqlParseEventWalker
into a new SqlParseSymbolTreeHelper class.

Usage:
    python3 tools/extract_symbol_tree.py

Outputs:
    parse/src/main/java/sql/symboltree/SqlParseSymbolTreeHelper.java  (new class)
    parse/src/main/java/sql/walker/SqlParseEventWalker.java           (modified)
"""

import re
import os
import sys

# --------------------------------------------------------------------------- #
# Methods to MOVE from SqlParseEventWalker -> SqlParseSymbolTreeHelper
# --------------------------------------------------------------------------- #
MOVE_METHODS = {
    # column/table qualified-reference building
    "getQualifiedTableReference",        # also called from grammar handlers -> prefix symbolTree.

    # unresolved-column finalization
    "finalizeTopLevelUnresolvedColumns",
    "rehomeUpdateUnqualifiedUnknownsToSingleFromTable",
    "ensureTableDictionaryEntry",
    "normalizeUpdateColumnRefs",
    "splitUnresolvedEntriesByQualification",
    "emitUnqualifiedUnresolvedColumnsError",
    "emitQualifiedUnresolvedColumnsFatal",

    # CTE / WITH scope
    "mergeCteListIntoQueryScope",
    "isTupleWithSubstitution",
    "resolveCurrentWithListItemScope",
    "nextSyntheticWithQueryAliasIndex",
    "containsKeyRecursive",
    "ensureCteListSymbolMap",

    # INSERT scope management
    "isColumnReferenceListNode",
    "resolveInsertUnqualifiedOrphanSourceColumnsToTargetTable",
    "removeUnresolvedColumnEntry",
    "mergeInsertScopeTableDictionaryIntoGlobal",
    "publishInsertScopeQueryDictionary",
    "publishUpdateScopeQueryDictionary",
    "buildInsertScopeQueryDictionaryFromTableDictionary",
    "containsIgnoreCase",
    "mergeReferenceCollections",
    "hasAnyColumnsInTableDictionary",
    "populateImplicitInsertTargetColumnsFromSourceDictionary",
    "getInsertTargetTableReference",
    "consumeInsertSourceScopeKey",
    "finalizeInsertSourceAtPrimaryExit",
    "finalizeSetOperationAtExit",
    "publishSetOperationInterfaceAtExit",
    "clearInsertSourceColumnSequence",
    "isInsertSourceScopeReference",
    "normalizeInsertSourceDefinition",
    "buildInsertInterfaceFromSource",
    "mapInsertTargetInterfaceFromResolvedSource",
    "buildInsertScopeQueryDictionaryFromMappedInterface",
    "applyImplicitInsertTargetTableDictionaryFromMappedSource",
    "resolveInsertSourceColumnSequence",
    "buildSingleInsertInterfaceReference",
    "extractInsertColumnNames",
    "extractInsertColumnNameFromEntry",

    # UPDATE / DELETE helpers
    "getUpdateTargetTableReference",
    "getDeleteTargetTableReference",
    "initializeUpdateTargetTableSubtree",
    "getSingleUpdateFromTableReference",
    "getUpdateNode",
    "moveAssignmentLhsToLhsUnresolvedColumns",
    "extractAssignmentLhsColumnReference",
    "makeQualifiedColumnReferenceKey",
    "addUpdateAssignmentSymbolReference",
    "extractAssignmentLhsName",
    "resolveAssignmentLhsTokenString",

    # VALUES scope
    "resolveCurrentValuesColumns",
    "buildValuesOutputInterface",
    "finalizeValuesScopeSymbolTable",

    # FROM / query-spec helpers
    "normalizeFromClauseCteAliasMappings",
    "collectFromClauseCteAliasMappingsRecursive",
    "registerCteBackedSourceAliasMappings",
    "upsertVisibleCteAliasMapping",
    "upsertCurrentTableAliasMapping",
    "mergeUnresolvedEntriesIntoCurrentScope",
    "shouldDeferSubqueryUnresolvedDiagnosticsToStatementBoundary",
    "projectSelectIntoTargetFromInterface",
    "partitionParentResolvableQualifiedUnknownsAndEmit",
    "emitQualifiedSourceNotFoundFatals",

    # CTE resolution helpers
    "hasCteListSymbolMap",
    "resolveCteScopeReference",
    "emitShadowedParentCteNameWarningIfNeeded",
    "pushSymbolTableWithParentCteList",
    "getAncestorSymbolTables",
    "getParentSymbolTable",
    "getCteListSymbolMap",
    "getTableAliasMap",
    "resolveCteScopeReferenceInSymbols",
    "getTopLevelQueryTableAliasMap",
    "emitQualifiedQueryAliasUnresolvedColumnsFatalAndPrune",
    "canResolveUnqualifiedFromSingleWildcardQuerySource",
    "hasWildcardInQueryOutputInterface",

    # select-item interface building
    "isQueryBackedSelectItemReference",
    "recordInsertSourceSelectItemSequence",
    "addAliasTokensObject",
    "emitDuplicateInterfaceColumnFatal",
    "buildInterfaceReferenceLabel",
    "addCurrentQueryScalarSubqueryAlias",
    "flattenSubTreeForInterfaceQueryReferences",
    "annotateSubqueryReference",
    "resolveQueryReferenceFromSubTree",
    "flattenSubTreeForInterfaceColumns",

    # core symbol-table resolution
    "convertSymbolTableToTableDictionary",
    "pruneUpdateTargetFromInputTableCollection",
    "resolveUpdateLhsColumnsToTargetTable",
    "mergeSelectListQualifiedQueryAliasRefsIntoSourceQueryDictionary",
    "aliasMapsToQuerySource",
    "resolveUpdateQualifiedUnresolvedColumnsToInputTables",
    "resolveUpdateUnqualifiedUnresolvedColumnsToTargetTableWhenNoInputSources",
    "resolveUpdateRhsUnqualifiedAssignmentColumnsToTargetTable",
    "resolveRemainingQualifiedUnresolvedColumnsToTargetTable",
    "mergeUpdateTargetAndLhsIntoTableDictionary",
    "extractLhsColumnName",
    "propagateUnqualifiedSelectStarToScopeTables",
    "retainOnlyLocallyResolvableExplicitQualifiedUnknowns",
    "resolveExplicitTableRefForUnknownEntry",
    "materializeResolvedUnqualifiedReference",
    "consumeUnqualifiedUnknownEntry",
    "consumeQualifiedUnknownEntry",
    "resolveUnqualifiedReferenceLocation",
    "getUnqualifiedUnknownEntry",
    "cloneReferenceWithResolvedTableRef",
    "assignTableRefsForColumnReferenceList",
    "resolvePreferredDeleteTargetForUnqualified",
    "collectUnqualifiedSourceReferences",
    "addIgnoringCase",
    "resolveAliasToQuerySourceFromAliasMap",
    "hasOnlyQueryBackedAliasSources",
    "emitUnqualifiedNotFoundInQueryAliasFatal",
    "buildUnqualifiedSuppressionKey",
    "shouldSuppressAmbiguousUnqualifiedDiagnostic",
    "hasUnqualifiedUnknownWithMultipleViableSources",
    "collectVisibleQuerySourceCollection",
    "isWildcardBackedQueryCandidate",
    "querySourceCanProvideColumn",
    "querySourceHasExactColumn",
    "isQuerySourceReference",
    "isValuesSourceReference",
    "isTableFunctionSourceReference",
    "hasColumnInQueryOutputInterface",
    "containsKeyIgnoreCase",
    "extractExplicitQualifiedUnknownEntries",
    "collectExplicitQualifiedUnknownKeysFromRefList",
    "emitExplicitQualifiedUnknownDiagnostics",
    "mergeExplicitQualifiedUnknownIntoSourceQueryDictionary",
    "promoteQualifiedWildcardIntoQuerySource",
    "getQueryDefinitionSymbol",
    "findInCurrentOrAncestorSymbolTables",
    "getCurrentTableAliasMap",
    "resolveCteOrQueryScopeReference",
    "resolveCteOrQueryScopeReferenceInVisibleScopes",
    "resolveCteOrExistingQueryScopeInVisibleScopes",
    "resolveExistingQueryScopeFromAliasMap",
    "registerTableFunctionSourceReference",
    "allocateTableFunctionSourceReference",
    "findTopLevelValuesScopeKey",
    "wrapValuesScopeAsDefinition",
    "addCurrentScopeValuesAliasMapping",
    "sanitizeQueryDictionaryForGlobalExport",
    "collectQuerySymbolTable",       # two overloads – both move
    "pruneInsertSourceSequenceFromNestedDefinitions",
    "pruneInsertSourceSequenceRecursive",
    "populateInsertTargetColumnsFromTargetSubtree",
    "captureClauseDependencies",
    "flattenSubTreeForClauseColumns",
    "getSubqueryReferenceKey",
}

# --------------------------------------------------------------------------- #
# Fields to move (replaced by symbolTree.getXxx() / symbolTree.setXxx() in walker)
# --------------------------------------------------------------------------- #
MOVE_FIELDS = [
    ("private int tableFunctionSourceCount = 0;", "tableFunctionSourceCount"),
    ("private final Set<String> suppressedAmbiguousUnqualifiedKeys;", "suppressedAmbiguousUnqualifiedKeys"),
    ("private final Set<String> tableFunctionSourceRefs;", "tableFunctionSourceRefs"),
]

MOVE_FIELD_NAMES = {f[1] for f in MOVE_FIELDS}

# --------------------------------------------------------------------------- #
# Helpers
# --------------------------------------------------------------------------- #

def is_in_string_or_comment(line, pos):
    """Very rough heuristic: check if we're inside a comment at or after `pos`."""
    stripped = line[:pos]
    if '//' in stripped:
        return True
    return False


def find_method_blocks(lines):
    """
    Parse the class body and return a list of (start_line_idx, end_line_idx, method_name)
    for every method whose simple name is in MOVE_METHODS.

    The returned start_line_idx includes any immediately-preceding @SuppressWarnings
    or @Override annotation lines.
    """
    # Pattern to detect a method signature line (inside the class, depth==1)
    # We look for lines that have the method name followed by '('
    method_sig_re = re.compile(
        r'(?:private|protected|public)\s+(?:static\s+)?(?:final\s+)?'
        r'(?:<[^>]+>\s+)?'
        r'(?:[\w$]+(?:\s*<[^>]*>)?(?:\[\])*\s+)?'   # return type (optional for constructors)
        r'([\w$]+)\s*\('                               # method name
    )

    results = []          # list of (start_idx, end_idx, method_name)
    depth = 0
    in_block_comment = False
    in_string = False

    # First pass: find class body start (depth becomes 1 after opening '{')
    class_start = 0
    for i, line in enumerate(lines):
        # Look for top-level class declaration
        if re.search(r'^public\s+class\s+', line):
            class_start = i
            break

    i = 0
    while i < len(lines):
        line = lines[i]

        # Track block comments
        if in_block_comment:
            if '*/' in line:
                in_block_comment = False
            i += 1
            continue

        # Detect start of block comment
        if '/*' in line and not '//' in line.split('/*')[0]:
            if '*/' not in line:
                in_block_comment = True
                i += 1
                continue

        # Count braces to track depth
        # (simplified: doesn't handle strings with braces, but good enough for Java class)
        in_char = False
        in_str = False
        escape_next = False
        for ch in line:
            if escape_next:
                escape_next = False
                continue
            if ch == '\\' and (in_str or in_char):
                escape_next = True
                continue
            if ch == '"' and not in_char:
                in_str = not in_str
            elif ch == '\'' and not in_str:
                in_char = not in_char
            elif not in_str and not in_char:
                if ch == '{':
                    depth += 1
                elif ch == '}':
                    depth -= 1

        # At depth==1 (inside the class, outside methods), look for method starts
        # After counting braces for this line, if depth transitioned from 1 to >1,
        # this line contains the opening brace of a method/block at depth 1.
        # We need to look at lines where the signature appears and then '{' opens them.
        # Strategy: look for method signatures at depth 1 (before the '{' increments depth).

        i += 1
        continue

    # Better approach: re-scan with look-ahead for method start
    results = _extract_method_blocks(lines)
    return results


def _extract_method_blocks(lines):
    """
    Walk lines tracking depth. When we're at depth==1 (inside class, not yet
    inside a method body), look for a method signature line.  When we find one
    whose name is in MOVE_METHODS, scan forward to collect the full method body.
    Return list of (annotation_start_idx, body_end_idx, method_name).
    """
    results = []
    depth = 0
    in_block_comment = False
    n = len(lines)

    # depth tracks curly-brace nesting
    i = 0
    while i < n:
        line = lines[i]
        raw = line

        # ---- block-comment handling ----
        if in_block_comment:
            if '*/' in line:
                in_block_comment = False
            depth_delta = _count_braces(line, skip=True)
            depth += depth_delta
            i += 1
            continue

        if '/*' in line:
            before_comment = line[:line.index('/*')]
            delta_before = _count_braces(before_comment, skip=False)
            if '*/' in line[line.index('/*'):]:
                # inline block comment: just count braces before it
                after_comment_start = line.index('*/') + 2
                delta_after = _count_braces(line[after_comment_start:], skip=False)
                depth += delta_before + delta_after
                i += 1
                continue
            else:
                in_block_comment = True
                depth += delta_before
                i += 1
                continue

        # Skip line comments for brace counting
        if '//' in line:
            code_part = line[:line.index('//')]
        else:
            code_part = line

        depth_before = depth
        depth += _count_braces(code_part, skip=False)

        # At depth 1 (class body level): look for method signatures
        if depth_before == 1 and depth >= 2:
            # This line opened a method/block. Check if it's a method we want.
            method_name = _extract_method_name(raw)
            if method_name and method_name in MOVE_METHODS:
                # Find where the preceding annotation starts (look back)
                annot_start = i
                j = i - 1
                while j >= 0:
                    prev = lines[j].strip()
                    if prev.startswith('@SuppressWarnings') or prev.startswith('@Override') or prev == '':
                        if prev != '':
                            annot_start = j
                        j -= 1
                    else:
                        break
                # But don't grab blank lines too far back – keep contiguous annotations
                annot_start = _find_annotation_start(lines, i)

                # Now scan forward to find the closing '}' that returns depth to 1
                body_depth = depth
                j = i + 1
                while j < n and body_depth > 1:
                    jline = lines[j]
                    if '//' in jline:
                        jcode = jline[:jline.index('//')]
                    else:
                        jcode = jline
                    # handle block comments inside method
                    body_depth += _count_braces(jcode, skip=False)
                    j += 1
                results.append((annot_start, j - 1, method_name))

        elif depth_before == 1 and depth == 1:
            # depth stays at 1 – could be an interface method, field, annotation line.
            # Check if this line starts a method signature that opens on the NEXT line.
            # Actually in Java methods always have '{' – let's check multi-line signatures.
            # If the line has a method name but no '{', the next line might have the brace.
            stripped = raw.strip()
            # Skip annotations, comments, fields
            if stripped.startswith('@') or stripped.startswith('//') or stripped.startswith('/*') or stripped.startswith('*'):
                i += 1
                continue
            method_name = _extract_method_name_no_brace(raw)
            if method_name and method_name in MOVE_METHODS:
                # Scan forward for the opening '{'
                j = i
                collected = [raw]
                while j < n:
                    if '{' in lines[j] and not lines[j].strip().startswith('//'):
                        # opening brace found in line j
                        code = lines[j] if '//' not in lines[j] else lines[j][:lines[j].index('//')]
                        sig_depth = depth_before + _count_braces(code, skip=False)
                        # Find annotation start
                        annot_start = _find_annotation_start(lines, i)
                        # Find body end
                        body_depth = sig_depth
                        k = j + 1
                        while k < n and body_depth > 1:
                            kline = lines[k]
                            if '//' in kline:
                                kcode = kline[:kline.index('//')]
                            else:
                                kcode = kline
                            body_depth += _count_braces(kcode, skip=False)
                            k += 1
                        results.append((annot_start, k - 1, method_name))
                        # advance i to after the method end
                        i = k
                        break
                    j += 1
                else:
                    i += 1
                continue

        i += 1

    return results


def _find_annotation_start(lines, method_line_idx):
    """
    Given the line index of the method opening (where '{' is), walk backwards
    to include @SuppressWarnings, @Override, Javadoc, and blank separator lines.
    Returns the index of the first annotation/comment line to include.
    """
    j = method_line_idx - 1
    start = method_line_idx
    # Also include the line(s) with return type if the signature is multi-line
    # Walk back through blank lines and annotation lines
    found_annot = False
    while j >= 0:
        prev = lines[j].strip()
        if prev == '':
            j -= 1
            continue
        if (prev.startswith('@SuppressWarnings') or
                prev.startswith('@Override') or
                prev.startswith('@NotNull') or
                prev.startswith('/**') or
                prev.startswith('/*') or
                prev.startswith('*') or
                prev.startswith('//')):
            start = j
            found_annot = True
            j -= 1
        else:
            # Check if it looks like a continuation of the method signature
            # (e.g. return type on previous line, or parameters continued)
            if found_annot:
                break
            # If the previous non-blank line ends with a comma or '(' it's a multi-line sig
            if prev.endswith(',') or prev.endswith('('):
                start = j
                j -= 1
            else:
                break

    # Also extend start back to include Javadoc if the first line was an annotation
    # and there's a /** block before it
    j2 = start - 1
    while j2 >= 0:
        p = lines[j2].strip()
        if p == '':
            j2 -= 1
            continue
        if p.startswith('/**') or p.startswith('/*') or p.startswith('*'):
            start = j2
            j2 -= 1
        else:
            break

    return start


def _count_braces(code, skip):
    """Count net brace delta in a code string. If skip=True, return 0 (inside block comment)."""
    if skip:
        return 0
    delta = 0
    in_str = False
    in_char = False
    escape = False
    for ch in code:
        if escape:
            escape = False
            continue
        if ch == '\\' and (in_str or in_char):
            escape = True
            continue
        if ch == '"' and not in_char:
            in_str = not in_str
        elif ch == '\'' and not in_str:
            in_char = not in_char
        elif not in_str and not in_char:
            if ch == '{':
                delta += 1
            elif ch == '}':
                delta -= 1
    return delta


def _extract_method_name(line):
    """
    Extract method name from a line that contains the opening '{' of a method body.
    Returns method name string or None.
    """
    # Patterns like:
    #   private void foo(... {
    #   private String bar(... {
    #   public HashMap<...> baz(... {
    sig_re = re.compile(
        r'(?:private|protected|public)\s+'
        r'(?:static\s+)?(?:final\s+)?(?:synchronized\s+)?'
        r'(?:<[^(]+>\s+)?'                    # optional type params
        r'[\w$]+(?:\s*<[^(>]+>)?(?:\[\])?\s+'  # return type
        r'([\w$]+)\s*\('                        # METHOD NAME
    )
    m = sig_re.search(line)
    if m:
        return m.group(1)
    return None


def _extract_method_name_no_brace(line):
    """Same as _extract_method_name but the line doesn't have '{' yet."""
    return _extract_method_name(line)


# --------------------------------------------------------------------------- #
# Main extraction
# --------------------------------------------------------------------------- #

def main():
    src_dir = os.path.join(os.path.dirname(__file__), '..', 'parse', 'src', 'main', 'java')
    walker_path = os.path.join(src_dir, 'sql', 'walker', 'SqlParseEventWalker.java')
    helper_dir  = os.path.join(src_dir, 'sql', 'symboltree')
    helper_path = os.path.join(helper_dir, 'SqlParseSymbolTreeHelper.java')

    os.makedirs(helper_dir, exist_ok=True)

    print(f"Reading {walker_path} ...")
    with open(walker_path, 'r', encoding='utf-8') as f:
        original_lines = f.readlines()

    print(f"  Total lines: {len(original_lines)}")

    # ------------------------------------------------------------------ #
    # Step 1 – find all method blocks to move
    # ------------------------------------------------------------------ #
    print("Extracting method blocks ...")
    blocks = _extract_method_blocks(original_lines)

    # Collect only methods in MOVE_METHODS
    move_blocks = [(s, e, name) for (s, e, name) in blocks if name in MOVE_METHODS]

    # Deduplicate (two overloads of collectQuerySymbolTable will both be captured)
    # Sort by start line
    move_blocks.sort(key=lambda x: x[0])
    print(f"  Found {len(move_blocks)} method blocks to move:")
    for s, e, name in move_blocks:
        print(f"    line {s+1}-{e+1}: {name}")

    # ------------------------------------------------------------------ #
    # Step 2 – build set of line indices to REMOVE from the event walker
    # ------------------------------------------------------------------ #
    lines_to_remove = set()
    for s, e, name in move_blocks:
        for idx in range(s, e + 1):
            lines_to_remove.add(idx)

    # Also remove the 3 moved field declarations and their constructor init lines
    field_patterns = [
        re.compile(r'private int tableFunctionSourceCount\s*='),
        re.compile(r'private final Set<String> suppressedAmbiguousUnqualifiedKeys\s*;'),
        re.compile(r'private final Set<String> tableFunctionSourceRefs\s*;'),
        re.compile(r'this\.suppressedAmbiguousUnqualifiedKeys\s*='),
        re.compile(r'this\.tableFunctionSourceRefs\s*='),
    ]
    for idx, line in enumerate(original_lines):
        for pat in field_patterns:
            if pat.search(line):
                lines_to_remove.add(idx)
                break

    # ------------------------------------------------------------------ #
    # Step 3 – collect extracted method text
    # ------------------------------------------------------------------ #
    extracted_methods = []
    for s, e, name in move_blocks:
        method_lines = original_lines[s:e+1]
        extracted_methods.append((name, method_lines))

    # ------------------------------------------------------------------ #
    # Step 4 – generate SqlParseSymbolTreeHelper.java
    # ------------------------------------------------------------------ #
    print(f"\nGenerating {helper_path} ...")
    _write_helper_class(helper_path, extracted_methods)

    # ------------------------------------------------------------------ #
    # Step 5 – build modified event walker
    # ------------------------------------------------------------------ #
    print(f"\nBuilding modified event walker ...")
    remaining_lines = [line for idx, line in enumerate(original_lines)
                       if idx not in lines_to_remove]

    # Add symbolTree field declaration and import after class declaration
    remaining_lines = _inject_symbol_tree_field(remaining_lines)

    # Replace direct calls to moved methods with symbolTree.xxx() calls
    remaining_lines = _replace_method_calls(remaining_lines, MOVE_METHODS)

    # Replace direct references to moved fields with symbolTree accessors
    remaining_lines = _replace_field_refs(remaining_lines)

    # ------------------------------------------------------------------ #
    # Step 6 – write modified event walker
    # ------------------------------------------------------------------ #
    print(f"Writing modified {walker_path} ...")
    with open(walker_path, 'w', encoding='utf-8') as f:
        f.writelines(remaining_lines)

    print("\nDone.  Please compile and fix any remaining issues.")
    print(f"  New class:         {helper_path}")
    print(f"  Modified walker:   {walker_path}")


# --------------------------------------------------------------------------- #
# Helper writers
# --------------------------------------------------------------------------- #

HELPER_HEADER = '''\
package sql.symboltree;

import java.util.ArrayList;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

import static mumble.MumbleConstants.*;
import static mumble.ASTWalkerHelperConstants.*;
import static mumble.SQLParserEndPoints.*;

import astwalkers.SqlASTWalkerHelper;
import errorhandling.ParseDiagnostic;
import sql.SQLSelectParserParser;

@SuppressWarnings("Convert2Diamond")
public class SqlParseSymbolTreeHelper {

\tprivate final SqlASTWalkerHelper walker;

\t// Fields moved from SqlParseEventWalker
\tprivate int tableFunctionSourceCount = 0;
\tprivate final Set<String> suppressedAmbiguousUnqualifiedKeys = new HashSet<String>();
\tprivate final Set<String> tableFunctionSourceRefs = new HashSet<String>();

\tpublic SqlParseSymbolTreeHelper(SqlASTWalkerHelper walkerHelper) {
\t\tthis.walker = walkerHelper;
\t}

\t// --- Getters/setters for moved fields ---

\tpublic int getTableFunctionSourceCount() { return tableFunctionSourceCount; }
\tpublic void setTableFunctionSourceCount(int count) { this.tableFunctionSourceCount = count; }
\tpublic Set<String> getSuppressedAmbiguousUnqualifiedKeys() { return suppressedAmbiguousUnqualifiedKeys; }
\tpublic Set<String> getTableFunctionSourceRefs() { return tableFunctionSourceRefs; }

\t// --- normalizeTableRef delegate (mirrors event-walker static helper) ---

\tprivate static String normalizeTableRef(String tableRef) {
\t\treturn SqlASTWalkerHelper.normalizeTableReference(tableRef);
\t}

\t// =========================================================================
\t// Methods moved from SqlParseEventWalker
\t// =========================================================================

'''

HELPER_FOOTER = '}\n'


def _write_helper_class(path, extracted_methods):
    with open(path, 'w', encoding='utf-8') as f:
        f.write(HELPER_HEADER)
        for name, method_lines in extracted_methods:
            # Write the method lines; replace any `private ` visibility with `\tpublic `
            # (keep them accessible to the event walker via the symbolTree reference)
            # Actually we keep original visibility except for `getQualifiedTableReference`
            # which must be public so the grammar handlers can call symbolTree.xxx()
            for line in method_lines:
                f.write(line)
            f.write('\n')
        f.write(HELPER_FOOTER)


def _inject_symbol_tree_field(lines):
    """
    Insert:
      import sql.symboltree.SqlParseSymbolTreeHelper;
    after the existing sql.walker imports block, and:
      private final SqlParseSymbolTreeHelper symbolTree;
    after the `private final SqlASTWalkerHelper walker;` field declaration.
    Also initialise it in the constructor.
    """
    result = list(lines)

    # Find the package/import section end to add import
    import_re = re.compile(r'^import\s+')
    last_import_idx = -1
    for idx, line in enumerate(result):
        if import_re.match(line):
            last_import_idx = idx
    if last_import_idx >= 0:
        result.insert(last_import_idx + 1,
                      'import sql.symboltree.SqlParseSymbolTreeHelper;\n')

    # Insert field declaration after `private final SqlASTWalkerHelper walker;`
    walker_field_re = re.compile(r'private final SqlASTWalkerHelper walker\s*;')
    for idx, line in enumerate(result):
        if walker_field_re.search(line):
            result.insert(idx + 1,
                          '\tprivate final SqlParseSymbolTreeHelper symbolTree;\n')
            break

    # Insert initialisation in constructor after `this.walker = walkerHelper;`
    walker_init_re = re.compile(r'this\.walker\s*=\s*\w+\s*;')
    for idx, line in enumerate(result):
        if walker_init_re.search(line):
            result.insert(idx + 1,
                          '\t\tthis.symbolTree = new SqlParseSymbolTreeHelper(walker);\n')
            break

    return result


def _replace_method_calls(lines, move_set):
    """
    In lines that remain in the event walker, replace bare `methodName(` with
    `symbolTree.methodName(` when `methodName` is in move_set.
    Skips lines that:
      - already have `symbolTree.methodName`
      - already have a dot before methodName (e.g. `walker.methodName`)
      - are comments
      - are declarations (contain `private|public|protected` keyword before the name)
    """
    # Build one big alternation pattern
    alt = '|'.join(re.escape(m) for m in sorted(move_set, key=len, reverse=True))
    call_re = re.compile(r'(?<![.\w])(' + alt + r')\s*\(')

    result = []
    for line in lines:
        stripped = line.lstrip()
        # Skip comment lines
        if stripped.startswith('//') or stripped.startswith('*') or stripped.startswith('/*'):
            result.append(line)
            continue
        # Skip declaration lines
        if re.match(r'\s*(?:private|public|protected)\s+', line):
            result.append(line)
            continue
        # Skip if it's already prefixed
        # Apply substitution, but only where the match is NOT immediately preceded by '.'
        new_line = call_re.sub(r'symbolTree.\1(', line)
        result.append(new_line)
    return result


def _replace_field_refs(lines):
    """
    Replace direct references to moved fields with symbolTree getter/setter calls.
    This handles simple assignment patterns only; complex usages are left for
    manual fixup.
    """
    replacements = [
        # tableFunctionSourceCount used only in moved methods, but be safe
        (re.compile(r'(?<![.\w])tableFunctionSourceCount\b'), 'symbolTree.getTableFunctionSourceCount()'),
        # suppressedAmbiguousUnqualifiedKeys - used only in moved methods
        # tableFunctionSourceRefs - used only in moved methods
    ]
    result = []
    for line in lines:
        stripped = line.lstrip()
        if stripped.startswith('//') or stripped.startswith('*') or stripped.startswith('/*'):
            result.append(line)
            continue
        if re.match(r'\s*(?:private|public|protected)\s+', line):
            result.append(line)
            continue
        for pat, repl in replacements:
            line = pat.sub(repl, line)
        result.append(line)
    return result


if __name__ == '__main__':
    main()
