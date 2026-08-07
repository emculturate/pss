package sql.walker;

import org.junit.Assert;
import org.junit.Test;

import access.Snippet;
import errorhandling.ParseDiagnostic;
import sql.SQLSelectParserParser;

/**
 * Dedicated DDL golden tests covering every grammar-recognized DDL form.
 * Assert AST, symbol table, and diagnostics (DDL scopes are otherwise thin).
 *
 * Option tails follow the grammar shape {@code name then options} (verbatim through
 * {@code ;}/EOF). Snowflake/Postgres dialect flavor shows up in option text and in
 * TRUNCATE variants, not in pre-name {@code IF EXISTS} (unsupported by current grammar).
 */
public class SqlEventWalkerDdlTests extends AbstractSqlParseEventWalkerTest {

	private void assertDdlAstSymbolDiagnostics(String query, String expectedAst, String expectedSymbols) {
		assertDdlAstSymbolDiagnostics(query, expectedAst, expectedSymbols, null, null, null, -1, -1);
	}

	private void assertDdlAstSymbolDiagnostics(
			String query,
			String expectedAst,
			String expectedSymbols,
			String expectedDialectWarningCode,
			String expectedDialectMessageFragment,
			String expectedDialectTokenFragment,
			int expectedDialectLine,
			int expectedDialectCharPosition) {
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runDdlParsertest(query, parser);
		Assert.assertEquals("AST is wrong for: " + query, expectedAst, extractor.getAsTree().toString());
		Assert.assertEquals("Symbol Table is wrong for: " + query, expectedSymbols,
				extractor.getSymbolTable().toString());
		Snippet snippet = extractor.getSnippet();
		if (expectedDialectWarningCode == null) {
			Assert.assertEquals("Diagnostics are wrong for: " + query, "[]",
					snippet.getParserDiagnosticList().toString());
			return;
		}
		assertDiagnosticAtPosition(
				snippet,
				expectedDialectWarningCode,
				ParseDiagnostic.Severity.WARNING,
				expectedDialectMessageFragment,
				expectedDialectTokenFragment,
				expectedDialectLine,
				expectedDialectCharPosition);
	}

	// -------------------------------------------------------------------------
	// Verbatim options / paren content (Phase 20.1 / 20.3)
	// -------------------------------------------------------------------------

	@Test
	public void dropTableIfExistsOptionsVerbatimTest() {
		assertDdlAstSymbolDiagnostics(
				"drop table mydb.myschema.tab1 if exists",
				"{DDL={drop={type=table, name={schema=myschema, dbname=mydb, table=tab1}, options=if exists}}}",
				"{def_drop0={}}");
	}

	@Test
	public void dropTableOptionsPreserveCaseAndParensTest() {
		assertDdlAstSymbolDiagnostics(
				"DROP TABLE demo.old IF EXISTS (CASCADE)",
				"{DDL={drop={type=TABLE, name={schema=demo, table=old}, options=IF EXISTS (CASCADE)}}}",
				"{def_drop0={}}");
	}

	@Test
	public void dropTableMultilineOptionsVerbatimTest() {
		assertDdlAstSymbolDiagnostics(
				"drop table demo.old\n  if exists\n  cascade",
				"{DDL={drop={type=table, name={schema=demo, table=old}, options=if exists\n  cascade}}}",
				"{def_drop0={}}");
	}

	@Test
	public void alterTableRenameOptionsVerbatimTest() {
		assertDdlAstSymbolDiagnostics(
				"alter table mydb.myschema.tab1 rename to tab2",
				"{DDL={alter={type=table, name={schema=myschema, dbname=mydb, table=tab1}, options=rename to tab2}}}",
				"{def_alter0={}}");
	}

	@Test
	public void createSequenceOptionsWithCommaAndParenVerbatimTest() {
		assertDdlAstSymbolDiagnostics(
				"create sequence mydb.myschema.seq1 START WITH 1, INCREMENT BY 1",
				"{DDL={create={type=sequence, name={schema=myschema, dbname=mydb, table=seq1}, clauses=START WITH 1, INCREMENT BY 1}}}",
				"{def_create0={}}");
	}

