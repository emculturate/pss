# pss
Project Semantic Stream is the code name for a developing capability for managing BIG VARIETY using Big Data technologies natively. "Big Variety" describes the characteristic of Big Data that there are data formats and sources that are too diverse to handle using traditional data management approaches.

This is a parser for the PSS SQL dialect.

The parser is designed to be used in a variety of contexts, including:

*   As a library for other Java applications
*   As a command-line tool for parsing SQL statements
*   As a service for parsing SQL statements

The parser is designed to be extensible, and it is possible to add new SQL statements and functions to the grammar.

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
