# pss
Project Semantic Stream is the code name for a developing capability for managing BIG VARIETY using Big Data technologies natively. "Big Variety" describes the characteristic of Big Data that there are data formats and sources that are too diverse to handle using traditional data management approaches.

This is a parser for the PSS SQL dialect.

The parser is designed to be used in a variety of contexts, including:

*   As a library for other Java applications
*   As a command-line tool for parsing SQL statements
*   As a service for parsing SQL statements

The parser is designed to be extensible, and it is possible to add new SQL statements and functions to the grammar.

## Building and testing alongside the IDE

When Java auto-build is enabled, use the isolated Maven profile so the IDE and
Maven do not overwrite each other's compiled classes:

```bash
mvn -Pisolated-build clean test
```

The profile keeps generated ANTLR sources, classes, test reports, and packaged
artifacts under each module's `target-maven/`, separate from the IDE's `target/`.
The VS Code Maven test tasks use this profile. Java auto-build can remain enabled.
Subsequent runs can use `mvn -Pisolated-build test`.

Use `target-maven/` for isolated test execution and its reports, **not as the
release JAR handoff location**. Tools that explicitly read `parse/target/`
do not consume isolated test output.
If the IDE reports missing Maven dependencies, its project model still needs
repair; isolated tests do not depend on the IDE's compiled output.

### Local release JARs

**Each module's standard `target/` is authoritative for release JARs.**
Keep isolated test builds separate, but do not distribute JARs from
`target-maven/` or retain competing release copies there.

1. In [workspace settings](.vscode/settings.json), record the current value of
   `java.autobuild.enabled`, set it to `false` and let any in-progress IDE
   compilation finish.
2. Run **Build Local Release JARs** in VS Code, or run this command from the
   repository root, **without** the `isolated-build` profile:

   ```bash
   mvn -Prelease-tests clean verify
   ```

3. Restore the prior `java.autobuild.enabled` value afterward, **including
   after a failed or cancelled build**. The task does not toggle this setting
   automatically.

The reactor builds the parser and companion documentation together, including
the release-test suite. The principal handoff files for the current version are:

- `parse/target/pss-parse-5.1.4-1-fat.jar`
- `pss-parse-docs/target/pss-parse-docs-5.1.4-1.jar`

The standard parser and test JARs are also produced in `parse/target/`.
Local packaging is not publication: do not run `deploy` or upload to Archiva
without explicit owner authorization.

### Test suite selection

**"Full suite" means the result-accuracy suite**, not slow corpus regressions or
manual timing/benchmark probes. This is the default for Maven and the Testing
sidebar. Agents must ask which set is intended if a request is ambiguous.

| Set | VS Code test configuration | Maven selection |
| --- | --- | --- |
| Full result-accuracy suite (default) | `pss-quiet` | `mvn -f parse/pom.xml -Pisolated-build test` |
| Release suite: accuracy plus slow corpus regressions | `pss-release` | `mvn -f parse/pom.xml -Pisolated-build,release-tests test` |
| Manual diagnostics: also enables timing probes | `pss-diagnostics` | Add `diagnostic-tests` and select the diagnostic class/method |

Set `java.test.defaultConfig` to the desired Java test configuration in
[workspace settings](.vscode/settings.json). In the Testing sidebar, run the desired test, class,
package, or all tests normally. Restore `pss-quiet` after a release/diagnostic
run. Assertions and failed-test navigation remain in the sidebar.

Slow/diagnostic tests remain discoverable but report **skipped** with an opt-in
message unless enabled. This uses JUnit 4 assumptions, so it works in both
Maven and the IDE, rather than relying on Maven-only exclusions. Selecting a
test by name does not bypass its suite guard.

The slow set contains the real-corpus MCP bound-query/Unicode regressions, the
MCP row-28 fixture regression, Cluster B production parsing, row-130 production
parsing, row-28 subMap regression, and diagnostic/production row-28 parity.
Lightweight fixture/index checks, diagnostic exception mapping, and ordinary
CLI framing/Unicode tests stay in the accuracy set. The overlapping row-130
subMap check is covered by
[`PantoRow130FullCsvRegressionTest`](parse/src/test/java/sql/latency/PantoRow130FullCsvRegressionTest.java),
which also checks walker stack alignment without parsing that row twice.

Manual latency probes use the diagnostic guard instead of permanent `@Ignore`,
so opting in actually executes them. For example:

```bash
mvn -f parse/pom.xml -Pisolated-build,diagnostic-tests \
  -Dtest=ParseLatencyDiagnosticTest#pantoRow28_acquia \
  -Dpss.walker.test.verbose=false test
```