	@Test
	public void createFunctionNestedParenParametersVerbatimTest() {
		assertDdlAstSymbolDiagnostics(
				"create function myschema.fn1(arg1 int, arg2 varchar(10)) returns int language sql",
				"{DDL={create={type=function, name={schema=myschema, table=fn1}, parameters=arg1 int, arg2 varchar(10), data_type={type=INT}, clauses=language sql}}}",
				"{def_create0={}}");
	}

	// -------------------------------------------------------------------------
	// DROP — every ddl_object_type (Phase 20.2 / 20.4 walked type)
	// -------------------------------------------------------------------------

	@Test
	public void dropTablePostgresCascadeTest() {
		assertDdlAstSymbolDiagnostics(
				"DROP TABLE analytics.events CASCADE",
				"{DDL={drop={type=TABLE, name={schema=analytics, table=events}, options=CASCADE}}}",
				"{def_drop0={}}");
	}

	@Test
	public void dropViewTest() {
		assertDdlAstSymbolDiagnostics(
				"DROP VIEW reporting.daily_summary",
				"{DDL={drop={type=VIEW, name={schema=reporting, table=daily_summary}}}}",
				"{def_drop0={}}");
	}

	@Test
	public void dropMaterializedViewMultiTokenTypeTest() {
		assertDdlAstSymbolDiagnostics(
				"DROP MATERIALIZED VIEW analytics.mv_daily CASCADE",
				"{DDL={drop={type=MATERIALIZED VIEW, name={schema=analytics, table=mv_daily}, options=CASCADE}}}",
				"{def_drop0={}}");
	}

	@Test
	public void dropIndexTest() {
		assertDdlAstSymbolDiagnostics(
				"DROP INDEX mydb.myschema.idx_events_ts",
				"{DDL={drop={type=INDEX, name={schema=myschema, dbname=mydb, table=idx_events_ts}}}}",
				"{def_drop0={}}");
	}

	@Test
	public void dropFunctionTest() {
		assertDdlAstSymbolDiagnostics(
				"DROP FUNCTION analytics.fn_normalize",
				"{DDL={drop={type=FUNCTION, name={schema=analytics, table=fn_normalize}}}}",
				"{def_drop0={}}");
	}

	@Test
	public void dropProcedureTest() {
		assertDdlAstSymbolDiagnostics(
				"DROP PROCEDURE mydb.myschema.pr_refresh",
				"{DDL={drop={type=PROCEDURE, name={schema=myschema, dbname=mydb, table=pr_refresh}}}}",
				"{def_drop0={}}");
	}

	@Test
	public void dropMacroTest() {
		assertDdlAstSymbolDiagnostics(
				"DROP MACRO mydb.myschema.mac_rollup",
				"{DDL={drop={type=MACRO, name={schema=myschema, dbname=mydb, table=mac_rollup}}}}",
				"{def_drop0={}}");
	}

	@Test
	public void dropSequencePostgresTest() {
		assertDdlAstSymbolDiagnostics(
				"DROP SEQUENCE demo.seq_order RESTRICT",
				"{DDL={drop={type=SEQUENCE, name={schema=demo, table=seq_order}, options=RESTRICT}}}",
				"{def_drop0={}}");
	}

	@Test
	public void dropSchemaPostgresCascadeTest() {
		assertDdlAstSymbolDiagnostics(
				"DROP SCHEMA staging CASCADE",
				"{DDL={drop={type=SCHEMA, name={table=staging}, options=CASCADE}}}",
				"{def_drop0={}}");
	}

	@Test
	public void dropDatabaseTest() {
		assertDdlAstSymbolDiagnostics(
				"DROP DATABASE analytics_raw",
				"{DDL={drop={type=DATABASE, name={table=analytics_raw}}}}",
				"{def_drop0={}}");
	}

	@Test
	public void dropRoleTest() {
		assertDdlAstSymbolDiagnostics(
				"DROP ROLE analyst",
				"{DDL={drop={type=ROLE, name={table=analyst}}}}",
				"{def_drop0={}}");
	}

	@Test
	public void dropUserTest() {
		assertDdlAstSymbolDiagnostics(
				"DROP USER bob",
				"{DDL={drop={type=USER, name={table=bob}}}}",
				"{def_drop0={}}");
	}

