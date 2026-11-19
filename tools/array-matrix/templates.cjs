const assert = require('node:assert/strict');
const SF = 'STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR';

function signatureCases({ name, args, covered = [] }) {
  assert.match(name, /^[a-z][a-z0-9_]*$/);
  assert.ok(Array.isArray(args) && args.length > 0, 'Specify the documented argument form');
  for (const arg of args) {
    assert.match(arg.column, /^[a-z][a-z0-9_]*$/);
    assert.ok(typeof arg.literal === 'string' && arg.literal.trim(), 'Supply a valid SQL literal/expression for each argument');
  }
  assert.equal(new Set(args.map(a => a.column)).size, args.length, 'Give argument positions distinct column names');
  const cases = [];
  const call = (values, fn = name) => `${fn}(${values.join(', ')})`;
  const columns = args.map(a => `a.${a.column}`);
  const literal = args.map(a => a.literal);
  const project = values => `SELECT a.id, ${call(values)} AS result_value FROM events a WHERE a.active = 1`;
  const add = (suffix, query, cells, extra = {}) => cases.push({ suffix, query, cells, codes: [SF], ...extra });
  const fn = call(columns);
  const body = project(columns);
  const placements = {
    Select: body,
    Where: `SELECT a.id, a.label FROM events a WHERE ${fn} IS NOT NULL AND a.active = 1`,
    Join: `SELECT a.id, b.label FROM events a JOIN labels b ON ${fn} = b.ref_value AND a.id = b.event_id WHERE b.active = 1`,
    Having: `SELECT a.id, count(*) AS total FROM events a GROUP BY ${['a.id', ...columns].join(', ')} HAVING ${fn} IS NOT NULL`,
    Qualify: `SELECT a.id, row_number() over (partition by a.group_id order by a.id) AS rn FROM events a QUALIFY ${fn} IS NOT NULL AND rn <= 5`,
    OrderBy: `SELECT a.id FROM events a WHERE a.active = 1 ORDER BY ${fn}, a.id`,
    Values: `VALUES (7, ${call(literal)})`,
    Insert: `INSERT INTO results (id, result_value) ${body}`,
    Update: `UPDATE events SET result_value = ${call(args.map(a => a.column))} WHERE active = 1`,
    Delete: `DELETE FROM events WHERE ${call(args.map(a => a.column))} IS NULL AND active = 0`,
    Ddl: `CREATE VIEW v_${name} AS ${body}`,
    Script: `${body};\nSELECT b.id FROM orders b WHERE b.active = 1;\nSELECT c.id, ${call(args.map(a => `c.${a.column}`))} AS other_value FROM archive_events c WHERE c.active = 1;`
  };
  for (const [placement, query] of Object.entries(placements))
    add(placement + 'Placement', query, ['placement:' + placement],
      placement === 'Ddl' ? { runner: 'ddl' } : placement === 'Script'
        ? { runner: 'script', routes: 2, codes: [SF, SF] } : {});
  for (let i = 0; i < args.length; i++) {
    const column = args[i].column;
    const isolated = value => columns.map((a, n) => n === i ? value : a);
    const cell = kind => [`arg${i + 1}:${kind}`];
    const addArg = (kind, query, extra) => add(`Arg${i + 1}${kind}`, query, cell(kind), extra);
    addArg('Unqualified', project(isolated(column)));
    addArg('Unresolved', `SELECT 7 AS marker, ${call(literal.map((a, n) => n === i ? column : a))} AS result_value`,
      { codes: [SF, 'UNRESOLVED_UNQUALIFIED_COLUMNS'] });
    addArg('Ambiguous', `SELECT a.id, ${call(isolated(column))} AS result_value FROM events a JOIN labels b ON a.id = b.event_id WHERE b.active = 1`,
      { codes: [SF, 'AMBIGUOUS_COLUMN_REFERENCE', 'UNRESOLVED_UNQUALIFIED_COLUMNS'] });
    addArg('MissingAlias', project(isolated(`z.${column}`)), { codes: [SF, 'QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE'] });
    addArg('HiddenAlias', `SELECT q.id, ${call(args.map((a, n) => `${n === i ? 'a' : 'q'}.${a.column}`))} AS result_value FROM (SELECT a.id, ${columns.join(', ')} FROM events a WHERE a.active = 1) q WHERE q.id > 0`,
      { codes: [SF, 'QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE'] });
    addArg('PredicandVariable', project(isolated(`<${column}_expr>`)), { types: { [`<${column}_expr>`]: 'predicand' } });
    addArg('ColumnVariable', project(isolated(`a.<${column}_col>`)), { types: { [`<${column}_col>`]: 'column' } });
    addArg('Null', project(isolated('NULL')));
  }
  if (args.length > 1) {
    add('UnqualifiedOperands', project(args.map(a => a.column)), ['combined:unqualified']);
    for (const [kind, type] of [['Predicand', 'predicand'], ['Column', 'column']]) {
      const variables = args.map(a => `<${a.column}_${type}>`);
      add('All' + kind + 'Variables', project(variables.map(v => type === 'column' ? 'a.' + v : v)),
        ['combined:' + type], { types: Object.fromEntries(variables.map(v => [v, type])) });
    }
  }
  add('NestedExpression', `SELECT a.id, coalesce(${fn}, a.fallback_value) AS result_value FROM events a WHERE a.active = 1`, ['deep:nested']);
  add('DerivedOutput', `SELECT q.id, q.result_value FROM (${body}) q WHERE q.result_value IS NOT NULL ORDER BY q.id`, ['deep:derived']);
  add('SetOpSiblingIsolation', `SELECT a.id AS left_id, ${fn} AS left_value FROM events a WHERE a.active = 1 UNION ALL SELECT b.id AS right_id, b.other_value AS right_value FROM archive_events b WHERE b.archived = 1`, ['deep:setop']);
  add('QualifiedUdfNeutral', `SELECT a.id, ${call(columns, 'util.' + name)} AS result_value FROM events a WHERE a.active = 1`,
    ['neutral:udf'], { routes: 0, codes: [] });
  add('SameNamedColumnNeutral', `SELECT a.id, a.${name} AS stored_value FROM events a WHERE a.active = 1`,
    ['neutral:column'], { routes: 0, codes: [] });
  add('Uppercase', `SELECT a.id, ${call(columns, name.toUpperCase())} AS result_value FROM events a WHERE a.active = 1`, ['dialect:uppercase']);
  add('MixedDialect', `SELECT a.id, ${fn} AS result_value, extract(isodow from a.created_at) AS weekday FROM events a WHERE a.active = 1`,
    ['dialect:mixed'], { codes: [SF, 'STATEMENT_MIXED_DIALECT_GRAMMAR'] });
  const malformed = {
    TrailingComma: `SELECT ${name}(${columns.join(', ')},) AS result_value FROM events a`,
    MissingRightParenthesis: `SELECT ${name}(${columns.join(', ')} AS result_value FROM events a`
  };
  if (args.length > 1) malformed.MissingComma = `SELECT ${name}(${columns[0]} ${columns.slice(1).join(', ')}) AS result_value FROM events a`;
  for (const [kind, query] of Object.entries(malformed))
    add(kind + 'Syntax', query, ['syntax:' + kind],
      { runner: 'syntax', codes: ['REPORT_ERROR', 'RECOVER', 'RECOVER_INLINE'] });
  const done = new Set(covered);
  return cases.filter(c => !c.cells.every(cell => done.has(cell)));
}