Diagnostic mode also enables slow regressions; release mode does **not** enable
manual diagnostics. Prefer a targeted diagnostic run: the batch benchmark
requires its generated manifest (`-Dpanto.timeout.manifest=/absolute/path/to/manifest.json`).
Existing elapsed-time assertions are measurements, not hard parser-cancellation
deadlines; this suite separation does not add process isolation or new timeouts.

The **Run All Tests (Maven)** task runs accuracy tests; **Run Release Tests
(Maven)** explicitly adds the slow regressions. Run the release suite before
publishing a JAR; a passing accuracy run alone is not release sign-off.

## Command-Line SQL Parsing

This project provides a command-line interface (CLI) to parse SQL statements directly from your terminal. The main class for this functionality is `cli.SqlParse`.

### How to Use

To parse a SQL statement, you need to run the `SqlParse` class and provide two arguments: a parser endpoint and the SQL text you want to parse.

#### Syntax

```bash
java -cp <classpath> cli.SqlParse <parser_end_point> "<sql_text>"
```

-   `<classpath>`: You will need to provide the path to the compiled classes or the project's JAR file. For example, `target/classes` or `target/pss-parse-5.0.1-1.jar`.
-   `<parser_end_point>`: A keyword that tells the parser which part of the SQL grammar to use as the starting point for parsing. This determines the type of SQL fragment that can be parsed. This argument is case-insensitive.
-   `<sql_text>`: The SQL statement or fragment to be parsed. It is recommended to enclose this in double quotes to avoid shell interpretation issues.

#### Example

```bash
java -cp target/classes cli.SqlParse SQL "SELECT * FROM mytable"
```

This command will parse the `SELECT` statement and print the resulting parse tree snippet to the console.

### Parser Endpoints

The `<parser_end_point>` argument specifies the parsing rule to start with. Here are the available endpoints and the type of SQL they are designed to parse:

| Endpoint         | Description                                                                                             |
| ---------------- | ------------------------------------------------------------------------------------------------------- |
| `SQL`            | Parses a complete SQL statement, such as a `SELECT`, `INSERT`, `UPDATE`, or `DELETE` statement.           |
| `QUERY`          | Parses a `SELECT` query.                                                                                |
| `INSERT`         | Parses an `INSERT` statement.                                                                           |
| `CONDITION`      | Parses a conditional expression, typically found in a `WHERE` or `HAVING` clause (e.g., `col1 > 10`).     |
| `PREDICAND`      | Parses a single operand within a predicate (e.g., a column name or a literal value).                    |
| `JOIN_EXTENSION` | Parses the extension of a `JOIN` clause (e.g., `ON a.id = b.id`).                                       |
| `IN_LIST`        | Parses the list of values inside an `IN` clause (e.g., `'a', 'b', 'c'`).                                |
| `COLUMN`         | Parses a column definition or reference.                                                                |
| `VALUES`         | Parses the `VALUES` clause of an `INSERT` statement (e.g., `VALUES (1, 'a'), (2, 'b')`).                |
| `TUPLE`          | Parses a tuple of values, like `(1, 'a', 'b')`.                                                         |

## Substitution variables in DML (alpha)

Substitution variables (`<name>`) in **INSERT**, **UPDATE**, and **DELETE** statements are supported in an **alpha** state. Behavior is exercised by tests, but coverage is not yet complete and some placements you might expect from SELECT-shaped SQL may not parse or type the way you want. Treat DML substitution support as evolving.

**Tested patterns that work today:**

| Statement | Example | Variable role | Type |
|-----------|---------|---------------|------|
| INSERT | `INSERT INTO tab1 <query variable>` | entire insert source | `query` |
| UPDATE | `UPDATE employees e SET e.<target col> = <source predicand>` | SET target column (qualified) | `column` |
| UPDATE | `UPDATE employees SET score = <source predicand>` | SET expression (RHS) | `predicand` |
| UPDATE | `UPDATE employees SET score = (<source predicand>)` | SET expression (RHS, parenthesized) | `predicand` |
| UPDATE | `UPDATE employees SET score = 1 WHERE <filter>` | WHERE filter | `condition` |
| UPDATE | `UPDATE t SET a = 1 FROM <query variable> src` | FROM source (alias required) | `tuple` |
| DELETE | `DELETE FROM employees WHERE <filter>` | WHERE filter | `condition` |
| DELETE | `DELETE FROM t USING <query variable> src` | USING source (alias required) | `tuple` |
| DELETE | `DELETE FROM tab1 t RETURNING t.col1, t.col2` | RETURNING output column | (column ref) |
| UPDATE | `UPDATE employees SET score = 1 RETURNING e.emp_id, <returning predicand>` | RETURNING output column / predicand | `predicand` |