	@Test
	public void dropStageSnowflakeTest() {
		assertDdlAstSymbolDiagnostics(
				"DROP STAGE mydb.myschema.stg_inbound",
				"{DDL={drop={type=STAGE, name={schema=myschema, dbname=mydb, table=stg_inbound}}}}",
				"{def_drop0={}}");
	}

	@Test
	public void dropFileFormatSnowflakeMultiTokenTypeTest() {
		assertDdlAstSymbolDiagnostics(
				"DROP FILE FORMAT mydb.myschema.ff_csv",
				"{DDL={drop={type=FILE FORMAT, name={schema=myschema, dbname=mydb, table=ff_csv}}}}",
				"{def_drop0={}}");
	}

	// -------------------------------------------------------------------------
	// ALTER — representative object types + dialect-ish option tails
	// -------------------------------------------------------------------------

	@Test
	public void alterViewPostgresOwnerTest() {
		assertDdlAstSymbolDiagnostics(
				"ALTER VIEW reporting.daily_summary OWNER TO analyst",
				"{DDL={alter={type=VIEW, name={schema=reporting, table=daily_summary}, options=OWNER TO analyst}}}",
				"{def_alter0={}}");
	}

	@Test
	public void alterMaterializedViewMultiTokenTypeTest() {
		assertDdlAstSymbolDiagnostics(
				"ALTER MATERIALIZED VIEW analytics.mv_daily CLUSTER BY (col1)",
				"{DDL={alter={type=MATERIALIZED VIEW, name={schema=analytics, table=mv_daily}, options=CLUSTER BY (col1)}}}",
				"{def_alter0={}}");
	}

	@Test
	public void alterStageSnowflakeTest() {
		assertDdlAstSymbolDiagnostics(
				"ALTER STAGE mydb.myschema.stg_inbound SET URL='s3://bucket/path'",
				"{DDL={alter={type=STAGE, name={schema=myschema, dbname=mydb, table=stg_inbound}, options=SET URL='s3://bucket/path'}}}",
				"{def_alter0={}}");
	}

	@Test
	public void alterFileFormatSnowflakeTest() {
		assertDdlAstSymbolDiagnostics(
				"ALTER FILE FORMAT mydb.myschema.ff_csv SET FIELD_DELIMITER=','",
				"{DDL={alter={type=FILE FORMAT, name={schema=myschema, dbname=mydb, table=ff_csv}, options=SET FIELD_DELIMITER=','}}}",
				"{def_alter0={}}");
	}

	@Test
	public void alterSequencePostgresTest() {
		assertDdlAstSymbolDiagnostics(
				"ALTER SEQUENCE demo.seq_order RESTART WITH 1000",
				"{DDL={alter={type=SEQUENCE, name={schema=demo, table=seq_order}, options=RESTART WITH 1000}}}",
				"{def_alter0={}}");
	}

	@Test
	public void alterSchemaPostgresRenameTest() {
		assertDdlAstSymbolDiagnostics(
				"ALTER SCHEMA staging RENAME TO staging_archive",
				"{DDL={alter={type=SCHEMA, name={table=staging}, options=RENAME TO staging_archive}}}",
				"{def_alter0={}}");
	}

	@Test
	public void alterFunctionTest() {
		assertDdlAstSymbolDiagnostics(
				"ALTER FUNCTION analytics.fn_normalize OWNER TO analyst",
				"{DDL={alter={type=FUNCTION, name={schema=analytics, table=fn_normalize}, options=OWNER TO analyst}}}",
				"{def_alter0={}}");
	}

	@Test
	public void alterProcedureTest() {
		assertDdlAstSymbolDiagnostics(
				"ALTER PROCEDURE mydb.myschema.pr_refresh RENAME TO pr_refresh_v2",
				"{DDL={alter={type=PROCEDURE, name={schema=myschema, dbname=mydb, table=pr_refresh}, options=RENAME TO pr_refresh_v2}}}",
				"{def_alter0={}}");
	}

	@Test
	public void alterIndexTest() {
		assertDdlAstSymbolDiagnostics(
				"ALTER INDEX mydb.myschema.idx_events_ts RENAME TO idx_events_ts_v2",
				"{DDL={alter={type=INDEX, name={schema=myschema, dbname=mydb, table=idx_events_ts}, options=RENAME TO idx_events_ts_v2}}}",
				"{def_alter0={}}");
	}

