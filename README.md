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