**DML statements with RETURNING:** **UPDATE** and **DELETE** (Postgres variant) support `RETURNING select_list` today. **INSERT RETURNING** is defined in the grammar (`postgres_insert`) but not yet enabled on the main `insert_expression` path (see Phase 13.2).

**Interface behavior for RETURNING:**

| Statement | Scope `interface` | Top-level `getInterface()` |
|-----------|-------------------|------------------------------|
| UPDATE without RETURNING | SET assignment targets only | SET targets |
| UPDATE with RETURNING | SET targets + RETURNING-only keys merged (`putIfAbsent`) | same merged set |
| DELETE without RETURNING | none (`interface=null`) | `[]` |
| DELETE with RETURNING (Postgres) | RETURNING output columns | RETURNING keys |
| INSERT (current) | source/VALUES output when present | scope `interface` keys |

For UPDATE with RETURNING, the update-scope `interface` map intermingles SET assignment-target columns with RETURNING output columns in a single flat namespace — there is no separate sub-map distinguishing SET targets from RETURNING values. DELETE RETURNING is RETURNING-only (no SET clause). This mixed-use / flat interface is alpha behavior.

The `SqlEventWalkerDmlUpdateInsertDeleteTruncateTests` suite also includes complex **I1–I10** / **U1–U10** scenarios with column-type substitutions inside nested SELECT sources (WHERE, GROUP BY, HAVING, QUALIFY, ORDER BY, JOIN ON, and related clauses).

For SELECT-shaped substitution typing (predicand vs condition vs query), see `parse/documents/symbol-table-resolution-consolidation-worklist.md` §13.4.1b.

## JaCoCo Before/After Helper

Use `tools/run-jacoco-with-previous.sh` when you want a before/after coverage comparison without losing the prior report.

What it does:

- Moves existing coverage artifacts from `parse/target/site/jacoco` (and `parse/target/jacoco.exec`) into a timestamped folder named `previous_jacoco_<timestamp>`.
- Runs Maven (default is `verify`) to generate a fresh JaCoCo report.
- Prints the paths to both previous and new `jacoco.xml` files.
- Copies timestamped previous/new `jacoco.xml` snapshots into git-visible history files under `parse/documents/coverage/history/`.
- Updates rolling files `jacoco_previous_latest.xml` and `jacoco_new_latest.xml` in that same folder.
- Appends run metadata to `parse/documents/coverage/history/manifest.log` so each run has an audit trail.

Why this matters:

- `parse/target/**` is ignored by git, so snapshots only kept there do not show up in your changed-file list.
- The `parse/documents/coverage/history/` snapshots are visible to git and survive across sessions, which makes before/after coverage comparisons easier to track in CI and later reviews.

Examples:

```bash
bash tools/run-jacoco-with-previous.sh
```

```bash
bash tools/run-jacoco-with-previous.sh verify -Dtest=sql.walker.SqlParseEventWalkerWithAccessObjectTest
```

## Symbol-Path Assertion Generator

Use this helper to generate generic path-based subtree assertions from parser output maps.

What it does:

- Runs SQL through the parser.
- Selects a root map/object (`symbolTable`, `tableDictionary`, `queryDictionary`, `sqlTree`, `substitutionsMap`, or `arrayOutputCollectors`).
- Walks all dotted subpaths under that root.
- Filters emitted paths with a regex you provide.
- Emits Java snippets:
	- `final String expected... = "...";`
	- `assertSymbolTreePathEquals(extractor.getSymbolTable(), "path", expected...);`

Usage:

```bash
python3 tools/generate_symbol_path_assertions.py --sql-file /tmp/query.sql
```

Example filtering to only `def_` symbol-table paths:

```bash
python3 tools/generate_symbol_path_assertions.py \
	--sql-file /tmp/query.sql \
	--root symbolTable \
	--path-regex '.*def_.*'
```

Optional arguments:

- `--assertion-method` (default: `assertSymbolTreePathEquals`)
- `--root` (default: `symbolTable`)
- `--path-regex` (default: `.*`)
- `--variable-prefix` (default: `expected`)
- `--skip-build` to skip Maven `test-compile` if classes are already built

Notes:

- The Java generator class is `cli.SymbolTreeAssertionGenerator`.
- Output is intended to be pasted into JUnit test methods where you want explicit path+golden assertions.