	@Test
	public void alterDatabaseTest() {
		assertDdlAstSymbolDiagnostics(
				"ALTER DATABASE analytics_raw RENAME TO analytics",
				"{DDL={alter={type=DATABASE, name={table=analytics_raw}, options=RENAME TO analytics}}}",
				"{def_alter0={}}");
	}

	@Test
	public void alterRoleTest() {
		assertDdlAstSymbolDiagnostics(
				"ALTER ROLE analyst RENAME TO senior_analyst",
				"{DDL={alter={type=ROLE, name={table=analyst}, options=RENAME TO senior_analyst}}}",
				"{def_alter0={}}");
	}

	@Test
	public void alterUserTest() {
		assertDdlAstSymbolDiagnostics(
				"ALTER USER bob SET PASSWORD = 'secret'",
				"{DDL={alter={type=USER, name={table=bob}, options=SET PASSWORD = 'secret'}}}",
				"{def_alter0={}}");
	}

	@Test
	public void alterMacroTest() {
		assertDdlAstSymbolDiagnostics(
				"ALTER MACRO mydb.myschema.mac_rollup RENAME TO mac_rollup_v2",
				"{DDL={alter={type=MACRO, name={schema=myschema, dbname=mydb, table=mac_rollup}, options=RENAME TO mac_rollup_v2}}}",
				"{def_alter0={}}");
	}

	// -------------------------------------------------------------------------
	// CREATE — every create_* rule (+ realistic option / column tails)
	// -------------------------------------------------------------------------

	@Test
	public void createTableAsSelectTest() {
		assertDdlAstSymbolDiagnostics(
				"CREATE TABLE demo.stage AS SELECT id FROM demo.src",
				"{DDL={create={type=TABLE, table={schema=demo, table=stage}, query={select={1={column={name=id, table_ref=null}}}, from={table={schema=demo, alias=null, table=src}}}}}}",
				"{def_create1={def_query0={query_dictionary={id=[[@7,34:35='id',<391>,1:34]]}, table_dictionary={demo.src={id=[[@7,34:35='id',<391>,1:34]]}}, interface={id=[{name=id, table_ref=demo.src}]}}}}");
	}

	@Test
	public void createTableWithColumnsPostgresTest() {
		assertDdlAstSymbolDiagnostics(
				"CREATE TABLE demo.stage (id INT PRIMARY KEY, name VARCHAR(100) NOT NULL)",
				"{DDL={create={type=TABLE, table={schema=demo, table=stage}, columns=id INT PRIMARY KEY, name VARCHAR(100) NOT NULL}}}",
				"{def_create0={}}");
	}

	@Test
	public void createTableWithColumnsAndOptionsSnowflakeTest() {
		assertDdlAstSymbolDiagnostics(
				"CREATE TABLE demo.stage (id INT, payload VARIANT) CLUSTER BY (id)",
				"{DDL={create={type=TABLE, table={schema=demo, table=stage}, columns=id INT, payload VARIANT, parameters=CLUSTER BY (id)}}}",
				"{def_create0={}}");
	}

	@Test
	public void createIndexTest() {
		assertDdlAstSymbolDiagnostics(
				"CREATE INDEX mydb.myschema.idx_events_ts ON mydb.myschema.events (event_ts)",
				"{DDL={create={type=INDEX, name={schema=myschema, dbname=mydb, table=idx_events_ts}, table={schema=myschema, dbname=mydb, table=events}, columns={1={column={name=event_ts, table_ref=null}}}}}}",
				"{def_create0={unresolved_column={event_ts={column={name=event_ts, table_ref=null}, locations=[[@14,66:73='event_ts',<391>,1:66]]}}}}");
	}

