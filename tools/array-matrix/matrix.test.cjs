const { test } = require('node:test');
const assert = require('node:assert/strict');
const fs = require('node:fs');
const os = require('node:os');
const path = require('node:path');
const { spawnSync } = require('node:child_process');
const { signatureCases, functionsFor, ledgerFor } = require('./templates.cjs');
const cli = path.join(__dirname, 'matrix.cjs');
const signature = { name: 'array_slice', args: [
  { column: 'tags', literal: "['x', 'y']" },
  { column: 'start_idx', literal: '0' },
  { column: 'end_idx', literal: '2' }
] };

test('templates isolate each eligible argument and skip only explicitly covered cells', () => {
  const all = signatureCases(signature);
  for (let i = 1; i <= 3; i++) {
    const c = all.find(c => c.suffix === `Arg${i}Ambiguous`);
    const operands = c.query.match(/array_slice\(([^)]+)\)/)[1].split(', ');
    assert.equal(operands.filter(a => a.startsWith('a.')).length, 2);
    assert.equal(operands[i - 1], signature.args[i - 1].column);
  }
  const missing = signatureCases({ ...signature, covered: ['placement:Select', 'arg2:Ambiguous'] });
  assert.equal(missing.length, all.length - 2);
  assert.ok(missing.some(c => c.suffix === 'Arg1Ambiguous'));
  assert.ok(missing.some(c => c.suffix === 'Arg3Ambiguous'));
  assert.equal(all.find(c => c.suffix === 'ScriptPlacement').routes, 2);
  assert.deepEqual(all.find(c => c.suffix === 'QualifiedUdfNeutral').codes, []);
});

test('paired catalogs retain separate ownership and accounting rejects missing evidence', () => {
  const f = { function: 'ARRAY_SLICE', prefix: 'newSlice', legacyPrefix: 'oldSlice', task: 'T008',
    plan: 'D1.2.2', legacyCount: 0, signature };
  const pair = functionsFor({ functions: [f, {
    function: 'ARRAY_SIZE', prefix: 'newSize', legacyPrefix: 'oldSize', task: 'T011',
    legacyCount: 0, signature: { name: 'array_size', args: [{ column: 'tags', literal: '[]' }] }
  }] });
  assert.equal(pair.length, 2);
  assert.equal(pair[0].cases.length, signatureCases(signature).length);
  const third = { function: 'ARRAY_COMPACT', prefix: 'newCompact', legacyPrefix: 'oldCompact',
    task: 'T010', legacyCount: 0,
    signature: { name: 'array_compact', args: [{ column: 'tags', literal: '[]' }] } };
  const triple = functionsFor({ functions: [...pair, third].map(({ cases, ...f }) => f) });
  assert.equal(triple.length, 3);
  assert.equal(triple[2].cases.length, signatureCases(third.signature).length);
  assert.throws(() => functionsFor({ functions: [...triple, third] }), /up to three/);
  assert.throws(() => functionsFor({ functions: [f, f] }), /Duplicate/);
  assert.throws(() => ledgerFor(f, [{ name: 'unmapped' }], { methods: 1 }), /assign evidence/);
  assert.throws(() => ledgerFor(f, [], { methods: 0 }), /missing cell/);
  const cases = pair[0].cases.map(c => ({ ...c, name: f.prefix + c.suffix + 'V0Test' }));
  const ledger = ledgerFor(f, cases, { methods: cases.length, six_goldens: cases.length - 3, syntax: 3 });
  for (const c of cases) assert.ok(ledger.includes('`' + c.name + '`'));
});