function functionsFor(config) {
  const functions = config.functions || [config];
  assert.ok(functions.length >= 1 && functions.length <= 3, 'Use one function or an approved related batch of up to three');
  for (const field of ['function', 'prefix', 'legacyPrefix', 'task'])
    assert.equal(new Set(functions.map(f => f[field])).size, functions.length, 'Duplicate ' + field);
  return functions.map(f => {
    assert.ok(Number.isInteger(f.legacyCount) && f.legacyCount >= 0);
    if (f.signature) assert.equal(f.signature.name, f.function.toLowerCase(), 'Signature/function mismatch');
    const legacy = f.legacyCoverage || {};
    const covered = [...Object.values(legacy).flat(), ...(f.cases || []).flatMap(c => c.cells || [])];
    const generated = f.signature ? signatureCases({ ...f.signature, covered }) : [];
    return { ...f, cases: [...generated, ...(f.cases || [])] };
  });
}

function ledgerFor(f, cases, counts) {
  const rows = new Map();
  for (const c of cases) {
    assert.ok(c.cells?.length, c.name + ': assign evidence cells before accounting');
    for (const cell of c.cells) {
      if (!rows.has(cell)) rows.set(cell, []);
      rows.get(cell).push('`' + c.name + '`');
    }
  }
  const required = f.signature ? signatureCases({ ...f.signature, covered: [] }).flatMap(c => c.cells) : [];
  for (const cell of [...required, ...(f.requiredCells || [])])
    assert.ok(rows.has(cell), f.function + ': missing cell ' + cell);
  return `## ${f.plan} ${f.function} method-level completion evidence\n\n`
    + `${counts.methods} methods; ${counts.six_goldens} six-output goldens; ${counts.syntax} parser-only controls. `
    + 'Every method compares complete exact diagnostics. Existing extractor goldens are preserved.\n\n'
    + '| Matrix cell | Methods |\n|---|---|\n'
    + [...rows].map(([cell, names]) => `| ${cell} | ${names.join(', ')} |`).join('\n') + '\n';
}

module.exports = { signatureCases, functionsFor, ledgerFor };