	@Test
	public void createViewAsSelectTest() {
		assertDdlAstSymbolDiagnostics(
				"CREATE VIEW reporting.daily_summary AS SELECT id FROM demo.stage",
				"{DDL={create={type=VIEW, name={schema=reporting, table=daily_summary}, query={select={1={column={name=id, table_ref=null}}}, from={table={schema=demo, alias=null, table=stage}}}}}}",
				"{def_create1={def_query0={query_dictionary={id=[[@7,46:47='id',<391>,1:46]]}, table_dictionary={demo.stage={id=[[@7,46:47='id',<391>,1:46]]}}, interface={id=[{name=id, table_ref=demo.stage}]}}}}");
	}

	@Test
	public void createMaterializedViewAsSelectTest() {
		assertDdlAstSymbolDiagnostics(
				"CREATE MATERIALIZED VIEW analytics.mv_daily AS SELECT id FROM demo.stage",
				"{DDL={create={type=MATERIALIZED VIEW, name={schema=analytics, table=mv_daily}, query={select={1={column={name=id, table_ref=null}}}, from={table={schema=demo, alias=null, table=stage}}}}}}",
				"{def_create1={def_query0={query_dictionary={id=[[@8,54:55='id',<391>,1:54]]}, table_dictionary={demo.stage={id=[[@8,54:55='id',<391>,1:54]]}}, interface={id=[{name=id, table_ref=demo.stage}]}}}}");
	}

	@Test
	public void createFunctionEmptyArgsPostgresTest() {
		assertDdlAstSymbolDiagnostics(
				"CREATE FUNCTION analytics.fn_now() RETURNS TIMESTAMP LANGUAGE SQL",
				"{DDL={create={type=FUNCTION, name={schema=analytics, table=fn_now}, data_type={type=TIMESTAMP}, clauses=LANGUAGE SQL}}}",
				"{def_create0={}}");
	}

	@Test
	public void createProcedureEmptyArgsTest() {
		assertDdlAstSymbolDiagnostics(
				"CREATE PROCEDURE mydb.myschema.pr_noop() LANGUAGE SQL",
				"{DDL={create={type=PROCEDURE, name={schema=myschema, dbname=mydb, table=pr_noop}, clauses=LANGUAGE SQL}}}",
				"{def_create0={}}");
	}

	@Test
	public void createProcedureSnowflakeLanguageTest() {
		assertDdlAstSymbolDiagnostics(
				"CREATE PROCEDURE mydb.myschema.pr_refresh(days int) RETURNS VARCHAR LANGUAGE JAVASCRIPT",
				"{DDL={create={type=PROCEDURE, name={schema=myschema, dbname=mydb, table=pr_refresh}, parameters=days int, clauses=RETURNS VARCHAR LANGUAGE JAVASCRIPT}}}",
				"{def_create0={}}");
	}

	@Test
	public void createMacroAsSelectTest() {
		assertDdlAstSymbolDiagnostics(
				"CREATE MACRO mydb.myschema.mac_one(arg1 int) AS SELECT 1 AS id",
				"{DDL={create={type=MACRO, name={schema=myschema, dbname=mydb, table=mac_one}, parameters=arg1 int, query={select={1={alias=id, literal=1}}}}}}",
				"{def_create1={def_query0={query_dictionary={id=[[@15,60:61='id',<391>,1:60]]}, interface={id=[]}}}}");
	}

	@Test
	public void createMacroEmptyArgsTest() {
		assertDdlAstSymbolDiagnostics(
				"CREATE MACRO mydb.myschema.mac_pi() AS SELECT 3 AS id",
				"{DDL={create={type=MACRO, name={schema=myschema, dbname=mydb, table=mac_pi}, query={select={1={alias=id, literal=3}}}}}}",
				"{def_create1={def_query0={query_dictionary={id=[[@13,51:52='id',<391>,1:51]]}, interface={id=[]}}}}");
	}

	@Test
	public void createMacroParametersPreserveCaseVerbatimTest() {
		assertDdlAstSymbolDiagnostics(
				"CREATE MACRO mydb.myschema.mac_typed(Arg1 INT) AS SELECT 1 AS id",
				"{DDL={create={type=MACRO, name={schema=myschema, dbname=mydb, table=mac_typed}, parameters=Arg1 INT, query={select={1={alias=id, literal=1}}}}}}",
				"{def_create1={def_query0={query_dictionary={id=[[@15,62:63='id',<391>,1:62]]}, interface={id=[]}}}}");
	}

