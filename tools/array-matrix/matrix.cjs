const fs = require('node:fs');
const path = require('node:path');
const cp = require('node:child_process');
const assert = require('node:assert/strict');
const crypto = require('node:crypto');
const { functionsFor, ledgerFor } = require('./templates.cjs');
const dir = path.resolve(process.argv[2]);
const action = process.argv[3];
const config = require(path.join(dir, 'batch.cjs'));
const functions = functionsFor(config);
const host = config.host || 'parse/src/test/java/sql/walker/SqlEventWalkerSnowflakeArrayTransformTests.java';
assert.match(host, /^parse\/src\/test\/java\/sql\/walker\/\w+\.java$/);
const suiteName = 'sql.walker.' + path.basename(host, '.java');
const reportPath = 'parse/target-maven/surefire-reports/TEST-' + suiteName + '.xml';
const q = value => value == null ? 'null' : JSON.stringify(value);
const scan = source => [...source.matchAll(/\t@Test\n\tpublic void (\w+)\(\) \{([\s\S]*?)\n\t\}/g)]
  .map(m => ({ name: m[1], body: m[2], text: m[0] }));
const hasSixOutputs = m => m.body.includes('assertOutputs(')
  || ['AST', 'Interface', 'Substitution List', 'Table Dictionary', 'Query Column Dictionary', 'Symbol Table']
    .every(label => m.body.includes('"' + label + ' is wrong"'));
const load = name => JSON.parse(fs.readFileSync(path.join(dir, name), 'utf8'));
const save = (name, value) => fs.writeFileSync(path.join(dir, name), JSON.stringify(value, null, 2) + '\n');
const hash = source => crypto.createHash('sha256').update(source).digest('hex');
const owner = name => {
  const found = functions.filter(f => name.startsWith(f.legacyPrefix) || name.startsWith(f.prefix));
  assert.equal(found.length, 1, name + ': ambiguous function ownership');
  return found[0];
};
const run = (command, args) => {
  const result = cp.spawnSync(command, args, { stdio: 'inherit' });
  if (result.error) throw result.error;
  assert.equal(result.status, 0, command + ' failed');
};
function diagnostic(d) {
  const details = d.details == null ? 'null' : 'java.util.Map.of('
    + Object.entries(d.details).sort(([a], [b]) => a.localeCompare(b))
      .flatMap(([k, v]) => [q(k), q(v)]).join(', ') + ')';
  return 'new ParseDiagnostic(ParseDiagnostic.Severity.' + d.severity + ',\n\t\t\t\t\t\t'
    + [q(d.code), q(d.message)].join(',\n\t\t\t\t\t\t') + ',\n\t\t\t\t\t\t'
    + [d.line, d.charPositionInLine, q(d.source), q(d.ruleName), q(d.tokenText),
      d.recoverable, q(d.phase), q(d.exceptionType), details].map(v => v == null ? 'null' : String(v)).join(', ') + ')';
}
const diagnosticArgs = result => result.diagnostics.map(diagnostic).join(',\n\t\t\t\t');
const exact = result => '\t\tassertExactDiagnostics(extractor'
  + (result.diagnostics.length ? ',\n\t\t\t\t' + diagnosticArgs(result) : '') + ');\n';