test('paired emission and accounting preserve isolation and reject unreviewed, stale or failed results', t => {
  const root = fs.mkdtempSync(path.join(os.tmpdir(), 'pss-matrix-test-'));
  t.after(() => fs.rmSync(root, { recursive: true, force: true }));
  const batch = path.join(root, 'batch');
  fs.mkdirSync(batch);
  const host = path.join(root, 'parse/src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java');
  fs.mkdirSync(path.dirname(host), { recursive: true });
  const method = name => '\t@Test\n\tpublic void ' + name + '() {\n'
    + '\t\tfinal String query = "SELECT 1";\n\t\tfinal SqlParseEventWalker extractor = runQuery(query);\n'
    + '\t\tassertOutputs(extractor, "ast", "interface", "subs", "table", "query", "symbols");\n\t}';
  const old = 'class Fixture {\n' + ['oldA', 'oldB', 'unrelated'].map(method).join('\n') + '\n}\n';
  fs.writeFileSync(host, old);
  fs.writeFileSync(path.join(batch, 'before.java'), old);
  fs.writeFileSync(path.join(batch, 'batch.cjs'), 'module.exports = ' + JSON.stringify({
    test_selection: 'SqlEventWalkerSnowflakeArrayTransformTests,RelatedCompositionTests',
    next_task: 'T003 / P3', functions: [
    { function: 'A', prefix: 'newA', legacyPrefix: 'oldA', legacyCount: 1, task: 'T001', plan: 'P1', production_changes: false,
      rule: 'array_transform_function', cases: [{ suffix: 'Case', query: 'SELECT 1', cells: ['new'], codes: [] }] },
    { function: 'B', prefix: 'newB', legacyPrefix: 'oldB', legacyCount: 1, task: 'T002', plan: 'P2', production_changes: false,
      rule: 'array_unary_transform_function', cases: [{ suffix: 'Case', query: 'SELECT 1', cells: ['new'], codes: [] }] }
  ] }));
  const cases = ['oldA', 'oldB', 'newACaseV0Test', 'newBCaseV0Test'].map((name, i) => ({
    name, legacy: i < 2, query: 'SELECT 1', runner: 'sql',
    function: i % 2 ? 'B' : 'A', cells: [i < 2 ? 'legacy' : 'new'],
    rule: i % 2 ? 'array_unary_transform_function' : 'array_transform_function'
  }));
  fs.writeFileSync(path.join(batch, 'cases.json'), JSON.stringify(cases));
  fs.writeFileSync(path.join(batch, 'captured.json'), JSON.stringify(cases.map(c => ({
    name: c.name, errors: 0, diagnostics: [], outputs: ['ast', 'interface', 'subs', 'table', 'query', 'symbols']
  }))));
  const invoke = (...args) => spawnSync(process.execPath, [cli, batch, ...args], { cwd: root, encoding: 'utf8' });
  assert.notEqual(invoke('emit').status, 0, 'Unreviewed captures must not emit');
  const emitted = invoke('emit', '--reviewed');
  assert.equal(emitted.status, 0, emitted.stderr);
  const source = fs.readFileSync(host, 'utf8');
  assert.ok(source.includes(method('unrelated')));
  assert.equal((source.match(/"ast", "interface", "subs", "table", "query", "symbols"/g) || []).length, 3);
  assert.ok(source.includes('runDedicatedFunctionQuery(query, SQLSelectParserParser.RULE_array_unary_transform_function)'));
  const xmlPath = path.join(root, 'parse/target-maven/surefire-reports/TEST-sql.walker.SqlEventWalkerSnowflakeArrayTransformTests.xml');
  fs.mkdirSync(path.dirname(xmlPath), { recursive: true });
  const writeSuite = failures => fs.writeFileSync(xmlPath, `<testsuite tests="5" failures="${failures}" errors="0" skipped="0">`
    + [...cases.map(c => c.name), 'unrelated'].map(name => `<testcase name="${name}"/>`).join('') + '</testsuite>');
  writeSuite(0);
  fs.utimesSync(xmlPath, new Date(), new Date(Date.now() + 1000));
  const planPath = path.join(root, 'parse/documents/sql-grammar-5.1.4-workplan.md');
  fs.mkdirSync(path.dirname(planPath), { recursive: true });
  fs.writeFileSync(planPath, '- [ ] T001 [Plan:P1]\n- [ ] T002 [Plan:P2]\n'
    + Array.from({ length: 22 }, (_, i) => '- [ ] T' + String(i + 3).padStart(3, '0') + ' pending').join('\n'));
  const finished = invoke('finish');
  assert.equal(finished.status, 0, finished.stderr);
  const accounting = fs.readFileSync(path.join(batch, 'accounting.md'), 'utf8');
  assert.ok(accounting.includes('## P1 A'));
  assert.ok(accounting.includes('## P2 B'));
  assert.ok(fs.readFileSync(path.join(batch, 'batch-report.yaml'), 'utf8').includes('completed_tasks: ["T001","T002"]'));
  assert.ok(fs.readFileSync(path.join(batch, 'batch-report.yaml'), 'utf8')
    .includes('-Dtest=SqlEventWalkerSnowflakeArrayTransformTests,RelatedCompositionTests'));
  const checkpoint = fs.readFileSync(path.join(batch, 'checkpoints/tasks-to-impl.yaml'), 'utf8');
  const chain = JSON.parse(checkpoint.match(/^traceability_chain: (.*)$/m)[1]);
  assert.ok(chain.every(trace => Array.isArray(trace.files) && trace.files.length === 3));
  writeSuite(1);
  assert.match(invoke('finish').stderr, /Focused failures must be zero/);
  writeSuite(0);
  fs.utimesSync(xmlPath, new Date(0), new Date(0));
  assert.match(invoke('finish').stderr, /Stale test report/);
  fs.appendFileSync(host, '\n');
  assert.match(invoke('finish').stderr, /Host changed after generation/);
});