	@Test
	public void createSequencePostgresTest() {
		assertDdlAstSymbolDiagnostics(
				"CREATE SEQUENCE demo.seq_order INCREMENT BY 1 MINVALUE 1",
				"{DDL={create={type=SEQUENCE, name={schema=demo, table=seq_order}, clauses=INCREMENT BY 1 MINVALUE 1}}}",
				"{def_create0={}}");
	}

	@Test
	public void createSchemaPostgresAuthorizationTest() {
		assertDdlAstSymbolDiagnostics(
				"CREATE SCHEMA staging AUTHORIZATION bob",
				"{DDL={create={type=SCHEMA, name={table=staging}, clauses=AUTHORIZATION bob}}}",
				"{def_create0={}}");
	}

	@Test
	public void createDatabasePostgresEncodingTest() {
		assertDdlAstSymbolDiagnostics(
				"CREATE DATABASE analytics_raw WITH ENCODING 'UTF8'",
				"{DDL={create={type=DATABASE, name={table=analytics_raw}, clauses=WITH ENCODING 'UTF8'}}}",
				"{def_create0={}}");
	}

	@Test
	public void createRoleTest() {
		assertDdlAstSymbolDiagnostics(
				"CREATE ROLE analyst",
				"{DDL={create={type=ROLE, name={table=analyst}}}}",
				"{def_create0={}}");
	}

	@Test
	public void createUserPostgresLoginTest() {
		assertDdlAstSymbolDiagnostics(
				"CREATE USER bob PASSWORD 'secret' LOGIN",
				"{DDL={create={type=USER, name={table=bob}, clauses=PASSWORD 'secret' LOGIN}}}",
				"{def_create0={}}");
	}

	@Test
	public void createStageSnowflakeTest() {
		assertDdlAstSymbolDiagnostics(
				"CREATE STAGE mydb.myschema.stg_inbound URL='s3://bucket/path' FILE_FORMAT=(TYPE=CSV)",
				"{DDL={create={type=STAGE, name={schema=myschema, dbname=mydb, table=stg_inbound}, clauses=URL='s3://bucket/path' FILE_FORMAT=(TYPE=CSV)}}}",
				"{def_create0={}}");
	}

	@Test
	public void createFileFormatSnowflakeTest() {
		assertDdlAstSymbolDiagnostics(
				"CREATE FILE FORMAT mydb.myschema.ff_csv TYPE=CSV FIELD_DELIMITER=','",
				"{DDL={create={type=FILE FORMAT, name={schema=myschema, dbname=mydb, table=ff_csv}, clauses=TYPE=CSV FIELD_DELIMITER=','}}}",
				"{def_create0={}}");
	}

	// -------------------------------------------------------------------------
	// TRUNCATE — Snowflake vs Postgres grammar variants
	// -------------------------------------------------------------------------

	@Test
	public void truncateTableSnowflakeTest() {
		assertDdlAstSymbolDiagnostics(
				"TRUNCATE TABLE demo.stage",
				"{DDL={truncate={type=TABLE, name={schema=demo, table=stage}}}}",
				"{def_truncate0={}}",
				"STATEMENT_SNOWFLAKE_DIALECT_GRAMMAR",
				"truncate_snowflake_expression",
				null,
				1,
				0);
	}

	@Test
	public void truncatePostgresWithoutTableKeywordTest() {
		assertDdlAstSymbolDiagnostics(
				"TRUNCATE demo.stage",
				"{DDL={truncate={type=TABLE, name={schema=demo, table=stage}}}}",
				"{def_truncate0={}}",
				"STATEMENT_POSTGRES_DIALECT_GRAMMAR",
				"truncate_postgres_expression",
				null,
				1,
				0);
	}

	@Test
	public void truncatePostgresMultipleTargetsTest() {
		assertDdlAstSymbolDiagnostics(
				"TRUNCATE TABLE demo.stage, demo.archive",
				"{DDL={truncate={type=TABLE, list={1={schema=demo, table=stage}, 2={schema=demo, table=archive}}}}}",
				"{def_truncate0={}}",
				"STATEMENT_POSTGRES_DIALECT_GRAMMAR",
				"truncate_postgres_expression",
				null,
				1,
				0);
	}
}