function prepare() {
  const source = fs.readFileSync(host, 'utf8');
  assert.ok(!fs.existsSync(path.join(dir, 'before.java')), 'Do not overwrite a batch baseline');
  const cases = functions.flatMap(f => {
    const old = scan(source).filter(m => m.name.startsWith(f.legacyPrefix));
    assert.equal(old.length, f.legacyCount, f.function + ': legacy inventory changed');
    const legacy = old.map(m => {
    const expression = m.body.match(/final String query\s*=\s*([\s\S]*?);\n/)[1];
    assert.equal(expression.replace(/"(?:\\.|[^"\\])*"/g, '').replace(/[+\s]/g, ''), '');
    return { name: m.name, query: [...expression.matchAll(/"(?:\\.|[^"\\])*"/g)].map(m => JSON.parse(m[0])).join(''),
      runner: m.body.includes('runScriptParsertest') ? 'script' : m.body.includes('runDdlParsertest') ? 'ddl' : 'sql',
      legacy: true, routes: 1, rule: f.rule, function: f.function,
      cells: f.legacyCoverage?.[m.name] };
    });
    return [...legacy, ...f.cases.map(c => ({ runner: 'sql', routes: 1, ...c, rule: c.rule || f.rule,
      name: f.prefix + c.suffix + 'V0Test', function: f.function, legacy: false }))];
  });
  assert.equal(new Set(cases.map(c => c.name)).size, cases.length);
  for (const c of cases.filter(c => !c.legacy))
    assert.ok(Array.isArray(c.codes), c.name + ': declare expected diagnostic codes');
  fs.writeFileSync(path.join(dir, 'before.java'), source);
  save('cases.json', cases);
  const xml = fs.readFileSync('parse/target-maven/surefire-reports/TEST-sql.walker.SqlEventWalkerSnowflakeArrayTransformTests.xml', 'utf8');
  const classpath = xml.match(/name="java.class.path" value="([^"]+)"/)[1].replace(/&amp;/g, '&');
  const javaHome = process.env.JAVA_HOME || xml.match(/name="java.home" value="([^"]+)"/)[1];
  const jdk = path.join(javaHome, 'bin');
  const classes = path.join(path.dirname(dir), 'array-matrix-cache', 'classes');
  const sourceFile = path.join(__dirname, 'Capture.java');
  const keyFile = path.join(classes, 'capture-key.txt');
  const key = hash(fs.readFileSync(sourceFile, 'utf8') + classpath + javaHome);
  if (!fs.existsSync(keyFile) || fs.readFileSync(keyFile, 'utf8') !== key
      || !fs.existsSync(path.join(classes, 'sql/walker/Capture.class'))) {
    run(path.join(jdk, 'javac'), ['-cp', classpath, '-d', classes, sourceFile]);
    fs.writeFileSync(keyFile, key);
  }
  run(path.join(jdk, 'java'), ['-Dpss.walker.test.verbose=false', '-cp', classes + path.delimiter + classpath,
    'sql.walker.Capture', path.join(dir, 'cases.json'), path.join(dir, 'captured.json')]);
  const results = new Map(load('captured.json').map(r => [r.name, r]));
  assert.equal(results.size, cases.length);
  for (const c of cases.filter(c => !c.legacy)) {
    const r = results.get(c.name);
    if (c.codes) assert.deepEqual(r.diagnostics.map(d => d.code), c.codes, c.name);
    for (const [name, type] of Object.entries(c.types || {}))
      assert.ok(r.outputs[2].includes(name + '=' + type), c.name + ': ' + name);
  }
  console.log('Captured and checked ' + cases.length + ' cases. New expectations only; legacy diagnostics only.');
}
function emit() {
  assert.ok(process.argv.includes('--reviewed'), 'Review the new captures before emit --reviewed');
  const cases = load('cases.json'), captured = new Map(load('captured.json').map(c => [c.name, c]));
  const before = fs.readFileSync(path.join(dir, 'before.java'), 'utf8');
  assert.equal(fs.readFileSync(host, 'utf8'), before, 'Host changed after snapshot; stop');
  let source = before;
  for (const m of scan(before).filter(m => functions.some(f => m.name.startsWith(f.legacyPrefix)))) {
    const f = owner(m.name);
    const r = captured.get(m.name);
    const header = m.body.slice(0, m.body.indexOf('\n', m.body.indexOf('final SqlParseEventWalker extractor =')))
      .replace('runQuery(query)', 'runDedicatedFunctionQuery(query, SQLSelectParserParser.RULE_' + f.rule + ')');
    const noFatal = r.diagnostics.some(d => d.severity === 'FATAL') ? '' : '\t\tassertNoFatalErrors(extractor);\n';
    const text = '\t@Test\n\tpublic void ' + m.name + '() {' + header + '\n' + noFatal + exact(r)
      + m.body.slice(m.body.indexOf('\t\tassertOutputs(')) + '\n\t}';
    source = source.replace(m.text, text);
  }
  const added = cases.filter(c => !c.legacy).map(c => {
    const r = captured.get(c.name);
    let text = '\t@Test\n\tpublic void ' + c.name + '() {\n\t\tfinal String query = ' + q(c.query) + ';\n';
    if (c.runner === 'syntax') {
      text += '\t\tfinal SQLSelectParserParser parser = parse(query);\n\t\tparser.sql();\n'
        + '\t\tfinal errorhandling.ParseErrorCollector collector =\n\t\t\t\t(errorhandling.ParseErrorCollector) parser.getErrorHandler();\n'
        + '\t\tAssert.assertEquals("Parser error count is wrong", ' + r.errors + ', collector.getErrorCount());\n'
        + '\t\tfinal java.util.List<ParseDiagnostic> diagnostics = new java.util.ArrayList<>(collector.getDiagnostics());\n'
        + '\t\tdiagnostics.addAll(collectParserListenerDiagnostics(parser));\n'
        + '\t\tAssert.assertEquals("Parser diagnostics are wrong", Arrays.asList(\n\t\t\t\t' + diagnosticArgs(r) + '), diagnostics);\n';
    } else {
      if (c.runner === 'ddl' || c.runner === 'script' || c.runner === 'snippet' || c.route_checks) {
        const type = c.runner === 'ddl' ? 'Ddl' : c.runner === 'script' ? 'Script' : 'Sql';
        if (c.runner === 'snippet') assert.match(c.entry, /^[a-z_]+$/);
        text += '\t\tfinal SQLSelectParserParser parser = parse(query);\n'
          + '\t\tfinal ' + (c.runner === 'snippet' ? 'org.antlr.v4.runtime.ParserRuleContext' : 'SQLSelectParserParser.' + type + 'Context')
          + ' tree = parser.' + (c.runner === 'snippet' ? c.entry : c.runner || 'sql') + '();\n'
          + '\t\tAssert.assertEquals("Expected no syntax errors", 0, parser.getNumberOfSyntaxErrors());\n'
          + '\t\tAssert.assertEquals("Unexpected primary array function routing", ' + c.routes + ',\n'
          + '\t\t\t\tTrees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_' + c.rule + ').size());\n';
        for (const check of c.route_checks || [])
          text += '\t\tAssert.assertEquals("Unexpected ' + check.rule + ' routing", ' + check.count + ',\n'
            + '\t\t\t\tTrees.findAllRuleNodes(tree, SQLSelectParserParser.RULE_' + check.rule + ').size());\n';
        text += '\t\tfinal SqlParseEventWalker extractor = runAnyParsertest(query, parser, tree, true);\n';
      } else text += '\t\tfinal SqlParseEventWalker extractor = ' + (c.routes === 0 ? 'runQuery(query)'
        : 'runDedicatedFunctionQuery(query, SQLSelectParserParser.RULE_' + c.rule + ')') + ';\n';
      if (!r.diagnostics.some(d => d.severity === 'FATAL')) text += '\t\tassertNoFatalErrors(extractor);\n';
      text += exact(r) + '\t\tassertOutputs(extractor,\n' + r.outputs.map(s => '\t\t\t\t' + q(s)).join(',\n') + ');\n';
      if (c.runner === 'snippet') {
        assert.equal(r.access_outputs.length, 6);
        const getters = ['getSqlAbstractTree', 'getQueryInterface', 'getSubstitutionsMap',
          'getTableDictionary', 'getQueryColumnDictionaryMap', 'getSymbolTable'];
        const labels = ['AST', 'Interface', 'Substitution List', 'Table Dictionary', 'Query Column Dictionary', 'Symbol Table'];
        text += '\t\tfinal access.SqlParserAccess accessParser = new access.SqlParserAccess(false, false, false);\n'
          + '\t\taccessParser.executeTheParse(query, ' + q(c.endpoint) + ');\n'
          + '\t\tAssert.assertEquals("Access syntax errors", 0, accessParser.getParser().getNumberOfSyntaxErrors());\n'
          + '\t\tAssert.assertEquals("Access diagnostics are wrong", Arrays.asList(' + diagnosticArgs(r)
          + '), accessParser.getAllDiagnostics());\n';
        for (let i = 0; i < getters.length; i++)
          text += '\t\tAssert.assertEquals("Access ' + labels[i] + ' is wrong", ' + q(r.access_outputs[i])
            + ', accessParser.getSnippet().' + getters[i] + '().toString());\n';
      }
    }
    return text + '\t}';
  }).join('\n\n');
  source = source.replace(/\n}\s*$/, '\n\n' + added + '\n}\n');
  const after = new Map(scan(source).map(m => [m.name, m]));
  for (const old of scan(before)) {
    const current = after.get(old.name);
    if (functions.some(f => old.name.startsWith(f.legacyPrefix))) {
      assert.equal(current.body.slice(current.body.indexOf('assertOutputs(')), old.body.slice(old.body.indexOf('assertOutputs(')), old.name);
    } else assert.equal(current.text, old.text, old.name);
  }
  for (const c of cases) {
    const body = after.get(c.name).body;
    assert.equal((body.match(/final String query\s*=/g) || []).length, 1, c.name);
    assert.ok(body.includes(c.runner === 'syntax' ? '"Parser diagnostics are wrong"' : 'assertExactDiagnostics('), c.name);
    if (c.runner !== 'syntax')
      assert.equal([...body.slice(body.indexOf('assertOutputs('),
        c.runner === 'snippet' ? body.indexOf('\t\tfinal access.SqlParserAccess') : undefined)
        .matchAll(/"(?:\\.|[^"\\])*"/g)].length, 6, c.name);
  }
  fs.writeFileSync(host, source);
  save('counts.json', { functions: functions.map(f => {
    const own = cases.filter(c => c.function === f.function);
    return { function: f.function, methods: own.length, new_methods: f.cases.length,
      legacy_diagnostics_strengthened: f.legacyCount, six_goldens: own.filter(c => c.runner !== 'syntax').length,
      syntax: own.filter(c => c.runner === 'syntax').length };
  }), host_hash: hash(source),
    unrelated_methods_preserved: scan(before).length - functions.reduce((n, f) => n + f.legacyCount, 0),
    host_methods: after.size, host_six_goldens: [...after.values()].filter(hasSixOutputs).length,
    host_syntax: [...after.values()].filter(m => m.body.includes('"Parser diagnostics are wrong"')).length });
  console.log(fs.readFileSync(path.join(dir, 'counts.json'), 'utf8'));
}
function finish() {
  const counts = load('counts.json'), cases = load('cases.json');
  const xmlPath = reportPath;
  const xml = fs.readFileSync(xmlPath, 'utf8');
  const suite = xml.match(/<testsuite\b[^>]*>/)[0];
  const number = name => Number(suite.match(new RegExp('\\b' + name + '="([^"]+)"'))[1]);
  assert.equal(number('tests'), counts.host_methods);
  for (const name of ['failures', 'errors', 'skipped'])
    assert.equal(number(name), 0, 'Focused ' + name + ' must be zero');
  const source = fs.readFileSync(host, 'utf8');
  assert.equal(hash(source), counts.host_hash, 'Host changed after generation');
  counts.host_six_goldens = scan(source).filter(hasSixOutputs).length;
  save('counts.json', counts);
  assert.ok(fs.statSync(xmlPath).mtimeMs >= fs.statSync(host).mtimeMs, 'Stale test report; run the focused host');
  assert.deepEqual([...xml.matchAll(/<testcase\b[^>]*\bname="([^"]+)"/g)].map(m => m[1]).sort(),
    scan(source).map(m => m.name).sort(), 'Test report does not cover the current host');
  const planPath = 'parse/documents/sql-grammar-5.1.4-workplan.md';
  const reconciliationPath = 'parse/documents/slice-d-array-function-reconciliation.md';
  const plan = fs.readFileSync(planPath, 'utf8');
  for (const f of functions) {
    assert.ok(plan.includes(f.task + ' [Plan:' + f.plan + ']'), 'Task missing from master plan: ' + f.task);
    for (const prerequisite of f.dependencies || [])
      assert.ok(functions.some(other => other.task === prerequisite) || plan.includes('- [x] ' + prerequisite + ' '),
        f.task + ': unmet prerequisite ' + prerequisite);
  }
  for (let n = Math.max(...functions.map(f => Number(f.task.slice(1)))) + 1; n <= 24; n++)
    assert.ok(plan.includes('- [ ] T' + String(n).padStart(3, '0') + ' '), 'Later task closed: ' + n);
  const ledgers = functions.map(f => {
    assert.equal(typeof f.production_changes, 'boolean', f.function + ': declare production-change status');
    if (f.production_changes)
      assert.ok(Array.isArray(f.production_files) && f.production_files.length > 0,
        f.function + ': list production_files for the proved fix');
    return ledgerFor(f, cases.filter(c => c.function === f.function),
      counts.functions.find(c => c.function === f.function));
  });
  assert.equal(typeof config.next_task, 'string', 'Declare the next task');
  const command = 'mvn -f parse/pom.xml -Pisolated-build -Dtest='
    + (config.test_selection || path.basename(host, '.java')) + ' '
    + '-Dpss.walker.test.verbose=false -Dpss.test.slow=false -Dpss.test.diagnostics=false test';
  const files = f => [host, planPath, reconciliationPath, ...(f.production_files || [])];
  const tests = { command, suite: suiteName,
    passed: number('tests'), failed: 0, errors: 0, skipped: 0, evidence: xmlPath };
  const traces = functions.map(f => ({ plan_item: f.plan, task: f.task, requirements: ['REQ-001', 'REQ-002', 'REQ-003'] }));
  const report = { batch_id: functions.map(f => f.plan + '-' + f.function).join('+'),
    completed_tasks: functions.map(f => f.task), blocked_tasks: [],
    files_changed: Object.fromEntries(functions.map(f => [f.task, files(f).map(file => ({ file, change_type: 'modified' }))])),
    upstream_artifacts_consumed: [planPath, reconciliationPath],
    traceability: traces, coverage: counts, implementation: { production_changes: functions.some(f => f.production_changes),
      lexer_changes: false, existing_extractor_goldens_rewritten: false,
      note: 'Runtime values/types are not evaluated; accepted arities and grammar routing do not certify database signatures. Review any production fixes separately.' },
    test_results: Object.fromEntries(functions.map(f => [f.task, tests])), errors: [], next_task: config.next_task };
  const checkpoint = { metadata: { generated: new Date().toISOString().slice(0, 10), source_tasks: planPath },
    summary: { total_tasks: functions.length, completed_tasks: functions.length, in_progress_tasks: 0, pending_tasks: 0, completion_percentage: 100 },
    tasks: functions.map((f, i) => ({ task_id: f.task, description: 'Complete independent ' + f.function + ' matrix.',
      status: 'completed', implementation: { files_changed: files(f).map(file => ({ file,
        changes: [{ change_type: 'modified', description: 'Completed matrix and recorded named-method evidence.' }] })) },
      verification: { build_passed: true, test_results: tests }, upstream_trace: traces[i] })),
    pending_tasks: [], untracked_changes: [],
    traceability_chain: traces.map((trace, i) => ({ ...trace, files: files(functions[i]), status: 'complete' })),
    validation: { passed: true, errors: [], warnings: [], blocking_issues: [] } };
  const yaml = (object, indent = '') => Object.entries(object).map(([key, value]) => indent + key + ':'
    + (value && typeof value === 'object' && !Array.isArray(value) && Object.keys(value).length
      ? '\n' + yaml(value, indent + '  ') : ' ' + JSON.stringify(value)) + '\n').join('');
  fs.mkdirSync(path.join(dir, 'checkpoints'), { recursive: true });
  fs.writeFileSync(path.join(dir, 'batch-report.yaml'), yaml(report));
  fs.writeFileSync(path.join(dir, 'checkpoints/tasks-to-impl.yaml'), yaml(checkpoint));
  fs.writeFileSync(path.join(dir, 'accounting.md'), ledgers.join('\n')
    + `\nFocused host: ${counts.host_methods} passed, zero failures/errors/skips; `
    + `${counts.host_six_goldens} six-output cases and ${counts.host_syntax} syntax controls.\n`);
  if (process.argv.includes('--record')) {
    const marker = '<!-- array-matrix:' + functions.map(f => f.task).join('+') + ' -->';
    const reconciliation = fs.readFileSync(reconciliationPath, 'utf8');
    assert.ok(!reconciliation.includes(marker), 'Evidence already recorded; review the existing section before replacing it');
    fs.appendFileSync(reconciliationPath, '\n' + marker + '\n'
      + fs.readFileSync(path.join(dir, 'accounting.md'), 'utf8'));
  }
  console.log('Verified ' + cases.length + ' methods and fresh focused results; generated accounting and reports. Apply only compact status/count edits to the master documents.');
}
if (action === 'capture') prepare();
else if (action === 'emit') emit();
else if (action === 'finish') finish();
else throw new Error('Expected capture, emit or finish');
