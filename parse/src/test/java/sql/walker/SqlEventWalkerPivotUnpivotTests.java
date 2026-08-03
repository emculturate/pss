package sql.walker;

import org.junit.Assert;
import org.junit.Test;

import access.Snippet;
import errorhandling.ParseDiagnostic;
import sql.SQLSelectParserParser;

public class SqlEventWalkerPivotUnpivotTests extends AbstractSqlParseEventWalkerTest {

	/**
	 * Matrix tags (§17.7.7-matrix): {@code subset=X | topo=S* | bucket=* | kind=derived|source | outcome=happy|unhappy}.
	 * Full heatmap: {@code parse/documents/phase-17.7.7-pivot-matrix-heatmap.md}.
	 */

	// UNPIVOT RELATIONAL OPERATOR TESTS

	@Test
	public void unpivotV0Test() {
		final String query = "SELECT id, metric_name, metric_value\n" 
			+ " FROM my_table \n "
			+ " UNPIVOT (\n" 
			+ " metric_value FOR metric_name IN (jan_sales, feb_sales, mar_sales));";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=id, table_ref=null}}, 2={column={name=metric_name, table_ref=null}}, 3={column={name=metric_value, table_ref=null}}}, from={unpivot={value={column={name=metric_value, table_ref=null}}, for={column={name=metric_name, table_ref=null}}, in={1={name=jan_sales, table_ref=null}, 2={name=feb_sales, table_ref=null}, 3={name=mar_sales, table_ref=null}}}, table={alias=null, table=my_table}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[metric_name, metric_value, id]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{my_table={jan_sales=[[@15,99:107='jan_sales',<381>,4:34]], mar_sales=[[@19,121:129='mar_sales',<381>,4:56]], id=[[@1,7:8='id',<381>,1:7]], feb_sales=[[@17,110:118='feb_sales',<381>,4:45]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={metric_value=[[@5,24:35='metric_value',<381>,1:24], [@10,66:77='metric_value',<381>,4:1]], id=[[@1,7:8='id',<381>,1:7]], metric_name=[[@3,11:21='metric_name',<381>,1:11], [@12,83:93='metric_name',<381>,4:18]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={metric_name=[[@3,11:21='metric_name',<381>,1:11], [@12,83:93='metric_name',<381>,4:18]], metric_value=[[@5,24:35='metric_value',<381>,1:24], [@10,66:77='metric_value',<381>,4:1]], id=[[@1,7:8='id',<381>,1:7]]}, table_dictionary={my_table={jan_sales=[[@15,99:107='jan_sales',<381>,4:34]], mar_sales=[[@19,121:129='mar_sales',<381>,4:56]], id=[[@1,7:8='id',<381>,1:7]], feb_sales=[[@17,110:118='feb_sales',<381>,4:45]]}}, derivation={source_columns={tuple_0=[{name=jan_sales, table_ref=my_table}, {name=feb_sales, table_ref=my_table}, {name=mar_sales, table_ref=my_table}]}, derived_columns={tuple_0={metric_value=[[@10,66:77='metric_value',<381>,4:1]], metric_name=[[@12,83:93='metric_name',<381>,4:18]]}}}, interface={metric_name=[{name=metric_name, table_ref=tuple_0}, {name=jan_sales, table_ref=my_table}, {name=feb_sales, table_ref=my_table}, {name=mar_sales, table_ref=my_table}], metric_value=[{name=jan_sales, table_ref=my_table}, {name=feb_sales, table_ref=my_table}, {name=mar_sales, table_ref=my_table}], id=[{name=id, table_ref=my_table}]}, table_alias={tuple_0=my_table}}}",
				extractor.getSymbolTable().toString());
	}

	/** Phase 17.6.8 (a): clause-site tokens for UNPIVOT derived outputs land on {@code query_dictionary}. */
	@Test
	public void unpivotV0WhereClauseDerivedQueryDictionaryV17_6_8Test() {
		final String query =
				"SELECT id, metric_name, metric_value\n"
						+ "FROM my_table\n"
						+ "UNPIVOT (\n"
						+ "  metric_value FOR metric_name IN (jan_sales, feb_sales, mar_sales))\n"
						+ "WHERE metric_name = 'jan_sales' AND metric_value > 0;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=id, table_ref=null}}, 2={column={name=metric_name, table_ref=null}}, 3={column={name=metric_value, table_ref=null}}}, from={unpivot={value={column={name=metric_value, table_ref=null}}, for={column={name=metric_name, table_ref=null}}, in={1={name=jan_sales, table_ref=null}, 2={name=feb_sales, table_ref=null}, 3={name=mar_sales, table_ref=null}}}, table={alias=null, table=my_table}}, where={and={1={condition={left={column={name=metric_name, table_ref=null}}, right={literal='jan_sales'}, operator==}}, 2={condition={left={column={name=metric_value, table_ref=null}}, right={literal=0}, operator=>}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[metric_name, metric_value, id]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{my_table={jan_sales=[[@15,96:104='jan_sales',<381>,4:35]], mar_sales=[[@19,118:126='mar_sales',<381>,4:57]], id=[[@1,7:8='id',<381>,1:7]], feb_sales=[[@17,107:115='feb_sales',<381>,4:46]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={metric_value=[[@5,24:35='metric_value',<381>,1:24], [@27,166:177='metric_value',<381>,5:36], [@10,63:74='metric_value',<381>,4:2]], id=[[@1,7:8='id',<381>,1:7]], metric_name=[[@3,11:21='metric_name',<381>,1:11], [@23,136:146='metric_name',<381>,5:6], [@12,80:90='metric_name',<381>,4:19]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={metric_name=[[@3,11:21='metric_name',<381>,1:11], [@23,136:146='metric_name',<381>,5:6], [@12,80:90='metric_name',<381>,4:19]], metric_value=[[@5,24:35='metric_value',<381>,1:24], [@27,166:177='metric_value',<381>,5:36], [@10,63:74='metric_value',<381>,4:2]], id=[[@1,7:8='id',<381>,1:7]]}, table_dictionary={my_table={jan_sales=[[@15,96:104='jan_sales',<381>,4:35]], mar_sales=[[@19,118:126='mar_sales',<381>,4:57]], id=[[@1,7:8='id',<381>,1:7]], feb_sales=[[@17,107:115='feb_sales',<381>,4:46]]}}, derivation={source_columns={tuple_0=[{name=jan_sales, table_ref=my_table}, {name=feb_sales, table_ref=my_table}, {name=mar_sales, table_ref=my_table}]}, derived_columns={tuple_0={metric_value=[[@10,63:74='metric_value',<381>,4:2]], metric_name=[[@12,80:90='metric_name',<381>,4:19]]}}}, filters=[{name=metric_name, table_ref=tuple_0}, {name=jan_sales, table_ref=my_table}, {name=feb_sales, table_ref=my_table}, {name=mar_sales, table_ref=my_table}, {name=metric_value, table_ref=tuple_0}], interface={metric_name=[{name=metric_name, table_ref=tuple_0}, {name=jan_sales, table_ref=my_table}, {name=feb_sales, table_ref=my_table}, {name=mar_sales, table_ref=my_table}], metric_value=[{name=jan_sales, table_ref=my_table}, {name=feb_sales, table_ref=my_table}, {name=mar_sales, table_ref=my_table}], id=[{name=id, table_ref=my_table}]}, table_alias={tuple_0=my_table}}}",
				extractor.getSymbolTable().toString());
	}

	/** Phase 17.6.8 (a): HAVING-site tokens for UNPIVOT derived outputs land on {@code query_dictionary}. */
	@Test
	public void unpivotV0HavingClauseDerivedQueryDictionaryV17_6_8Test() {
		final String query =
				"SELECT id, metric_name, metric_value\n"
						+ "FROM my_table\n"
						+ "UNPIVOT (\n"
						+ "  metric_value FOR metric_name IN (jan_sales, feb_sales, mar_sales))\n"
						+ "GROUP BY id, metric_name, metric_value\n"
						+ "HAVING metric_name = 'jan_sales' AND metric_value > 0;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=id, table_ref=null}}, 2={column={name=metric_name, table_ref=null}}, 3={column={name=metric_value, table_ref=null}}}, having={and={1={condition={left={column={name=metric_name, table_ref=null}}, right={literal='jan_sales'}, operator==}}, 2={condition={left={column={name=metric_value, table_ref=null}}, right={literal=0}, operator=>}}}}, from={unpivot={value={column={name=metric_value, table_ref=null}}, for={column={name=metric_name, table_ref=null}}, in={1={name=jan_sales, table_ref=null}, 2={name=feb_sales, table_ref=null}, 3={name=mar_sales, table_ref=null}}}, table={alias=null, table=my_table}}, groupby={1={column={name=id, table_ref=null}}, 2={column={name=metric_name, table_ref=null}}, 3={column={name=metric_value, table_ref=null}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[metric_name, metric_value, id]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{my_table={jan_sales=[[@15,96:104='jan_sales',<381>,4:35]], mar_sales=[[@19,118:126='mar_sales',<381>,4:57]], id=[[@1,7:8='id',<381>,1:7], [@24,139:140='id',<381>,5:9]], feb_sales=[[@17,107:115='feb_sales',<381>,4:46]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={metric_value=[[@5,24:35='metric_value',<381>,1:24], [@34,206:217='metric_value',<381>,6:37], [@28,156:167='metric_value',<381>,5:26], [@10,63:74='metric_value',<381>,4:2]], id=[[@1,7:8='id',<381>,1:7], [@24,139:140='id',<381>,5:9]], metric_name=[[@3,11:21='metric_name',<381>,1:11], [@30,176:186='metric_name',<381>,6:7], [@26,143:153='metric_name',<381>,5:13], [@12,80:90='metric_name',<381>,4:19]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={metric_name=[[@3,11:21='metric_name',<381>,1:11], [@30,176:186='metric_name',<381>,6:7], [@26,143:153='metric_name',<381>,5:13], [@12,80:90='metric_name',<381>,4:19]], metric_value=[[@5,24:35='metric_value',<381>,1:24], [@34,206:217='metric_value',<381>,6:37], [@28,156:167='metric_value',<381>,5:26], [@10,63:74='metric_value',<381>,4:2]], id=[[@1,7:8='id',<381>,1:7], [@24,139:140='id',<381>,5:9]]}, table_dictionary={my_table={jan_sales=[[@15,96:104='jan_sales',<381>,4:35]], mar_sales=[[@19,118:126='mar_sales',<381>,4:57]], id=[[@1,7:8='id',<381>,1:7], [@24,139:140='id',<381>,5:9]], feb_sales=[[@17,107:115='feb_sales',<381>,4:46]]}}, grouped_by=[{name=id, table_ref=null}, {name=metric_name, table_ref=tuple_0}, {name=jan_sales, table_ref=my_table}, {name=feb_sales, table_ref=my_table}, {name=mar_sales, table_ref=my_table}, {name=metric_value, table_ref=tuple_0}], derivation={source_columns={tuple_0=[{name=jan_sales, table_ref=my_table}, {name=feb_sales, table_ref=my_table}, {name=mar_sales, table_ref=my_table}]}, derived_columns={tuple_0={metric_value=[[@10,63:74='metric_value',<381>,4:2]], metric_name=[[@12,80:90='metric_name',<381>,4:19]]}}}, filters=[{name=metric_name, table_ref=tuple_0}, {name=jan_sales, table_ref=my_table}, {name=feb_sales, table_ref=my_table}, {name=mar_sales, table_ref=my_table}, {name=metric_value, table_ref=tuple_0}], interface={metric_name=[{name=metric_name, table_ref=tuple_0}, {name=jan_sales, table_ref=my_table}, {name=feb_sales, table_ref=my_table}, {name=mar_sales, table_ref=my_table}], metric_value=[{name=jan_sales, table_ref=my_table}, {name=feb_sales, table_ref=my_table}, {name=mar_sales, table_ref=my_table}], id=[{name=id, table_ref=my_table}]}, table_alias={tuple_0=my_table}}}",
				extractor.getSymbolTable().toString());
	}

	/** Phase 17.6.8 (a): QUALIFY-site tokens for UNPIVOT derived outputs land on {@code query_dictionary}. */
	@Test
	public void unpivotV0QualifyClauseDerivedQueryDictionaryV17_6_8Test() {
		final String query =
				"SELECT id, metric_name, metric_value\n"
						+ "FROM my_table\n"
						+ "UNPIVOT (\n"
						+ "  metric_value FOR metric_name IN (jan_sales, feb_sales, mar_sales))\n"
						+ "QUALIFY metric_name = 'jan_sales' AND metric_value > 0;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=id, table_ref=null}}, 2={column={name=metric_name, table_ref=null}}, 3={column={name=metric_value, table_ref=null}}}, from={unpivot={value={column={name=metric_value, table_ref=null}}, for={column={name=metric_name, table_ref=null}}, in={1={name=jan_sales, table_ref=null}, 2={name=feb_sales, table_ref=null}, 3={name=mar_sales, table_ref=null}}}, table={alias=null, table=my_table}}, qualify={and={1={condition={left={column={name=metric_name, table_ref=null}}, right={literal='jan_sales'}, operator==}}, 2={condition={left={column={name=metric_value, table_ref=null}}, right={literal=0}, operator=>}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[metric_name, metric_value, id]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{my_table={jan_sales=[[@15,96:104='jan_sales',<381>,4:35]], mar_sales=[[@19,118:126='mar_sales',<381>,4:57]], id=[[@1,7:8='id',<381>,1:7]], feb_sales=[[@17,107:115='feb_sales',<381>,4:46]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={metric_value=[[@5,24:35='metric_value',<381>,1:24], [@27,168:179='metric_value',<381>,5:38], [@10,63:74='metric_value',<381>,4:2]], id=[[@1,7:8='id',<381>,1:7]], metric_name=[[@3,11:21='metric_name',<381>,1:11], [@23,138:148='metric_name',<381>,5:8], [@12,80:90='metric_name',<381>,4:19]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={metric_name=[[@3,11:21='metric_name',<381>,1:11], [@23,138:148='metric_name',<381>,5:8], [@12,80:90='metric_name',<381>,4:19]], metric_value=[[@5,24:35='metric_value',<381>,1:24], [@27,168:179='metric_value',<381>,5:38], [@10,63:74='metric_value',<381>,4:2]], id=[[@1,7:8='id',<381>,1:7]]}, table_dictionary={my_table={jan_sales=[[@15,96:104='jan_sales',<381>,4:35]], mar_sales=[[@19,118:126='mar_sales',<381>,4:57]], id=[[@1,7:8='id',<381>,1:7]], feb_sales=[[@17,107:115='feb_sales',<381>,4:46]]}}, derivation={source_columns={tuple_0=[{name=jan_sales, table_ref=my_table}, {name=feb_sales, table_ref=my_table}, {name=mar_sales, table_ref=my_table}]}, derived_columns={tuple_0={metric_value=[[@10,63:74='metric_value',<381>,4:2]], metric_name=[[@12,80:90='metric_name',<381>,4:19]]}}}, filters=[{name=metric_name, table_ref=tuple_0}, {name=jan_sales, table_ref=my_table}, {name=feb_sales, table_ref=my_table}, {name=mar_sales, table_ref=my_table}, {name=metric_value, table_ref=tuple_0}], interface={metric_name=[{name=metric_name, table_ref=tuple_0}, {name=jan_sales, table_ref=my_table}, {name=feb_sales, table_ref=my_table}, {name=mar_sales, table_ref=my_table}], metric_value=[{name=jan_sales, table_ref=my_table}, {name=feb_sales, table_ref=my_table}, {name=mar_sales, table_ref=my_table}], id=[{name=id, table_ref=my_table}]}, table_alias={tuple_0=my_table}}}",
				extractor.getSymbolTable().toString());
	}

	/** Phase 17.6.8 (b): UNPIVOT derived outputs in {@code OVER} partition/order → {@code query_dictionary}.
	 * Columns referenced only in {@code OVER} are also listed in SELECT until **17.6.9** tightens window {@code query_dictionary} policy. */
	@Test
	public void unpivotV0WindowDerivedColumnsQueryDictionaryV17_6_8Test() {
		final String query =
				"SELECT metric_name, metric_value,\n"
						+ "  ROW_NUMBER() OVER (PARTITION BY metric_name ORDER BY metric_value) AS rn\n"
						+ "FROM my_table\n"
						+ "UNPIVOT (\n"
						+ "  metric_value FOR metric_name IN (jan_sales, feb_sales, mar_sales));";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("Interface is wrong", "[metric_name, metric_value, rn]",
				extractor.getInterface().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={metric_value=[[@3,20:31='metric_value',<381>,1:20], [@15,89:100='metric_value',<381>,2:55], [@23,135:146='metric_value',<381>,5:2]], metric_name=[[@1,7:17='metric_name',<381>,1:7], [@12,68:78='metric_name',<381>,2:34], [@25,152:162='metric_name',<381>,5:19]], rn=[[@18,106:107='rn',<381>,2:72]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={window_ordered_by=[{name=metric_value, table_ref=tuple_0}, {name=jan_sales, table_ref=my_table}, {name=feb_sales, table_ref=my_table}, {name=mar_sales, table_ref=my_table}], query_dictionary={metric_name=[[@1,7:17='metric_name',<381>,1:7], [@12,68:78='metric_name',<381>,2:34], [@25,152:162='metric_name',<381>,5:19]], metric_value=[[@3,20:31='metric_value',<381>,1:20], [@15,89:100='metric_value',<381>,2:55], [@23,135:146='metric_value',<381>,5:2]], rn=[[@18,106:107='rn',<381>,2:72]]}, table_dictionary={my_table={jan_sales=[[@28,168:176='jan_sales',<381>,5:35]], mar_sales=[[@32,190:198='mar_sales',<381>,5:57]], feb_sales=[[@30,179:187='feb_sales',<381>,5:46]]}}, window_partition_by=[{name=metric_name, table_ref=tuple_0}, {name=jan_sales, table_ref=my_table}, {name=feb_sales, table_ref=my_table}, {name=mar_sales, table_ref=my_table}], derivation={source_columns={tuple_0=[{name=jan_sales, table_ref=my_table}, {name=feb_sales, table_ref=my_table}, {name=mar_sales, table_ref=my_table}]}, derived_columns={tuple_0={metric_value=[[@23,135:146='metric_value',<381>,5:2]], metric_name=[[@25,152:162='metric_name',<381>,5:19]]}}}, interface={metric_name=[{name=metric_name, table_ref=tuple_0}, {name=jan_sales, table_ref=my_table}, {name=feb_sales, table_ref=my_table}, {name=mar_sales, table_ref=my_table}], metric_value=[{name=jan_sales, table_ref=my_table}, {name=feb_sales, table_ref=my_table}, {name=mar_sales, table_ref=my_table}], rn=[{name=metric_name, table_ref=tuple_0}, {name=jan_sales, table_ref=my_table}, {name=feb_sales, table_ref=my_table}, {name=mar_sales, table_ref=my_table}]}, table_alias={tuple_0=my_table}}}",
				extractor.getSymbolTable().toString());
	}

	/** Phase 17.6.8 (b): UNPIVOT source (IN-list) column in {@code OVER} → {@code query_dictionary}. See **17.6.9** for window-only {@code query_dictionary} policy. */
	@Test
	public void unpivotV0WindowSourceColumnQueryDictionaryV17_6_8Test() {
		final String query =
				"SELECT jan_sales, metric_value,\n"
						+ "  ROW_NUMBER() OVER (PARTITION BY jan_sales ORDER BY metric_value) AS rn\n"
						+ "FROM my_table\n"
						+ "UNPIVOT (\n"
						+ "  metric_value FOR metric_name IN (jan_sales, feb_sales, mar_sales));";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("Interface is wrong", "[jan_sales, metric_value, rn]",
				extractor.getInterface().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={jan_sales=[[@1,7:15='jan_sales',<381>,1:7], [@12,66:74='jan_sales',<381>,2:34]], metric_value=[[@3,18:29='metric_value',<381>,1:18], [@15,85:96='metric_value',<381>,2:53], [@23,131:142='metric_value',<381>,5:2]], rn=[[@18,102:103='rn',<381>,2:70]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={window_ordered_by=[{name=metric_value, table_ref=tuple_0}, {name=jan_sales, table_ref=my_table}, {name=feb_sales, table_ref=my_table}, {name=mar_sales, table_ref=my_table}], query_dictionary={jan_sales=[[@1,7:15='jan_sales',<381>,1:7], [@12,66:74='jan_sales',<381>,2:34]], metric_value=[[@3,18:29='metric_value',<381>,1:18], [@15,85:96='metric_value',<381>,2:53], [@23,131:142='metric_value',<381>,5:2]], rn=[[@18,102:103='rn',<381>,2:70]]}, table_dictionary={my_table={jan_sales=[[@28,164:172='jan_sales',<381>,5:35], [@1,7:15='jan_sales',<381>,1:7], [@12,66:74='jan_sales',<381>,2:34]], mar_sales=[[@32,186:194='mar_sales',<381>,5:57]], feb_sales=[[@30,175:183='feb_sales',<381>,5:46]]}}, window_partition_by=[{name=jan_sales, table_ref=null}], derivation={source_columns={tuple_0=[{name=jan_sales, table_ref=my_table}, {name=feb_sales, table_ref=my_table}, {name=mar_sales, table_ref=my_table}]}, derived_columns={tuple_0={metric_value=[[@23,131:142='metric_value',<381>,5:2]], metric_name=[[@25,148:158='metric_name',<381>,5:19]]}}}, interface={jan_sales=[{name=jan_sales, table_ref=my_table}], metric_value=[{name=jan_sales, table_ref=my_table}, {name=feb_sales, table_ref=my_table}, {name=mar_sales, table_ref=my_table}], rn=[{name=jan_sales, table_ref=my_table}, {name=feb_sales, table_ref=my_table}, {name=mar_sales, table_ref=my_table}]}, table_alias={tuple_0=my_table}}}",
				extractor.getSymbolTable().toString());
	}

	/** Phase 17.6.8 (c): INSERT … SELECT UNPIVOT + RETURNING derived outputs → {@code query_dictionary}. */
	@Test
	public void insertUnpivotDerivedReturningQueryDictionaryV17_6_8Test() {
		final String query =
				"INSERT INTO dest (metric_name, metric_value)\n"
						+ "SELECT metric_name, metric_value\n"
						+ "FROM my_table\n"
						+ "UNPIVOT (\n"
						+ "  metric_value FOR metric_name IN (jan_sales, feb_sales, mar_sales))\n"
						+ "RETURNING metric_name, metric_value;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("Interface is wrong", "[metric_name, metric_value]",
				extractor.getInterface().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={metric_name=[[@9,52:62='metric_name',<381>,2:7], [@18,121:131='metric_name',<381>,5:19]], metric_value=[[@11,65:76='metric_value',<381>,2:20], [@16,104:115='metric_value',<381>,5:2]]}, insert1={metric_name=[[@4,18:28='metric_name',<381>,1:18], [@29,181:191='metric_name',<381>,6:10], [@29,181:191='metric_name',<381>,6:10]], metric_value=[[@6,31:42='metric_value',<381>,1:31], [@31,194:205='metric_value',<381>,6:23], [@31,194:205='metric_value',<381>,6:23]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
	}

	/** Phase 17.6.8 (c): UPDATE … FROM PIVOT + RETURNING derived outputs → {@code query_dictionary}. */
	@Test
	public void updatePivotDerivedReturningQueryDictionaryV17_6_8Test() {
		final String query =
				"UPDATE dest d\n"
						+ "SET flag = 1\n"
						+ "FROM my_table\n"
						+ "PIVOT (SUM(metric_value) FOR metric_name IN ('jan_sales', 'feb_sales', 'mar_sales')) p\n"
						+ "WHERE d.jan_sales_sum = p.jan_sales_sum\n"
						+ "RETURNING p.jan_sales_sum, p.feb_sales_sum;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("Interface is wrong", "[jan_sales_sum, flag, feb_sales_sum]",
				extractor.getInterface().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{update1={feb_sales_sum=[[@42,197:209='feb_sales_sum',<381>,6:29], [@11,48:50='SUM',<141>,4:7], [@21,99:109=''feb_sales'',<389>,4:58]], jan_sales_sum=[[@38,180:192='jan_sales_sum',<381>,6:12], [@30,136:148='jan_sales_sum',<381>,5:8], [@34,154:166='jan_sales_sum',<381>,5:26], [@11,48:50='SUM',<141>,4:7], [@19,86:96=''jan_sales'',<389>,4:45]], flag=[[@4,18:21='flag',<381>,2:4]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
	}

	/** Phase 17.6.8 (c): DELETE … USING UNPIVOT + RETURNING → {@code query_dictionary}. */
	@Test
	public void deleteUnpivotDerivedReturningQueryDictionaryV17_6_8Test() {
		final String query =
				"DELETE FROM dest d\n"
						+ "USING my_table\n"
						+ "UNPIVOT (\n"
						+ "  metric_value FOR metric_name IN (jan_sales, feb_sales, mar_sales)) u\n"
						+ "WHERE d.metric_name = u.metric_name\n"
						+ "RETURNING d.metric_name, u.metric_value;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("Interface is wrong", "[metric_name, metric_value]",
				extractor.getInterface().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{delete0={metric_name=[[@32,163:173='metric_name',<381>,6:12], [@24,123:133='metric_name',<381>,5:8], [@28,139:149='metric_name',<381>,5:24], [@10,63:73='metric_name',<381>,4:19]], metric_value=[[@36,178:189='metric_value',<381>,6:27], [@8,46:57='metric_value',<381>,4:2]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
	}

	@Test
	public void unpivotV1Test() {
		final String query = "SELECT id, metric_name, jan_sales, feb_sales, mar_sales, metric_value\n" 
			+ " FROM my_table \n "
			+ " UNPIVOT (\n" 
			+ " metric_value FOR metric_name IN (jan_sales, feb_sales, mar_sales));";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=id, table_ref=null}}, 2={column={name=metric_name, table_ref=null}}, 3={column={name=jan_sales, table_ref=null}}, 4={column={name=feb_sales, table_ref=null}}, 5={column={name=mar_sales, table_ref=null}}, 6={column={name=metric_value, table_ref=null}}}, from={unpivot={value={column={name=metric_value, table_ref=null}}, for={column={name=metric_name, table_ref=null}}, in={1={name=jan_sales, table_ref=null}, 2={name=feb_sales, table_ref=null}, 3={name=mar_sales, table_ref=null}}}, table={alias=null, table=my_table}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[jan_sales, mar_sales, metric_name, metric_value, id, feb_sales]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{my_table={jan_sales=[[@21,132:140='jan_sales',<381>,4:34], [@5,24:32='jan_sales',<381>,1:24]], mar_sales=[[@25,154:162='mar_sales',<381>,4:56], [@9,46:54='mar_sales',<381>,1:46]], id=[[@1,7:8='id',<381>,1:7]], feb_sales=[[@23,143:151='feb_sales',<381>,4:45], [@7,35:43='feb_sales',<381>,1:35]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={jan_sales=[[@5,24:32='jan_sales',<381>,1:24]], mar_sales=[[@9,46:54='mar_sales',<381>,1:46]], metric_name=[[@3,11:21='metric_name',<381>,1:11], [@18,116:126='metric_name',<381>,4:18]], metric_value=[[@11,57:68='metric_value',<381>,1:57], [@16,99:110='metric_value',<381>,4:1]], id=[[@1,7:8='id',<381>,1:7]], feb_sales=[[@7,35:43='feb_sales',<381>,1:35]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={jan_sales=[[@5,24:32='jan_sales',<381>,1:24]], mar_sales=[[@9,46:54='mar_sales',<381>,1:46]], metric_name=[[@3,11:21='metric_name',<381>,1:11], [@18,116:126='metric_name',<381>,4:18]], metric_value=[[@11,57:68='metric_value',<381>,1:57], [@16,99:110='metric_value',<381>,4:1]], id=[[@1,7:8='id',<381>,1:7]], feb_sales=[[@7,35:43='feb_sales',<381>,1:35]]}, table_dictionary={my_table={jan_sales=[[@21,132:140='jan_sales',<381>,4:34], [@5,24:32='jan_sales',<381>,1:24]], mar_sales=[[@25,154:162='mar_sales',<381>,4:56], [@9,46:54='mar_sales',<381>,1:46]], id=[[@1,7:8='id',<381>,1:7]], feb_sales=[[@23,143:151='feb_sales',<381>,4:45], [@7,35:43='feb_sales',<381>,1:35]]}}, derivation={source_columns={tuple_0=[{name=jan_sales, table_ref=my_table}, {name=feb_sales, table_ref=my_table}, {name=mar_sales, table_ref=my_table}]}, derived_columns={tuple_0={metric_value=[[@16,99:110='metric_value',<381>,4:1]], metric_name=[[@18,116:126='metric_name',<381>,4:18]]}}}, interface={jan_sales=[{name=jan_sales, table_ref=my_table}], mar_sales=[{name=mar_sales, table_ref=my_table}], metric_name=[{name=metric_name, table_ref=tuple_0}, {name=jan_sales, table_ref=my_table}, {name=feb_sales, table_ref=my_table}, {name=mar_sales, table_ref=my_table}], metric_value=[{name=jan_sales, table_ref=my_table}, {name=feb_sales, table_ref=my_table}, {name=mar_sales, table_ref=my_table}], id=[{name=id, table_ref=my_table}], feb_sales=[{name=feb_sales, table_ref=my_table}]}, table_alias={tuple_0=my_table}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void unpivotPostModifierAliasV1Test() {
		final String query =
				"SELECT sales_amount, outer_up.feb_sales\n" +
				"FROM monthly_sales\n" +
				"UNPIVOT (sales_amount FOR month_name IN (jan_sales, feb_sales)) outer_up;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=sales_amount, table_ref=null}}, 2={column={name=feb_sales, table_ref=outer_up}}}, from={unpivot={value={column={name=sales_amount, table_ref=null}}, for={column={name=month_name, table_ref=null}}, in={1={name=jan_sales, table_ref=null}, 2={name=feb_sales, table_ref=null}}}, alias=outer_up, table={alias=null, table=monthly_sales}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[sales_amount, feb_sales]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{monthly_sales={jan_sales=[[@15,100:108='jan_sales',<381>,3:41]], feb_sales=[[@17,111:119='feb_sales',<381>,3:52], [@3,21:28='outer_up',<381>,1:21], [@5,30:38='feb_sales',<381>,1:30]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={feb_sales=[[@5,30:38='feb_sales',<381>,1:30]], sales_amount=[[@1,7:18='sales_amount',<381>,1:7], [@10,68:79='sales_amount',<381>,3:9]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={sales_amount=[[@1,7:18='sales_amount',<381>,1:7], [@10,68:79='sales_amount',<381>,3:9]], feb_sales=[[@5,30:38='feb_sales',<381>,1:30]]}, table_dictionary={monthly_sales={jan_sales=[[@15,100:108='jan_sales',<381>,3:41]], feb_sales=[[@17,111:119='feb_sales',<381>,3:52], [@3,21:28='outer_up',<381>,1:21], [@5,30:38='feb_sales',<381>,1:30]]}}, derivation={source_columns={outer_up=[{name=jan_sales, table_ref=monthly_sales}, {name=feb_sales, table_ref=monthly_sales}]}, derived_columns={outer_up={sales_amount=[[@10,68:79='sales_amount',<381>,3:9]], month_name=[[@12,85:94='month_name',<381>,3:26]]}}}, interface={sales_amount=[{name=jan_sales, table_ref=outer_up}, {name=feb_sales, table_ref=outer_up}], feb_sales=[{name=feb_sales, table_ref=outer_up}]}, table_alias={outer_up=monthly_sales}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void unpivotTableWithInAliasesJanFebMarV2Test() {
		final String query =
			"SELECT empid, month_name, sales_amount, units, sales_amount / units \n" +
			"FROM monthly_sales\n" +
			"UNPIVOT (sales_amount FOR month_name IN (jan_sales AS 'JAN', feb_sales AS 'FEB', mar_sales AS 'MAR'))\n" +
			"where sales_amount/units > 1.00;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=empid, table_ref=null}}, 2={column={name=month_name, table_ref=null}}, 3={column={name=sales_amount, table_ref=null}}, 4={column={name=units, table_ref=null}}, 5={calc={left={column={name=sales_amount, table_ref=null}}, right={column={name=units, table_ref=null}}, operator=/}}}, from={unpivot={value={column={name=sales_amount, table_ref=null}}, for={column={name=month_name, table_ref=null}}, in={1={name=jan_sales, label='JAN', table_ref=null}, 2={name=feb_sales, label='FEB', table_ref=null}, 3={name=mar_sales, label='MAR', table_ref=null}}}, table={alias=null, table=monthly_sales}}, where={condition={left={calc={left={column={name=sales_amount, table_ref=null}}, right={column={name=units, table_ref=null}}, operator=/}}, right={literal=1.00}, operator=>}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[empid, month_name, sales_amount, units, unnamed_0]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{monthly_sales={jan_sales=[[@21,129:137='jan_sales',<381>,3:41]], mar_sales=[[@29,169:177='mar_sales',<381>,3:81]], empid=[[@1,7:11='empid',<381>,1:7]], units=[[@7,40:44='units',<381>,1:40], [@11,62:66='units',<381>,1:62], [@37,209:213='units',<381>,4:19]], feb_sales=[[@25,149:157='feb_sales',<381>,3:61]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={empid=[[@1,7:11='empid',<381>,1:7]], month_name=[[@3,14:23='month_name',<381>,1:14], [@18,114:123='month_name',<381>,3:26]], sales_amount=[[@5,26:37='sales_amount',<381>,1:26], [@35,196:207='sales_amount',<381>,4:6], [@16,97:108='sales_amount',<381>,3:9]], units=[[@7,40:44='units',<381>,1:40], [@37,209:213='units',<381>,4:19]], unnamed_0=[[@11,62:66='units',<381>,1:62]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={empid=[[@1,7:11='empid',<381>,1:7]], month_name=[[@3,14:23='month_name',<381>,1:14], [@18,114:123='month_name',<381>,3:26]], sales_amount=[[@5,26:37='sales_amount',<381>,1:26], [@35,196:207='sales_amount',<381>,4:6], [@16,97:108='sales_amount',<381>,3:9]], units=[[@7,40:44='units',<381>,1:40], [@37,209:213='units',<381>,4:19]], unnamed_0=[[@11,62:66='units',<381>,1:62]]}, table_dictionary={monthly_sales={jan_sales=[[@21,129:137='jan_sales',<381>,3:41]], mar_sales=[[@29,169:177='mar_sales',<381>,3:81]], empid=[[@1,7:11='empid',<381>,1:7]], units=[[@7,40:44='units',<381>,1:40], [@11,62:66='units',<381>,1:62], [@37,209:213='units',<381>,4:19]], feb_sales=[[@25,149:157='feb_sales',<381>,3:61]]}}, derivation={source_columns={tuple_0=[{name=jan_sales, table_ref=monthly_sales}, {name=feb_sales, table_ref=monthly_sales}, {name=mar_sales, table_ref=monthly_sales}]}, derived_columns={tuple_0={sales_amount=[[@16,97:108='sales_amount',<381>,3:9]], month_name=[[@18,114:123='month_name',<381>,3:26]]}}}, filters=[{name=sales_amount, table_ref=tuple_0}, {name=jan_sales, table_ref=monthly_sales}, {name=feb_sales, table_ref=monthly_sales}, {name=mar_sales, table_ref=monthly_sales}, {name=units, table_ref=null}], interface={empid=[{name=empid, table_ref=monthly_sales}], month_name=[{name=month_name, table_ref=tuple_0}, {name=jan_sales, table_ref=monthly_sales}, {name=feb_sales, table_ref=monthly_sales}, {name=mar_sales, table_ref=monthly_sales}], sales_amount=[{name=jan_sales, table_ref=monthly_sales}, {name=feb_sales, table_ref=monthly_sales}, {name=mar_sales, table_ref=monthly_sales}], units=[{name=units, table_ref=monthly_sales}], unnamed_0=[{name=jan_sales, table_ref=monthly_sales}, {name=feb_sales, table_ref=monthly_sales}, {name=mar_sales, table_ref=monthly_sales}, {name=units, table_ref=monthly_sales}]}, table_alias={tuple_0=monthly_sales}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void unpivotTableWithInAliasesJanFebMarV2WithTabAliasTest() {
		final String query =
			"SELECT empid, month_name, sales_amount, units, sales_amount / units \n" +
			"FROM monthly_sales\n" +
			"UNPIVOT (sales_amount FOR month_name IN (jan_sales AS 'JAN', feb_sales AS 'FEB', mar_sales AS 'MAR')) unpvt\n" +
			"where sales_amount/units > 1.00;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=empid, table_ref=null}}, 2={column={name=month_name, table_ref=null}}, 3={column={name=sales_amount, table_ref=null}}, 4={column={name=units, table_ref=null}}, 5={calc={left={column={name=sales_amount, table_ref=null}}, right={column={name=units, table_ref=null}}, operator=/}}}, from={unpivot={value={column={name=sales_amount, table_ref=null}}, for={column={name=month_name, table_ref=null}}, in={1={name=jan_sales, label='JAN', table_ref=null}, 2={name=feb_sales, label='FEB', table_ref=null}, 3={name=mar_sales, label='MAR', table_ref=null}}}, alias=unpvt, table={alias=null, table=monthly_sales}}, where={condition={left={calc={left={column={name=sales_amount, table_ref=null}}, right={column={name=units, table_ref=null}}, operator=/}}, right={literal=1.00}, operator=>}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[empid, month_name, sales_amount, units, unnamed_0]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{monthly_sales={jan_sales=[[@21,129:137='jan_sales',<381>,3:41]], mar_sales=[[@29,169:177='mar_sales',<381>,3:81]], empid=[[@1,7:11='empid',<381>,1:7]], units=[[@7,40:44='units',<381>,1:40], [@11,62:66='units',<381>,1:62], [@38,215:219='units',<381>,4:19]], feb_sales=[[@25,149:157='feb_sales',<381>,3:61]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={empid=[[@1,7:11='empid',<381>,1:7]], month_name=[[@3,14:23='month_name',<381>,1:14], [@18,114:123='month_name',<381>,3:26]], sales_amount=[[@5,26:37='sales_amount',<381>,1:26], [@36,202:213='sales_amount',<381>,4:6], [@16,97:108='sales_amount',<381>,3:9]], units=[[@7,40:44='units',<381>,1:40], [@38,215:219='units',<381>,4:19]], unnamed_0=[[@11,62:66='units',<381>,1:62]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={empid=[[@1,7:11='empid',<381>,1:7]], month_name=[[@3,14:23='month_name',<381>,1:14], [@18,114:123='month_name',<381>,3:26]], sales_amount=[[@5,26:37='sales_amount',<381>,1:26], [@36,202:213='sales_amount',<381>,4:6], [@16,97:108='sales_amount',<381>,3:9]], units=[[@7,40:44='units',<381>,1:40], [@38,215:219='units',<381>,4:19]], unnamed_0=[[@11,62:66='units',<381>,1:62]]}, table_dictionary={monthly_sales={jan_sales=[[@21,129:137='jan_sales',<381>,3:41]], mar_sales=[[@29,169:177='mar_sales',<381>,3:81]], empid=[[@1,7:11='empid',<381>,1:7]], units=[[@7,40:44='units',<381>,1:40], [@11,62:66='units',<381>,1:62], [@38,215:219='units',<381>,4:19]], feb_sales=[[@25,149:157='feb_sales',<381>,3:61]]}}, derivation={source_columns={unpvt=[{name=jan_sales, table_ref=monthly_sales}, {name=feb_sales, table_ref=monthly_sales}, {name=mar_sales, table_ref=monthly_sales}]}, derived_columns={unpvt={sales_amount=[[@16,97:108='sales_amount',<381>,3:9]], month_name=[[@18,114:123='month_name',<381>,3:26]]}}}, filters=[{name=sales_amount, table_ref=unpvt}, {name=jan_sales, table_ref=monthly_sales}, {name=feb_sales, table_ref=monthly_sales}, {name=mar_sales, table_ref=monthly_sales}, {name=units, table_ref=null}], interface={empid=[{name=empid, table_ref=monthly_sales}], month_name=[{name=month_name, table_ref=unpvt}, {name=jan_sales, table_ref=monthly_sales}, {name=feb_sales, table_ref=monthly_sales}, {name=mar_sales, table_ref=monthly_sales}], sales_amount=[{name=jan_sales, table_ref=unpvt}, {name=feb_sales, table_ref=unpvt}, {name=mar_sales, table_ref=unpvt}], units=[{name=units, table_ref=monthly_sales}], unnamed_0=[{name=jan_sales, table_ref=unpvt}, {name=feb_sales, table_ref=unpvt}, {name=mar_sales, table_ref=unpvt}, {name=units, table_ref=monthly_sales}]}, table_alias={unpvt=monthly_sales}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void unpivotTableWithGroupByAndOrderBySalesAmountV2GroupOrderTest() {
		final String query =
			"SELECT month_name, sales_amount, units \n" +
			"FROM monthly_sales\n" +
			"UNPIVOT (sales_amount FOR month_name IN (jan_sales AS 'JAN', feb_sales AS 'FEB', mar_sales AS 'MAR'))\n" +
			"WHERE sales_amount / units > 1.00\n" +
			"GROUP BY month_name, sales_amount, units\n" +
			"ORDER BY sales_amount;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=month_name, table_ref=null}}, 2={column={name=sales_amount, table_ref=null}}, 3={column={name=units, table_ref=null}}}, orderby={1={null_order=null, predicand={column={name=sales_amount, table_ref=null}}, sort_order=ASC}}, from={unpivot={value={column={name=sales_amount, table_ref=null}}, for={column={name=month_name, table_ref=null}}, in={1={name=jan_sales, label='JAN', table_ref=null}, 2={name=feb_sales, label='FEB', table_ref=null}, 3={name=mar_sales, label='MAR', table_ref=null}}}, table={alias=null, table=monthly_sales}}, where={condition={left={calc={left={column={name=sales_amount, table_ref=null}}, right={column={name=units, table_ref=null}}, operator=/}}, right={literal=1.00}, operator=>}}, groupby={1={column={name=month_name, table_ref=null}}, 2={column={name=sales_amount, table_ref=null}}, 3={column={name=units, table_ref=null}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[month_name, sales_amount, units]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{monthly_sales={jan_sales=[[@15,100:108='jan_sales',<381>,3:41]], mar_sales=[[@23,140:148='mar_sales',<381>,3:81]], units=[[@5,33:37='units',<381>,1:33], [@31,182:186='units',<381>,4:21], [@42,230:234='units',<381>,5:35]], feb_sales=[[@19,120:128='feb_sales',<381>,3:61]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={month_name=[[@1,7:16='month_name',<381>,1:7], [@38,204:213='month_name',<381>,5:9], [@12,85:94='month_name',<381>,3:26]], sales_amount=[[@3,19:30='sales_amount',<381>,1:19], [@29,167:178='sales_amount',<381>,4:6], [@40,216:227='sales_amount',<381>,5:21], [@45,245:256='sales_amount',<381>,6:9], [@10,68:79='sales_amount',<381>,3:9]], units=[[@5,33:37='units',<381>,1:33], [@31,182:186='units',<381>,4:21], [@42,230:234='units',<381>,5:35]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={month_name=[[@1,7:16='month_name',<381>,1:7], [@38,204:213='month_name',<381>,5:9], [@12,85:94='month_name',<381>,3:26]], sales_amount=[[@3,19:30='sales_amount',<381>,1:19], [@29,167:178='sales_amount',<381>,4:6], [@40,216:227='sales_amount',<381>,5:21], [@45,245:256='sales_amount',<381>,6:9], [@10,68:79='sales_amount',<381>,3:9]], units=[[@5,33:37='units',<381>,1:33], [@31,182:186='units',<381>,4:21], [@42,230:234='units',<381>,5:35]]}, table_dictionary={monthly_sales={jan_sales=[[@15,100:108='jan_sales',<381>,3:41]], mar_sales=[[@23,140:148='mar_sales',<381>,3:81]], units=[[@5,33:37='units',<381>,1:33], [@31,182:186='units',<381>,4:21], [@42,230:234='units',<381>,5:35]], feb_sales=[[@19,120:128='feb_sales',<381>,3:61]]}}, grouped_by=[{name=month_name, table_ref=tuple_0}, {name=jan_sales, table_ref=monthly_sales}, {name=feb_sales, table_ref=monthly_sales}, {name=mar_sales, table_ref=monthly_sales}, {name=sales_amount, table_ref=tuple_0}, {name=units, table_ref=null}], derivation={source_columns={tuple_0=[{name=jan_sales, table_ref=monthly_sales}, {name=feb_sales, table_ref=monthly_sales}, {name=mar_sales, table_ref=monthly_sales}]}, derived_columns={tuple_0={sales_amount=[[@10,68:79='sales_amount',<381>,3:9]], month_name=[[@12,85:94='month_name',<381>,3:26]]}}}, ordered_by=[{name=sales_amount, table_ref=tuple_0}, {name=jan_sales, table_ref=monthly_sales}, {name=feb_sales, table_ref=monthly_sales}, {name=mar_sales, table_ref=monthly_sales}], filters=[{name=sales_amount, table_ref=tuple_0}, {name=jan_sales, table_ref=monthly_sales}, {name=feb_sales, table_ref=monthly_sales}, {name=mar_sales, table_ref=monthly_sales}, {name=units, table_ref=null}], interface={month_name=[{name=month_name, table_ref=tuple_0}, {name=jan_sales, table_ref=monthly_sales}, {name=feb_sales, table_ref=monthly_sales}, {name=mar_sales, table_ref=monthly_sales}], sales_amount=[{name=jan_sales, table_ref=monthly_sales}, {name=feb_sales, table_ref=monthly_sales}, {name=mar_sales, table_ref=monthly_sales}], units=[{name=units, table_ref=monthly_sales}]}, table_alias={tuple_0=monthly_sales}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void unpivotTableWithGroupByAndOrderBySalesAmountV2GroupOrderWithTabAliasTest() {
		final String query =
			"SELECT month_name, sales_amount, units \n" +
			"FROM monthly_sales\n" +
			"UNPIVOT (sales_amount FOR month_name IN (jan_sales AS 'JAN', feb_sales AS 'FEB', mar_sales AS 'MAR')) unpvt\n" +
			"WHERE sales_amount / units > 1.00\n" +
			"GROUP BY month_name, sales_amount, units\n" +
			"ORDER BY sales_amount;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=month_name, table_ref=null}}, 2={column={name=sales_amount, table_ref=null}}, 3={column={name=units, table_ref=null}}}, orderby={1={null_order=null, predicand={column={name=sales_amount, table_ref=null}}, sort_order=ASC}}, from={unpivot={value={column={name=sales_amount, table_ref=null}}, for={column={name=month_name, table_ref=null}}, in={1={name=jan_sales, label='JAN', table_ref=null}, 2={name=feb_sales, label='FEB', table_ref=null}, 3={name=mar_sales, label='MAR', table_ref=null}}}, alias=unpvt, table={alias=null, table=monthly_sales}}, where={condition={left={calc={left={column={name=sales_amount, table_ref=null}}, right={column={name=units, table_ref=null}}, operator=/}}, right={literal=1.00}, operator=>}}, groupby={1={column={name=month_name, table_ref=null}}, 2={column={name=sales_amount, table_ref=null}}, 3={column={name=units, table_ref=null}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[month_name, sales_amount, units]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{monthly_sales={jan_sales=[[@15,100:108='jan_sales',<381>,3:41]], mar_sales=[[@23,140:148='mar_sales',<381>,3:81]], units=[[@5,33:37='units',<381>,1:33], [@32,188:192='units',<381>,4:21], [@43,236:240='units',<381>,5:35]], feb_sales=[[@19,120:128='feb_sales',<381>,3:61]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={month_name=[[@1,7:16='month_name',<381>,1:7], [@39,210:219='month_name',<381>,5:9], [@12,85:94='month_name',<381>,3:26]], sales_amount=[[@3,19:30='sales_amount',<381>,1:19], [@30,173:184='sales_amount',<381>,4:6], [@41,222:233='sales_amount',<381>,5:21], [@46,251:262='sales_amount',<381>,6:9], [@10,68:79='sales_amount',<381>,3:9]], units=[[@5,33:37='units',<381>,1:33], [@32,188:192='units',<381>,4:21], [@43,236:240='units',<381>,5:35]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={month_name=[[@1,7:16='month_name',<381>,1:7], [@39,210:219='month_name',<381>,5:9], [@12,85:94='month_name',<381>,3:26]], sales_amount=[[@3,19:30='sales_amount',<381>,1:19], [@30,173:184='sales_amount',<381>,4:6], [@41,222:233='sales_amount',<381>,5:21], [@46,251:262='sales_amount',<381>,6:9], [@10,68:79='sales_amount',<381>,3:9]], units=[[@5,33:37='units',<381>,1:33], [@32,188:192='units',<381>,4:21], [@43,236:240='units',<381>,5:35]]}, table_dictionary={monthly_sales={jan_sales=[[@15,100:108='jan_sales',<381>,3:41]], mar_sales=[[@23,140:148='mar_sales',<381>,3:81]], units=[[@5,33:37='units',<381>,1:33], [@32,188:192='units',<381>,4:21], [@43,236:240='units',<381>,5:35]], feb_sales=[[@19,120:128='feb_sales',<381>,3:61]]}}, grouped_by=[{name=month_name, table_ref=unpvt}, {name=jan_sales, table_ref=monthly_sales}, {name=feb_sales, table_ref=monthly_sales}, {name=mar_sales, table_ref=monthly_sales}, {name=sales_amount, table_ref=unpvt}, {name=units, table_ref=null}], derivation={source_columns={unpvt=[{name=jan_sales, table_ref=monthly_sales}, {name=feb_sales, table_ref=monthly_sales}, {name=mar_sales, table_ref=monthly_sales}]}, derived_columns={unpvt={sales_amount=[[@10,68:79='sales_amount',<381>,3:9]], month_name=[[@12,85:94='month_name',<381>,3:26]]}}}, ordered_by=[{name=sales_amount, table_ref=unpvt}, {name=jan_sales, table_ref=monthly_sales}, {name=feb_sales, table_ref=monthly_sales}, {name=mar_sales, table_ref=monthly_sales}], filters=[{name=sales_amount, table_ref=unpvt}, {name=jan_sales, table_ref=monthly_sales}, {name=feb_sales, table_ref=monthly_sales}, {name=mar_sales, table_ref=monthly_sales}, {name=units, table_ref=null}], interface={month_name=[{name=month_name, table_ref=unpvt}, {name=jan_sales, table_ref=monthly_sales}, {name=feb_sales, table_ref=monthly_sales}, {name=mar_sales, table_ref=monthly_sales}], sales_amount=[{name=jan_sales, table_ref=unpvt}, {name=feb_sales, table_ref=unpvt}, {name=mar_sales, table_ref=unpvt}], units=[{name=units, table_ref=monthly_sales}]}, table_alias={unpvt=monthly_sales}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void unpivotTableWithHavingAndOrderBySalesAmountV2HavingOrderTest() {
		final String query =
			"SELECT month_name, sales_amount, units \n" +
			"FROM monthly_sales\n" +
			"UNPIVOT (sales_amount FOR month_name IN (jan_sales AS 'JAN', feb_sales AS 'FEB', mar_sales AS 'MAR'))\n" +
			"GROUP BY month_name, sales_amount, units\n" +
			"HAVING sales_amount > 100\n" +
			"ORDER BY sales_amount;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=month_name, table_ref=null}}, 2={column={name=sales_amount, table_ref=null}}, 3={column={name=units, table_ref=null}}}, having={condition={left={column={name=sales_amount, table_ref=null}}, right={literal=100}, operator=>}}, orderby={1={null_order=null, predicand={column={name=sales_amount, table_ref=null}}, sort_order=ASC}}, from={unpivot={value={column={name=sales_amount, table_ref=null}}, for={column={name=month_name, table_ref=null}}, in={1={name=jan_sales, label='JAN', table_ref=null}, 2={name=feb_sales, label='FEB', table_ref=null}, 3={name=mar_sales, label='MAR', table_ref=null}}}, table={alias=null, table=monthly_sales}}, groupby={1={column={name=month_name, table_ref=null}}, 2={column={name=sales_amount, table_ref=null}}, 3={column={name=units, table_ref=null}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[month_name, sales_amount, units]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{monthly_sales={jan_sales=[[@15,100:108='jan_sales',<381>,3:41]], mar_sales=[[@23,140:148='mar_sales',<381>,3:81]], units=[[@5,33:37='units',<381>,1:33], [@34,196:200='units',<381>,4:35]], feb_sales=[[@19,120:128='feb_sales',<381>,3:61]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={month_name=[[@1,7:16='month_name',<381>,1:7], [@30,170:179='month_name',<381>,4:9], [@12,85:94='month_name',<381>,3:26]], sales_amount=[[@3,19:30='sales_amount',<381>,1:19], [@36,209:220='sales_amount',<381>,5:7], [@32,182:193='sales_amount',<381>,4:21], [@41,237:248='sales_amount',<381>,6:9], [@10,68:79='sales_amount',<381>,3:9]], units=[[@5,33:37='units',<381>,1:33], [@34,196:200='units',<381>,4:35]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={month_name=[[@1,7:16='month_name',<381>,1:7], [@30,170:179='month_name',<381>,4:9], [@12,85:94='month_name',<381>,3:26]], sales_amount=[[@3,19:30='sales_amount',<381>,1:19], [@36,209:220='sales_amount',<381>,5:7], [@32,182:193='sales_amount',<381>,4:21], [@41,237:248='sales_amount',<381>,6:9], [@10,68:79='sales_amount',<381>,3:9]], units=[[@5,33:37='units',<381>,1:33], [@34,196:200='units',<381>,4:35]]}, table_dictionary={monthly_sales={jan_sales=[[@15,100:108='jan_sales',<381>,3:41]], mar_sales=[[@23,140:148='mar_sales',<381>,3:81]], units=[[@5,33:37='units',<381>,1:33], [@34,196:200='units',<381>,4:35]], feb_sales=[[@19,120:128='feb_sales',<381>,3:61]]}}, grouped_by=[{name=month_name, table_ref=tuple_0}, {name=jan_sales, table_ref=monthly_sales}, {name=feb_sales, table_ref=monthly_sales}, {name=mar_sales, table_ref=monthly_sales}, {name=sales_amount, table_ref=tuple_0}, {name=units, table_ref=null}], derivation={source_columns={tuple_0=[{name=jan_sales, table_ref=monthly_sales}, {name=feb_sales, table_ref=monthly_sales}, {name=mar_sales, table_ref=monthly_sales}]}, derived_columns={tuple_0={sales_amount=[[@10,68:79='sales_amount',<381>,3:9]], month_name=[[@12,85:94='month_name',<381>,3:26]]}}}, ordered_by=[{name=sales_amount, table_ref=tuple_0}, {name=jan_sales, table_ref=monthly_sales}, {name=feb_sales, table_ref=monthly_sales}, {name=mar_sales, table_ref=monthly_sales}], filters=[{name=sales_amount, table_ref=tuple_0}, {name=jan_sales, table_ref=monthly_sales}, {name=feb_sales, table_ref=monthly_sales}, {name=mar_sales, table_ref=monthly_sales}], interface={month_name=[{name=month_name, table_ref=tuple_0}, {name=jan_sales, table_ref=monthly_sales}, {name=feb_sales, table_ref=monthly_sales}, {name=mar_sales, table_ref=monthly_sales}], sales_amount=[{name=jan_sales, table_ref=monthly_sales}, {name=feb_sales, table_ref=monthly_sales}, {name=mar_sales, table_ref=monthly_sales}], units=[{name=units, table_ref=monthly_sales}]}, table_alias={tuple_0=monthly_sales}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void unpivotTableWithHavingAndOrderBySalesAmountV2HavingOrderWithTabAliasTest() {
		final String query =
			"SELECT month_name, sales_amount, units \n" +
			"FROM monthly_sales\n" +
			"UNPIVOT (sales_amount FOR month_name IN (jan_sales AS 'JAN', feb_sales AS 'FEB', mar_sales AS 'MAR')) unpvt\n" +
			"GROUP BY month_name, sales_amount, units\n" +
			"HAVING sales_amount > 100\n" +
			"ORDER BY sales_amount;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=month_name, table_ref=null}}, 2={column={name=sales_amount, table_ref=null}}, 3={column={name=units, table_ref=null}}}, having={condition={left={column={name=sales_amount, table_ref=null}}, right={literal=100}, operator=>}}, orderby={1={null_order=null, predicand={column={name=sales_amount, table_ref=null}}, sort_order=ASC}}, from={unpivot={value={column={name=sales_amount, table_ref=null}}, for={column={name=month_name, table_ref=null}}, in={1={name=jan_sales, label='JAN', table_ref=null}, 2={name=feb_sales, label='FEB', table_ref=null}, 3={name=mar_sales, label='MAR', table_ref=null}}}, alias=unpvt, table={alias=null, table=monthly_sales}}, groupby={1={column={name=month_name, table_ref=null}}, 2={column={name=sales_amount, table_ref=null}}, 3={column={name=units, table_ref=null}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[month_name, sales_amount, units]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{monthly_sales={jan_sales=[[@15,100:108='jan_sales',<381>,3:41]], mar_sales=[[@23,140:148='mar_sales',<381>,3:81]], units=[[@5,33:37='units',<381>,1:33], [@35,202:206='units',<381>,4:35]], feb_sales=[[@19,120:128='feb_sales',<381>,3:61]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={month_name=[[@1,7:16='month_name',<381>,1:7], [@31,176:185='month_name',<381>,4:9], [@12,85:94='month_name',<381>,3:26]], sales_amount=[[@3,19:30='sales_amount',<381>,1:19], [@37,215:226='sales_amount',<381>,5:7], [@33,188:199='sales_amount',<381>,4:21], [@42,243:254='sales_amount',<381>,6:9], [@10,68:79='sales_amount',<381>,3:9]], units=[[@5,33:37='units',<381>,1:33], [@35,202:206='units',<381>,4:35]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={month_name=[[@1,7:16='month_name',<381>,1:7], [@31,176:185='month_name',<381>,4:9], [@12,85:94='month_name',<381>,3:26]], sales_amount=[[@3,19:30='sales_amount',<381>,1:19], [@37,215:226='sales_amount',<381>,5:7], [@33,188:199='sales_amount',<381>,4:21], [@42,243:254='sales_amount',<381>,6:9], [@10,68:79='sales_amount',<381>,3:9]], units=[[@5,33:37='units',<381>,1:33], [@35,202:206='units',<381>,4:35]]}, table_dictionary={monthly_sales={jan_sales=[[@15,100:108='jan_sales',<381>,3:41]], mar_sales=[[@23,140:148='mar_sales',<381>,3:81]], units=[[@5,33:37='units',<381>,1:33], [@35,202:206='units',<381>,4:35]], feb_sales=[[@19,120:128='feb_sales',<381>,3:61]]}}, grouped_by=[{name=month_name, table_ref=unpvt}, {name=jan_sales, table_ref=monthly_sales}, {name=feb_sales, table_ref=monthly_sales}, {name=mar_sales, table_ref=monthly_sales}, {name=sales_amount, table_ref=unpvt}, {name=units, table_ref=null}], derivation={source_columns={unpvt=[{name=jan_sales, table_ref=monthly_sales}, {name=feb_sales, table_ref=monthly_sales}, {name=mar_sales, table_ref=monthly_sales}]}, derived_columns={unpvt={sales_amount=[[@10,68:79='sales_amount',<381>,3:9]], month_name=[[@12,85:94='month_name',<381>,3:26]]}}}, ordered_by=[{name=sales_amount, table_ref=unpvt}, {name=jan_sales, table_ref=monthly_sales}, {name=feb_sales, table_ref=monthly_sales}, {name=mar_sales, table_ref=monthly_sales}], filters=[{name=sales_amount, table_ref=unpvt}, {name=jan_sales, table_ref=monthly_sales}, {name=feb_sales, table_ref=monthly_sales}, {name=mar_sales, table_ref=monthly_sales}], interface={month_name=[{name=month_name, table_ref=unpvt}, {name=jan_sales, table_ref=monthly_sales}, {name=feb_sales, table_ref=monthly_sales}, {name=mar_sales, table_ref=monthly_sales}], sales_amount=[{name=jan_sales, table_ref=unpvt}, {name=feb_sales, table_ref=unpvt}, {name=mar_sales, table_ref=unpvt}], units=[{name=units, table_ref=monthly_sales}]}, table_alias={unpvt=monthly_sales}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void unpivotTableJoinOnWithUnqualifiedSalesAmountProbeTest() {
		final String query =
			"SELECT month_name, sales_amount, t.target_amount \n" +
			"FROM monthly_sales\n" +
			"UNPIVOT (sales_amount FOR month_name IN (jan_sales AS 'JAN', feb_sales AS 'FEB', mar_sales AS 'MAR'))\n" +
			"JOIN targets t ON sales_amount >= t.target_amount;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=month_name, table_ref=null}}, 2={column={name=sales_amount, table_ref=null}}, 3={column={name=target_amount, table_ref=t}}}, from={join={1={unpivot={value={column={name=sales_amount, table_ref=null}}, for={column={name=month_name, table_ref=null}}, in={1={name=jan_sales, label='JAN', table_ref=null}, 2={name=feb_sales, label='FEB', table_ref=null}, 3={name=mar_sales, label='MAR', table_ref=null}}}, table={alias=null, table=monthly_sales}}, 2={join=JOIN, on={condition={left={column={name=sales_amount, table_ref=null}}, right={column={name=target_amount, table_ref=t}}, operator=>=}}}, 3={table={alias=t, table=targets}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[month_name, target_amount, sales_amount]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{monthly_sales={jan_sales=[[@17,110:118='jan_sales',<381>,3:41]], mar_sales=[[@25,150:158='mar_sales',<381>,3:81]], feb_sales=[[@21,130:138='feb_sales',<381>,3:61]]}, targets={target_amount=[[@5,33:33='t',<381>,1:33], [@36,205:205='t',<381>,4:34]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
		 "{query0={month_name=[[@1,7:16='month_name',<381>,1:7], [@14,95:104='month_name',<381>,3:26]], sales_amount=[[@3,19:30='sales_amount',<381>,1:19], [@34,189:200='sales_amount',<381>,4:18], [@12,78:89='sales_amount',<381>,3:9]], target_amount=[[@7,35:47='target_amount',<381>,1:35], [@38,207:219='target_amount',<381>,4:36]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={month_name=[[@1,7:16='month_name',<381>,1:7], [@14,95:104='month_name',<381>,3:26]], target_amount=[[@7,35:47='target_amount',<381>,1:35], [@38,207:219='target_amount',<381>,4:36]], sales_amount=[[@3,19:30='sales_amount',<381>,1:19], [@34,189:200='sales_amount',<381>,4:18], [@12,78:89='sales_amount',<381>,3:9]]}, table_dictionary={monthly_sales={jan_sales=[[@17,110:118='jan_sales',<381>,3:41]], mar_sales=[[@25,150:158='mar_sales',<381>,3:81]], feb_sales=[[@21,130:138='feb_sales',<381>,3:61]]}, targets={target_amount=[[@5,33:33='t',<381>,1:33], [@36,205:205='t',<381>,4:34]]}}, derivation={source_columns={tuple_0=[{name=jan_sales, table_ref=monthly_sales}, {name=feb_sales, table_ref=monthly_sales}, {name=mar_sales, table_ref=monthly_sales}]}, derived_columns={tuple_0={sales_amount=[[@12,78:89='sales_amount',<381>,3:9]], month_name=[[@14,95:104='month_name',<381>,3:26]]}}}, filters=[{name=sales_amount, table_ref=tuple_0}, {name=jan_sales, table_ref=monthly_sales}, {name=feb_sales, table_ref=monthly_sales}, {name=mar_sales, table_ref=monthly_sales}, {name=target_amount, table_ref=t}], interface={month_name=[{name=month_name, table_ref=tuple_0}, {name=jan_sales, table_ref=monthly_sales}, {name=feb_sales, table_ref=monthly_sales}, {name=mar_sales, table_ref=monthly_sales}], target_amount=[{name=target_amount, table_ref=t}], sales_amount=[{name=jan_sales, table_ref=monthly_sales}, {name=feb_sales, table_ref=monthly_sales}, {name=mar_sales, table_ref=monthly_sales}]}, table_alias={t=targets, tuple_0=monthly_sales}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void unpivotTableJoinOnWithUnqualifiedSalesAmountProbeWithTabAliasTest() {
		final String query =
			"SELECT month_name, sales_amount, t.target_amount \n" +
			"FROM monthly_sales\n" +
			"UNPIVOT (sales_amount FOR month_name IN (jan_sales AS 'JAN', feb_sales AS 'FEB', mar_sales AS 'MAR')) unpvt\n" +
			"JOIN targets t ON sales_amount >= t.target_amount;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=month_name, table_ref=null}}, 2={column={name=sales_amount, table_ref=null}}, 3={column={name=target_amount, table_ref=t}}}, from={join={1={unpivot={value={column={name=sales_amount, table_ref=null}}, for={column={name=month_name, table_ref=null}}, in={1={name=jan_sales, label='JAN', table_ref=null}, 2={name=feb_sales, label='FEB', table_ref=null}, 3={name=mar_sales, label='MAR', table_ref=null}}}, alias=unpvt, table={alias=null, table=monthly_sales}}, 2={join=JOIN, on={condition={left={column={name=sales_amount, table_ref=null}}, right={column={name=target_amount, table_ref=t}}, operator=>=}}}, 3={table={alias=t, table=targets}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[month_name, target_amount, sales_amount]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{monthly_sales={jan_sales=[[@17,110:118='jan_sales',<381>,3:41]], mar_sales=[[@25,150:158='mar_sales',<381>,3:81]], feb_sales=[[@21,130:138='feb_sales',<381>,3:61]]}, targets={target_amount=[[@5,33:33='t',<381>,1:33], [@37,211:211='t',<381>,4:34]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
		 "{query0={month_name=[[@1,7:16='month_name',<381>,1:7], [@14,95:104='month_name',<381>,3:26]], sales_amount=[[@3,19:30='sales_amount',<381>,1:19], [@35,195:206='sales_amount',<381>,4:18], [@12,78:89='sales_amount',<381>,3:9]], target_amount=[[@7,35:47='target_amount',<381>,1:35], [@39,213:225='target_amount',<381>,4:36]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={month_name=[[@1,7:16='month_name',<381>,1:7], [@14,95:104='month_name',<381>,3:26]], target_amount=[[@7,35:47='target_amount',<381>,1:35], [@39,213:225='target_amount',<381>,4:36]], sales_amount=[[@3,19:30='sales_amount',<381>,1:19], [@35,195:206='sales_amount',<381>,4:18], [@12,78:89='sales_amount',<381>,3:9]]}, table_dictionary={monthly_sales={jan_sales=[[@17,110:118='jan_sales',<381>,3:41]], mar_sales=[[@25,150:158='mar_sales',<381>,3:81]], feb_sales=[[@21,130:138='feb_sales',<381>,3:61]]}, targets={target_amount=[[@5,33:33='t',<381>,1:33], [@37,211:211='t',<381>,4:34]]}}, derivation={source_columns={unpvt=[{name=jan_sales, table_ref=monthly_sales}, {name=feb_sales, table_ref=monthly_sales}, {name=mar_sales, table_ref=monthly_sales}]}, derived_columns={unpvt={sales_amount=[[@12,78:89='sales_amount',<381>,3:9]], month_name=[[@14,95:104='month_name',<381>,3:26]]}}}, filters=[{name=sales_amount, table_ref=unpvt}, {name=jan_sales, table_ref=monthly_sales}, {name=feb_sales, table_ref=monthly_sales}, {name=mar_sales, table_ref=monthly_sales}, {name=target_amount, table_ref=t}], interface={month_name=[{name=month_name, table_ref=unpvt}], target_amount=[{name=target_amount, table_ref=t}], sales_amount=[{name=jan_sales, table_ref=unpvt}, {name=feb_sales, table_ref=unpvt}, {name=mar_sales, table_ref=unpvt}]}, table_alias={unpvt=monthly_sales, t=targets}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void unpivotTableWithQualifySalesAmountProbeTest() {
		final String query =
			"SELECT month_name, sales_amount, units \n" +
			"FROM monthly_sales\n" +
			"UNPIVOT (sales_amount FOR month_name IN (jan_sales AS 'JAN', feb_sales AS 'FEB', mar_sales AS 'MAR'))\n" +
			"QUALIFY sales_amount > 100;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=month_name, table_ref=null}}, 2={column={name=sales_amount, table_ref=null}}, 3={column={name=units, table_ref=null}}}, from={unpivot={value={column={name=sales_amount, table_ref=null}}, for={column={name=month_name, table_ref=null}}, in={1={name=jan_sales, label='JAN', table_ref=null}, 2={name=feb_sales, label='FEB', table_ref=null}, 3={name=mar_sales, label='MAR', table_ref=null}}}, table={alias=null, table=monthly_sales}}, qualify={condition={left={column={name=sales_amount, table_ref=null}}, right={literal=100}, operator=>}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[month_name, sales_amount, units]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{monthly_sales={jan_sales=[[@15,100:108='jan_sales',<381>,3:41]], mar_sales=[[@23,140:148='mar_sales',<381>,3:81]], units=[[@5,33:37='units',<381>,1:33]], feb_sales=[[@19,120:128='feb_sales',<381>,3:61]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={month_name=[[@1,7:16='month_name',<381>,1:7], [@12,85:94='month_name',<381>,3:26]], sales_amount=[[@3,19:30='sales_amount',<381>,1:19], [@29,169:180='sales_amount',<381>,4:8], [@10,68:79='sales_amount',<381>,3:9]], units=[[@5,33:37='units',<381>,1:33]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={month_name=[[@1,7:16='month_name',<381>,1:7], [@12,85:94='month_name',<381>,3:26]], sales_amount=[[@3,19:30='sales_amount',<381>,1:19], [@29,169:180='sales_amount',<381>,4:8], [@10,68:79='sales_amount',<381>,3:9]], units=[[@5,33:37='units',<381>,1:33]]}, table_dictionary={monthly_sales={jan_sales=[[@15,100:108='jan_sales',<381>,3:41]], mar_sales=[[@23,140:148='mar_sales',<381>,3:81]], units=[[@5,33:37='units',<381>,1:33]], feb_sales=[[@19,120:128='feb_sales',<381>,3:61]]}}, derivation={source_columns={tuple_0=[{name=jan_sales, table_ref=monthly_sales}, {name=feb_sales, table_ref=monthly_sales}, {name=mar_sales, table_ref=monthly_sales}]}, derived_columns={tuple_0={sales_amount=[[@10,68:79='sales_amount',<381>,3:9]], month_name=[[@12,85:94='month_name',<381>,3:26]]}}}, filters=[{name=sales_amount, table_ref=tuple_0}, {name=jan_sales, table_ref=monthly_sales}, {name=feb_sales, table_ref=monthly_sales}, {name=mar_sales, table_ref=monthly_sales}], interface={month_name=[{name=month_name, table_ref=tuple_0}, {name=jan_sales, table_ref=monthly_sales}, {name=feb_sales, table_ref=monthly_sales}, {name=mar_sales, table_ref=monthly_sales}], sales_amount=[{name=jan_sales, table_ref=monthly_sales}, {name=feb_sales, table_ref=monthly_sales}, {name=mar_sales, table_ref=monthly_sales}], units=[{name=units, table_ref=monthly_sales}]}, table_alias={tuple_0=monthly_sales}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void unpivotTableWithQualifySalesAmountProbeWithTabAliasTest() {
		final String query =
			"SELECT month_name, sales_amount, units \n" +
			"FROM monthly_sales\n" +
			"UNPIVOT (sales_amount FOR month_name IN (jan_sales AS 'JAN', feb_sales AS 'FEB', mar_sales AS 'MAR')) unpvt\n" +
			"QUALIFY sales_amount > 100;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=month_name, table_ref=null}}, 2={column={name=sales_amount, table_ref=null}}, 3={column={name=units, table_ref=null}}}, from={unpivot={value={column={name=sales_amount, table_ref=null}}, for={column={name=month_name, table_ref=null}}, in={1={name=jan_sales, label='JAN', table_ref=null}, 2={name=feb_sales, label='FEB', table_ref=null}, 3={name=mar_sales, label='MAR', table_ref=null}}}, alias=unpvt, table={alias=null, table=monthly_sales}}, qualify={condition={left={column={name=sales_amount, table_ref=null}}, right={literal=100}, operator=>}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[month_name, sales_amount, units]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{monthly_sales={jan_sales=[[@15,100:108='jan_sales',<381>,3:41]], mar_sales=[[@23,140:148='mar_sales',<381>,3:81]], units=[[@5,33:37='units',<381>,1:33]], feb_sales=[[@19,120:128='feb_sales',<381>,3:61]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={month_name=[[@1,7:16='month_name',<381>,1:7], [@12,85:94='month_name',<381>,3:26]], sales_amount=[[@3,19:30='sales_amount',<381>,1:19], [@30,175:186='sales_amount',<381>,4:8], [@10,68:79='sales_amount',<381>,3:9]], units=[[@5,33:37='units',<381>,1:33]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={month_name=[[@1,7:16='month_name',<381>,1:7], [@12,85:94='month_name',<381>,3:26]], sales_amount=[[@3,19:30='sales_amount',<381>,1:19], [@30,175:186='sales_amount',<381>,4:8], [@10,68:79='sales_amount',<381>,3:9]], units=[[@5,33:37='units',<381>,1:33]]}, table_dictionary={monthly_sales={jan_sales=[[@15,100:108='jan_sales',<381>,3:41]], mar_sales=[[@23,140:148='mar_sales',<381>,3:81]], units=[[@5,33:37='units',<381>,1:33]], feb_sales=[[@19,120:128='feb_sales',<381>,3:61]]}}, derivation={source_columns={unpvt=[{name=jan_sales, table_ref=monthly_sales}, {name=feb_sales, table_ref=monthly_sales}, {name=mar_sales, table_ref=monthly_sales}]}, derived_columns={unpvt={sales_amount=[[@10,68:79='sales_amount',<381>,3:9]], month_name=[[@12,85:94='month_name',<381>,3:26]]}}}, filters=[{name=sales_amount, table_ref=unpvt}, {name=jan_sales, table_ref=monthly_sales}, {name=feb_sales, table_ref=monthly_sales}, {name=mar_sales, table_ref=monthly_sales}], interface={month_name=[{name=month_name, table_ref=unpvt}, {name=jan_sales, table_ref=monthly_sales}, {name=feb_sales, table_ref=monthly_sales}, {name=mar_sales, table_ref=monthly_sales}], sales_amount=[{name=jan_sales, table_ref=unpvt}, {name=feb_sales, table_ref=unpvt}, {name=mar_sales, table_ref=unpvt}], units=[{name=units, table_ref=monthly_sales}]}, table_alias={unpvt=monthly_sales}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void unpivotTableWithOrderByExpressionSalesAmountProbeTest() {
		final String query =
			"SELECT month_name, sales_amount, units \n" +
			"FROM monthly_sales\n" +
			"UNPIVOT (sales_amount FOR month_name IN (jan_sales AS 'JAN', feb_sales AS 'FEB', mar_sales AS 'MAR'))\n" +
			"ORDER BY sales_amount / units;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=month_name, table_ref=null}}, 2={column={name=sales_amount, table_ref=null}}, 3={column={name=units, table_ref=null}}}, orderby={1={null_order=null, predicand={calc={left={column={name=sales_amount, table_ref=null}}, right={column={name=units, table_ref=null}}, operator=/}}, sort_order=ASC}}, from={unpivot={value={column={name=sales_amount, table_ref=null}}, for={column={name=month_name, table_ref=null}}, in={1={name=jan_sales, label='JAN', table_ref=null}, 2={name=feb_sales, label='FEB', table_ref=null}, 3={name=mar_sales, label='MAR', table_ref=null}}}, table={alias=null, table=monthly_sales}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[month_name, sales_amount, units]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{monthly_sales={jan_sales=[[@15,100:108='jan_sales',<381>,3:41]], mar_sales=[[@23,140:148='mar_sales',<381>,3:81]], units=[[@5,33:37='units',<381>,1:33], [@32,185:189='units',<381>,4:24]], feb_sales=[[@19,120:128='feb_sales',<381>,3:61]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={month_name=[[@1,7:16='month_name',<381>,1:7], [@12,85:94='month_name',<381>,3:26]], sales_amount=[[@3,19:30='sales_amount',<381>,1:19], [@30,170:181='sales_amount',<381>,4:9], [@10,68:79='sales_amount',<381>,3:9]], units=[[@5,33:37='units',<381>,1:33], [@32,185:189='units',<381>,4:24]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={month_name=[[@1,7:16='month_name',<381>,1:7], [@12,85:94='month_name',<381>,3:26]], sales_amount=[[@3,19:30='sales_amount',<381>,1:19], [@30,170:181='sales_amount',<381>,4:9], [@10,68:79='sales_amount',<381>,3:9]], units=[[@5,33:37='units',<381>,1:33], [@32,185:189='units',<381>,4:24]]}, table_dictionary={monthly_sales={jan_sales=[[@15,100:108='jan_sales',<381>,3:41]], mar_sales=[[@23,140:148='mar_sales',<381>,3:81]], units=[[@5,33:37='units',<381>,1:33], [@32,185:189='units',<381>,4:24]], feb_sales=[[@19,120:128='feb_sales',<381>,3:61]]}}, derivation={source_columns={tuple_0=[{name=jan_sales, table_ref=monthly_sales}, {name=feb_sales, table_ref=monthly_sales}, {name=mar_sales, table_ref=monthly_sales}]}, derived_columns={tuple_0={sales_amount=[[@10,68:79='sales_amount',<381>,3:9]], month_name=[[@12,85:94='month_name',<381>,3:26]]}}}, ordered_by=[{name=sales_amount, table_ref=tuple_0}, {name=jan_sales, table_ref=monthly_sales}, {name=feb_sales, table_ref=monthly_sales}, {name=mar_sales, table_ref=monthly_sales}, {name=units, table_ref=null}], interface={month_name=[{name=month_name, table_ref=tuple_0}, {name=jan_sales, table_ref=monthly_sales}, {name=feb_sales, table_ref=monthly_sales}, {name=mar_sales, table_ref=monthly_sales}], sales_amount=[{name=jan_sales, table_ref=monthly_sales}, {name=feb_sales, table_ref=monthly_sales}, {name=mar_sales, table_ref=monthly_sales}], units=[{name=units, table_ref=monthly_sales}]}, table_alias={tuple_0=monthly_sales}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void unpivotTableWithOrderByExpressionSalesAmountProbeWithTabAliasTest() {
		final String query =
			"SELECT month_name, sales_amount, units \n" +
			"FROM monthly_sales\n" +
			"UNPIVOT (sales_amount FOR month_name IN (jan_sales AS 'JAN', feb_sales AS 'FEB', mar_sales AS 'MAR')) unpvt\n" +
			"ORDER BY sales_amount / units;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=month_name, table_ref=null}}, 2={column={name=sales_amount, table_ref=null}}, 3={column={name=units, table_ref=null}}}, orderby={1={null_order=null, predicand={calc={left={column={name=sales_amount, table_ref=null}}, right={column={name=units, table_ref=null}}, operator=/}}, sort_order=ASC}}, from={unpivot={value={column={name=sales_amount, table_ref=null}}, for={column={name=month_name, table_ref=null}}, in={1={name=jan_sales, label='JAN', table_ref=null}, 2={name=feb_sales, label='FEB', table_ref=null}, 3={name=mar_sales, label='MAR', table_ref=null}}}, alias=unpvt, table={alias=null, table=monthly_sales}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[month_name, sales_amount, units]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{monthly_sales={jan_sales=[[@15,100:108='jan_sales',<381>,3:41]], mar_sales=[[@23,140:148='mar_sales',<381>,3:81]], units=[[@5,33:37='units',<381>,1:33], [@33,191:195='units',<381>,4:24]], feb_sales=[[@19,120:128='feb_sales',<381>,3:61]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={month_name=[[@1,7:16='month_name',<381>,1:7], [@12,85:94='month_name',<381>,3:26]], sales_amount=[[@3,19:30='sales_amount',<381>,1:19], [@31,176:187='sales_amount',<381>,4:9], [@10,68:79='sales_amount',<381>,3:9]], units=[[@5,33:37='units',<381>,1:33], [@33,191:195='units',<381>,4:24]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={month_name=[[@1,7:16='month_name',<381>,1:7], [@12,85:94='month_name',<381>,3:26]], sales_amount=[[@3,19:30='sales_amount',<381>,1:19], [@31,176:187='sales_amount',<381>,4:9], [@10,68:79='sales_amount',<381>,3:9]], units=[[@5,33:37='units',<381>,1:33], [@33,191:195='units',<381>,4:24]]}, table_dictionary={monthly_sales={jan_sales=[[@15,100:108='jan_sales',<381>,3:41]], mar_sales=[[@23,140:148='mar_sales',<381>,3:81]], units=[[@5,33:37='units',<381>,1:33], [@33,191:195='units',<381>,4:24]], feb_sales=[[@19,120:128='feb_sales',<381>,3:61]]}}, derivation={source_columns={unpvt=[{name=jan_sales, table_ref=monthly_sales}, {name=feb_sales, table_ref=monthly_sales}, {name=mar_sales, table_ref=monthly_sales}]}, derived_columns={unpvt={sales_amount=[[@10,68:79='sales_amount',<381>,3:9]], month_name=[[@12,85:94='month_name',<381>,3:26]]}}}, ordered_by=[{name=sales_amount, table_ref=unpvt}, {name=jan_sales, table_ref=monthly_sales}, {name=feb_sales, table_ref=monthly_sales}, {name=mar_sales, table_ref=monthly_sales}, {name=units, table_ref=null}], interface={month_name=[{name=month_name, table_ref=unpvt}, {name=jan_sales, table_ref=monthly_sales}, {name=feb_sales, table_ref=monthly_sales}, {name=mar_sales, table_ref=monthly_sales}], sales_amount=[{name=jan_sales, table_ref=unpvt}, {name=feb_sales, table_ref=unpvt}, {name=mar_sales, table_ref=unpvt}], units=[{name=units, table_ref=monthly_sales}]}, table_alias={unpvt=monthly_sales}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void unpivotFromDerivedAdjustedColumnsV3Test() {
		final String query =
			"SELECT empid, month_name, sales_amount\n" +
			"FROM (SELECT empid, jan_sales * 1.10 AS jan_adjusted, feb_sales * 1.10 AS feb_adjusted FROM monthly_sales)\n" +
			"UNPIVOT (sales_amount FOR month_name IN (jan_adjusted AS 'JAN', feb_adjusted AS 'FEB'));";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=empid, table_ref=null}}, 2={column={name=month_name, table_ref=null}}, 3={column={name=sales_amount, table_ref=null}}}, from={unpivot={value={column={name=sales_amount, table_ref=null}}, for={column={name=month_name, table_ref=null}}, in={1={name=jan_adjusted, label='JAN', table_ref=null}, 2={name=feb_adjusted, label='FEB', table_ref=null}}}, select={1={column={name=empid, table_ref=null}}, 2={alias=jan_adjusted, calc={left={column={name=jan_sales, table_ref=null}}, right={literal=1.10}, operator=*}}, 3={alias=feb_adjusted, calc={left={column={name=feb_sales, table_ref=null}}, right={literal=1.10}, operator=*}}}, from={table={alias=null, table=monthly_sales}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[empid, month_name, sales_amount]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{monthly_sales={jan_sales=[[@11,59:67='jan_sales',<381>,2:20]], empid=[[@9,52:56='empid',<381>,2:13]], feb_sales=[[@19,93:101='feb_sales',<381>,2:54]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={empid=[[@9,52:56='empid',<381>,2:13], [@1,7:11='empid',<381>,1:7]], feb_adjusted=[[@25,113:124='feb_adjusted',<381>,2:74], [@40,210:221='feb_adjusted',<381>,3:64]], jan_adjusted=[[@17,79:90='jan_adjusted',<381>,2:40], [@36,187:198='jan_adjusted',<381>,3:41]]}, query1={empid=[[@1,7:11='empid',<381>,1:7]], month_name=[[@3,14:23='month_name',<381>,1:14], [@33,172:181='month_name',<381>,3:26]], sales_amount=[[@5,26:37='sales_amount',<381>,1:26], [@31,155:166='sales_amount',<381>,3:9]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query1={query_dictionary={empid=[[@1,7:11='empid',<381>,1:7]], month_name=[[@3,14:23='month_name',<381>,1:14], [@33,172:181='month_name',<381>,3:26]], sales_amount=[[@5,26:37='sales_amount',<381>,1:26], [@31,155:166='sales_amount',<381>,3:9]]}, def_query0={query_dictionary={empid=[[@9,52:56='empid',<381>,2:13], [@1,7:11='empid',<381>,1:7]], feb_adjusted=[[@25,113:124='feb_adjusted',<381>,2:74], [@40,210:221='feb_adjusted',<381>,3:64]], jan_adjusted=[[@17,79:90='jan_adjusted',<381>,2:40], [@36,187:198='jan_adjusted',<381>,3:41]]}, table_dictionary={monthly_sales={jan_sales=[[@11,59:67='jan_sales',<381>,2:20]], empid=[[@9,52:56='empid',<381>,2:13]], feb_sales=[[@19,93:101='feb_sales',<381>,2:54]]}}, interface={empid=[{name=empid, table_ref=monthly_sales}], feb_adjusted=[{name=feb_sales, table_ref=monthly_sales}], jan_adjusted=[{name=jan_sales, table_ref=monthly_sales}]}}, derivation={derived_columns={tuple_0={sales_amount=[[@31,155:166='sales_amount',<381>,3:9]], month_name=[[@33,172:181='month_name',<381>,3:26]]}}}, interface={empid=[{name=empid, table_ref=query0}], month_name=[{name=month_name, table_ref=null}], sales_amount=[{name=sales_amount, table_ref=null}]}, table_alias={query0=query0}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void unpivotWithTaxAndWhereV4Test() {
		final String query =
			"SELECT empid, month_name, sales_amount, sales_amount * 0.07 AS tax\n" +
			"FROM monthly_sales\n" +
			"UNPIVOT (sales_amount FOR month_name IN (jan_sales, feb_sales, mar_sales)) WHERE sales_amount > 100;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=empid, table_ref=null}}, 2={column={name=month_name, table_ref=null}}, 3={column={name=sales_amount, table_ref=null}}, 4={alias=tax, calc={left={column={name=sales_amount, table_ref=null}}, right={literal=0.07}, operator=*}}}, from={unpivot={value={column={name=sales_amount, table_ref=null}}, for={column={name=month_name, table_ref=null}}, in={1={name=jan_sales, table_ref=null}, 2={name=feb_sales, table_ref=null}, 3={name=mar_sales, table_ref=null}}}, table={alias=null, table=monthly_sales}}, where={condition={left={column={name=sales_amount, table_ref=null}}, right={literal=100}, operator=>}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[empid, month_name, sales_amount, tax]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{monthly_sales={jan_sales=[[@23,127:135='jan_sales',<381>,3:41]], mar_sales=[[@27,149:157='mar_sales',<381>,3:63]], empid=[[@1,7:11='empid',<381>,1:7]], feb_sales=[[@25,138:146='feb_sales',<381>,3:52]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={empid=[[@1,7:11='empid',<381>,1:7]], month_name=[[@3,14:23='month_name',<381>,1:14], [@20,112:121='month_name',<381>,3:26]], sales_amount=[[@5,26:37='sales_amount',<381>,1:26], [@31,167:178='sales_amount',<381>,3:81], [@18,95:106='sales_amount',<381>,3:9]], tax=[[@13,63:65='tax',<381>,1:63]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={empid=[[@1,7:11='empid',<381>,1:7]], month_name=[[@3,14:23='month_name',<381>,1:14], [@20,112:121='month_name',<381>,3:26]], sales_amount=[[@5,26:37='sales_amount',<381>,1:26], [@31,167:178='sales_amount',<381>,3:81], [@18,95:106='sales_amount',<381>,3:9]], tax=[[@13,63:65='tax',<381>,1:63]]}, table_dictionary={monthly_sales={jan_sales=[[@23,127:135='jan_sales',<381>,3:41]], mar_sales=[[@27,149:157='mar_sales',<381>,3:63]], empid=[[@1,7:11='empid',<381>,1:7]], feb_sales=[[@25,138:146='feb_sales',<381>,3:52]]}}, derivation={source_columns={tuple_0=[{name=jan_sales, table_ref=monthly_sales}, {name=feb_sales, table_ref=monthly_sales}, {name=mar_sales, table_ref=monthly_sales}]}, derived_columns={tuple_0={sales_amount=[[@18,95:106='sales_amount',<381>,3:9]], month_name=[[@20,112:121='month_name',<381>,3:26]]}}}, filters=[{name=sales_amount, table_ref=tuple_0}, {name=jan_sales, table_ref=monthly_sales}, {name=feb_sales, table_ref=monthly_sales}, {name=mar_sales, table_ref=monthly_sales}], interface={empid=[{name=empid, table_ref=monthly_sales}], month_name=[{name=month_name, table_ref=tuple_0}, {name=jan_sales, table_ref=monthly_sales}, {name=feb_sales, table_ref=monthly_sales}, {name=mar_sales, table_ref=monthly_sales}], sales_amount=[{name=jan_sales, table_ref=monthly_sales}, {name=feb_sales, table_ref=monthly_sales}, {name=mar_sales, table_ref=monthly_sales}], tax=[{name=jan_sales, table_ref=monthly_sales}, {name=feb_sales, table_ref=monthly_sales}, {name=mar_sales, table_ref=monthly_sales}]}, table_alias={tuple_0=monthly_sales}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void unpivotJoinTargetsWithFilterV5Test() {
		final String query =
			"SELECT empid, month_name, sales_amount, sales_amount * 0.07 AS tax\n" +
			"FROM monthly_sales UNPIVOT (sales_amount FOR month_name IN (jan_sales, feb_sales)) u\n" +
			"JOIN targets t ON u.month_name = t.month_name AND u.sales_amount >= t.target_amount WHERE sales_amount > 100;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertDiagnosticAtPosition(
				extractor.getSnippet(),
				"UNRESOLVED_UNQUALIFIED_COLUMNS",
				errorhandling.ParseDiagnostic.Severity.ERROR,
				null,
				"empid",
				1,
				7);
		assertDiagnosticAtPosition(
				extractor.getSnippet(),
				"AMBIGUOUS_COLUMN_REFERENCE",
				errorhandling.ParseDiagnostic.Severity.SEVERE_WARNING,
				"Ambiguous column reference 'empid'",
				"empid",
				1,
				7);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=empid, table_ref=null}}, 2={column={name=month_name, table_ref=null}}, 3={column={name=sales_amount, table_ref=null}}, 4={alias=tax, calc={left={column={name=sales_amount, table_ref=null}}, right={literal=0.07}, operator=*}}}, from={join={1={unpivot={value={column={name=sales_amount, table_ref=null}}, for={column={name=month_name, table_ref=null}}, in={1={name=jan_sales, table_ref=null}, 2={name=feb_sales, table_ref=null}}}, alias=u, table={alias=null, table=monthly_sales}}, 2={join=JOIN, on={and={1={condition={left={column={name=month_name, table_ref=u}}, right={column={name=month_name, table_ref=t}}, operator==}}, 2={condition={left={column={name=sales_amount, table_ref=u}}, right={column={name=target_amount, table_ref=t}}, operator=>=}}}}}, 3={table={alias=t, table=targets}}}}, where={condition={left={column={name=sales_amount, table_ref=null}}, right={literal=100}, operator=>}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[empid, month_name, sales_amount, tax]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{monthly_sales={jan_sales=[[@23,127:135='jan_sales',<381>,2:60]], feb_sales=[[@25,138:146='feb_sales',<381>,2:71]]}, targets={target_amount=[[@45,220:220='t',<381>,3:68]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={empid=[[@1,7:11='empid',<381>,1:7]], month_name=[[@3,14:23='month_name',<381>,1:14], [@35,172:181='month_name',<381>,3:20], [@39,187:196='month_name',<381>,3:35], [@20,112:121='month_name',<381>,2:45]], sales_amount=[[@5,26:37='sales_amount',<381>,1:26], [@43,204:215='sales_amount',<381>,3:52], [@49,242:253='sales_amount',<381>,3:90], [@18,95:106='sales_amount',<381>,2:28]], tax=[[@13,63:65='tax',<381>,1:63]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={empid=[[@1,7:11='empid',<381>,1:7]], month_name=[[@3,14:23='month_name',<381>,1:14], [@35,172:181='month_name',<381>,3:20], [@39,187:196='month_name',<381>,3:35], [@20,112:121='month_name',<381>,2:45]], sales_amount=[[@5,26:37='sales_amount',<381>,1:26], [@43,204:215='sales_amount',<381>,3:52], [@49,242:253='sales_amount',<381>,3:90], [@18,95:106='sales_amount',<381>,2:28]], tax=[[@13,63:65='tax',<381>,1:63]]}, table_dictionary={monthly_sales={jan_sales=[[@23,127:135='jan_sales',<381>,2:60]], feb_sales=[[@25,138:146='feb_sales',<381>,2:71]]}, targets={target_amount=[[@45,220:220='t',<381>,3:68]]}}, derivation={source_columns={u=[{name=jan_sales, table_ref=monthly_sales}, {name=feb_sales, table_ref=monthly_sales}]}, derived_columns={u={sales_amount=[[@18,95:106='sales_amount',<381>,2:28]], month_name=[[@20,112:121='month_name',<381>,2:45]]}}}, filters=[{name=month_name, table_ref=u}, {name=jan_sales, table_ref=monthly_sales}, {name=feb_sales, table_ref=monthly_sales}, {name=sales_amount, table_ref=u}, {name=target_amount, table_ref=t}], interface={empid=[{name=empid, table_ref=null}], month_name=[{name=month_name, table_ref=u}], sales_amount=[{name=jan_sales, table_ref=u}, {name=feb_sales, table_ref=u}], tax=[{name=jan_sales, table_ref=u}, {name=feb_sales, table_ref=u}]}, table_alias={t=targets, u=monthly_sales}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void unpivotKeepingOriginalMonthColumnsV6Test() {
		final String query =
			"SELECT empid, jan_sales, feb_sales, month_name, sales_amount\n" +
			"FROM monthly_sales\n" +
			"UNPIVOT (sales_amount FOR month_name IN (jan_sales, feb_sales, mar_sales));";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=empid, table_ref=null}}, 2={column={name=jan_sales, table_ref=null}}, 3={column={name=feb_sales, table_ref=null}}, 4={column={name=month_name, table_ref=null}}, 5={column={name=sales_amount, table_ref=null}}}, from={unpivot={value={column={name=sales_amount, table_ref=null}}, for={column={name=month_name, table_ref=null}}, in={1={name=jan_sales, table_ref=null}, 2={name=feb_sales, table_ref=null}, 3={name=mar_sales, table_ref=null}}}, table={alias=null, table=monthly_sales}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[jan_sales, empid, month_name, sales_amount, feb_sales]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{monthly_sales={jan_sales=[[@19,121:129='jan_sales',<381>,3:41], [@3,14:22='jan_sales',<381>,1:14]], mar_sales=[[@23,143:151='mar_sales',<381>,3:63]], empid=[[@1,7:11='empid',<381>,1:7]], feb_sales=[[@21,132:140='feb_sales',<381>,3:52], [@5,25:33='feb_sales',<381>,1:25]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={jan_sales=[[@3,14:22='jan_sales',<381>,1:14]], empid=[[@1,7:11='empid',<381>,1:7]], month_name=[[@7,36:45='month_name',<381>,1:36], [@16,106:115='month_name',<381>,3:26]], sales_amount=[[@9,48:59='sales_amount',<381>,1:48], [@14,89:100='sales_amount',<381>,3:9]], feb_sales=[[@5,25:33='feb_sales',<381>,1:25]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={jan_sales=[[@3,14:22='jan_sales',<381>,1:14]], empid=[[@1,7:11='empid',<381>,1:7]], month_name=[[@7,36:45='month_name',<381>,1:36], [@16,106:115='month_name',<381>,3:26]], sales_amount=[[@9,48:59='sales_amount',<381>,1:48], [@14,89:100='sales_amount',<381>,3:9]], feb_sales=[[@5,25:33='feb_sales',<381>,1:25]]}, table_dictionary={monthly_sales={jan_sales=[[@19,121:129='jan_sales',<381>,3:41], [@3,14:22='jan_sales',<381>,1:14]], mar_sales=[[@23,143:151='mar_sales',<381>,3:63]], empid=[[@1,7:11='empid',<381>,1:7]], feb_sales=[[@21,132:140='feb_sales',<381>,3:52], [@5,25:33='feb_sales',<381>,1:25]]}}, derivation={source_columns={tuple_0=[{name=jan_sales, table_ref=monthly_sales}, {name=feb_sales, table_ref=monthly_sales}, {name=mar_sales, table_ref=monthly_sales}]}, derived_columns={tuple_0={sales_amount=[[@14,89:100='sales_amount',<381>,3:9]], month_name=[[@16,106:115='month_name',<381>,3:26]]}}}, interface={jan_sales=[{name=jan_sales, table_ref=monthly_sales}], empid=[{name=empid, table_ref=monthly_sales}], month_name=[{name=month_name, table_ref=tuple_0}, {name=jan_sales, table_ref=monthly_sales}, {name=feb_sales, table_ref=monthly_sales}, {name=mar_sales, table_ref=monthly_sales}], sales_amount=[{name=jan_sales, table_ref=monthly_sales}, {name=feb_sales, table_ref=monthly_sales}, {name=mar_sales, table_ref=monthly_sales}], feb_sales=[{name=feb_sales, table_ref=monthly_sales}]}, table_alias={tuple_0=monthly_sales}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void unpivotBasicMonthSalesV7Test() {
		final String query =
			"SELECT empid, month_name, sales_amount\n" +
			"FROM monthly_sales\n" +
			"UNPIVOT (sales_amount FOR month_name IN (jan_sales, feb_sales, mar_sales));";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=empid, table_ref=null}}, 2={column={name=month_name, table_ref=null}}, 3={column={name=sales_amount, table_ref=null}}}, from={unpivot={value={column={name=sales_amount, table_ref=null}}, for={column={name=month_name, table_ref=null}}, in={1={name=jan_sales, table_ref=null}, 2={name=feb_sales, table_ref=null}, 3={name=mar_sales, table_ref=null}}}, table={alias=null, table=monthly_sales}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[empid, month_name, sales_amount]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{monthly_sales={jan_sales=[[@15,99:107='jan_sales',<381>,3:41]], mar_sales=[[@19,121:129='mar_sales',<381>,3:63]], empid=[[@1,7:11='empid',<381>,1:7]], feb_sales=[[@17,110:118='feb_sales',<381>,3:52]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={empid=[[@1,7:11='empid',<381>,1:7]], month_name=[[@3,14:23='month_name',<381>,1:14], [@12,84:93='month_name',<381>,3:26]], sales_amount=[[@5,26:37='sales_amount',<381>,1:26], [@10,67:78='sales_amount',<381>,3:9]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={empid=[[@1,7:11='empid',<381>,1:7]], month_name=[[@3,14:23='month_name',<381>,1:14], [@12,84:93='month_name',<381>,3:26]], sales_amount=[[@5,26:37='sales_amount',<381>,1:26], [@10,67:78='sales_amount',<381>,3:9]]}, table_dictionary={monthly_sales={jan_sales=[[@15,99:107='jan_sales',<381>,3:41]], mar_sales=[[@19,121:129='mar_sales',<381>,3:63]], empid=[[@1,7:11='empid',<381>,1:7]], feb_sales=[[@17,110:118='feb_sales',<381>,3:52]]}}, derivation={source_columns={tuple_0=[{name=jan_sales, table_ref=monthly_sales}, {name=feb_sales, table_ref=monthly_sales}, {name=mar_sales, table_ref=monthly_sales}]}, derived_columns={tuple_0={sales_amount=[[@10,67:78='sales_amount',<381>,3:9]], month_name=[[@12,84:93='month_name',<381>,3:26]]}}}, interface={empid=[{name=empid, table_ref=monthly_sales}], month_name=[{name=month_name, table_ref=tuple_0}, {name=jan_sales, table_ref=monthly_sales}, {name=feb_sales, table_ref=monthly_sales}, {name=mar_sales, table_ref=monthly_sales}], sales_amount=[{name=jan_sales, table_ref=monthly_sales}, {name=feb_sales, table_ref=monthly_sales}, {name=mar_sales, table_ref=monthly_sales}]}, table_alias={tuple_0=monthly_sales}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void unpivotBasicMonthSalesV8Test() {
		final String query =
			"SELECT empid, month_name, sales_amount, t2.a1, t2.a2\n" +
			"FROM monthly_sales\n" +
			"UNPIVOT (sales_amount FOR month_name IN (jan_sales, feb_sales, mar_sales))\n" +
			"JOIN metrics_table t2 ON month_name = t2.metric_label;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertDiagnosticAtPosition(
				extractor.getSnippet(),
				"UNRESOLVED_UNQUALIFIED_COLUMNS",
				errorhandling.ParseDiagnostic.Severity.ERROR,
				null,
				"empid",
				1,
				7);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=empid, table_ref=null}}, 2={column={name=month_name, table_ref=null}}, 3={column={name=sales_amount, table_ref=null}}, 4={column={name=a1, table_ref=t2}}, 5={column={name=a2, table_ref=t2}}}, from={join={1={unpivot={value={column={name=sales_amount, table_ref=null}}, for={column={name=month_name, table_ref=null}}, in={1={name=jan_sales, table_ref=null}, 2={name=feb_sales, table_ref=null}, 3={name=mar_sales, table_ref=null}}}, table={alias=null, table=monthly_sales}}, 2={join=JOIN, on={condition={left={column={name=month_name, table_ref=null}}, right={column={name=metric_label, table_ref=t2}}, operator==}}}, 3={table={alias=t2, table=metrics_table}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[a1, empid, month_name, a2, sales_amount]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{monthly_sales={jan_sales=[[@23,113:121='jan_sales',<381>,3:41]], mar_sales=[[@27,135:143='mar_sales',<381>,3:63]], feb_sales=[[@25,124:132='feb_sales',<381>,3:52]]}, metrics_table={a1=[[@7,40:41='t2',<381>,1:40]], a2=[[@11,47:48='t2',<381>,1:47]], metric_label=[[@36,185:186='t2',<381>,4:38]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={a1=[[@9,43:44='a1',<381>,1:43]], empid=[[@1,7:11='empid',<381>,1:7]], month_name=[[@3,14:23='month_name',<381>,1:14], [@34,172:181='month_name',<381>,4:25], [@20,98:107='month_name',<381>,3:26]], a2=[[@13,50:51='a2',<381>,1:50]], sales_amount=[[@5,26:37='sales_amount',<381>,1:26], [@18,81:92='sales_amount',<381>,3:9]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={a1=[[@9,43:44='a1',<381>,1:43]], empid=[[@1,7:11='empid',<381>,1:7]], month_name=[[@3,14:23='month_name',<381>,1:14], [@34,172:181='month_name',<381>,4:25], [@20,98:107='month_name',<381>,3:26]], a2=[[@13,50:51='a2',<381>,1:50]], sales_amount=[[@5,26:37='sales_amount',<381>,1:26], [@18,81:92='sales_amount',<381>,3:9]]}, table_dictionary={monthly_sales={jan_sales=[[@23,113:121='jan_sales',<381>,3:41]], mar_sales=[[@27,135:143='mar_sales',<381>,3:63]], feb_sales=[[@25,124:132='feb_sales',<381>,3:52]]}, metrics_table={a1=[[@7,40:41='t2',<381>,1:40]], a2=[[@11,47:48='t2',<381>,1:47]], metric_label=[[@36,185:186='t2',<381>,4:38]]}}, derivation={source_columns={tuple_0=[{name=jan_sales, table_ref=monthly_sales}, {name=feb_sales, table_ref=monthly_sales}, {name=mar_sales, table_ref=monthly_sales}]}, derived_columns={tuple_0={sales_amount=[[@18,81:92='sales_amount',<381>,3:9]], month_name=[[@20,98:107='month_name',<381>,3:26]]}}}, filters=[{name=month_name, table_ref=tuple_0}, {name=jan_sales, table_ref=monthly_sales}, {name=feb_sales, table_ref=monthly_sales}, {name=mar_sales, table_ref=monthly_sales}, {name=metric_label, table_ref=t2}], interface={a1=[{name=a1, table_ref=t2}], empid=[{name=empid, table_ref=null}], month_name=[{name=month_name, table_ref=tuple_0}, {name=jan_sales, table_ref=monthly_sales}, {name=feb_sales, table_ref=monthly_sales}, {name=mar_sales, table_ref=monthly_sales}], a2=[{name=a2, table_ref=t2}], sales_amount=[{name=jan_sales, table_ref=monthly_sales}, {name=feb_sales, table_ref=monthly_sales}, {name=mar_sales, table_ref=monthly_sales}]}, table_alias={t2=metrics_table, tuple_0=monthly_sales}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void unpivotBasicMonthSalesV9Test() {
		final String query =
			"SELECT empid, month_name, sales_amount, t2.a1, t2.a2\n" +
			"FROM monthly_sales\n" +
			"UNPIVOT (sales_amount FOR month_name IN (jan_sales, feb_sales, mar_sales)) up\n" +
			"JOIN metrics_table t2 ON up.month_name = t2.metric_label;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertDiagnosticAtPosition(
				extractor.getSnippet(),
				"UNRESOLVED_UNQUALIFIED_COLUMNS",
				errorhandling.ParseDiagnostic.Severity.ERROR,
				null,
				"empid",
				1,
				7);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=empid, table_ref=null}}, 2={column={name=month_name, table_ref=null}}, 3={column={name=sales_amount, table_ref=null}}, 4={column={name=a1, table_ref=t2}}, 5={column={name=a2, table_ref=t2}}}, from={join={1={unpivot={value={column={name=sales_amount, table_ref=null}}, for={column={name=month_name, table_ref=null}}, in={1={name=jan_sales, table_ref=null}, 2={name=feb_sales, table_ref=null}, 3={name=mar_sales, table_ref=null}}}, alias=up, table={alias=null, table=monthly_sales}}, 2={join=JOIN, on={condition={left={column={name=month_name, table_ref=up}}, right={column={name=metric_label, table_ref=t2}}, operator==}}}, 3={table={alias=t2, table=metrics_table}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[a1, empid, month_name, a2, sales_amount]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{monthly_sales={jan_sales=[[@23,113:121='jan_sales',<381>,3:41]], mar_sales=[[@27,135:143='mar_sales',<381>,3:63]], feb_sales=[[@25,124:132='feb_sales',<381>,3:52]]}, metrics_table={a1=[[@7,40:41='t2',<381>,1:40]], a2=[[@11,47:48='t2',<381>,1:47]], metric_label=[[@39,191:192='t2',<381>,4:41]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={a1=[[@9,43:44='a1',<381>,1:43]], empid=[[@1,7:11='empid',<381>,1:7]], month_name=[[@3,14:23='month_name',<381>,1:14], [@37,178:187='month_name',<381>,4:28], [@20,98:107='month_name',<381>,3:26]], a2=[[@13,50:51='a2',<381>,1:50]], sales_amount=[[@5,26:37='sales_amount',<381>,1:26], [@18,81:92='sales_amount',<381>,3:9]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={a1=[[@9,43:44='a1',<381>,1:43]], empid=[[@1,7:11='empid',<381>,1:7]], month_name=[[@3,14:23='month_name',<381>,1:14], [@37,178:187='month_name',<381>,4:28], [@20,98:107='month_name',<381>,3:26]], a2=[[@13,50:51='a2',<381>,1:50]], sales_amount=[[@5,26:37='sales_amount',<381>,1:26], [@18,81:92='sales_amount',<381>,3:9]]}, table_dictionary={monthly_sales={jan_sales=[[@23,113:121='jan_sales',<381>,3:41]], mar_sales=[[@27,135:143='mar_sales',<381>,3:63]], feb_sales=[[@25,124:132='feb_sales',<381>,3:52]]}, metrics_table={a1=[[@7,40:41='t2',<381>,1:40]], a2=[[@11,47:48='t2',<381>,1:47]], metric_label=[[@39,191:192='t2',<381>,4:41]]}}, derivation={source_columns={up=[{name=jan_sales, table_ref=monthly_sales}, {name=feb_sales, table_ref=monthly_sales}, {name=mar_sales, table_ref=monthly_sales}]}, derived_columns={up={sales_amount=[[@18,81:92='sales_amount',<381>,3:9]], month_name=[[@20,98:107='month_name',<381>,3:26]]}}}, filters=[{name=month_name, table_ref=up}, {name=metric_label, table_ref=t2}], interface={a1=[{name=a1, table_ref=t2}], empid=[{name=empid, table_ref=null}], month_name=[{name=month_name, table_ref=up}], a2=[{name=a2, table_ref=t2}], sales_amount=[{name=jan_sales, table_ref=up}, {name=feb_sales, table_ref=up}, {name=mar_sales, table_ref=up}]}, table_alias={up=monthly_sales, t2=metrics_table}}}",
				extractor.getSymbolTable().toString());
	}

	// PIVOT RELATIONAL OPERATOR TESTS

	@Test
	public void pivotInIdentifierDirectTableFatalV1Test() {
		final String query = "select * from tab1 pivot (sum(col1) for col2 in (A))";

		final SQLSelectParserParser parser = parse(query);
		ParserRunResult runResult = runSQLParsertestAllowErrors(query, parser);
		SqlParseEventWalker extractor = runResult.getExtractor();

		Assert.assertNotNull(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=*, table_ref=*}}}, from={pivot={value={function={function_name=sum, parameters={column={name=col1, table_ref=null}}}}, for={column={name=col2, table_ref=null}}, in={1={pivot_literal=A}}}, table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{tab1={*=[[@1,7:7='*',<291>,1:7]], col2=[[@11,40:43='col2',<381>,1:40]], col1=[[@8,30:33='col1',<381>,1:30]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query1={*=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query1={query_dictionary={*=[[@1,7:7='*',<291>,1:7]]}, table_dictionary={tab1={*=[[@1,7:7='*',<291>,1:7]], col2=[[@11,40:43='col2',<381>,1:40]], col1=[[@8,30:33='col1',<381>,1:30]]}}, derivation={source_columns={tuple_0=[{name=col2, table_ref=tab1}, {name=col1, table_ref=tab1}]}, derived_columns={tuple_0={A_sum=[[@6,26:28='sum',<141>,1:26], [@14,49:49='A',<381>,1:49]]}}}, interface={*=[{name=*, table_ref=*}]}, table_alias={tuple_0=tab1}}}",
				extractor.getSymbolTable().toString());
		assertFatalDiagnosticAtPosition(
				extractor.getSnippet(),
				"PIVOT_IN_IDENTIFIER_UNRESOLVED",
				"cannot be resolved against the PIVOT source",
				"A",
				1,
				49);
		Assert.assertEquals(
				"Identifier-form PIVOT IN value against a table should not get the reference warning",
				0,
				countDiagnosticsBySeverity(
						extractor.getSnippet(),
						"PIVOT_IN_IDENTIFIER_REFERENCE",
						errorhandling.ParseDiagnostic.Severity.SEVERE_WARNING,
						null,
						"A"));
	}

	@Test
	public void pivotInIdentifierMissingFromSubqueryFatalV1Test() {
		final String query = "select * from (select col1, col2 from tab1) q pivot (sum(col1) for col2 in (A))";

		final SQLSelectParserParser parser = parse(query);
		ParserRunResult runResult = runSQLParsertestAllowErrors(query, parser);
		SqlParseEventWalker extractor = runResult.getExtractor();

		Assert.assertNotNull(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=*, table_ref=*}}}, from={pivot={value={function={function_name=sum, parameters={column={name=col1, table_ref=null}}}}, for={column={name=col2, table_ref=null}}, in={1={pivot_literal=A}}}, table={alias=q, query={select={1={column={name=col1, table_ref=null}}, 2={column={name=col2, table_ref=null}}}, from={table={alias=null, table=tab1}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{q={*=[[@1,7:7='*',<291>,1:7]], col2=[[@19,67:70='col2',<381>,1:67]], col1=[[@16,57:60='col1',<381>,1:57]]}, tab1={col2=[[@7,28:31='col2',<381>,1:28]], col1=[[@5,22:25='col1',<381>,1:22]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={col2=[[@7,28:31='col2',<381>,1:28]], *=[[@1,7:7='*',<291>,1:7]], col1=[[@5,22:25='col1',<381>,1:22]]}, query2={*=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query2={query_dictionary={*=[[@1,7:7='*',<291>,1:7]]}, table_dictionary={q={*=[[@1,7:7='*',<291>,1:7]], col2=[[@19,67:70='col2',<381>,1:67]], col1=[[@16,57:60='col1',<381>,1:57]]}}, def_query0={query_dictionary={*=[[@1,7:7='*',<291>,1:7]], col2=[[@7,28:31='col2',<381>,1:28]], col1=[[@5,22:25='col1',<381>,1:22]]}, table_dictionary={tab1={col2=[[@7,28:31='col2',<381>,1:28]], col1=[[@5,22:25='col1',<381>,1:22]]}}, interface={col2=[{name=col2, table_ref=tab1}], col1=[{name=col1, table_ref=tab1}]}}, derivation={source_columns={tuple_0=[{name=col2, table_ref=q}, {name=col1, table_ref=q}]}, derived_columns={tuple_0={A_sum=[[@14,53:55='sum',<141>,1:53], [@22,76:76='A',<381>,1:76]]}}}, interface={*=[{name=*, table_ref=*}]}, table_alias={q=query0, tuple_0=q}}}",
				extractor.getSymbolTable().toString());
		assertFatalDiagnosticAtPosition(
				extractor.getSnippet(),
				"PIVOT_IN_IDENTIFIER_UNRESOLVED",
				"cannot be resolved against the PIVOT source",
				"A",
				1,
				76);
		Assert.assertEquals(
				"Unresolved subquery PIVOT IN identifier should not get the reference warning",
				0,
				countDiagnosticsBySeverity(
						extractor.getSnippet(),
						"PIVOT_IN_IDENTIFIER_REFERENCE",
						errorhandling.ParseDiagnostic.Severity.SEVERE_WARNING,
						null,
						"A"));
	}

	/** Phase 17.7.4 — PIVOT aggregate/FOR source operands vs subquery-backed source interface. */
	@Test
	public void pivotSourceOperandUnresolvedSubqueryFatalV1Test() {
		final String query =
				"SELECT * FROM (SELECT col1, col2 FROM tab1) q "
						+ "PIVOT (SUM(missing_amount) FOR col2 IN ('A'))";

		ParserRunResult runResult = runSQLParsertestAllowErrors(query, parse(query));
		SqlParseEventWalker extractor = runResult.getExtractor();
		Assert.assertNotNull(extractor);
		assertFatalDiagnosticAtPosition(
				extractor.getSnippet(),
				"RELATIONAL_MODIFIER_SOURCE_OPERAND_UNRESOLVED",
				"cannot be resolved against the PIVOT source interface",
				"missing_amount",
				1,
				57);
		Assert.assertEquals(
				"Valid FOR operand on subquery should not get source-operand unresolved",
				0,
				countFatalDiagnostics(
						extractor.getSnippet(),
						"RELATIONAL_MODIFIER_SOURCE_OPERAND_UNRESOLVED",
						null,
						"col2"));
	}

	/** Phase 17.7.4 — UNPIVOT IN-list operand vs subquery-backed source interface. */
	@Test
	public void unpivotSourceOperandUnresolvedSubqueryFatalV1Test() {
		final String query =
				"SELECT * FROM (SELECT empid, jan_sales, feb_sales FROM monthly_sales) u "
						+ "UNPIVOT (sales_amount FOR month_name IN (missing_wide, feb_sales))";

		ParserRunResult runResult = runSQLParsertestAllowErrors(query, parse(query));
		SqlParseEventWalker extractor = runResult.getExtractor();
		Assert.assertNotNull(extractor);
		assertFatalDiagnosticAtPosition(
				extractor.getSnippet(),
				"RELATIONAL_MODIFIER_SOURCE_OPERAND_UNRESOLVED",
				"cannot be resolved against the UNPIVOT source interface",
				"missing_wide",
				1,
				113);
	}

	/** Phase 17.7.4 — PIVOT aggregate/FOR source operands vs VALUES-backed source interface. */
	@Test
	public void pivotSourceOperandUnresolvedValuesFatalV1Test() {
		final String query =
				"SELECT * FROM (VALUES (1, 'A', 10.0), (2, 'B', 20.0)) AS v (col1, col2, amount) "
						+ "PIVOT (SUM(missing_amount) FOR col2 IN ('A'))";

		ParserRunResult runResult = runSQLParsertestAllowErrors(query, parse(query));
		SqlParseEventWalker extractor = runResult.getExtractor();
		Assert.assertNotNull(extractor);
		assertFatalDiagnosticAtPosition(
				extractor.getSnippet(),
				"RELATIONAL_MODIFIER_SOURCE_OPERAND_UNRESOLVED",
				"cannot be resolved against the PIVOT source interface",
				"missing_amount",
				1,
				91);
		Assert.assertEquals(
				"Valid FOR operand on VALUES should not get source-operand unresolved",
				0,
				countFatalDiagnostics(
						extractor.getSnippet(),
						"RELATIONAL_MODIFIER_SOURCE_OPERAND_UNRESOLVED",
						null,
						"col2"));
	}

	/** Phase 17.7.4 — UNPIVOT IN-list operand vs VALUES-backed source interface. */
	@Test
	public void unpivotSourceOperandUnresolvedValuesFatalV1Test() {
		final String query =
				"SELECT * FROM (VALUES (1, 100.0, 200.0), (2, 110.0, 210.0)) AS u (empid, jan_sales, feb_sales) "
						+ "UNPIVOT (sales_amount FOR month_name IN (missing_wide, feb_sales))";

		ParserRunResult runResult = runSQLParsertestAllowErrors(query, parse(query));
		SqlParseEventWalker extractor = runResult.getExtractor();
		Assert.assertNotNull(extractor);
		assertFatalDiagnosticAtPosition(
				extractor.getSnippet(),
				"RELATIONAL_MODIFIER_SOURCE_OPERAND_UNRESOLVED",
				"cannot be resolved against the UNPIVOT source interface",
				"missing_wide",
				1,
				136);
	}

	@Test
	public void pivotInIdentifierResolvedFromSubqueryWarningV1Test() {
		final String query = "select * from (select col1, col2, 1 as A from tab1) q pivot (sum(col1) for col2 in (A))";

		final SQLSelectParserParser parser = parse(query);
		ParserRunResult runResult = runSQLParsertestAllowErrors(query, parser);
		SqlParseEventWalker extractor = runResult.getExtractor();

		assertNoFatalErrors(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=*, table_ref=*}}}, from={pivot={value={function={function_name=sum, parameters={column={name=col1, table_ref=null}}}}, for={column={name=col2, table_ref=null}}, in={1={pivot_literal=A}}}, table={alias=q, query={select={1={column={name=col1, table_ref=null}}, 2={column={name=col2, table_ref=null}}, 3={alias=A, literal=1}}, from={table={alias=null, table=tab1}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{q={*=[[@1,7:7='*',<291>,1:7]], col2=[[@23,75:78='col2',<381>,1:75]], col1=[[@20,65:68='col1',<381>,1:65]]}, tab1={col2=[[@7,28:31='col2',<381>,1:28]], col1=[[@5,22:25='col1',<381>,1:22]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={A=[[@11,39:39='A',<381>,1:39]], *=[[@1,7:7='*',<291>,1:7]], col2=[[@7,28:31='col2',<381>,1:28]], col1=[[@5,22:25='col1',<381>,1:22]]}, query2={*=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query2={query_dictionary={*=[[@1,7:7='*',<291>,1:7]]}, table_dictionary={q={*=[[@1,7:7='*',<291>,1:7]], col2=[[@23,75:78='col2',<381>,1:75]], col1=[[@20,65:68='col1',<381>,1:65]]}}, def_query0={query_dictionary={A=[[@11,39:39='A',<381>,1:39]], *=[[@1,7:7='*',<291>,1:7]], col2=[[@7,28:31='col2',<381>,1:28]], col1=[[@5,22:25='col1',<381>,1:22]]}, table_dictionary={tab1={col2=[[@7,28:31='col2',<381>,1:28]], col1=[[@5,22:25='col1',<381>,1:22]]}}, interface={A=[], col2=[{name=col2, table_ref=tab1}], col1=[{name=col1, table_ref=tab1}]}}, derivation={source_columns={tuple_0=[{name=col2, table_ref=q}, {name=col1, table_ref=q}]}, derived_columns={tuple_0={A_sum=[[@18,61:63='sum',<141>,1:61], [@26,84:84='A',<381>,1:84]]}}}, interface={*=[{name=*, table_ref=*}]}, table_alias={q=query0, tuple_0=q}}}",
				extractor.getSymbolTable().toString());
		Assert.assertEquals(
				"Resolved subquery PIVOT IN identifier should get exactly one warning",
				1,
				countDiagnosticsBySeverity(
						extractor.getSnippet(),
						"PIVOT_IN_IDENTIFIER_REFERENCE",
						errorhandling.ParseDiagnostic.Severity.SEVERE_WARNING,
						null,
						"A"));
		assertDiagnosticAtPosition(
				extractor.getSnippet(),
				"PIVOT_IN_IDENTIFIER_REFERENCE",
				ParseDiagnostic.Severity.SEVERE_WARNING,
				"PIVOT IN identifier \"A\" at (l:1 c:84) is interpreted as a column reference.",
				"A",
				1,
				84);
		Assert.assertEquals(
				"Resolved subquery PIVOT IN identifier should not get the fatal diagnostic",
				0,
				countFatalDiagnostics(
						extractor.getSnippet(),
						"PIVOT_IN_IDENTIFIER_UNRESOLVED",
						null,
						"A"));
	}

	@Test
	public void pivotV1Tab1Test() {
		final String query = "select *, A_sum from tab1 "
		+"\n pivot (sum(col1), avg(col2), count(col3), max(col4), min(col5) "
		+"\n for col2 in (A, B))";

		final SQLSelectParserParser parser = parse(query);
		ParserRunResult runResult = runSQLParsertestAllowErrors(query, parser);
		SqlParseEventWalker extractor = runResult.getExtractor();
		Assert.assertNotNull(extractor);

		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=*, table_ref=*}}, 2={column={name=A_sum, table_ref=null}}}, from={pivot={value={1={function={function_name=sum, parameters={column={name=col1, table_ref=null}}}}, 2={function={function_name=avg, parameters={column={name=col2, table_ref=null}}}}, 3={function={function_name=count, parameters={column={name=col3, table_ref=null}}}}, 4={function={function_name=max, parameters={column={name=col4, table_ref=null}}}}, 5={function={function_name=min, parameters={column={name=col5, table_ref=null}}}}}, for={column={name=col2, table_ref=null}}, in={1={pivot_literal=A}, 2={pivot_literal=B}}}, table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*, A_sum]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{tab1={col4=[[@25,74:77='col4',<381>,2:47]], *=[[@1,7:7='*',<291>,1:7]], col5=[[@30,85:88='col5',<381>,2:58]], col2=[[@33,97:100='col2',<381>,3:5], [@15,50:53='col2',<381>,2:23]], col3=[[@20,63:66='col3',<381>,2:36]], col1=[[@10,39:42='col1',<381>,2:12]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query1={A_sum=[[@3,10:14='A_sum',<381>,1:10], [@8,35:37='sum',<141>,2:8], [@36,106:106='A',<381>,3:14]], *=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query1={query_dictionary={*=[[@1,7:7='*',<291>,1:7]], A_sum=[[@3,10:14='A_sum',<381>,1:10], [@8,35:37='sum',<141>,2:8], [@36,106:106='A',<381>,3:14]]}, table_dictionary={tab1={col4=[[@25,74:77='col4',<381>,2:47]], *=[[@1,7:7='*',<291>,1:7]], col5=[[@30,85:88='col5',<381>,2:58]], col2=[[@33,97:100='col2',<381>,3:5], [@15,50:53='col2',<381>,2:23]], col3=[[@20,63:66='col3',<381>,2:36]], col1=[[@10,39:42='col1',<381>,2:12]]}}, derivation={source_columns={tuple_0=[{name=col2, table_ref=tab1}, {name=col1, table_ref=tab1}, {name=col3, table_ref=tab1}, {name=col4, table_ref=tab1}, {name=col5, table_ref=tab1}]}, derived_columns={tuple_0={A_sum=[[@8,35:37='sum',<141>,2:8], [@36,106:106='A',<381>,3:14]], A_avg=[[@13,46:48='avg',<61>,2:19], [@36,106:106='A',<381>,3:14]], A_count=[[@18,57:61='count',<69>,2:30], [@36,106:106='A',<381>,3:14]], A_max=[[@23,70:72='max',<108>,2:43], [@36,106:106='A',<381>,3:14]], A_min=[[@28,81:83='min',<113>,2:54], [@36,106:106='A',<381>,3:14]], B_sum=[[@8,35:37='sum',<141>,2:8], [@38,109:109='B',<381>,3:17]], B_avg=[[@13,46:48='avg',<61>,2:19], [@38,109:109='B',<381>,3:17]], B_count=[[@18,57:61='count',<69>,2:30], [@38,109:109='B',<381>,3:17]], B_max=[[@23,70:72='max',<108>,2:43], [@38,109:109='B',<381>,3:17]], B_min=[[@28,81:83='min',<113>,2:54], [@38,109:109='B',<381>,3:17]]}}}, interface={*=[{name=*, table_ref=*}], A_sum=[{name=A_sum, table_ref=tuple_0}, {name=col2, table_ref=tab1}, {name=col1, table_ref=tab1}, {name=col3, table_ref=tab1}, {name=col4, table_ref=tab1}, {name=col5, table_ref=tab1}]}, table_alias={tuple_0=tab1}}}",
				extractor.getSymbolTable().toString());
		assertFatalDiagnosticAtPosition(
				extractor.getSnippet(),
				"PIVOT_IN_IDENTIFIER_UNRESOLVED",
				"cannot be resolved against the PIVOT source",
				"A",
				3,
				14);
		assertFatalDiagnosticAtPosition(
				extractor.getSnippet(),
				"PIVOT_IN_IDENTIFIER_UNRESOLVED",
				"cannot be resolved against the PIVOT source",
				"B",
				3,
				17);
	}

	@Test
	public void pivotV1Tab1QuotedSelectorsSuccessTest() {
		final String query = "select *, A_sum from tab1 "
		+"\n pivot (sum(col1), avg(col2), count(col3), max(col4), min(col5) "
		+"\n for col2 in ('A', 'B'))";

		final SQLSelectParserParser parser = parse(query);
		ParserRunResult runResult = runSQLParsertestAllowErrors(query, parser);
		SqlParseEventWalker extractor = runResult.getExtractor();

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=*, table_ref=*}}, 2={column={name=A_sum, table_ref=null}}}, from={pivot={value={1={function={function_name=sum, parameters={column={name=col1, table_ref=null}}}}, 2={function={function_name=avg, parameters={column={name=col2, table_ref=null}}}}, 3={function={function_name=count, parameters={column={name=col3, table_ref=null}}}}, 4={function={function_name=max, parameters={column={name=col4, table_ref=null}}}}, 5={function={function_name=min, parameters={column={name=col5, table_ref=null}}}}}, for={column={name=col2, table_ref=null}}, in={1={pivot_literal='A'}, 2={pivot_literal='B'}}}, table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*, A_sum]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{tab1={col4=[[@25,74:77='col4',<381>,2:47]], *=[[@1,7:7='*',<291>,1:7]], col5=[[@30,85:88='col5',<381>,2:58]], col2=[[@33,97:100='col2',<381>,3:5], [@15,50:53='col2',<381>,2:23]], col3=[[@20,63:66='col3',<381>,2:36]], col1=[[@10,39:42='col1',<381>,2:12]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query1={A_sum=[[@3,10:14='A_sum',<381>,1:10], [@8,35:37='sum',<141>,2:8], [@36,106:108=''A'',<389>,3:14]], *=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query1={query_dictionary={*=[[@1,7:7='*',<291>,1:7]], A_sum=[[@3,10:14='A_sum',<381>,1:10], [@8,35:37='sum',<141>,2:8], [@36,106:108=''A'',<389>,3:14]]}, table_dictionary={tab1={col4=[[@25,74:77='col4',<381>,2:47]], *=[[@1,7:7='*',<291>,1:7]], col5=[[@30,85:88='col5',<381>,2:58]], col2=[[@33,97:100='col2',<381>,3:5], [@15,50:53='col2',<381>,2:23]], col3=[[@20,63:66='col3',<381>,2:36]], col1=[[@10,39:42='col1',<381>,2:12]]}}, derivation={source_columns={tuple_0=[{name=col2, table_ref=tab1}, {name=col1, table_ref=tab1}, {name=col3, table_ref=tab1}, {name=col4, table_ref=tab1}, {name=col5, table_ref=tab1}]}, derived_columns={tuple_0={A_sum=[[@8,35:37='sum',<141>,2:8], [@36,106:108=''A'',<389>,3:14]], A_avg=[[@13,46:48='avg',<61>,2:19], [@36,106:108=''A'',<389>,3:14]], A_count=[[@18,57:61='count',<69>,2:30], [@36,106:108=''A'',<389>,3:14]], A_max=[[@23,70:72='max',<108>,2:43], [@36,106:108=''A'',<389>,3:14]], A_min=[[@28,81:83='min',<113>,2:54], [@36,106:108=''A'',<389>,3:14]], B_sum=[[@8,35:37='sum',<141>,2:8], [@38,111:113=''B'',<389>,3:19]], B_avg=[[@13,46:48='avg',<61>,2:19], [@38,111:113=''B'',<389>,3:19]], B_count=[[@18,57:61='count',<69>,2:30], [@38,111:113=''B'',<389>,3:19]], B_max=[[@23,70:72='max',<108>,2:43], [@38,111:113=''B'',<389>,3:19]], B_min=[[@28,81:83='min',<113>,2:54], [@38,111:113=''B'',<389>,3:19]]}}}, interface={*=[{name=*, table_ref=*}], A_sum=[{name=A_sum, table_ref=tuple_0}, {name=col2, table_ref=tab1}, {name=col1, table_ref=tab1}, {name=col3, table_ref=tab1}, {name=col4, table_ref=tab1}, {name=col5, table_ref=tab1}]}, table_alias={tuple_0=tab1}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void pivotV1Tab1WithAliasQuotedSelectorsSuccessTest() {
		final String query = "select *, pvt.A_sum asum1, A_sum asum2, tab1.A_sum asum3 from tab1 "
		+"\n pivot (sum(col1), avg(col2), count(col3), max(col4), min(col5) "
		+"\n for col2 in ('A', 'B')) as pvt";

		final SQLSelectParserParser parser = parse(query);
		ParserRunResult runResult = runSQLParsertestAllowErrors(query, parser);
		SqlParseEventWalker extractor = runResult.getExtractor();

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=*, table_ref=*}}, 2={column={name=A_sum, table_ref=pvt}, alias=asum1}, 3={column={name=A_sum, table_ref=null}, alias=asum2}, 4={column={name=A_sum, table_ref=tab1}, alias=asum3}}, from={pivot={value={1={function={function_name=sum, parameters={column={name=col1, table_ref=null}}}}, 2={function={function_name=avg, parameters={column={name=col2, table_ref=null}}}}, 3={function={function_name=count, parameters={column={name=col3, table_ref=null}}}}, 4={function={function_name=max, parameters={column={name=col4, table_ref=null}}}}, 5={function={function_name=min, parameters={column={name=col5, table_ref=null}}}}}, for={column={name=col2, table_ref=null}}, in={1={pivot_literal='A'}, 2={pivot_literal='B'}}}, alias=pvt, table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[asum2, asum3, *, asum1]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{tab1={col4=[[@36,115:118='col4',<381>,2:47]], *=[[@1,7:7='*',<291>,1:7]], col5=[[@41,126:129='col5',<381>,2:58]], col2=[[@44,138:141='col2',<381>,3:5], [@26,91:94='col2',<381>,2:23]], col3=[[@31,104:107='col3',<381>,2:36]], col1=[[@21,80:83='col1',<381>,2:12]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query1={*=[[@1,7:7='*',<291>,1:7]], asum1=[[@6,20:24='asum1',<381>,1:20]], asum2=[[@9,33:37='asum2',<381>,1:33]], asum3=[[@14,51:55='asum3',<381>,1:51]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query1={query_dictionary={asum2=[[@9,33:37='asum2',<381>,1:33]], asum3=[[@14,51:55='asum3',<381>,1:51]], *=[[@1,7:7='*',<291>,1:7]], asum1=[[@6,20:24='asum1',<381>,1:20]]}, table_dictionary={tab1={col4=[[@36,115:118='col4',<381>,2:47]], *=[[@1,7:7='*',<291>,1:7]], col5=[[@41,126:129='col5',<381>,2:58]], col2=[[@44,138:141='col2',<381>,3:5], [@26,91:94='col2',<381>,2:23]], col3=[[@31,104:107='col3',<381>,2:36]], col1=[[@21,80:83='col1',<381>,2:12]]}}, derivation={source_columns={pvt=[{name=col2, table_ref=tab1}, {name=col1, table_ref=tab1}, {name=col3, table_ref=tab1}, {name=col4, table_ref=tab1}, {name=col5, table_ref=tab1}]}, derived_columns={pvt={A_sum=[[@19,76:78='sum',<141>,2:8], [@47,147:149=''A'',<389>,3:14]], A_avg=[[@24,87:89='avg',<61>,2:19], [@47,147:149=''A'',<389>,3:14]], A_count=[[@29,98:102='count',<69>,2:30], [@47,147:149=''A'',<389>,3:14]], A_max=[[@34,111:113='max',<108>,2:43], [@47,147:149=''A'',<389>,3:14]], A_min=[[@39,122:124='min',<113>,2:54], [@47,147:149=''A'',<389>,3:14]], B_sum=[[@19,76:78='sum',<141>,2:8], [@49,152:154=''B'',<389>,3:19]], B_avg=[[@24,87:89='avg',<61>,2:19], [@49,152:154=''B'',<389>,3:19]], B_count=[[@29,98:102='count',<69>,2:30], [@49,152:154=''B'',<389>,3:19]], B_max=[[@34,111:113='max',<108>,2:43], [@49,152:154=''B'',<389>,3:19]], B_min=[[@39,122:124='min',<113>,2:54], [@49,152:154=''B'',<389>,3:19]]}}}, interface={asum2=[{name=A_sum, table_ref=pvt}, {name=col2, table_ref=tab1}, {name=col1, table_ref=tab1}, {name=col3, table_ref=tab1}, {name=col4, table_ref=tab1}, {name=col5, table_ref=tab1}], asum3=[{name=A_sum, table_ref=pvt}, {name=col2, table_ref=tab1}, {name=col1, table_ref=tab1}, {name=col3, table_ref=tab1}, {name=col4, table_ref=tab1}, {name=col5, table_ref=tab1}], *=[{name=*, table_ref=*}], asum1=[{name=A_sum, table_ref=pvt}]}, table_alias={pvt=tab1}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void pivotV1QueryTest() {
		final String query = "select A_sum, A_avg, A_count, A_max, A_min, "
		+ "\n B_sum, B_avg, B_count, B_max, B_min "
		+ "\nfrom (select col1, col2, col3, col4, col5 from tab1) q "
		+"\n pivot (sum(col1), avg(col2), count(col3), max(col4), min(col5) "
		+"\n for col2 in (A, B)) u";

		final SQLSelectParserParser parser = parse(query);
		ParserRunResult runResult = runSQLParsertestAllowErrors(query, parser);
		SqlParseEventWalker extractor = runResult.getExtractor();
		Assert.assertNotNull(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=A_sum, table_ref=null}}, 2={column={name=A_avg, table_ref=null}}, 3={column={name=A_count, table_ref=null}}, 4={column={name=A_max, table_ref=null}}, 5={column={name=A_min, table_ref=null}}, 6={column={name=B_sum, table_ref=null}}, 7={column={name=B_avg, table_ref=null}}, 8={column={name=B_count, table_ref=null}}, 9={column={name=B_max, table_ref=null}}, 10={column={name=B_min, table_ref=null}}}, from={pivot={value={1={function={function_name=sum, parameters={column={name=col1, table_ref=null}}}}, 2={function={function_name=avg, parameters={column={name=col2, table_ref=null}}}}, 3={function={function_name=count, parameters={column={name=col3, table_ref=null}}}}, 4={function={function_name=max, parameters={column={name=col4, table_ref=null}}}}, 5={function={function_name=min, parameters={column={name=col5, table_ref=null}}}}}, for={column={name=col2, table_ref=null}}, in={1={pivot_literal=A}, 2={pivot_literal=B}}}, alias=u, table={alias=q, query={select={1={column={name=col1, table_ref=null}}, 2={column={name=col2, table_ref=null}}, 3={column={name=col3, table_ref=null}}, 4={column={name=col4, table_ref=null}}, 5={column={name=col5, table_ref=null}}}, from={table={alias=null, table=tab1}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[B_sum, A_avg, A_min, A_max, A_count, B_max, B_avg, B_min, B_count, A_sum]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{q={col4=[[@55,186:189='col4',<381>,4:47]], col5=[[@60,197:200='col5',<381>,4:58]], col2=[[@63,209:212='col2',<381>,5:5], [@45,162:165='col2',<381>,4:23]], col3=[[@50,175:178='col3',<381>,4:36]], col1=[[@40,151:154='col1',<381>,4:12]]}, tab1={col4=[[@29,114:117='col4',<381>,3:31]], col5=[[@31,120:123='col5',<381>,3:37]], col2=[[@25,102:105='col2',<381>,3:19]], col3=[[@27,108:111='col3',<381>,3:25]], col1=[[@23,96:99='col1',<381>,3:13]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
		 "{query0={col4=[[@29,114:117='col4',<381>,3:31]], col5=[[@31,120:123='col5',<381>,3:37]], col2=[[@25,102:105='col2',<381>,3:19]], col3=[[@27,108:111='col3',<381>,3:25]], col1=[[@23,96:99='col1',<381>,3:13]]}, query2={B_sum=[[@11,46:50='B_sum',<381>,2:1], [@38,147:149='sum',<141>,4:8], [@68,221:221='B',<381>,5:17]], A_avg=[[@3,14:18='A_avg',<381>,1:14], [@43,158:160='avg',<61>,4:19], [@66,218:218='A',<381>,5:14]], A_min=[[@9,37:41='A_min',<381>,1:37], [@58,193:195='min',<113>,4:54], [@66,218:218='A',<381>,5:14]], A_max=[[@7,30:34='A_max',<381>,1:30], [@53,182:184='max',<108>,4:43], [@66,218:218='A',<381>,5:14]], A_count=[[@5,21:27='A_count',<381>,1:21], [@48,169:173='count',<69>,4:30], [@66,218:218='A',<381>,5:14]], B_max=[[@17,69:73='B_max',<381>,2:24], [@53,182:184='max',<108>,4:43], [@68,221:221='B',<381>,5:17]], B_avg=[[@13,53:57='B_avg',<381>,2:8], [@43,158:160='avg',<61>,4:19], [@68,221:221='B',<381>,5:17]], B_min=[[@19,76:80='B_min',<381>,2:31], [@58,193:195='min',<113>,4:54], [@68,221:221='B',<381>,5:17]], B_count=[[@15,60:66='B_count',<381>,2:15], [@48,169:173='count',<69>,4:30], [@68,221:221='B',<381>,5:17]], A_sum=[[@1,7:11='A_sum',<381>,1:7], [@38,147:149='sum',<141>,4:8], [@66,218:218='A',<381>,5:14]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query2={query_dictionary={B_sum=[[@11,46:50='B_sum',<381>,2:1], [@38,147:149='sum',<141>,4:8], [@68,221:221='B',<381>,5:17]], A_avg=[[@3,14:18='A_avg',<381>,1:14], [@43,158:160='avg',<61>,4:19], [@66,218:218='A',<381>,5:14]], A_min=[[@9,37:41='A_min',<381>,1:37], [@58,193:195='min',<113>,4:54], [@66,218:218='A',<381>,5:14]], A_max=[[@7,30:34='A_max',<381>,1:30], [@53,182:184='max',<108>,4:43], [@66,218:218='A',<381>,5:14]], A_count=[[@5,21:27='A_count',<381>,1:21], [@48,169:173='count',<69>,4:30], [@66,218:218='A',<381>,5:14]], B_max=[[@17,69:73='B_max',<381>,2:24], [@53,182:184='max',<108>,4:43], [@68,221:221='B',<381>,5:17]], B_avg=[[@13,53:57='B_avg',<381>,2:8], [@43,158:160='avg',<61>,4:19], [@68,221:221='B',<381>,5:17]], B_min=[[@19,76:80='B_min',<381>,2:31], [@58,193:195='min',<113>,4:54], [@68,221:221='B',<381>,5:17]], B_count=[[@15,60:66='B_count',<381>,2:15], [@48,169:173='count',<69>,4:30], [@68,221:221='B',<381>,5:17]], A_sum=[[@1,7:11='A_sum',<381>,1:7], [@38,147:149='sum',<141>,4:8], [@66,218:218='A',<381>,5:14]]}, table_dictionary={q={col4=[[@55,186:189='col4',<381>,4:47]], col5=[[@60,197:200='col5',<381>,4:58]], col2=[[@63,209:212='col2',<381>,5:5], [@45,162:165='col2',<381>,4:23]], col3=[[@50,175:178='col3',<381>,4:36]], col1=[[@40,151:154='col1',<381>,4:12]]}}, def_query0={query_dictionary={col4=[[@29,114:117='col4',<381>,3:31]], col5=[[@31,120:123='col5',<381>,3:37]], col2=[[@25,102:105='col2',<381>,3:19]], col3=[[@27,108:111='col3',<381>,3:25]], col1=[[@23,96:99='col1',<381>,3:13]]}, table_dictionary={tab1={col4=[[@29,114:117='col4',<381>,3:31]], col5=[[@31,120:123='col5',<381>,3:37]], col2=[[@25,102:105='col2',<381>,3:19]], col3=[[@27,108:111='col3',<381>,3:25]], col1=[[@23,96:99='col1',<381>,3:13]]}}, interface={col4=[{name=col4, table_ref=tab1}], col5=[{name=col5, table_ref=tab1}], col2=[{name=col2, table_ref=tab1}], col3=[{name=col3, table_ref=tab1}], col1=[{name=col1, table_ref=tab1}]}}, derivation={source_columns={u=[{name=col2, table_ref=q}, {name=col1, table_ref=q}, {name=col3, table_ref=q}, {name=col4, table_ref=q}, {name=col5, table_ref=q}]}, derived_columns={u={A_sum=[[@38,147:149='sum',<141>,4:8], [@66,218:218='A',<381>,5:14]], A_avg=[[@43,158:160='avg',<61>,4:19], [@66,218:218='A',<381>,5:14]], A_count=[[@48,169:173='count',<69>,4:30], [@66,218:218='A',<381>,5:14]], A_max=[[@53,182:184='max',<108>,4:43], [@66,218:218='A',<381>,5:14]], A_min=[[@58,193:195='min',<113>,4:54], [@66,218:218='A',<381>,5:14]], B_sum=[[@38,147:149='sum',<141>,4:8], [@68,221:221='B',<381>,5:17]], B_avg=[[@43,158:160='avg',<61>,4:19], [@68,221:221='B',<381>,5:17]], B_count=[[@48,169:173='count',<69>,4:30], [@68,221:221='B',<381>,5:17]], B_max=[[@53,182:184='max',<108>,4:43], [@68,221:221='B',<381>,5:17]], B_min=[[@58,193:195='min',<113>,4:54], [@68,221:221='B',<381>,5:17]]}}}, interface={B_sum=[{name=B_sum, table_ref=u}, {name=col2, table_ref=q}, {name=col1, table_ref=q}, {name=col3, table_ref=q}, {name=col4, table_ref=q}, {name=col5, table_ref=q}], A_avg=[{name=A_avg, table_ref=u}, {name=col2, table_ref=q}, {name=col1, table_ref=q}, {name=col3, table_ref=q}, {name=col4, table_ref=q}, {name=col5, table_ref=q}], A_min=[{name=A_min, table_ref=u}, {name=col2, table_ref=q}, {name=col1, table_ref=q}, {name=col3, table_ref=q}, {name=col4, table_ref=q}, {name=col5, table_ref=q}], A_max=[{name=A_max, table_ref=u}, {name=col2, table_ref=q}, {name=col1, table_ref=q}, {name=col3, table_ref=q}, {name=col4, table_ref=q}, {name=col5, table_ref=q}], A_count=[{name=A_count, table_ref=u}, {name=col2, table_ref=q}, {name=col1, table_ref=q}, {name=col3, table_ref=q}, {name=col4, table_ref=q}, {name=col5, table_ref=q}], B_max=[{name=B_max, table_ref=u}, {name=col2, table_ref=q}, {name=col1, table_ref=q}, {name=col3, table_ref=q}, {name=col4, table_ref=q}, {name=col5, table_ref=q}], B_avg=[{name=B_avg, table_ref=u}, {name=col2, table_ref=q}, {name=col1, table_ref=q}, {name=col3, table_ref=q}, {name=col4, table_ref=q}, {name=col5, table_ref=q}], B_min=[{name=B_min, table_ref=u}, {name=col2, table_ref=q}, {name=col1, table_ref=q}, {name=col3, table_ref=q}, {name=col4, table_ref=q}, {name=col5, table_ref=q}], B_count=[{name=B_count, table_ref=u}, {name=col2, table_ref=q}, {name=col1, table_ref=q}, {name=col3, table_ref=q}, {name=col4, table_ref=q}, {name=col5, table_ref=q}], A_sum=[{name=A_sum, table_ref=u}, {name=col2, table_ref=q}, {name=col1, table_ref=q}, {name=col3, table_ref=q}, {name=col4, table_ref=q}, {name=col5, table_ref=q}]}, table_alias={q=query0, u=q}}}",
				extractor.getSymbolTable().toString());
		assertFatalDiagnosticAtPosition(
				extractor.getSnippet(),
				"PIVOT_IN_IDENTIFIER_UNRESOLVED",
				"cannot be resolved against the PIVOT source",
				"A",
				5,
				14);
		assertFatalDiagnosticAtPosition(
				extractor.getSnippet(),
				"PIVOT_IN_IDENTIFIER_UNRESOLVED",
				"cannot be resolved against the PIVOT source",
				"B",
				5,
				17);
	}

	@Test
	public void pivotV1QueryQuotedSelectorsSuccessTest() {
		final String query = "select A_sum, A_avg, A_count, A_max, A_min, "
		+ "\n B_sum, B_avg, B_count, B_max, B_min "
		+ "\nfrom (select col1, col2, col3, col4, col5 from tab1) q "
		+"\n pivot (sum(col1), avg(col2), count(col3), max(col4), min(col5) "
		+"\n for col2 in ('A', 'B')) u";

		final SQLSelectParserParser parser = parse(query);
		ParserRunResult runResult = runSQLParsertestAllowErrors(query, parser);
		SqlParseEventWalker extractor = runResult.getExtractor();

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=A_sum, table_ref=null}}, 2={column={name=A_avg, table_ref=null}}, 3={column={name=A_count, table_ref=null}}, 4={column={name=A_max, table_ref=null}}, 5={column={name=A_min, table_ref=null}}, 6={column={name=B_sum, table_ref=null}}, 7={column={name=B_avg, table_ref=null}}, 8={column={name=B_count, table_ref=null}}, 9={column={name=B_max, table_ref=null}}, 10={column={name=B_min, table_ref=null}}}, from={pivot={value={1={function={function_name=sum, parameters={column={name=col1, table_ref=null}}}}, 2={function={function_name=avg, parameters={column={name=col2, table_ref=null}}}}, 3={function={function_name=count, parameters={column={name=col3, table_ref=null}}}}, 4={function={function_name=max, parameters={column={name=col4, table_ref=null}}}}, 5={function={function_name=min, parameters={column={name=col5, table_ref=null}}}}}, for={column={name=col2, table_ref=null}}, in={1={pivot_literal='A'}, 2={pivot_literal='B'}}}, alias=u, table={alias=q, query={select={1={column={name=col1, table_ref=null}}, 2={column={name=col2, table_ref=null}}, 3={column={name=col3, table_ref=null}}, 4={column={name=col4, table_ref=null}}, 5={column={name=col5, table_ref=null}}}, from={table={alias=null, table=tab1}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[B_sum, A_avg, A_min, A_max, A_count, B_max, B_avg, B_min, B_count, A_sum]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{q={col4=[[@55,186:189='col4',<381>,4:47]], col5=[[@60,197:200='col5',<381>,4:58]], col2=[[@63,209:212='col2',<381>,5:5], [@45,162:165='col2',<381>,4:23]], col3=[[@50,175:178='col3',<381>,4:36]], col1=[[@40,151:154='col1',<381>,4:12]]}, tab1={col4=[[@29,114:117='col4',<381>,3:31]], col5=[[@31,120:123='col5',<381>,3:37]], col2=[[@25,102:105='col2',<381>,3:19]], col3=[[@27,108:111='col3',<381>,3:25]], col1=[[@23,96:99='col1',<381>,3:13]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={col4=[[@29,114:117='col4',<381>,3:31]], col5=[[@31,120:123='col5',<381>,3:37]], col2=[[@25,102:105='col2',<381>,3:19]], col3=[[@27,108:111='col3',<381>,3:25]], col1=[[@23,96:99='col1',<381>,3:13]]}, query2={B_sum=[[@11,46:50='B_sum',<381>,2:1], [@38,147:149='sum',<141>,4:8], [@68,223:225=''B'',<389>,5:19]], A_avg=[[@3,14:18='A_avg',<381>,1:14], [@43,158:160='avg',<61>,4:19], [@66,218:220=''A'',<389>,5:14]], A_min=[[@9,37:41='A_min',<381>,1:37], [@58,193:195='min',<113>,4:54], [@66,218:220=''A'',<389>,5:14]], A_max=[[@7,30:34='A_max',<381>,1:30], [@53,182:184='max',<108>,4:43], [@66,218:220=''A'',<389>,5:14]], A_count=[[@5,21:27='A_count',<381>,1:21], [@48,169:173='count',<69>,4:30], [@66,218:220=''A'',<389>,5:14]], B_max=[[@17,69:73='B_max',<381>,2:24], [@53,182:184='max',<108>,4:43], [@68,223:225=''B'',<389>,5:19]], B_avg=[[@13,53:57='B_avg',<381>,2:8], [@43,158:160='avg',<61>,4:19], [@68,223:225=''B'',<389>,5:19]], B_min=[[@19,76:80='B_min',<381>,2:31], [@58,193:195='min',<113>,4:54], [@68,223:225=''B'',<389>,5:19]], B_count=[[@15,60:66='B_count',<381>,2:15], [@48,169:173='count',<69>,4:30], [@68,223:225=''B'',<389>,5:19]], A_sum=[[@1,7:11='A_sum',<381>,1:7], [@38,147:149='sum',<141>,4:8], [@66,218:220=''A'',<389>,5:14]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query2={query_dictionary={B_sum=[[@11,46:50='B_sum',<381>,2:1], [@38,147:149='sum',<141>,4:8], [@68,223:225=''B'',<389>,5:19]], A_avg=[[@3,14:18='A_avg',<381>,1:14], [@43,158:160='avg',<61>,4:19], [@66,218:220=''A'',<389>,5:14]], A_min=[[@9,37:41='A_min',<381>,1:37], [@58,193:195='min',<113>,4:54], [@66,218:220=''A'',<389>,5:14]], A_max=[[@7,30:34='A_max',<381>,1:30], [@53,182:184='max',<108>,4:43], [@66,218:220=''A'',<389>,5:14]], A_count=[[@5,21:27='A_count',<381>,1:21], [@48,169:173='count',<69>,4:30], [@66,218:220=''A'',<389>,5:14]], B_max=[[@17,69:73='B_max',<381>,2:24], [@53,182:184='max',<108>,4:43], [@68,223:225=''B'',<389>,5:19]], B_avg=[[@13,53:57='B_avg',<381>,2:8], [@43,158:160='avg',<61>,4:19], [@68,223:225=''B'',<389>,5:19]], B_min=[[@19,76:80='B_min',<381>,2:31], [@58,193:195='min',<113>,4:54], [@68,223:225=''B'',<389>,5:19]], B_count=[[@15,60:66='B_count',<381>,2:15], [@48,169:173='count',<69>,4:30], [@68,223:225=''B'',<389>,5:19]], A_sum=[[@1,7:11='A_sum',<381>,1:7], [@38,147:149='sum',<141>,4:8], [@66,218:220=''A'',<389>,5:14]]}, table_dictionary={q={col4=[[@55,186:189='col4',<381>,4:47]], col5=[[@60,197:200='col5',<381>,4:58]], col2=[[@63,209:212='col2',<381>,5:5], [@45,162:165='col2',<381>,4:23]], col3=[[@50,175:178='col3',<381>,4:36]], col1=[[@40,151:154='col1',<381>,4:12]]}}, def_query0={query_dictionary={col4=[[@29,114:117='col4',<381>,3:31]], col5=[[@31,120:123='col5',<381>,3:37]], col2=[[@25,102:105='col2',<381>,3:19]], col3=[[@27,108:111='col3',<381>,3:25]], col1=[[@23,96:99='col1',<381>,3:13]]}, table_dictionary={tab1={col4=[[@29,114:117='col4',<381>,3:31]], col5=[[@31,120:123='col5',<381>,3:37]], col2=[[@25,102:105='col2',<381>,3:19]], col3=[[@27,108:111='col3',<381>,3:25]], col1=[[@23,96:99='col1',<381>,3:13]]}}, interface={col4=[{name=col4, table_ref=tab1}], col5=[{name=col5, table_ref=tab1}], col2=[{name=col2, table_ref=tab1}], col3=[{name=col3, table_ref=tab1}], col1=[{name=col1, table_ref=tab1}]}}, derivation={source_columns={u=[{name=col2, table_ref=q}, {name=col1, table_ref=q}, {name=col3, table_ref=q}, {name=col4, table_ref=q}, {name=col5, table_ref=q}]}, derived_columns={u={A_sum=[[@38,147:149='sum',<141>,4:8], [@66,218:220=''A'',<389>,5:14]], A_avg=[[@43,158:160='avg',<61>,4:19], [@66,218:220=''A'',<389>,5:14]], A_count=[[@48,169:173='count',<69>,4:30], [@66,218:220=''A'',<389>,5:14]], A_max=[[@53,182:184='max',<108>,4:43], [@66,218:220=''A'',<389>,5:14]], A_min=[[@58,193:195='min',<113>,4:54], [@66,218:220=''A'',<389>,5:14]], B_sum=[[@38,147:149='sum',<141>,4:8], [@68,223:225=''B'',<389>,5:19]], B_avg=[[@43,158:160='avg',<61>,4:19], [@68,223:225=''B'',<389>,5:19]], B_count=[[@48,169:173='count',<69>,4:30], [@68,223:225=''B'',<389>,5:19]], B_max=[[@53,182:184='max',<108>,4:43], [@68,223:225=''B'',<389>,5:19]], B_min=[[@58,193:195='min',<113>,4:54], [@68,223:225=''B'',<389>,5:19]]}}}, interface={B_sum=[{name=B_sum, table_ref=u}, {name=col2, table_ref=q}, {name=col1, table_ref=q}, {name=col3, table_ref=q}, {name=col4, table_ref=q}, {name=col5, table_ref=q}], A_avg=[{name=A_avg, table_ref=u}, {name=col2, table_ref=q}, {name=col1, table_ref=q}, {name=col3, table_ref=q}, {name=col4, table_ref=q}, {name=col5, table_ref=q}], A_min=[{name=A_min, table_ref=u}, {name=col2, table_ref=q}, {name=col1, table_ref=q}, {name=col3, table_ref=q}, {name=col4, table_ref=q}, {name=col5, table_ref=q}], A_max=[{name=A_max, table_ref=u}, {name=col2, table_ref=q}, {name=col1, table_ref=q}, {name=col3, table_ref=q}, {name=col4, table_ref=q}, {name=col5, table_ref=q}], A_count=[{name=A_count, table_ref=u}, {name=col2, table_ref=q}, {name=col1, table_ref=q}, {name=col3, table_ref=q}, {name=col4, table_ref=q}, {name=col5, table_ref=q}], B_max=[{name=B_max, table_ref=u}, {name=col2, table_ref=q}, {name=col1, table_ref=q}, {name=col3, table_ref=q}, {name=col4, table_ref=q}, {name=col5, table_ref=q}], B_avg=[{name=B_avg, table_ref=u}, {name=col2, table_ref=q}, {name=col1, table_ref=q}, {name=col3, table_ref=q}, {name=col4, table_ref=q}, {name=col5, table_ref=q}], B_min=[{name=B_min, table_ref=u}, {name=col2, table_ref=q}, {name=col1, table_ref=q}, {name=col3, table_ref=q}, {name=col4, table_ref=q}, {name=col5, table_ref=q}], B_count=[{name=B_count, table_ref=u}, {name=col2, table_ref=q}, {name=col1, table_ref=q}, {name=col3, table_ref=q}, {name=col4, table_ref=q}, {name=col5, table_ref=q}], A_sum=[{name=A_sum, table_ref=u}, {name=col2, table_ref=q}, {name=col1, table_ref=q}, {name=col3, table_ref=q}, {name=col4, table_ref=q}, {name=col5, table_ref=q}]}, table_alias={q=query0, u=q}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void pivotV1QueryInvalidAggregateFormulaReportsParserErrorTest() {
		final String query = "select A_sum, A_avg, A_count, A_max, A_min, "
		+ "\n B_sum, B_avg, B_count, B_max, B_min "
		+ "\nfrom (select col1, col2, col3, col4, col5 from tab1) q "
		+"\n pivot (sum(col1), avg(col2), count(col3 - col1), max(col4), min(col5) "
		+"\n for col2 in (A, B))";

		final SQLSelectParserParser parser = parse(query);
		ParserRunResult runResult = runSQLParsertestAllowErrors(query, parser);
		SqlParseEventWalker extractor = runResult.getExtractor();

		Assert.assertNull(
				"Extractor should be null when parser rejects an invalid PIVOT aggregate parameter expression.",
				extractor);
		Assert.assertNotNull("Expected parser failure for invalid PIVOT aggregate parameter expression.",
				runResult.getFailure());
		Assert.assertTrue(
				"Expected parser diagnostics or parser errors for invalid PIVOT aggregate syntax.",
				runResult.getParserErrorCount() > 0
						|| !runResult.getParserErrors().isEmpty()
						|| !runResult.getListenerDiagnostics().isEmpty());

		Assert.assertTrue(
				"Expected parser to record at least one parser error entry.",
				runResult.getParserErrorCount() > 0 || !runResult.getParserErrors().isEmpty());
	}

	@Test
	public void pivotV2Tab1Test() {
		final String query = "select * from tab1 "
		+"\n pivot (sum(col1) sum, avg(col2) ave, count(col3) cnt,"
		+"\n max(col4) max, min(col5) min for col2 in (sales as A, units as B))";

		final SQLSelectParserParser parser = parse(query);
		ParserRunResult runResult = runSQLParsertestAllowErrors(query, parser);
		SqlParseEventWalker extractor = runResult.getExtractor();

		Assert.assertNotNull(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=*, table_ref=*}}}, from={pivot={value={1={function={function_name=sum, parameters={column={name=col1, table_ref=null}}}, alias=sum}, 2={function={function_name=avg, parameters={column={name=col2, table_ref=null}}}, alias=ave}, 3={function={function_name=count, parameters={column={name=col3, table_ref=null}}}, alias=cnt}, 4={function={function_name=max, parameters={column={name=col4, table_ref=null}}}, alias=max}, 5={function={function_name=min, parameters={column={name=col5, table_ref=null}}}, alias=min}}, for={column={name=col2, table_ref=null}}, in={1={pivot_literal=sales, pivot_prefix=A}, 2={pivot_literal=units, pivot_prefix=B}}}, table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={col4=[[@26,80:83='col4',<381>,3:5]], *=[[@1,7:7='*',<291>,1:7]], col5=[[@32,95:98='col5',<381>,3:20]], col2=[[@36,109:112='col2',<381>,3:34], [@14,47:50='col2',<381>,2:27]], col3=[[@20,64:67='col3',<381>,2:44]], col1=[[@8,32:35='col1',<381>,2:12]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query1={*=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query1={query_dictionary={*=[[@1,7:7='*',<291>,1:7]]}, table_dictionary={tab1={col4=[[@26,80:83='col4',<381>,3:5]], *=[[@1,7:7='*',<291>,1:7]], col5=[[@32,95:98='col5',<381>,3:20]], col2=[[@36,109:112='col2',<381>,3:34], [@14,47:50='col2',<381>,2:27]], col3=[[@20,64:67='col3',<381>,2:44]], col1=[[@8,32:35='col1',<381>,2:12]]}}, derivation={source_columns={tuple_0=[{name=col2, table_ref=tab1}, {name=col1, table_ref=tab1}, {name=col3, table_ref=tab1}, {name=col4, table_ref=tab1}, {name=col5, table_ref=tab1}]}, derived_columns={tuple_0={A_sum=[[@6,28:30='sum',<141>,2:8], [@39,118:122='sales',<381>,3:43]], A_ave=[[@12,43:45='avg',<61>,2:23], [@39,118:122='sales',<381>,3:43]], A_cnt=[[@18,58:62='count',<69>,2:38], [@39,118:122='sales',<381>,3:43]], A_max=[[@24,76:78='max',<108>,3:1], [@39,118:122='sales',<381>,3:43]], A_min=[[@30,91:93='min',<113>,3:16], [@39,118:122='sales',<381>,3:43]], B_sum=[[@6,28:30='sum',<141>,2:8], [@43,130:134='units',<381>,3:55]], B_ave=[[@12,43:45='avg',<61>,2:23], [@43,130:134='units',<381>,3:55]], B_cnt=[[@18,58:62='count',<69>,2:38], [@43,130:134='units',<381>,3:55]], B_max=[[@24,76:78='max',<108>,3:1], [@43,130:134='units',<381>,3:55]], B_min=[[@30,91:93='min',<113>,3:16], [@43,130:134='units',<381>,3:55]]}}}, interface={*=[{name=*, table_ref=*}]}, table_alias={tuple_0=tab1}}}",
				extractor.getSymbolTable().toString());
		assertFatalDiagnosticAtPosition(
				extractor.getSnippet(),
				"PIVOT_IN_IDENTIFIER_UNRESOLVED",
				"cannot be resolved against the PIVOT source",
				"sales",
				3,
				43);
		assertFatalDiagnosticAtPosition(
				extractor.getSnippet(),
				"PIVOT_IN_IDENTIFIER_UNRESOLVED",
				"cannot be resolved against the PIVOT source",
				"units",
				3,
				55);

	}

	@Test
	public void pivotV2Tab1QuotedSelectorsSuccessTest() {
		final String query = "select * from tab1 "
		+"\n pivot (sum(col1) sum, avg(col2) ave, count(col3) cnt,"
		+"\n max(col4) max, min(col5) min for col2 in ('sales' as A, 'units' as B))";

		final SQLSelectParserParser parser = parse(query);
		ParserRunResult runResult = runSQLParsertestAllowErrors(query, parser);
		SqlParseEventWalker extractor = runResult.getExtractor();

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=*, table_ref=*}}}, from={pivot={value={1={function={function_name=sum, parameters={column={name=col1, table_ref=null}}}, alias=sum}, 2={function={function_name=avg, parameters={column={name=col2, table_ref=null}}}, alias=ave}, 3={function={function_name=count, parameters={column={name=col3, table_ref=null}}}, alias=cnt}, 4={function={function_name=max, parameters={column={name=col4, table_ref=null}}}, alias=max}, 5={function={function_name=min, parameters={column={name=col5, table_ref=null}}}, alias=min}}, for={column={name=col2, table_ref=null}}, in={1={pivot_literal='sales', pivot_prefix=A}, 2={pivot_literal='units', pivot_prefix=B}}}, table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={col4=[[@26,80:83='col4',<381>,3:5]], *=[[@1,7:7='*',<291>,1:7]], col5=[[@32,95:98='col5',<381>,3:20]], col2=[[@36,109:112='col2',<381>,3:34], [@14,47:50='col2',<381>,2:27]], col3=[[@20,64:67='col3',<381>,2:44]], col1=[[@8,32:35='col1',<381>,2:12]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query1={*=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query1={query_dictionary={*=[[@1,7:7='*',<291>,1:7]]}, table_dictionary={tab1={col4=[[@26,80:83='col4',<381>,3:5]], *=[[@1,7:7='*',<291>,1:7]], col5=[[@32,95:98='col5',<381>,3:20]], col2=[[@36,109:112='col2',<381>,3:34], [@14,47:50='col2',<381>,2:27]], col3=[[@20,64:67='col3',<381>,2:44]], col1=[[@8,32:35='col1',<381>,2:12]]}}, derivation={source_columns={tuple_0=[{name=col2, table_ref=tab1}, {name=col1, table_ref=tab1}, {name=col3, table_ref=tab1}, {name=col4, table_ref=tab1}, {name=col5, table_ref=tab1}]}, derived_columns={tuple_0={A_sum=[[@6,28:30='sum',<141>,2:8], [@39,118:124=''sales'',<389>,3:43]], A_ave=[[@12,43:45='avg',<61>,2:23], [@39,118:124=''sales'',<389>,3:43]], A_cnt=[[@18,58:62='count',<69>,2:38], [@39,118:124=''sales'',<389>,3:43]], A_max=[[@24,76:78='max',<108>,3:1], [@39,118:124=''sales'',<389>,3:43]], A_min=[[@30,91:93='min',<113>,3:16], [@39,118:124=''sales'',<389>,3:43]], B_sum=[[@6,28:30='sum',<141>,2:8], [@43,132:138=''units'',<389>,3:57]], B_ave=[[@12,43:45='avg',<61>,2:23], [@43,132:138=''units'',<389>,3:57]], B_cnt=[[@18,58:62='count',<69>,2:38], [@43,132:138=''units'',<389>,3:57]], B_max=[[@24,76:78='max',<108>,3:1], [@43,132:138=''units'',<389>,3:57]], B_min=[[@30,91:93='min',<113>,3:16], [@43,132:138=''units'',<389>,3:57]]}}}, interface={*=[{name=*, table_ref=*}]}, table_alias={tuple_0=tab1}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void pivotV2QueryTest() {
		final String query = "select A_sum, A_ave, A_cnt, A_max, A_min, "
		+ "\n B_sum, B_ave, B_cnt, B_max, B_min "
		+ "\nfrom (select col1, col2, col3, col4, col5 from tab1) q "
		+"\n pivot (sum(col1) sum, avg(col2) ave, count(col3) cnt,"
		+"\n max(col4) max, min(col5) min for col2 in (sales as A, units as B))";

		final SQLSelectParserParser parser = parse(query);
		ParserRunResult runResult = runSQLParsertestAllowErrors(query, parser);
		SqlParseEventWalker extractor = runResult.getExtractor();

		Assert.assertNotNull(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=A_sum, table_ref=null}}, 2={column={name=A_ave, table_ref=null}}, 3={column={name=A_cnt, table_ref=null}}, 4={column={name=A_max, table_ref=null}}, 5={column={name=A_min, table_ref=null}}, 6={column={name=B_sum, table_ref=null}}, 7={column={name=B_ave, table_ref=null}}, 8={column={name=B_cnt, table_ref=null}}, 9={column={name=B_max, table_ref=null}}, 10={column={name=B_min, table_ref=null}}}, from={pivot={value={1={function={function_name=sum, parameters={column={name=col1, table_ref=null}}}, alias=sum}, 2={function={function_name=avg, parameters={column={name=col2, table_ref=null}}}, alias=ave}, 3={function={function_name=count, parameters={column={name=col3, table_ref=null}}}, alias=cnt}, 4={function={function_name=max, parameters={column={name=col4, table_ref=null}}}, alias=max}, 5={function={function_name=min, parameters={column={name=col5, table_ref=null}}}, alias=min}}, for={column={name=col2, table_ref=null}}, in={1={pivot_literal=sales, pivot_prefix=A}, 2={pivot_literal=units, pivot_prefix=B}}}, table={alias=q, query={select={1={column={name=col1, table_ref=null}}, 2={column={name=col2, table_ref=null}}, 3={column={name=col3, table_ref=null}}, 4={column={name=col4, table_ref=null}}, 5={column={name=col5, table_ref=null}}}, from={table={alias=null, table=tab1}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[B_sum, A_ave, B_cnt, A_min, A_max, B_max, A_cnt, B_min, A_sum, B_ave]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{q={col4=[[@58,195:198='col4',<381>,5:5]], col5=[[@64,210:213='col5',<381>,5:20]], col2=[[@68,224:227='col2',<381>,5:34], [@46,162:165='col2',<381>,4:27]], col3=[[@52,179:182='col3',<381>,4:44]], col1=[[@40,147:150='col1',<381>,4:12]]}, tab1={col4=[[@29,110:113='col4',<381>,3:31]], col5=[[@31,116:119='col5',<381>,3:37]], col2=[[@25,98:101='col2',<381>,3:19]], col3=[[@27,104:107='col3',<381>,3:25]], col1=[[@23,92:95='col1',<381>,3:13]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
		 "{query0={col4=[[@29,110:113='col4',<381>,3:31]], col5=[[@31,116:119='col5',<381>,3:37]], col2=[[@25,98:101='col2',<381>,3:19]], col3=[[@27,104:107='col3',<381>,3:25]], col1=[[@23,92:95='col1',<381>,3:13]]}, query2={B_sum=[[@11,44:48='B_sum',<381>,2:1], [@38,143:145='sum',<141>,4:8], [@75,245:249='units',<381>,5:55]], A_ave=[[@3,14:18='A_ave',<381>,1:14], [@44,158:160='avg',<61>,4:23], [@71,233:237='sales',<381>,5:43]], B_cnt=[[@15,58:62='B_cnt',<381>,2:15], [@50,173:177='count',<69>,4:38], [@75,245:249='units',<381>,5:55]], A_min=[[@9,35:39='A_min',<381>,1:35], [@62,206:208='min',<113>,5:16], [@71,233:237='sales',<381>,5:43]], A_max=[[@7,28:32='A_max',<381>,1:28], [@56,191:193='max',<108>,5:1], [@71,233:237='sales',<381>,5:43]], B_max=[[@17,65:69='B_max',<381>,2:22], [@56,191:193='max',<108>,5:1], [@75,245:249='units',<381>,5:55]], A_cnt=[[@5,21:25='A_cnt',<381>,1:21], [@50,173:177='count',<69>,4:38], [@71,233:237='sales',<381>,5:43]], B_min=[[@19,72:76='B_min',<381>,2:29], [@62,206:208='min',<113>,5:16], [@75,245:249='units',<381>,5:55]], A_sum=[[@1,7:11='A_sum',<381>,1:7], [@38,143:145='sum',<141>,4:8], [@71,233:237='sales',<381>,5:43]], B_ave=[[@13,51:55='B_ave',<381>,2:8], [@44,158:160='avg',<61>,4:23], [@75,245:249='units',<381>,5:55]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query2={query_dictionary={B_sum=[[@11,44:48='B_sum',<381>,2:1], [@38,143:145='sum',<141>,4:8], [@75,245:249='units',<381>,5:55]], A_ave=[[@3,14:18='A_ave',<381>,1:14], [@44,158:160='avg',<61>,4:23], [@71,233:237='sales',<381>,5:43]], B_cnt=[[@15,58:62='B_cnt',<381>,2:15], [@50,173:177='count',<69>,4:38], [@75,245:249='units',<381>,5:55]], A_min=[[@9,35:39='A_min',<381>,1:35], [@62,206:208='min',<113>,5:16], [@71,233:237='sales',<381>,5:43]], A_max=[[@7,28:32='A_max',<381>,1:28], [@56,191:193='max',<108>,5:1], [@71,233:237='sales',<381>,5:43]], B_max=[[@17,65:69='B_max',<381>,2:22], [@56,191:193='max',<108>,5:1], [@75,245:249='units',<381>,5:55]], A_cnt=[[@5,21:25='A_cnt',<381>,1:21], [@50,173:177='count',<69>,4:38], [@71,233:237='sales',<381>,5:43]], B_min=[[@19,72:76='B_min',<381>,2:29], [@62,206:208='min',<113>,5:16], [@75,245:249='units',<381>,5:55]], A_sum=[[@1,7:11='A_sum',<381>,1:7], [@38,143:145='sum',<141>,4:8], [@71,233:237='sales',<381>,5:43]], B_ave=[[@13,51:55='B_ave',<381>,2:8], [@44,158:160='avg',<61>,4:23], [@75,245:249='units',<381>,5:55]]}, table_dictionary={q={col4=[[@58,195:198='col4',<381>,5:5]], col5=[[@64,210:213='col5',<381>,5:20]], col2=[[@68,224:227='col2',<381>,5:34], [@46,162:165='col2',<381>,4:27]], col3=[[@52,179:182='col3',<381>,4:44]], col1=[[@40,147:150='col1',<381>,4:12]]}}, def_query0={query_dictionary={col4=[[@29,110:113='col4',<381>,3:31]], col5=[[@31,116:119='col5',<381>,3:37]], col2=[[@25,98:101='col2',<381>,3:19]], col3=[[@27,104:107='col3',<381>,3:25]], col1=[[@23,92:95='col1',<381>,3:13]]}, table_dictionary={tab1={col4=[[@29,110:113='col4',<381>,3:31]], col5=[[@31,116:119='col5',<381>,3:37]], col2=[[@25,98:101='col2',<381>,3:19]], col3=[[@27,104:107='col3',<381>,3:25]], col1=[[@23,92:95='col1',<381>,3:13]]}}, interface={col4=[{name=col4, table_ref=tab1}], col5=[{name=col5, table_ref=tab1}], col2=[{name=col2, table_ref=tab1}], col3=[{name=col3, table_ref=tab1}], col1=[{name=col1, table_ref=tab1}]}}, derivation={source_columns={tuple_0=[{name=col2, table_ref=q}, {name=col1, table_ref=q}, {name=col3, table_ref=q}, {name=col4, table_ref=q}, {name=col5, table_ref=q}]}, derived_columns={tuple_0={A_sum=[[@38,143:145='sum',<141>,4:8], [@71,233:237='sales',<381>,5:43]], A_ave=[[@44,158:160='avg',<61>,4:23], [@71,233:237='sales',<381>,5:43]], A_cnt=[[@50,173:177='count',<69>,4:38], [@71,233:237='sales',<381>,5:43]], A_max=[[@56,191:193='max',<108>,5:1], [@71,233:237='sales',<381>,5:43]], A_min=[[@62,206:208='min',<113>,5:16], [@71,233:237='sales',<381>,5:43]], B_sum=[[@38,143:145='sum',<141>,4:8], [@75,245:249='units',<381>,5:55]], B_ave=[[@44,158:160='avg',<61>,4:23], [@75,245:249='units',<381>,5:55]], B_cnt=[[@50,173:177='count',<69>,4:38], [@75,245:249='units',<381>,5:55]], B_max=[[@56,191:193='max',<108>,5:1], [@75,245:249='units',<381>,5:55]], B_min=[[@62,206:208='min',<113>,5:16], [@75,245:249='units',<381>,5:55]]}}}, interface={B_sum=[{name=B_sum, table_ref=tuple_0}, {name=col2, table_ref=q}, {name=col1, table_ref=q}, {name=col3, table_ref=q}, {name=col4, table_ref=q}, {name=col5, table_ref=q}], A_ave=[{name=A_ave, table_ref=tuple_0}, {name=col2, table_ref=q}, {name=col1, table_ref=q}, {name=col3, table_ref=q}, {name=col4, table_ref=q}, {name=col5, table_ref=q}], B_cnt=[{name=B_cnt, table_ref=tuple_0}, {name=col2, table_ref=q}, {name=col1, table_ref=q}, {name=col3, table_ref=q}, {name=col4, table_ref=q}, {name=col5, table_ref=q}], A_min=[{name=A_min, table_ref=tuple_0}, {name=col2, table_ref=q}, {name=col1, table_ref=q}, {name=col3, table_ref=q}, {name=col4, table_ref=q}, {name=col5, table_ref=q}], A_max=[{name=A_max, table_ref=tuple_0}, {name=col2, table_ref=q}, {name=col1, table_ref=q}, {name=col3, table_ref=q}, {name=col4, table_ref=q}, {name=col5, table_ref=q}], B_max=[{name=B_max, table_ref=tuple_0}, {name=col2, table_ref=q}, {name=col1, table_ref=q}, {name=col3, table_ref=q}, {name=col4, table_ref=q}, {name=col5, table_ref=q}], A_cnt=[{name=A_cnt, table_ref=tuple_0}, {name=col2, table_ref=q}, {name=col1, table_ref=q}, {name=col3, table_ref=q}, {name=col4, table_ref=q}, {name=col5, table_ref=q}], B_min=[{name=B_min, table_ref=tuple_0}, {name=col2, table_ref=q}, {name=col1, table_ref=q}, {name=col3, table_ref=q}, {name=col4, table_ref=q}, {name=col5, table_ref=q}], A_sum=[{name=A_sum, table_ref=tuple_0}, {name=col2, table_ref=q}, {name=col1, table_ref=q}, {name=col3, table_ref=q}, {name=col4, table_ref=q}, {name=col5, table_ref=q}], B_ave=[{name=B_ave, table_ref=tuple_0}, {name=col2, table_ref=q}, {name=col1, table_ref=q}, {name=col3, table_ref=q}, {name=col4, table_ref=q}, {name=col5, table_ref=q}]}, table_alias={q=query0, tuple_0=q}}}",
				extractor.getSymbolTable().toString());
		assertFatalDiagnosticAtPosition(
				extractor.getSnippet(),
				"PIVOT_IN_IDENTIFIER_UNRESOLVED",
				"cannot be resolved against the PIVOT source",
				"sales",
				5,
				43);
		assertFatalDiagnosticAtPosition(
				extractor.getSnippet(),
				"PIVOT_IN_IDENTIFIER_UNRESOLVED",
				"cannot be resolved against the PIVOT source",
				"units",
				5,
				55);

	}

	@Test
	public void pivotV2QueryQuotedSelectorsSuccessTest() {
		final String query = "select A_sum, A_ave, A_cnt, A_max, A_min, "
		+ "\n B_sum, B_ave, B_cnt, B_max, B_min "
		+ "\nfrom (select col1, col2, col3, col4, col5 from tab1) q "
		+"\n pivot (sum(col1) sum, avg(col2) ave, count(col3) cnt,"
		+"\n max(col4) max, min(col5) min for col2 in ('sales' as A, 'units' as B))";

		final SQLSelectParserParser parser = parse(query);
		ParserRunResult runResult = runSQLParsertestAllowErrors(query, parser);
		SqlParseEventWalker extractor = runResult.getExtractor();

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=A_sum, table_ref=null}}, 2={column={name=A_ave, table_ref=null}}, 3={column={name=A_cnt, table_ref=null}}, 4={column={name=A_max, table_ref=null}}, 5={column={name=A_min, table_ref=null}}, 6={column={name=B_sum, table_ref=null}}, 7={column={name=B_ave, table_ref=null}}, 8={column={name=B_cnt, table_ref=null}}, 9={column={name=B_max, table_ref=null}}, 10={column={name=B_min, table_ref=null}}}, from={pivot={value={1={function={function_name=sum, parameters={column={name=col1, table_ref=null}}}, alias=sum}, 2={function={function_name=avg, parameters={column={name=col2, table_ref=null}}}, alias=ave}, 3={function={function_name=count, parameters={column={name=col3, table_ref=null}}}, alias=cnt}, 4={function={function_name=max, parameters={column={name=col4, table_ref=null}}}, alias=max}, 5={function={function_name=min, parameters={column={name=col5, table_ref=null}}}, alias=min}}, for={column={name=col2, table_ref=null}}, in={1={pivot_literal='sales', pivot_prefix=A}, 2={pivot_literal='units', pivot_prefix=B}}}, table={alias=q, query={select={1={column={name=col1, table_ref=null}}, 2={column={name=col2, table_ref=null}}, 3={column={name=col3, table_ref=null}}, 4={column={name=col4, table_ref=null}}, 5={column={name=col5, table_ref=null}}}, from={table={alias=null, table=tab1}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[B_sum, A_ave, B_cnt, A_min, A_max, B_max, A_cnt, B_min, A_sum, B_ave]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{q={col4=[[@58,195:198='col4',<381>,5:5]], col5=[[@64,210:213='col5',<381>,5:20]], col2=[[@68,224:227='col2',<381>,5:34], [@46,162:165='col2',<381>,4:27]], col3=[[@52,179:182='col3',<381>,4:44]], col1=[[@40,147:150='col1',<381>,4:12]]}, tab1={col4=[[@29,110:113='col4',<381>,3:31]], col5=[[@31,116:119='col5',<381>,3:37]], col2=[[@25,98:101='col2',<381>,3:19]], col3=[[@27,104:107='col3',<381>,3:25]], col1=[[@23,92:95='col1',<381>,3:13]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
		 "{query0={col4=[[@29,110:113='col4',<381>,3:31]], col5=[[@31,116:119='col5',<381>,3:37]], col2=[[@25,98:101='col2',<381>,3:19]], col3=[[@27,104:107='col3',<381>,3:25]], col1=[[@23,92:95='col1',<381>,3:13]]}, query2={B_sum=[[@11,44:48='B_sum',<381>,2:1], [@38,143:145='sum',<141>,4:8], [@75,247:253=''units'',<389>,5:57]], A_ave=[[@3,14:18='A_ave',<381>,1:14], [@44,158:160='avg',<61>,4:23], [@71,233:239=''sales'',<389>,5:43]], B_cnt=[[@15,58:62='B_cnt',<381>,2:15], [@50,173:177='count',<69>,4:38], [@75,247:253=''units'',<389>,5:57]], A_min=[[@9,35:39='A_min',<381>,1:35], [@62,206:208='min',<113>,5:16], [@71,233:239=''sales'',<389>,5:43]], A_max=[[@7,28:32='A_max',<381>,1:28], [@56,191:193='max',<108>,5:1], [@71,233:239=''sales'',<389>,5:43]], B_max=[[@17,65:69='B_max',<381>,2:22], [@56,191:193='max',<108>,5:1], [@75,247:253=''units'',<389>,5:57]], A_cnt=[[@5,21:25='A_cnt',<381>,1:21], [@50,173:177='count',<69>,4:38], [@71,233:239=''sales'',<389>,5:43]], B_min=[[@19,72:76='B_min',<381>,2:29], [@62,206:208='min',<113>,5:16], [@75,247:253=''units'',<389>,5:57]], A_sum=[[@1,7:11='A_sum',<381>,1:7], [@38,143:145='sum',<141>,4:8], [@71,233:239=''sales'',<389>,5:43]], B_ave=[[@13,51:55='B_ave',<381>,2:8], [@44,158:160='avg',<61>,4:23], [@75,247:253=''units'',<389>,5:57]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query2={query_dictionary={B_sum=[[@11,44:48='B_sum',<381>,2:1], [@38,143:145='sum',<141>,4:8], [@75,247:253=''units'',<389>,5:57]], A_ave=[[@3,14:18='A_ave',<381>,1:14], [@44,158:160='avg',<61>,4:23], [@71,233:239=''sales'',<389>,5:43]], B_cnt=[[@15,58:62='B_cnt',<381>,2:15], [@50,173:177='count',<69>,4:38], [@75,247:253=''units'',<389>,5:57]], A_min=[[@9,35:39='A_min',<381>,1:35], [@62,206:208='min',<113>,5:16], [@71,233:239=''sales'',<389>,5:43]], A_max=[[@7,28:32='A_max',<381>,1:28], [@56,191:193='max',<108>,5:1], [@71,233:239=''sales'',<389>,5:43]], B_max=[[@17,65:69='B_max',<381>,2:22], [@56,191:193='max',<108>,5:1], [@75,247:253=''units'',<389>,5:57]], A_cnt=[[@5,21:25='A_cnt',<381>,1:21], [@50,173:177='count',<69>,4:38], [@71,233:239=''sales'',<389>,5:43]], B_min=[[@19,72:76='B_min',<381>,2:29], [@62,206:208='min',<113>,5:16], [@75,247:253=''units'',<389>,5:57]], A_sum=[[@1,7:11='A_sum',<381>,1:7], [@38,143:145='sum',<141>,4:8], [@71,233:239=''sales'',<389>,5:43]], B_ave=[[@13,51:55='B_ave',<381>,2:8], [@44,158:160='avg',<61>,4:23], [@75,247:253=''units'',<389>,5:57]]}, table_dictionary={q={col4=[[@58,195:198='col4',<381>,5:5]], col5=[[@64,210:213='col5',<381>,5:20]], col2=[[@68,224:227='col2',<381>,5:34], [@46,162:165='col2',<381>,4:27]], col3=[[@52,179:182='col3',<381>,4:44]], col1=[[@40,147:150='col1',<381>,4:12]]}}, def_query0={query_dictionary={col4=[[@29,110:113='col4',<381>,3:31]], col5=[[@31,116:119='col5',<381>,3:37]], col2=[[@25,98:101='col2',<381>,3:19]], col3=[[@27,104:107='col3',<381>,3:25]], col1=[[@23,92:95='col1',<381>,3:13]]}, table_dictionary={tab1={col4=[[@29,110:113='col4',<381>,3:31]], col5=[[@31,116:119='col5',<381>,3:37]], col2=[[@25,98:101='col2',<381>,3:19]], col3=[[@27,104:107='col3',<381>,3:25]], col1=[[@23,92:95='col1',<381>,3:13]]}}, interface={col4=[{name=col4, table_ref=tab1}], col5=[{name=col5, table_ref=tab1}], col2=[{name=col2, table_ref=tab1}], col3=[{name=col3, table_ref=tab1}], col1=[{name=col1, table_ref=tab1}]}}, derivation={source_columns={tuple_0=[{name=col2, table_ref=q}, {name=col1, table_ref=q}, {name=col3, table_ref=q}, {name=col4, table_ref=q}, {name=col5, table_ref=q}]}, derived_columns={tuple_0={A_sum=[[@38,143:145='sum',<141>,4:8], [@71,233:239=''sales'',<389>,5:43]], A_ave=[[@44,158:160='avg',<61>,4:23], [@71,233:239=''sales'',<389>,5:43]], A_cnt=[[@50,173:177='count',<69>,4:38], [@71,233:239=''sales'',<389>,5:43]], A_max=[[@56,191:193='max',<108>,5:1], [@71,233:239=''sales'',<389>,5:43]], A_min=[[@62,206:208='min',<113>,5:16], [@71,233:239=''sales'',<389>,5:43]], B_sum=[[@38,143:145='sum',<141>,4:8], [@75,247:253=''units'',<389>,5:57]], B_ave=[[@44,158:160='avg',<61>,4:23], [@75,247:253=''units'',<389>,5:57]], B_cnt=[[@50,173:177='count',<69>,4:38], [@75,247:253=''units'',<389>,5:57]], B_max=[[@56,191:193='max',<108>,5:1], [@75,247:253=''units'',<389>,5:57]], B_min=[[@62,206:208='min',<113>,5:16], [@75,247:253=''units'',<389>,5:57]]}}}, interface={B_sum=[{name=B_sum, table_ref=tuple_0}, {name=col2, table_ref=q}, {name=col1, table_ref=q}, {name=col3, table_ref=q}, {name=col4, table_ref=q}, {name=col5, table_ref=q}], A_ave=[{name=A_ave, table_ref=tuple_0}, {name=col2, table_ref=q}, {name=col1, table_ref=q}, {name=col3, table_ref=q}, {name=col4, table_ref=q}, {name=col5, table_ref=q}], B_cnt=[{name=B_cnt, table_ref=tuple_0}, {name=col2, table_ref=q}, {name=col1, table_ref=q}, {name=col3, table_ref=q}, {name=col4, table_ref=q}, {name=col5, table_ref=q}], A_min=[{name=A_min, table_ref=tuple_0}, {name=col2, table_ref=q}, {name=col1, table_ref=q}, {name=col3, table_ref=q}, {name=col4, table_ref=q}, {name=col5, table_ref=q}], A_max=[{name=A_max, table_ref=tuple_0}, {name=col2, table_ref=q}, {name=col1, table_ref=q}, {name=col3, table_ref=q}, {name=col4, table_ref=q}, {name=col5, table_ref=q}], B_max=[{name=B_max, table_ref=tuple_0}, {name=col2, table_ref=q}, {name=col1, table_ref=q}, {name=col3, table_ref=q}, {name=col4, table_ref=q}, {name=col5, table_ref=q}], A_cnt=[{name=A_cnt, table_ref=tuple_0}, {name=col2, table_ref=q}, {name=col1, table_ref=q}, {name=col3, table_ref=q}, {name=col4, table_ref=q}, {name=col5, table_ref=q}], B_min=[{name=B_min, table_ref=tuple_0}, {name=col2, table_ref=q}, {name=col1, table_ref=q}, {name=col3, table_ref=q}, {name=col4, table_ref=q}, {name=col5, table_ref=q}], A_sum=[{name=A_sum, table_ref=tuple_0}, {name=col2, table_ref=q}, {name=col1, table_ref=q}, {name=col3, table_ref=q}, {name=col4, table_ref=q}, {name=col5, table_ref=q}], B_ave=[{name=B_ave, table_ref=tuple_0}, {name=col2, table_ref=q}, {name=col1, table_ref=q}, {name=col3, table_ref=q}, {name=col4, table_ref=q}, {name=col5, table_ref=q}]}, table_alias={q=query0, tuple_0=q}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void pivotV3Tab1Test() {
		final String query = "select * from tab1 "
		+"\n pivot (sum(col1) sums, avg(col2) ave, count(col3) cnts,"
		+"\n max(col4) max, min(col5) min for col2 in ('sales' as A, 'units' as B))";

		final SQLSelectParserParser parser = parse(query);
		ParserRunResult runResult = runSQLParsertestAllowErrors(query, parser);
		SqlParseEventWalker extractor = runResult.getExtractor();

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=*, table_ref=*}}}, from={pivot={value={1={function={function_name=sum, parameters={column={name=col1, table_ref=null}}}, alias=sums}, 2={function={function_name=avg, parameters={column={name=col2, table_ref=null}}}, alias=ave}, 3={function={function_name=count, parameters={column={name=col3, table_ref=null}}}, alias=cnts}, 4={function={function_name=max, parameters={column={name=col4, table_ref=null}}}, alias=max}, 5={function={function_name=min, parameters={column={name=col5, table_ref=null}}}, alias=min}}, for={column={name=col2, table_ref=null}}, in={1={pivot_literal='sales', pivot_prefix=A}, 2={pivot_literal='units', pivot_prefix=B}}}, table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{tab1={col4=[[@26,82:85='col4',<381>,3:5]], *=[[@1,7:7='*',<291>,1:7]], col5=[[@32,97:100='col5',<381>,3:20]], col2=[[@36,111:114='col2',<381>,3:34], [@14,48:51='col2',<381>,2:28]], col3=[[@20,65:68='col3',<381>,2:45]], col1=[[@8,32:35='col1',<381>,2:12]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query1={*=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query1={query_dictionary={*=[[@1,7:7='*',<291>,1:7]]}, table_dictionary={tab1={col4=[[@26,82:85='col4',<381>,3:5]], *=[[@1,7:7='*',<291>,1:7]], col5=[[@32,97:100='col5',<381>,3:20]], col2=[[@36,111:114='col2',<381>,3:34], [@14,48:51='col2',<381>,2:28]], col3=[[@20,65:68='col3',<381>,2:45]], col1=[[@8,32:35='col1',<381>,2:12]]}}, derivation={source_columns={tuple_0=[{name=col2, table_ref=tab1}, {name=col1, table_ref=tab1}, {name=col3, table_ref=tab1}, {name=col4, table_ref=tab1}, {name=col5, table_ref=tab1}]}, derived_columns={tuple_0={A_sums=[[@6,28:30='sum',<141>,2:8], [@39,120:126=''sales'',<389>,3:43]], A_ave=[[@12,44:46='avg',<61>,2:24], [@39,120:126=''sales'',<389>,3:43]], A_cnts=[[@18,59:63='count',<69>,2:39], [@39,120:126=''sales'',<389>,3:43]], A_max=[[@24,78:80='max',<108>,3:1], [@39,120:126=''sales'',<389>,3:43]], A_min=[[@30,93:95='min',<113>,3:16], [@39,120:126=''sales'',<389>,3:43]], B_sums=[[@6,28:30='sum',<141>,2:8], [@43,134:140=''units'',<389>,3:57]], B_ave=[[@12,44:46='avg',<61>,2:24], [@43,134:140=''units'',<389>,3:57]], B_cnts=[[@18,59:63='count',<69>,2:39], [@43,134:140=''units'',<389>,3:57]], B_max=[[@24,78:80='max',<108>,3:1], [@43,134:140=''units'',<389>,3:57]], B_min=[[@30,93:95='min',<113>,3:16], [@43,134:140=''units'',<389>,3:57]]}}}, interface={*=[{name=*, table_ref=*}]}, table_alias={tuple_0=tab1}}}",
				extractor.getSymbolTable().toString());
		Assert.assertTrue("Expected PIVOT IN item prefixes to be preserved",
				extractor.getAsTree().toString().contains("pivot_prefix=A")
					&& extractor.getAsTree().toString().contains("pivot_prefix=B"));
	}

	@Test
	public void pivotV3QueryTest() {
		final String query = "select A_sums, A_ave, A_cnts, A_max, A_min, B_sums, B_ave, B_cnts, B_max, B_min from (select col1, col2, col3, col4, col5 from tab1) q "
		+"\n pivot (sum(col1) sums, avg(col2) ave, count(col3) cnts,"
		+"\n max(col4) max, min(col5) min for col2 in ('sales' as A, 'units' as B))";

		final SQLSelectParserParser parser = parse(query);
		ParserRunResult runResult = runSQLParsertestAllowErrors(query, parser);
		SqlParseEventWalker extractor = runResult.getExtractor();

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=A_sums, table_ref=null}}, 2={column={name=A_ave, table_ref=null}}, 3={column={name=A_cnts, table_ref=null}}, 4={column={name=A_max, table_ref=null}}, 5={column={name=A_min, table_ref=null}}, 6={column={name=B_sums, table_ref=null}}, 7={column={name=B_ave, table_ref=null}}, 8={column={name=B_cnts, table_ref=null}}, 9={column={name=B_max, table_ref=null}}, 10={column={name=B_min, table_ref=null}}}, from={pivot={value={1={function={function_name=sum, parameters={column={name=col1, table_ref=null}}}, alias=sums}, 2={function={function_name=avg, parameters={column={name=col2, table_ref=null}}}, alias=ave}, 3={function={function_name=count, parameters={column={name=col3, table_ref=null}}}, alias=cnts}, 4={function={function_name=max, parameters={column={name=col4, table_ref=null}}}, alias=max}, 5={function={function_name=min, parameters={column={name=col5, table_ref=null}}}, alias=min}}, for={column={name=col2, table_ref=null}}, in={1={pivot_literal='sales', pivot_prefix=A}, 2={pivot_literal='units', pivot_prefix=B}}}, table={alias=q, query={select={1={column={name=col1, table_ref=null}}, 2={column={name=col2, table_ref=null}}, 3={column={name=col3, table_ref=null}}, 4={column={name=col4, table_ref=null}}, 5={column={name=col5, table_ref=null}}}, from={table={alias=null, table=tab1}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[A_ave, A_min, B_sums, A_max, B_max, B_min, A_cnts, A_sums, B_ave, B_cnts]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{q={col4=[[@58,198:201='col4',<381>,3:5]], col5=[[@64,213:216='col5',<381>,3:20]], col2=[[@68,227:230='col2',<381>,3:34], [@46,164:167='col2',<381>,2:28]], col3=[[@52,181:184='col3',<381>,2:45]], col1=[[@40,148:151='col1',<381>,2:12]]}, tab1={col4=[[@29,111:114='col4',<381>,1:111]], col5=[[@31,117:120='col5',<381>,1:117]], col2=[[@25,99:102='col2',<381>,1:99]], col3=[[@27,105:108='col3',<381>,1:105]], col1=[[@23,93:96='col1',<381>,1:93]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={col4=[[@29,111:114='col4',<381>,1:111]], col5=[[@31,117:120='col5',<381>,1:117]], col2=[[@25,99:102='col2',<381>,1:99]], col3=[[@27,105:108='col3',<381>,1:105]], col1=[[@23,93:96='col1',<381>,1:93]]}, query2={A_ave=[[@3,15:19='A_ave',<381>,1:15], [@44,160:162='avg',<61>,2:24], [@71,236:242=''sales'',<389>,3:43]], A_min=[[@9,37:41='A_min',<381>,1:37], [@62,209:211='min',<113>,3:16], [@71,236:242=''sales'',<389>,3:43]], B_sums=[[@11,44:49='B_sums',<381>,1:44], [@38,144:146='sum',<141>,2:8], [@75,250:256=''units'',<389>,3:57]], A_max=[[@7,30:34='A_max',<381>,1:30], [@56,194:196='max',<108>,3:1], [@71,236:242=''sales'',<389>,3:43]], B_max=[[@17,67:71='B_max',<381>,1:67], [@56,194:196='max',<108>,3:1], [@75,250:256=''units'',<389>,3:57]], B_min=[[@19,74:78='B_min',<381>,1:74], [@62,209:211='min',<113>,3:16], [@75,250:256=''units'',<389>,3:57]], A_cnts=[[@5,22:27='A_cnts',<381>,1:22], [@50,175:179='count',<69>,2:39], [@71,236:242=''sales'',<389>,3:43]], A_sums=[[@1,7:12='A_sums',<381>,1:7], [@38,144:146='sum',<141>,2:8], [@71,236:242=''sales'',<389>,3:43]], B_ave=[[@13,52:56='B_ave',<381>,1:52], [@44,160:162='avg',<61>,2:24], [@75,250:256=''units'',<389>,3:57]], B_cnts=[[@15,59:64='B_cnts',<381>,1:59], [@50,175:179='count',<69>,2:39], [@75,250:256=''units'',<389>,3:57]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query2={query_dictionary={A_ave=[[@3,15:19='A_ave',<381>,1:15], [@44,160:162='avg',<61>,2:24], [@71,236:242=''sales'',<389>,3:43]], A_min=[[@9,37:41='A_min',<381>,1:37], [@62,209:211='min',<113>,3:16], [@71,236:242=''sales'',<389>,3:43]], B_sums=[[@11,44:49='B_sums',<381>,1:44], [@38,144:146='sum',<141>,2:8], [@75,250:256=''units'',<389>,3:57]], A_max=[[@7,30:34='A_max',<381>,1:30], [@56,194:196='max',<108>,3:1], [@71,236:242=''sales'',<389>,3:43]], B_max=[[@17,67:71='B_max',<381>,1:67], [@56,194:196='max',<108>,3:1], [@75,250:256=''units'',<389>,3:57]], B_min=[[@19,74:78='B_min',<381>,1:74], [@62,209:211='min',<113>,3:16], [@75,250:256=''units'',<389>,3:57]], A_cnts=[[@5,22:27='A_cnts',<381>,1:22], [@50,175:179='count',<69>,2:39], [@71,236:242=''sales'',<389>,3:43]], A_sums=[[@1,7:12='A_sums',<381>,1:7], [@38,144:146='sum',<141>,2:8], [@71,236:242=''sales'',<389>,3:43]], B_ave=[[@13,52:56='B_ave',<381>,1:52], [@44,160:162='avg',<61>,2:24], [@75,250:256=''units'',<389>,3:57]], B_cnts=[[@15,59:64='B_cnts',<381>,1:59], [@50,175:179='count',<69>,2:39], [@75,250:256=''units'',<389>,3:57]]}, table_dictionary={q={col4=[[@58,198:201='col4',<381>,3:5]], col5=[[@64,213:216='col5',<381>,3:20]], col2=[[@68,227:230='col2',<381>,3:34], [@46,164:167='col2',<381>,2:28]], col3=[[@52,181:184='col3',<381>,2:45]], col1=[[@40,148:151='col1',<381>,2:12]]}}, def_query0={query_dictionary={col4=[[@29,111:114='col4',<381>,1:111]], col5=[[@31,117:120='col5',<381>,1:117]], col2=[[@25,99:102='col2',<381>,1:99]], col3=[[@27,105:108='col3',<381>,1:105]], col1=[[@23,93:96='col1',<381>,1:93]]}, table_dictionary={tab1={col4=[[@29,111:114='col4',<381>,1:111]], col5=[[@31,117:120='col5',<381>,1:117]], col2=[[@25,99:102='col2',<381>,1:99]], col3=[[@27,105:108='col3',<381>,1:105]], col1=[[@23,93:96='col1',<381>,1:93]]}}, interface={col4=[{name=col4, table_ref=tab1}], col5=[{name=col5, table_ref=tab1}], col2=[{name=col2, table_ref=tab1}], col3=[{name=col3, table_ref=tab1}], col1=[{name=col1, table_ref=tab1}]}}, derivation={source_columns={tuple_0=[{name=col2, table_ref=q}, {name=col1, table_ref=q}, {name=col3, table_ref=q}, {name=col4, table_ref=q}, {name=col5, table_ref=q}]}, derived_columns={tuple_0={A_sums=[[@38,144:146='sum',<141>,2:8], [@71,236:242=''sales'',<389>,3:43]], A_ave=[[@44,160:162='avg',<61>,2:24], [@71,236:242=''sales'',<389>,3:43]], A_cnts=[[@50,175:179='count',<69>,2:39], [@71,236:242=''sales'',<389>,3:43]], A_max=[[@56,194:196='max',<108>,3:1], [@71,236:242=''sales'',<389>,3:43]], A_min=[[@62,209:211='min',<113>,3:16], [@71,236:242=''sales'',<389>,3:43]], B_sums=[[@38,144:146='sum',<141>,2:8], [@75,250:256=''units'',<389>,3:57]], B_ave=[[@44,160:162='avg',<61>,2:24], [@75,250:256=''units'',<389>,3:57]], B_cnts=[[@50,175:179='count',<69>,2:39], [@75,250:256=''units'',<389>,3:57]], B_max=[[@56,194:196='max',<108>,3:1], [@75,250:256=''units'',<389>,3:57]], B_min=[[@62,209:211='min',<113>,3:16], [@75,250:256=''units'',<389>,3:57]]}}}, interface={A_ave=[{name=A_ave, table_ref=tuple_0}, {name=col2, table_ref=q}, {name=col1, table_ref=q}, {name=col3, table_ref=q}, {name=col4, table_ref=q}, {name=col5, table_ref=q}], A_min=[{name=A_min, table_ref=tuple_0}, {name=col2, table_ref=q}, {name=col1, table_ref=q}, {name=col3, table_ref=q}, {name=col4, table_ref=q}, {name=col5, table_ref=q}], B_sums=[{name=B_sums, table_ref=tuple_0}, {name=col2, table_ref=q}, {name=col1, table_ref=q}, {name=col3, table_ref=q}, {name=col4, table_ref=q}, {name=col5, table_ref=q}], A_max=[{name=A_max, table_ref=tuple_0}, {name=col2, table_ref=q}, {name=col1, table_ref=q}, {name=col3, table_ref=q}, {name=col4, table_ref=q}, {name=col5, table_ref=q}], B_max=[{name=B_max, table_ref=tuple_0}, {name=col2, table_ref=q}, {name=col1, table_ref=q}, {name=col3, table_ref=q}, {name=col4, table_ref=q}, {name=col5, table_ref=q}], B_min=[{name=B_min, table_ref=tuple_0}, {name=col2, table_ref=q}, {name=col1, table_ref=q}, {name=col3, table_ref=q}, {name=col4, table_ref=q}, {name=col5, table_ref=q}], A_cnts=[{name=A_cnts, table_ref=tuple_0}, {name=col2, table_ref=q}, {name=col1, table_ref=q}, {name=col3, table_ref=q}, {name=col4, table_ref=q}, {name=col5, table_ref=q}], A_sums=[{name=A_sums, table_ref=tuple_0}, {name=col2, table_ref=q}, {name=col1, table_ref=q}, {name=col3, table_ref=q}, {name=col4, table_ref=q}, {name=col5, table_ref=q}], B_ave=[{name=B_ave, table_ref=tuple_0}, {name=col2, table_ref=q}, {name=col1, table_ref=q}, {name=col3, table_ref=q}, {name=col4, table_ref=q}, {name=col5, table_ref=q}], B_cnts=[{name=B_cnts, table_ref=tuple_0}, {name=col2, table_ref=q}, {name=col1, table_ref=q}, {name=col3, table_ref=q}, {name=col4, table_ref=q}, {name=col5, table_ref=q}]}, table_alias={q=query0, tuple_0=q}}}",
				extractor.getSymbolTable().toString());
		Assert.assertTrue("Expected PIVOT IN item prefixes to be preserved",
				extractor.getAsTree().toString().contains("pivot_prefix=A")
					&& extractor.getAsTree().toString().contains("pivot_prefix=B"));
	}


	@Test
	public void pivotV4Tab1Test() {
		final String query = "select * from tab1 "
		+"\n pivot (sum(col1) sum, avg(col2) ave, count(col3) cnt,"
		+"\n max(col4) max, min(col5) min for col2 in ('sales', 'units'))";

		final SQLSelectParserParser parser = parse(query);
		ParserRunResult runResult = runSQLParsertestAllowErrors(query, parser);
		SqlParseEventWalker extractor = runResult.getExtractor();

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=*, table_ref=*}}}, from={pivot={value={1={function={function_name=sum, parameters={column={name=col1, table_ref=null}}}, alias=sum}, 2={function={function_name=avg, parameters={column={name=col2, table_ref=null}}}, alias=ave}, 3={function={function_name=count, parameters={column={name=col3, table_ref=null}}}, alias=cnt}, 4={function={function_name=max, parameters={column={name=col4, table_ref=null}}}, alias=max}, 5={function={function_name=min, parameters={column={name=col5, table_ref=null}}}, alias=min}}, for={column={name=col2, table_ref=null}}, in={1={pivot_literal='sales'}, 2={pivot_literal='units'}}}, table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[*]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{tab1={col4=[[@26,80:83='col4',<381>,3:5]], *=[[@1,7:7='*',<291>,1:7]], col5=[[@32,95:98='col5',<381>,3:20]], col2=[[@36,109:112='col2',<381>,3:34], [@14,47:50='col2',<381>,2:27]], col3=[[@20,64:67='col3',<381>,2:44]], col1=[[@8,32:35='col1',<381>,2:12]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query1={*=[[@1,7:7='*',<291>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query1={query_dictionary={*=[[@1,7:7='*',<291>,1:7]]}, table_dictionary={tab1={col4=[[@26,80:83='col4',<381>,3:5]], *=[[@1,7:7='*',<291>,1:7]], col5=[[@32,95:98='col5',<381>,3:20]], col2=[[@36,109:112='col2',<381>,3:34], [@14,47:50='col2',<381>,2:27]], col3=[[@20,64:67='col3',<381>,2:44]], col1=[[@8,32:35='col1',<381>,2:12]]}}, derivation={source_columns={tuple_0=[{name=col2, table_ref=tab1}, {name=col1, table_ref=tab1}, {name=col3, table_ref=tab1}, {name=col4, table_ref=tab1}, {name=col5, table_ref=tab1}]}, derived_columns={tuple_0={sales_sum=[[@6,28:30='sum',<141>,2:8], [@39,118:124=''sales'',<389>,3:43]], sales_ave=[[@12,43:45='avg',<61>,2:23], [@39,118:124=''sales'',<389>,3:43]], sales_cnt=[[@18,58:62='count',<69>,2:38], [@39,118:124=''sales'',<389>,3:43]], sales_max=[[@24,76:78='max',<108>,3:1], [@39,118:124=''sales'',<389>,3:43]], sales_min=[[@30,91:93='min',<113>,3:16], [@39,118:124=''sales'',<389>,3:43]], units_sum=[[@6,28:30='sum',<141>,2:8], [@41,127:133=''units'',<389>,3:52]], units_ave=[[@12,43:45='avg',<61>,2:23], [@41,127:133=''units'',<389>,3:52]], units_cnt=[[@18,58:62='count',<69>,2:38], [@41,127:133=''units'',<389>,3:52]], units_max=[[@24,76:78='max',<108>,3:1], [@41,127:133=''units'',<389>,3:52]], units_min=[[@30,91:93='min',<113>,3:16], [@41,127:133=''units'',<389>,3:52]]}}}, interface={*=[{name=*, table_ref=*}]}, table_alias={tuple_0=tab1}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void pivotV4QueryTest() {
		final String query = "select sales_sum, sales_ave, sales_cnt, sales_max, sales_min, units_sum, units_ave, units_cnt, units_max, units_min from (select col1, col2, col3, col4, col5 from tab1) q "
		+"\n pivot (sum(col1) sum, avg(col2) ave, count(col3) cnt,"
		+"\n max(col4) max, min(col5) min for col2 in ('sales', 'units'))";

		final SQLSelectParserParser parser = parse(query);
		ParserRunResult runResult = runSQLParsertestAllowErrors(query, parser);
		SqlParseEventWalker extractor = runResult.getExtractor();

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=sales_sum, table_ref=null}}, 2={column={name=sales_ave, table_ref=null}}, 3={column={name=sales_cnt, table_ref=null}}, 4={column={name=sales_max, table_ref=null}}, 5={column={name=sales_min, table_ref=null}}, 6={column={name=units_sum, table_ref=null}}, 7={column={name=units_ave, table_ref=null}}, 8={column={name=units_cnt, table_ref=null}}, 9={column={name=units_max, table_ref=null}}, 10={column={name=units_min, table_ref=null}}}, from={pivot={value={1={function={function_name=sum, parameters={column={name=col1, table_ref=null}}}, alias=sum}, 2={function={function_name=avg, parameters={column={name=col2, table_ref=null}}}, alias=ave}, 3={function={function_name=count, parameters={column={name=col3, table_ref=null}}}, alias=cnt}, 4={function={function_name=max, parameters={column={name=col4, table_ref=null}}}, alias=max}, 5={function={function_name=min, parameters={column={name=col5, table_ref=null}}}, alias=min}}, for={column={name=col2, table_ref=null}}, in={1={pivot_literal='sales'}, 2={pivot_literal='units'}}}, table={alias=q, query={select={1={column={name=col1, table_ref=null}}, 2={column={name=col2, table_ref=null}}, 3={column={name=col3, table_ref=null}}, 4={column={name=col4, table_ref=null}}, 5={column={name=col5, table_ref=null}}}, from={table={alias=null, table=tab1}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[units_cnt, units_sum, sales_sum, sales_min, sales_ave, units_ave, sales_max, units_min, sales_cnt, units_max]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{q={col4=[[@58,232:235='col4',<381>,3:5]], col5=[[@64,247:250='col5',<381>,3:20]], col2=[[@68,261:264='col2',<381>,3:34], [@46,199:202='col2',<381>,2:27]], col3=[[@52,216:219='col3',<381>,2:44]], col1=[[@40,184:187='col1',<381>,2:12]]}, tab1={col4=[[@29,147:150='col4',<381>,1:147]], col5=[[@31,153:156='col5',<381>,1:153]], col2=[[@25,135:138='col2',<381>,1:135]], col3=[[@27,141:144='col3',<381>,1:141]], col1=[[@23,129:132='col1',<381>,1:129]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={col4=[[@29,147:150='col4',<381>,1:147]], col5=[[@31,153:156='col5',<381>,1:153]], col2=[[@25,135:138='col2',<381>,1:135]], col3=[[@27,141:144='col3',<381>,1:141]], col1=[[@23,129:132='col1',<381>,1:129]]}, query2={units_cnt=[[@15,84:92='units_cnt',<381>,1:84], [@50,210:214='count',<69>,2:38], [@73,279:285=''units'',<389>,3:52]], units_sum=[[@11,62:70='units_sum',<381>,1:62], [@38,180:182='sum',<141>,2:8], [@73,279:285=''units'',<389>,3:52]], sales_sum=[[@1,7:15='sales_sum',<381>,1:7], [@38,180:182='sum',<141>,2:8], [@71,270:276=''sales'',<389>,3:43]], sales_min=[[@9,51:59='sales_min',<381>,1:51], [@62,243:245='min',<113>,3:16], [@71,270:276=''sales'',<389>,3:43]], sales_ave=[[@3,18:26='sales_ave',<381>,1:18], [@44,195:197='avg',<61>,2:23], [@71,270:276=''sales'',<389>,3:43]], units_ave=[[@13,73:81='units_ave',<381>,1:73], [@44,195:197='avg',<61>,2:23], [@73,279:285=''units'',<389>,3:52]], sales_max=[[@7,40:48='sales_max',<381>,1:40], [@56,228:230='max',<108>,3:1], [@71,270:276=''sales'',<389>,3:43]], units_min=[[@19,106:114='units_min',<381>,1:106], [@62,243:245='min',<113>,3:16], [@73,279:285=''units'',<389>,3:52]], sales_cnt=[[@5,29:37='sales_cnt',<381>,1:29], [@50,210:214='count',<69>,2:38], [@71,270:276=''sales'',<389>,3:43]], units_max=[[@17,95:103='units_max',<381>,1:95], [@56,228:230='max',<108>,3:1], [@73,279:285=''units'',<389>,3:52]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query2={query_dictionary={units_cnt=[[@15,84:92='units_cnt',<381>,1:84], [@50,210:214='count',<69>,2:38], [@73,279:285=''units'',<389>,3:52]], units_sum=[[@11,62:70='units_sum',<381>,1:62], [@38,180:182='sum',<141>,2:8], [@73,279:285=''units'',<389>,3:52]], sales_sum=[[@1,7:15='sales_sum',<381>,1:7], [@38,180:182='sum',<141>,2:8], [@71,270:276=''sales'',<389>,3:43]], sales_min=[[@9,51:59='sales_min',<381>,1:51], [@62,243:245='min',<113>,3:16], [@71,270:276=''sales'',<389>,3:43]], sales_ave=[[@3,18:26='sales_ave',<381>,1:18], [@44,195:197='avg',<61>,2:23], [@71,270:276=''sales'',<389>,3:43]], units_ave=[[@13,73:81='units_ave',<381>,1:73], [@44,195:197='avg',<61>,2:23], [@73,279:285=''units'',<389>,3:52]], sales_max=[[@7,40:48='sales_max',<381>,1:40], [@56,228:230='max',<108>,3:1], [@71,270:276=''sales'',<389>,3:43]], units_min=[[@19,106:114='units_min',<381>,1:106], [@62,243:245='min',<113>,3:16], [@73,279:285=''units'',<389>,3:52]], sales_cnt=[[@5,29:37='sales_cnt',<381>,1:29], [@50,210:214='count',<69>,2:38], [@71,270:276=''sales'',<389>,3:43]], units_max=[[@17,95:103='units_max',<381>,1:95], [@56,228:230='max',<108>,3:1], [@73,279:285=''units'',<389>,3:52]]}, table_dictionary={q={col4=[[@58,232:235='col4',<381>,3:5]], col5=[[@64,247:250='col5',<381>,3:20]], col2=[[@68,261:264='col2',<381>,3:34], [@46,199:202='col2',<381>,2:27]], col3=[[@52,216:219='col3',<381>,2:44]], col1=[[@40,184:187='col1',<381>,2:12]]}}, def_query0={query_dictionary={col4=[[@29,147:150='col4',<381>,1:147]], col5=[[@31,153:156='col5',<381>,1:153]], col2=[[@25,135:138='col2',<381>,1:135]], col3=[[@27,141:144='col3',<381>,1:141]], col1=[[@23,129:132='col1',<381>,1:129]]}, table_dictionary={tab1={col4=[[@29,147:150='col4',<381>,1:147]], col5=[[@31,153:156='col5',<381>,1:153]], col2=[[@25,135:138='col2',<381>,1:135]], col3=[[@27,141:144='col3',<381>,1:141]], col1=[[@23,129:132='col1',<381>,1:129]]}}, interface={col4=[{name=col4, table_ref=tab1}], col5=[{name=col5, table_ref=tab1}], col2=[{name=col2, table_ref=tab1}], col3=[{name=col3, table_ref=tab1}], col1=[{name=col1, table_ref=tab1}]}}, derivation={source_columns={tuple_0=[{name=col2, table_ref=q}, {name=col1, table_ref=q}, {name=col3, table_ref=q}, {name=col4, table_ref=q}, {name=col5, table_ref=q}]}, derived_columns={tuple_0={sales_sum=[[@38,180:182='sum',<141>,2:8], [@71,270:276=''sales'',<389>,3:43]], sales_ave=[[@44,195:197='avg',<61>,2:23], [@71,270:276=''sales'',<389>,3:43]], sales_cnt=[[@50,210:214='count',<69>,2:38], [@71,270:276=''sales'',<389>,3:43]], sales_max=[[@56,228:230='max',<108>,3:1], [@71,270:276=''sales'',<389>,3:43]], sales_min=[[@62,243:245='min',<113>,3:16], [@71,270:276=''sales'',<389>,3:43]], units_sum=[[@38,180:182='sum',<141>,2:8], [@73,279:285=''units'',<389>,3:52]], units_ave=[[@44,195:197='avg',<61>,2:23], [@73,279:285=''units'',<389>,3:52]], units_cnt=[[@50,210:214='count',<69>,2:38], [@73,279:285=''units'',<389>,3:52]], units_max=[[@56,228:230='max',<108>,3:1], [@73,279:285=''units'',<389>,3:52]], units_min=[[@62,243:245='min',<113>,3:16], [@73,279:285=''units'',<389>,3:52]]}}}, interface={units_cnt=[{name=units_cnt, table_ref=tuple_0}, {name=col2, table_ref=q}, {name=col1, table_ref=q}, {name=col3, table_ref=q}, {name=col4, table_ref=q}, {name=col5, table_ref=q}], units_sum=[{name=units_sum, table_ref=tuple_0}, {name=col2, table_ref=q}, {name=col1, table_ref=q}, {name=col3, table_ref=q}, {name=col4, table_ref=q}, {name=col5, table_ref=q}], sales_sum=[{name=sales_sum, table_ref=tuple_0}, {name=col2, table_ref=q}, {name=col1, table_ref=q}, {name=col3, table_ref=q}, {name=col4, table_ref=q}, {name=col5, table_ref=q}], sales_min=[{name=sales_min, table_ref=tuple_0}, {name=col2, table_ref=q}, {name=col1, table_ref=q}, {name=col3, table_ref=q}, {name=col4, table_ref=q}, {name=col5, table_ref=q}], sales_ave=[{name=sales_ave, table_ref=tuple_0}, {name=col2, table_ref=q}, {name=col1, table_ref=q}, {name=col3, table_ref=q}, {name=col4, table_ref=q}, {name=col5, table_ref=q}], units_ave=[{name=units_ave, table_ref=tuple_0}, {name=col2, table_ref=q}, {name=col1, table_ref=q}, {name=col3, table_ref=q}, {name=col4, table_ref=q}, {name=col5, table_ref=q}], sales_max=[{name=sales_max, table_ref=tuple_0}, {name=col2, table_ref=q}, {name=col1, table_ref=q}, {name=col3, table_ref=q}, {name=col4, table_ref=q}, {name=col5, table_ref=q}], units_min=[{name=units_min, table_ref=tuple_0}, {name=col2, table_ref=q}, {name=col1, table_ref=q}, {name=col3, table_ref=q}, {name=col4, table_ref=q}, {name=col5, table_ref=q}], sales_cnt=[{name=sales_cnt, table_ref=tuple_0}, {name=col2, table_ref=q}, {name=col1, table_ref=q}, {name=col3, table_ref=q}, {name=col4, table_ref=q}, {name=col5, table_ref=q}], units_max=[{name=units_max, table_ref=tuple_0}, {name=col2, table_ref=q}, {name=col1, table_ref=q}, {name=col3, table_ref=q}, {name=col4, table_ref=q}, {name=col5, table_ref=q}]}, table_alias={q=query0, tuple_0=q}}}",
				extractor.getSymbolTable().toString());
	}

	// PIVOT IN-LIST ALIAS / PHYSICAL LINEAGE TESTS (V1–V8)
	//
	// Bare IN-list values (e.g. jan_sales) in SELECT and subclauses are Snowflake-style output
	// aliases that resolve to the pivot source table (physical lineage). They are not entries in
	// derived_columns. Derived pivot names are {inValue}_{aggregate} (e.g. jan_sales_SUM).
	// See pivotBasicMonthSalesV7Test and pivotMonthlySalesLongDerived* tests below.

	@Test
	// SELECT lists derived pivot output names (jan_sales_sum, …); symbol table holds derivation buckets only.
	public void pivotBasicMetricColumnsV0Test() {
		final String query =
			"SELECT id, jan_sales_sum, feb_sales_sum, mar_sales_sum\n" +
			"FROM my_table\n" +
			"PIVOT (SUM(metric_value) FOR metric_name IN ('jan_sales', 'feb_sales', 'mar_sales'));";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoFatalErrors(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=id, table_ref=null}}, 2={column={name=jan_sales_sum, table_ref=null}}, 3={column={name=feb_sales_sum, table_ref=null}}, 4={column={name=mar_sales_sum, table_ref=null}}}, from={pivot={value={function={function_name=SUM, parameters={column={name=metric_value, table_ref=null}}}}, for={column={name=metric_name, table_ref=null}}, in={1={pivot_literal='jan_sales'}, 2={pivot_literal='feb_sales'}, 3={pivot_literal='mar_sales'}}}, table={alias=null, table=my_table}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[jan_sales_sum, feb_sales_sum, mar_sales_sum, id]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{my_table={metric_name=[[@17,98:108='metric_name',<381>,3:29]], metric_value=[[@14,80:91='metric_value',<381>,3:11]], id=[[@1,7:8='id',<381>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query1={feb_sales_sum=[[@5,26:38='feb_sales_sum',<381>,1:26], [@12,76:78='SUM',<141>,3:7], [@22,127:137=''feb_sales'',<389>,3:58]], jan_sales_sum=[[@3,11:23='jan_sales_sum',<381>,1:11], [@12,76:78='SUM',<141>,3:7], [@20,114:124=''jan_sales'',<389>,3:45]], mar_sales_sum=[[@7,41:53='mar_sales_sum',<381>,1:41], [@12,76:78='SUM',<141>,3:7], [@24,140:150=''mar_sales'',<389>,3:71]], id=[[@1,7:8='id',<381>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query1={query_dictionary={jan_sales_sum=[[@3,11:23='jan_sales_sum',<381>,1:11], [@12,76:78='SUM',<141>,3:7], [@20,114:124=''jan_sales'',<389>,3:45]], feb_sales_sum=[[@5,26:38='feb_sales_sum',<381>,1:26], [@12,76:78='SUM',<141>,3:7], [@22,127:137=''feb_sales'',<389>,3:58]], mar_sales_sum=[[@7,41:53='mar_sales_sum',<381>,1:41], [@12,76:78='SUM',<141>,3:7], [@24,140:150=''mar_sales'',<389>,3:71]], id=[[@1,7:8='id',<381>,1:7]]}, table_dictionary={my_table={metric_name=[[@17,98:108='metric_name',<381>,3:29]], metric_value=[[@14,80:91='metric_value',<381>,3:11]], id=[[@1,7:8='id',<381>,1:7]]}}, derivation={source_columns={tuple_0=[{name=metric_name, table_ref=my_table}, {name=metric_value, table_ref=my_table}]}, derived_columns={tuple_0={jan_sales_SUM=[[@12,76:78='SUM',<141>,3:7], [@20,114:124=''jan_sales'',<389>,3:45]], feb_sales_SUM=[[@12,76:78='SUM',<141>,3:7], [@22,127:137=''feb_sales'',<389>,3:58]], mar_sales_SUM=[[@12,76:78='SUM',<141>,3:7], [@24,140:150=''mar_sales'',<389>,3:71]]}}}, interface={jan_sales_sum=[{name=jan_sales_SUM, table_ref=tuple_0}, {name=metric_name, table_ref=my_table}, {name=metric_value, table_ref=my_table}], feb_sales_sum=[{name=feb_sales_SUM, table_ref=tuple_0}, {name=metric_name, table_ref=my_table}, {name=metric_value, table_ref=my_table}], mar_sales_sum=[{name=mar_sales_SUM, table_ref=tuple_0}, {name=metric_name, table_ref=my_table}, {name=metric_value, table_ref=my_table}], id=[{name=id, table_ref=my_table}]}, table_alias={tuple_0=my_table}}}",
				extractor.getSymbolTable().toString());
	}

	/** Phase 17.6.8 (a): WHERE-site tokens for PIVOT derived outputs land on {@code query_dictionary}. */
	@Test
	public void pivotBasicMetricColumnsV0WhereClauseDerivedQueryDictionaryV17_6_8Test() {
		final String query =
				"SELECT id, jan_sales_sum, feb_sales_sum, mar_sales_sum\n"
						+ "FROM my_table\n"
						+ "PIVOT (SUM(metric_value) FOR metric_name IN ('jan_sales', 'feb_sales', 'mar_sales'))\n"
						+ "WHERE jan_sales_sum > 0 AND feb_sales_sum > 0;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=id, table_ref=null}}, 2={column={name=jan_sales_sum, table_ref=null}}, 3={column={name=feb_sales_sum, table_ref=null}}, 4={column={name=mar_sales_sum, table_ref=null}}}, from={pivot={value={function={function_name=SUM, parameters={column={name=metric_value, table_ref=null}}}}, for={column={name=metric_name, table_ref=null}}, in={1={pivot_literal='jan_sales'}, 2={pivot_literal='feb_sales'}, 3={pivot_literal='mar_sales'}}}, table={alias=null, table=my_table}}, where={and={1={condition={left={column={name=jan_sales_sum, table_ref=null}}, right={literal=0}, operator=>}}, 2={condition={left={column={name=feb_sales_sum, table_ref=null}}, right={literal=0}, operator=>}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[jan_sales_sum, feb_sales_sum, mar_sales_sum, id]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{my_table={metric_name=[[@17,98:108='metric_name',<381>,3:29]], metric_value=[[@14,80:91='metric_value',<381>,3:11]], id=[[@1,7:8='id',<381>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query1={feb_sales_sum=[[@5,26:38='feb_sales_sum',<381>,1:26], [@32,182:194='feb_sales_sum',<381>,4:28], [@12,76:78='SUM',<141>,3:7], [@22,127:137=''feb_sales'',<389>,3:58]], jan_sales_sum=[[@3,11:23='jan_sales_sum',<381>,1:11], [@28,160:172='jan_sales_sum',<381>,4:6], [@12,76:78='SUM',<141>,3:7], [@20,114:124=''jan_sales'',<389>,3:45]], mar_sales_sum=[[@7,41:53='mar_sales_sum',<381>,1:41], [@12,76:78='SUM',<141>,3:7], [@24,140:150=''mar_sales'',<389>,3:71]], id=[[@1,7:8='id',<381>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query1={query_dictionary={jan_sales_sum=[[@3,11:23='jan_sales_sum',<381>,1:11], [@28,160:172='jan_sales_sum',<381>,4:6], [@12,76:78='SUM',<141>,3:7], [@20,114:124=''jan_sales'',<389>,3:45]], feb_sales_sum=[[@5,26:38='feb_sales_sum',<381>,1:26], [@32,182:194='feb_sales_sum',<381>,4:28], [@12,76:78='SUM',<141>,3:7], [@22,127:137=''feb_sales'',<389>,3:58]], mar_sales_sum=[[@7,41:53='mar_sales_sum',<381>,1:41], [@12,76:78='SUM',<141>,3:7], [@24,140:150=''mar_sales'',<389>,3:71]], id=[[@1,7:8='id',<381>,1:7]]}, table_dictionary={my_table={metric_name=[[@17,98:108='metric_name',<381>,3:29]], metric_value=[[@14,80:91='metric_value',<381>,3:11]], id=[[@1,7:8='id',<381>,1:7]]}}, derivation={source_columns={tuple_0=[{name=metric_name, table_ref=my_table}, {name=metric_value, table_ref=my_table}]}, derived_columns={tuple_0={jan_sales_SUM=[[@12,76:78='SUM',<141>,3:7], [@20,114:124=''jan_sales'',<389>,3:45]], feb_sales_SUM=[[@12,76:78='SUM',<141>,3:7], [@22,127:137=''feb_sales'',<389>,3:58]], mar_sales_SUM=[[@12,76:78='SUM',<141>,3:7], [@24,140:150=''mar_sales'',<389>,3:71]]}}}, filters=[{name=jan_sales_SUM, table_ref=tuple_0}, {name=metric_name, table_ref=my_table}, {name=metric_value, table_ref=my_table}, {name=feb_sales_SUM, table_ref=tuple_0}], interface={jan_sales_sum=[{name=jan_sales_SUM, table_ref=tuple_0}, {name=metric_name, table_ref=my_table}, {name=metric_value, table_ref=my_table}], feb_sales_sum=[{name=feb_sales_SUM, table_ref=tuple_0}, {name=metric_name, table_ref=my_table}, {name=metric_value, table_ref=my_table}], mar_sales_sum=[{name=mar_sales_SUM, table_ref=tuple_0}, {name=metric_name, table_ref=my_table}, {name=metric_value, table_ref=my_table}], id=[{name=id, table_ref=my_table}]}, table_alias={tuple_0=my_table}}}",
				extractor.getSymbolTable().toString());
	}

	/** Phase 17.6.8 (a): HAVING-site tokens for PIVOT derived outputs land on {@code query_dictionary}. */
	@Test
	public void pivotBasicMetricColumnsV0HavingClauseDerivedQueryDictionaryV17_6_8Test() {
		final String query =
				"SELECT id, jan_sales_sum, feb_sales_sum, mar_sales_sum\n"
						+ "FROM my_table\n"
						+ "PIVOT (SUM(metric_value) FOR metric_name IN ('jan_sales', 'feb_sales', 'mar_sales'))\n"
						+ "GROUP BY id, jan_sales_sum, feb_sales_sum, mar_sales_sum\n"
						+ "HAVING jan_sales_sum > 0 AND mar_sales_sum > 0;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=id, table_ref=null}}, 2={column={name=jan_sales_sum, table_ref=null}}, 3={column={name=feb_sales_sum, table_ref=null}}, 4={column={name=mar_sales_sum, table_ref=null}}}, having={and={1={condition={left={column={name=jan_sales_sum, table_ref=null}}, right={literal=0}, operator=>}}, 2={condition={left={column={name=mar_sales_sum, table_ref=null}}, right={literal=0}, operator=>}}}}, from={pivot={value={function={function_name=SUM, parameters={column={name=metric_value, table_ref=null}}}}, for={column={name=metric_name, table_ref=null}}, in={1={pivot_literal='jan_sales'}, 2={pivot_literal='feb_sales'}, 3={pivot_literal='mar_sales'}}}, table={alias=null, table=my_table}}, groupby={1={column={name=id, table_ref=null}}, 2={column={name=jan_sales_sum, table_ref=null}}, 3={column={name=feb_sales_sum, table_ref=null}}, 4={column={name=mar_sales_sum, table_ref=null}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[jan_sales_sum, feb_sales_sum, mar_sales_sum, id]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{my_table={metric_name=[[@17,98:108='metric_name',<381>,3:29]], metric_value=[[@14,80:91='metric_value',<381>,3:11]], id=[[@1,7:8='id',<381>,1:7], [@29,163:164='id',<381>,4:9]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query1={feb_sales_sum=[[@5,26:38='feb_sales_sum',<381>,1:26], [@33,182:194='feb_sales_sum',<381>,4:28], [@12,76:78='SUM',<141>,3:7], [@22,127:137=''feb_sales'',<389>,3:58]], jan_sales_sum=[[@3,11:23='jan_sales_sum',<381>,1:11], [@37,218:230='jan_sales_sum',<381>,5:7], [@31,167:179='jan_sales_sum',<381>,4:13], [@12,76:78='SUM',<141>,3:7], [@20,114:124=''jan_sales'',<389>,3:45]], mar_sales_sum=[[@7,41:53='mar_sales_sum',<381>,1:41], [@41,240:252='mar_sales_sum',<381>,5:29], [@35,197:209='mar_sales_sum',<381>,4:43], [@12,76:78='SUM',<141>,3:7], [@24,140:150=''mar_sales'',<389>,3:71]], id=[[@1,7:8='id',<381>,1:7], [@29,163:164='id',<381>,4:9]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query1={query_dictionary={jan_sales_sum=[[@3,11:23='jan_sales_sum',<381>,1:11], [@37,218:230='jan_sales_sum',<381>,5:7], [@31,167:179='jan_sales_sum',<381>,4:13], [@12,76:78='SUM',<141>,3:7], [@20,114:124=''jan_sales'',<389>,3:45]], feb_sales_sum=[[@5,26:38='feb_sales_sum',<381>,1:26], [@33,182:194='feb_sales_sum',<381>,4:28], [@12,76:78='SUM',<141>,3:7], [@22,127:137=''feb_sales'',<389>,3:58]], mar_sales_sum=[[@7,41:53='mar_sales_sum',<381>,1:41], [@41,240:252='mar_sales_sum',<381>,5:29], [@35,197:209='mar_sales_sum',<381>,4:43], [@12,76:78='SUM',<141>,3:7], [@24,140:150=''mar_sales'',<389>,3:71]], id=[[@1,7:8='id',<381>,1:7], [@29,163:164='id',<381>,4:9]]}, table_dictionary={my_table={metric_name=[[@17,98:108='metric_name',<381>,3:29]], metric_value=[[@14,80:91='metric_value',<381>,3:11]], id=[[@1,7:8='id',<381>,1:7], [@29,163:164='id',<381>,4:9]]}}, grouped_by=[{name=id, table_ref=null}, {name=jan_sales_SUM, table_ref=tuple_0}, {name=metric_name, table_ref=my_table}, {name=metric_value, table_ref=my_table}, {name=feb_sales_SUM, table_ref=tuple_0}, {name=mar_sales_SUM, table_ref=tuple_0}], derivation={source_columns={tuple_0=[{name=metric_name, table_ref=my_table}, {name=metric_value, table_ref=my_table}]}, derived_columns={tuple_0={jan_sales_SUM=[[@12,76:78='SUM',<141>,3:7], [@20,114:124=''jan_sales'',<389>,3:45]], feb_sales_SUM=[[@12,76:78='SUM',<141>,3:7], [@22,127:137=''feb_sales'',<389>,3:58]], mar_sales_SUM=[[@12,76:78='SUM',<141>,3:7], [@24,140:150=''mar_sales'',<389>,3:71]]}}}, filters=[{name=jan_sales_SUM, table_ref=tuple_0}, {name=metric_name, table_ref=my_table}, {name=metric_value, table_ref=my_table}, {name=mar_sales_SUM, table_ref=tuple_0}], interface={jan_sales_sum=[{name=jan_sales_SUM, table_ref=tuple_0}, {name=metric_name, table_ref=my_table}, {name=metric_value, table_ref=my_table}], feb_sales_sum=[{name=feb_sales_SUM, table_ref=tuple_0}, {name=metric_name, table_ref=my_table}, {name=metric_value, table_ref=my_table}], mar_sales_sum=[{name=mar_sales_SUM, table_ref=tuple_0}, {name=metric_name, table_ref=my_table}, {name=metric_value, table_ref=my_table}], id=[{name=id, table_ref=my_table}]}, table_alias={tuple_0=my_table}}}",
				extractor.getSymbolTable().toString());
	}

	/** Phase 17.6.8 (a): QUALIFY-site tokens for PIVOT derived outputs land on {@code query_dictionary}. */
	@Test
	public void pivotBasicMetricColumnsV0QualifyClauseDerivedQueryDictionaryV17_6_8Test() {
		final String query =
				"SELECT id, jan_sales_sum, feb_sales_sum, mar_sales_sum\n"
						+ "FROM my_table\n"
						+ "PIVOT (SUM(metric_value) FOR metric_name IN ('jan_sales', 'feb_sales', 'mar_sales'))\n"
						+ "QUALIFY jan_sales_sum > 0 AND mar_sales_sum > 0;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=id, table_ref=null}}, 2={column={name=jan_sales_sum, table_ref=null}}, 3={column={name=feb_sales_sum, table_ref=null}}, 4={column={name=mar_sales_sum, table_ref=null}}}, from={pivot={value={function={function_name=SUM, parameters={column={name=metric_value, table_ref=null}}}}, for={column={name=metric_name, table_ref=null}}, in={1={pivot_literal='jan_sales'}, 2={pivot_literal='feb_sales'}, 3={pivot_literal='mar_sales'}}}, table={alias=null, table=my_table}}, qualify={and={1={condition={left={column={name=jan_sales_sum, table_ref=null}}, right={literal=0}, operator=>}}, 2={condition={left={column={name=mar_sales_sum, table_ref=null}}, right={literal=0}, operator=>}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[jan_sales_sum, feb_sales_sum, mar_sales_sum, id]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{my_table={metric_name=[[@17,98:108='metric_name',<381>,3:29]], metric_value=[[@14,80:91='metric_value',<381>,3:11]], id=[[@1,7:8='id',<381>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query1={feb_sales_sum=[[@5,26:38='feb_sales_sum',<381>,1:26], [@12,76:78='SUM',<141>,3:7], [@22,127:137=''feb_sales'',<389>,3:58]], jan_sales_sum=[[@3,11:23='jan_sales_sum',<381>,1:11], [@28,162:174='jan_sales_sum',<381>,4:8], [@12,76:78='SUM',<141>,3:7], [@20,114:124=''jan_sales'',<389>,3:45]], mar_sales_sum=[[@7,41:53='mar_sales_sum',<381>,1:41], [@32,184:196='mar_sales_sum',<381>,4:30], [@12,76:78='SUM',<141>,3:7], [@24,140:150=''mar_sales'',<389>,3:71]], id=[[@1,7:8='id',<381>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query1={query_dictionary={jan_sales_sum=[[@3,11:23='jan_sales_sum',<381>,1:11], [@28,162:174='jan_sales_sum',<381>,4:8], [@12,76:78='SUM',<141>,3:7], [@20,114:124=''jan_sales'',<389>,3:45]], feb_sales_sum=[[@5,26:38='feb_sales_sum',<381>,1:26], [@12,76:78='SUM',<141>,3:7], [@22,127:137=''feb_sales'',<389>,3:58]], mar_sales_sum=[[@7,41:53='mar_sales_sum',<381>,1:41], [@32,184:196='mar_sales_sum',<381>,4:30], [@12,76:78='SUM',<141>,3:7], [@24,140:150=''mar_sales'',<389>,3:71]], id=[[@1,7:8='id',<381>,1:7]]}, table_dictionary={my_table={metric_name=[[@17,98:108='metric_name',<381>,3:29]], metric_value=[[@14,80:91='metric_value',<381>,3:11]], id=[[@1,7:8='id',<381>,1:7]]}}, derivation={source_columns={tuple_0=[{name=metric_name, table_ref=my_table}, {name=metric_value, table_ref=my_table}]}, derived_columns={tuple_0={jan_sales_SUM=[[@12,76:78='SUM',<141>,3:7], [@20,114:124=''jan_sales'',<389>,3:45]], feb_sales_SUM=[[@12,76:78='SUM',<141>,3:7], [@22,127:137=''feb_sales'',<389>,3:58]], mar_sales_SUM=[[@12,76:78='SUM',<141>,3:7], [@24,140:150=''mar_sales'',<389>,3:71]]}}}, filters=[{name=jan_sales_SUM, table_ref=tuple_0}, {name=metric_name, table_ref=my_table}, {name=metric_value, table_ref=my_table}, {name=mar_sales_SUM, table_ref=tuple_0}], interface={jan_sales_sum=[{name=jan_sales_SUM, table_ref=tuple_0}, {name=metric_name, table_ref=my_table}, {name=metric_value, table_ref=my_table}], feb_sales_sum=[{name=feb_sales_SUM, table_ref=tuple_0}, {name=metric_name, table_ref=my_table}, {name=metric_value, table_ref=my_table}], mar_sales_sum=[{name=mar_sales_SUM, table_ref=tuple_0}, {name=metric_name, table_ref=my_table}, {name=metric_value, table_ref=my_table}], id=[{name=id, table_ref=my_table}]}, table_alias={tuple_0=my_table}}}",
				extractor.getSymbolTable().toString());
	}

	/** Phase 17.6.8 (b): PIVOT derived outputs in {@code OVER} partition/order → {@code query_dictionary}. See **17.6.9** for window-only {@code query_dictionary} policy. */
	@Test
	public void pivotBasicMetricColumnsV0WindowDerivedColumnsQueryDictionaryV17_6_8Test() {
		final String query =
				"SELECT jan_sales_sum, feb_sales_sum,\n"
						+ "  ROW_NUMBER() OVER (PARTITION BY jan_sales_sum ORDER BY feb_sales_sum) AS rn\n"
						+ "FROM my_table\n"
						+ "PIVOT (SUM(metric_value) FOR metric_name IN ('jan_sales', 'feb_sales', 'mar_sales'));";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("Interface is wrong", "[jan_sales_sum, feb_sales_sum, rn]",
				extractor.getInterface().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query1={feb_sales_sum=[[@3,22:34='feb_sales_sum',<381>,1:22], [@15,94:106='feb_sales_sum',<381>,2:57], [@23,136:138='SUM',<141>,4:7], [@33,187:197=''feb_sales'',<389>,4:58]], jan_sales_sum=[[@1,7:19='jan_sales_sum',<381>,1:7], [@12,71:83='jan_sales_sum',<381>,2:34], [@23,136:138='SUM',<141>,4:7], [@31,174:184=''jan_sales'',<389>,4:45]], rn=[[@18,112:113='rn',<381>,2:75]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query1={window_ordered_by=[{name=feb_sales_SUM, table_ref=tuple_0}, {name=metric_name, table_ref=my_table}, {name=metric_value, table_ref=my_table}], query_dictionary={jan_sales_sum=[[@1,7:19='jan_sales_sum',<381>,1:7], [@12,71:83='jan_sales_sum',<381>,2:34], [@23,136:138='SUM',<141>,4:7], [@31,174:184=''jan_sales'',<389>,4:45]], feb_sales_sum=[[@3,22:34='feb_sales_sum',<381>,1:22], [@15,94:106='feb_sales_sum',<381>,2:57], [@23,136:138='SUM',<141>,4:7], [@33,187:197=''feb_sales'',<389>,4:58]], rn=[[@18,112:113='rn',<381>,2:75]]}, table_dictionary={my_table={metric_name=[[@28,158:168='metric_name',<381>,4:29]], metric_value=[[@25,140:151='metric_value',<381>,4:11]]}}, window_partition_by=[{name=jan_sales_SUM, table_ref=tuple_0}, {name=metric_name, table_ref=my_table}, {name=metric_value, table_ref=my_table}], derivation={source_columns={tuple_0=[{name=metric_name, table_ref=my_table}, {name=metric_value, table_ref=my_table}]}, derived_columns={tuple_0={jan_sales_SUM=[[@23,136:138='SUM',<141>,4:7], [@31,174:184=''jan_sales'',<389>,4:45]], feb_sales_SUM=[[@23,136:138='SUM',<141>,4:7], [@33,187:197=''feb_sales'',<389>,4:58]], mar_sales_SUM=[[@23,136:138='SUM',<141>,4:7], [@35,200:210=''mar_sales'',<389>,4:71]]}}}, interface={jan_sales_sum=[{name=jan_sales_SUM, table_ref=tuple_0}, {name=metric_name, table_ref=my_table}, {name=metric_value, table_ref=my_table}], feb_sales_sum=[{name=feb_sales_SUM, table_ref=tuple_0}, {name=metric_name, table_ref=my_table}, {name=metric_value, table_ref=my_table}], rn=[{name=jan_sales_SUM, table_ref=tuple_0}, {name=metric_name, table_ref=my_table}, {name=metric_value, table_ref=my_table}, {name=feb_sales_SUM, table_ref=tuple_0}]}, table_alias={tuple_0=my_table}}}",
				extractor.getSymbolTable().toString());
	}

	/** Phase 17.6.8 (b): PIVOT source operand in {@code OVER} → {@code query_dictionary}. See **17.6.9** for window-only {@code query_dictionary} policy. */
	@Test
	public void pivotBasicMetricColumnsV0WindowSourceColumnQueryDictionaryV17_6_8Test() {
		final String query =
				"SELECT jan_sales_sum, metric_value,\n"
						+ "  ROW_NUMBER() OVER (PARTITION BY metric_value ORDER BY jan_sales_sum) AS rn\n"
						+ "FROM my_table\n"
						+ "PIVOT (SUM(metric_value) FOR metric_name IN ('jan_sales', 'feb_sales', 'mar_sales'));";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("Interface is wrong", "[jan_sales_sum, metric_value, rn]",
				extractor.getInterface().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query1={jan_sales_sum=[[@1,7:19='jan_sales_sum',<381>,1:7], [@15,92:104='jan_sales_sum',<381>,2:56], [@23,134:136='SUM',<141>,4:7], [@31,172:182=''jan_sales'',<389>,4:45]], metric_value=[[@3,22:33='metric_value',<381>,1:22], [@12,70:81='metric_value',<381>,2:34]], rn=[[@18,110:111='rn',<381>,2:74]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query1={window_ordered_by=[{name=jan_sales_SUM, table_ref=tuple_0}, {name=metric_name, table_ref=my_table}, {name=metric_value, table_ref=my_table}], query_dictionary={jan_sales_sum=[[@1,7:19='jan_sales_sum',<381>,1:7], [@15,92:104='jan_sales_sum',<381>,2:56], [@23,134:136='SUM',<141>,4:7], [@31,172:182=''jan_sales'',<389>,4:45]], metric_value=[[@3,22:33='metric_value',<381>,1:22], [@12,70:81='metric_value',<381>,2:34]], rn=[[@18,110:111='rn',<381>,2:74]]}, table_dictionary={my_table={metric_name=[[@28,156:166='metric_name',<381>,4:29]], metric_value=[[@25,138:149='metric_value',<381>,4:11]]}}, window_partition_by=[{name=metric_value, table_ref=null}], derivation={source_columns={tuple_0=[{name=metric_name, table_ref=my_table}, {name=metric_value, table_ref=my_table}]}, derived_columns={tuple_0={jan_sales_SUM=[[@23,134:136='SUM',<141>,4:7], [@31,172:182=''jan_sales'',<389>,4:45]], feb_sales_SUM=[[@23,134:136='SUM',<141>,4:7], [@33,185:195=''feb_sales'',<389>,4:58]], mar_sales_SUM=[[@23,134:136='SUM',<141>,4:7], [@35,198:208=''mar_sales'',<389>,4:71]]}}}, interface={jan_sales_sum=[{name=jan_sales_SUM, table_ref=tuple_0}, {name=metric_name, table_ref=my_table}, {name=metric_value, table_ref=my_table}], metric_value=[{name=metric_value, table_ref=my_table}], rn=[{name=metric_value, table_ref=my_table}, {name=jan_sales_SUM, table_ref=tuple_0}, {name=metric_name, table_ref=my_table}]}, table_alias={tuple_0=my_table}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	// IN-list alias SELECT: bare jan_sales/feb_sales/mar_sales bind to my_table, not derived_columns.
	public void pivotBasicMetricColumnsV1Test() {
		final String query =
			"SELECT id, jan_sales, feb_sales, mar_sales\n" +
			"FROM my_table\n" +
			"PIVOT (SUM(metric_value) FOR metric_name IN ('jan_sales', 'feb_sales', 'mar_sales'));";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoFatalErrors(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=id, table_ref=null}}, 2={column={name=jan_sales, table_ref=null}}, 3={column={name=feb_sales, table_ref=null}}, 4={column={name=mar_sales, table_ref=null}}}, from={pivot={value={function={function_name=SUM, parameters={column={name=metric_value, table_ref=null}}}}, for={column={name=metric_name, table_ref=null}}, in={1={pivot_literal='jan_sales'}, 2={pivot_literal='feb_sales'}, 3={pivot_literal='mar_sales'}}}, table={alias=null, table=my_table}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[jan_sales, mar_sales, id, feb_sales]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{my_table={jan_sales=[[@3,11:19='jan_sales',<381>,1:11]], mar_sales=[[@7,33:41='mar_sales',<381>,1:33]], metric_name=[[@17,86:96='metric_name',<381>,3:29]], metric_value=[[@14,68:79='metric_value',<381>,3:11]], id=[[@1,7:8='id',<381>,1:7]], feb_sales=[[@5,22:30='feb_sales',<381>,1:22]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query1={jan_sales=[[@3,11:19='jan_sales',<381>,1:11]], mar_sales=[[@7,33:41='mar_sales',<381>,1:33]], id=[[@1,7:8='id',<381>,1:7]], feb_sales=[[@5,22:30='feb_sales',<381>,1:22]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query1={query_dictionary={jan_sales=[[@3,11:19='jan_sales',<381>,1:11]], mar_sales=[[@7,33:41='mar_sales',<381>,1:33]], id=[[@1,7:8='id',<381>,1:7]], feb_sales=[[@5,22:30='feb_sales',<381>,1:22]]}, table_dictionary={my_table={jan_sales=[[@3,11:19='jan_sales',<381>,1:11]], mar_sales=[[@7,33:41='mar_sales',<381>,1:33]], metric_name=[[@17,86:96='metric_name',<381>,3:29]], metric_value=[[@14,68:79='metric_value',<381>,3:11]], id=[[@1,7:8='id',<381>,1:7]], feb_sales=[[@5,22:30='feb_sales',<381>,1:22]]}}, derivation={source_columns={tuple_0=[{name=metric_name, table_ref=my_table}, {name=metric_value, table_ref=my_table}]}, derived_columns={tuple_0={jan_sales_SUM=[[@12,64:66='SUM',<141>,3:7], [@20,102:112=''jan_sales'',<389>,3:45]], feb_sales_SUM=[[@12,64:66='SUM',<141>,3:7], [@22,115:125=''feb_sales'',<389>,3:58]], mar_sales_SUM=[[@12,64:66='SUM',<141>,3:7], [@24,128:138=''mar_sales'',<389>,3:71]]}}}, interface={jan_sales=[{name=jan_sales, table_ref=my_table}], mar_sales=[{name=mar_sales, table_ref=my_table}], id=[{name=id, table_ref=my_table}], feb_sales=[{name=feb_sales, table_ref=my_table}]}, table_alias={tuple_0=my_table}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	// IN-list alias SELECT + WHERE on a physical source column (units), not a derived column.
	public void pivotTableWithInAliasesJanFebMarV2Test() {
		final String query =
			"SELECT empid, units, jan_sales, feb_sales, mar_sales\n" +
			"FROM monthly_sales_long\n" +
			"PIVOT (SUM(sales_amount) FOR month_name IN ('jan_sales', 'feb_sales', 'mar_sales'))\n" +
			"WHERE units > 1.00;";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoFatalErrors(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=empid, table_ref=null}}, 2={column={name=units, table_ref=null}}, 3={column={name=jan_sales, table_ref=null}}, 4={column={name=feb_sales, table_ref=null}}, 5={column={name=mar_sales, table_ref=null}}}, from={pivot={value={function={function_name=SUM, parameters={column={name=sales_amount, table_ref=null}}}}, for={column={name=month_name, table_ref=null}}, in={1={pivot_literal='jan_sales'}, 2={pivot_literal='feb_sales'}, 3={pivot_literal='mar_sales'}}}, table={alias=null, table=monthly_sales_long}}, where={condition={left={column={name=units, table_ref=null}}, right={literal=1.00}, operator=>}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[jan_sales, empid, mar_sales, units, feb_sales]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{monthly_sales_long={jan_sales=[[@5,21:29='jan_sales',<381>,1:21]], month_name=[[@19,106:115='month_name',<381>,3:29]], empid=[[@1,7:11='empid',<381>,1:7]], mar_sales=[[@9,43:51='mar_sales',<381>,1:43]], sales_amount=[[@16,88:99='sales_amount',<381>,3:11]], units=[[@3,14:18='units',<381>,1:14], [@30,167:171='units',<381>,4:6]], feb_sales=[[@7,32:40='feb_sales',<381>,1:32]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query1={jan_sales=[[@5,21:29='jan_sales',<381>,1:21]], empid=[[@1,7:11='empid',<381>,1:7]], mar_sales=[[@9,43:51='mar_sales',<381>,1:43]], units=[[@3,14:18='units',<381>,1:14], [@30,167:171='units',<381>,4:6]], feb_sales=[[@7,32:40='feb_sales',<381>,1:32]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query1={query_dictionary={jan_sales=[[@5,21:29='jan_sales',<381>,1:21]], empid=[[@1,7:11='empid',<381>,1:7]], mar_sales=[[@9,43:51='mar_sales',<381>,1:43]], units=[[@3,14:18='units',<381>,1:14], [@30,167:171='units',<381>,4:6]], feb_sales=[[@7,32:40='feb_sales',<381>,1:32]]}, table_dictionary={monthly_sales_long={jan_sales=[[@5,21:29='jan_sales',<381>,1:21]], month_name=[[@19,106:115='month_name',<381>,3:29]], empid=[[@1,7:11='empid',<381>,1:7]], mar_sales=[[@9,43:51='mar_sales',<381>,1:43]], sales_amount=[[@16,88:99='sales_amount',<381>,3:11]], units=[[@3,14:18='units',<381>,1:14], [@30,167:171='units',<381>,4:6]], feb_sales=[[@7,32:40='feb_sales',<381>,1:32]]}}, derivation={source_columns={tuple_0=[{name=month_name, table_ref=monthly_sales_long}, {name=sales_amount, table_ref=monthly_sales_long}]}, derived_columns={tuple_0={jan_sales_SUM=[[@14,84:86='SUM',<141>,3:7], [@22,121:131=''jan_sales'',<389>,3:44]], feb_sales_SUM=[[@14,84:86='SUM',<141>,3:7], [@24,134:144=''feb_sales'',<389>,3:57]], mar_sales_SUM=[[@14,84:86='SUM',<141>,3:7], [@26,147:157=''mar_sales'',<389>,3:70]]}}}, filters=[{name=units, table_ref=null}], interface={jan_sales=[{name=jan_sales, table_ref=monthly_sales_long}], empid=[{name=empid, table_ref=monthly_sales_long}], mar_sales=[{name=mar_sales, table_ref=monthly_sales_long}], units=[{name=units, table_ref=monthly_sales_long}], feb_sales=[{name=feb_sales, table_ref=monthly_sales_long}]}, table_alias={tuple_0=monthly_sales_long}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	// IN-list alias in GROUP BY and ORDER BY: jan_sales resolves to monthly_sales_long.
	public void pivotTableWithGroupByAndOrderByV2GroupOrderTest() {
		final String query =
			"SELECT empid, jan_sales, feb_sales, mar_sales\n" +
			"FROM monthly_sales_long\n" +
			"PIVOT (SUM(sales_amount) FOR month_name IN ('jan_sales', 'feb_sales', 'mar_sales'))\n" +
			"GROUP BY empid, jan_sales, feb_sales, mar_sales\n" +
			"ORDER BY jan_sales;";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoFatalErrors(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=empid, table_ref=null}}, 2={column={name=jan_sales, table_ref=null}}, 3={column={name=feb_sales, table_ref=null}}, 4={column={name=mar_sales, table_ref=null}}}, orderby={1={null_order=null, predicand={column={name=jan_sales, table_ref=null}}, sort_order=ASC}}, from={pivot={value={function={function_name=SUM, parameters={column={name=sales_amount, table_ref=null}}}}, for={column={name=month_name, table_ref=null}}, in={1={pivot_literal='jan_sales'}, 2={pivot_literal='feb_sales'}, 3={pivot_literal='mar_sales'}}}, table={alias=null, table=monthly_sales_long}}, groupby={1={column={name=empid, table_ref=null}}, 2={column={name=jan_sales, table_ref=null}}, 3={column={name=feb_sales, table_ref=null}}, 4={column={name=mar_sales, table_ref=null}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[jan_sales, empid, mar_sales, feb_sales]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{monthly_sales_long={jan_sales=[[@3,14:22='jan_sales',<381>,1:14], [@31,170:178='jan_sales',<381>,4:16], [@38,211:219='jan_sales',<381>,5:9]], month_name=[[@17,99:108='month_name',<381>,3:29]], empid=[[@1,7:11='empid',<381>,1:7], [@29,163:167='empid',<381>,4:9]], mar_sales=[[@7,36:44='mar_sales',<381>,1:36], [@35,192:200='mar_sales',<381>,4:38]], sales_amount=[[@14,81:92='sales_amount',<381>,3:11]], feb_sales=[[@5,25:33='feb_sales',<381>,1:25], [@33,181:189='feb_sales',<381>,4:27]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query1={jan_sales=[[@3,14:22='jan_sales',<381>,1:14], [@31,170:178='jan_sales',<381>,4:16], [@38,211:219='jan_sales',<381>,5:9]], empid=[[@1,7:11='empid',<381>,1:7], [@29,163:167='empid',<381>,4:9]], mar_sales=[[@7,36:44='mar_sales',<381>,1:36], [@35,192:200='mar_sales',<381>,4:38]], feb_sales=[[@5,25:33='feb_sales',<381>,1:25], [@33,181:189='feb_sales',<381>,4:27]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query1={query_dictionary={jan_sales=[[@3,14:22='jan_sales',<381>,1:14], [@31,170:178='jan_sales',<381>,4:16], [@38,211:219='jan_sales',<381>,5:9]], empid=[[@1,7:11='empid',<381>,1:7], [@29,163:167='empid',<381>,4:9]], mar_sales=[[@7,36:44='mar_sales',<381>,1:36], [@35,192:200='mar_sales',<381>,4:38]], feb_sales=[[@5,25:33='feb_sales',<381>,1:25], [@33,181:189='feb_sales',<381>,4:27]]}, table_dictionary={monthly_sales_long={jan_sales=[[@3,14:22='jan_sales',<381>,1:14], [@31,170:178='jan_sales',<381>,4:16], [@38,211:219='jan_sales',<381>,5:9]], month_name=[[@17,99:108='month_name',<381>,3:29]], empid=[[@1,7:11='empid',<381>,1:7], [@29,163:167='empid',<381>,4:9]], mar_sales=[[@7,36:44='mar_sales',<381>,1:36], [@35,192:200='mar_sales',<381>,4:38]], sales_amount=[[@14,81:92='sales_amount',<381>,3:11]], feb_sales=[[@5,25:33='feb_sales',<381>,1:25], [@33,181:189='feb_sales',<381>,4:27]]}}, grouped_by=[{name=empid, table_ref=null}, {name=jan_sales, table_ref=null}, {name=feb_sales, table_ref=null}, {name=mar_sales, table_ref=null}], derivation={source_columns={tuple_0=[{name=month_name, table_ref=monthly_sales_long}, {name=sales_amount, table_ref=monthly_sales_long}]}, derived_columns={tuple_0={jan_sales_SUM=[[@12,77:79='SUM',<141>,3:7], [@20,114:124=''jan_sales'',<389>,3:44]], feb_sales_SUM=[[@12,77:79='SUM',<141>,3:7], [@22,127:137=''feb_sales'',<389>,3:57]], mar_sales_SUM=[[@12,77:79='SUM',<141>,3:7], [@24,140:150=''mar_sales'',<389>,3:70]]}}}, ordered_by=[{name=jan_sales, table_ref=null}], interface={jan_sales=[{name=jan_sales, table_ref=monthly_sales_long}], empid=[{name=empid, table_ref=monthly_sales_long}], mar_sales=[{name=mar_sales, table_ref=monthly_sales_long}], feb_sales=[{name=feb_sales, table_ref=monthly_sales_long}]}, table_alias={tuple_0=monthly_sales_long}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	// IN-list alias in HAVING and ORDER BY: jan_sales resolves to monthly_sales_long.
	public void pivotTableWithHavingAndOrderByV2HavingOrderTest() {
		final String query =
			"SELECT empid, jan_sales, feb_sales, mar_sales\n" +
			"FROM monthly_sales_long\n" +
			"PIVOT (SUM(sales_amount) FOR month_name IN ('jan_sales', 'feb_sales', 'mar_sales'))\n" +
			"GROUP BY empid, jan_sales, feb_sales, mar_sales\n" +
			"HAVING jan_sales > 100\n" +
			"ORDER BY jan_sales;";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoFatalErrors(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=empid, table_ref=null}}, 2={column={name=jan_sales, table_ref=null}}, 3={column={name=feb_sales, table_ref=null}}, 4={column={name=mar_sales, table_ref=null}}}, having={condition={left={column={name=jan_sales, table_ref=null}}, right={literal=100}, operator=>}}, orderby={1={null_order=null, predicand={column={name=jan_sales, table_ref=null}}, sort_order=ASC}}, from={pivot={value={function={function_name=SUM, parameters={column={name=sales_amount, table_ref=null}}}}, for={column={name=month_name, table_ref=null}}, in={1={pivot_literal='jan_sales'}, 2={pivot_literal='feb_sales'}, 3={pivot_literal='mar_sales'}}}, table={alias=null, table=monthly_sales_long}}, groupby={1={column={name=empid, table_ref=null}}, 2={column={name=jan_sales, table_ref=null}}, 3={column={name=feb_sales, table_ref=null}}, 4={column={name=mar_sales, table_ref=null}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[jan_sales, empid, mar_sales, feb_sales]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{monthly_sales_long={jan_sales=[[@3,14:22='jan_sales',<381>,1:14], [@31,170:178='jan_sales',<381>,4:16], [@37,209:217='jan_sales',<381>,5:7], [@42,234:242='jan_sales',<381>,6:9]], month_name=[[@17,99:108='month_name',<381>,3:29]], empid=[[@1,7:11='empid',<381>,1:7], [@29,163:167='empid',<381>,4:9]], mar_sales=[[@7,36:44='mar_sales',<381>,1:36], [@35,192:200='mar_sales',<381>,4:38]], sales_amount=[[@14,81:92='sales_amount',<381>,3:11]], feb_sales=[[@5,25:33='feb_sales',<381>,1:25], [@33,181:189='feb_sales',<381>,4:27]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query1={jan_sales=[[@3,14:22='jan_sales',<381>,1:14], [@37,209:217='jan_sales',<381>,5:7], [@31,170:178='jan_sales',<381>,4:16], [@42,234:242='jan_sales',<381>,6:9]], empid=[[@1,7:11='empid',<381>,1:7], [@29,163:167='empid',<381>,4:9]], mar_sales=[[@7,36:44='mar_sales',<381>,1:36], [@35,192:200='mar_sales',<381>,4:38]], feb_sales=[[@5,25:33='feb_sales',<381>,1:25], [@33,181:189='feb_sales',<381>,4:27]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query1={query_dictionary={jan_sales=[[@3,14:22='jan_sales',<381>,1:14], [@37,209:217='jan_sales',<381>,5:7], [@31,170:178='jan_sales',<381>,4:16], [@42,234:242='jan_sales',<381>,6:9]], empid=[[@1,7:11='empid',<381>,1:7], [@29,163:167='empid',<381>,4:9]], mar_sales=[[@7,36:44='mar_sales',<381>,1:36], [@35,192:200='mar_sales',<381>,4:38]], feb_sales=[[@5,25:33='feb_sales',<381>,1:25], [@33,181:189='feb_sales',<381>,4:27]]}, table_dictionary={monthly_sales_long={jan_sales=[[@3,14:22='jan_sales',<381>,1:14], [@31,170:178='jan_sales',<381>,4:16], [@37,209:217='jan_sales',<381>,5:7], [@42,234:242='jan_sales',<381>,6:9]], month_name=[[@17,99:108='month_name',<381>,3:29]], empid=[[@1,7:11='empid',<381>,1:7], [@29,163:167='empid',<381>,4:9]], mar_sales=[[@7,36:44='mar_sales',<381>,1:36], [@35,192:200='mar_sales',<381>,4:38]], sales_amount=[[@14,81:92='sales_amount',<381>,3:11]], feb_sales=[[@5,25:33='feb_sales',<381>,1:25], [@33,181:189='feb_sales',<381>,4:27]]}}, grouped_by=[{name=empid, table_ref=null}, {name=jan_sales, table_ref=null}, {name=feb_sales, table_ref=null}, {name=mar_sales, table_ref=null}], derivation={source_columns={tuple_0=[{name=month_name, table_ref=monthly_sales_long}, {name=sales_amount, table_ref=monthly_sales_long}]}, derived_columns={tuple_0={jan_sales_SUM=[[@12,77:79='SUM',<141>,3:7], [@20,114:124=''jan_sales'',<389>,3:44]], feb_sales_SUM=[[@12,77:79='SUM',<141>,3:7], [@22,127:137=''feb_sales'',<389>,3:57]], mar_sales_SUM=[[@12,77:79='SUM',<141>,3:7], [@24,140:150=''mar_sales'',<389>,3:70]]}}}, ordered_by=[{name=jan_sales, table_ref=null}], filters=[{name=jan_sales, table_ref=null}], interface={jan_sales=[{name=jan_sales, table_ref=monthly_sales_long}], empid=[{name=empid, table_ref=monthly_sales_long}], mar_sales=[{name=mar_sales, table_ref=monthly_sales_long}], feb_sales=[{name=feb_sales, table_ref=monthly_sales_long}]}, table_alias={tuple_0=monthly_sales_long}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	// IN-list alias in JOIN ON (E0e clause probe): jan_sales is not a derived_columns key.
	public void pivotTableJoinOnWithUnqualifiedJanSalesProbeTest() {
		final String query =
			"SELECT empid, jan_sales, p.target_amount\n" +
			"FROM monthly_sales_long\n" +
			"PIVOT (SUM(sales_amount) FOR month_name IN ('jan_sales', 'feb_sales', 'mar_sales'))\n" +
			"JOIN targets p ON jan_sales >= p.target_amount;";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoFatalErrors(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=empid, table_ref=null}}, 2={column={name=jan_sales, table_ref=null}}, 3={column={name=target_amount, table_ref=p}}}, from={join={1={pivot={value={function={function_name=SUM, parameters={column={name=sales_amount, table_ref=null}}}}, for={column={name=month_name, table_ref=null}}, in={1={pivot_literal='jan_sales'}, 2={pivot_literal='feb_sales'}, 3={pivot_literal='mar_sales'}}}, table={alias=null, table=monthly_sales_long}}, 2={join=JOIN, on={condition={left={column={name=jan_sales, table_ref=null}}, right={column={name=target_amount, table_ref=p}}, operator=>=}}}, 3={table={alias=p, table=targets}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[jan_sales, empid, target_amount]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{targets={month_name=[[@17,94:103='month_name',<381>,3:29]], target_amount=[[@5,25:25='p',<381>,1:25], [@33,180:180='p',<381>,4:31]], sales_amount=[[@14,76:87='sales_amount',<381>,3:11]]}, monthly_sales_long={month_name=[[@17,94:103='month_name',<381>,3:29]], sales_amount=[[@14,76:87='sales_amount',<381>,3:11]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query1={jan_sales=[[@3,14:22='jan_sales',<381>,1:14], [@31,167:175='jan_sales',<381>,4:18]], empid=[[@1,7:11='empid',<381>,1:7]], target_amount=[[@7,27:39='target_amount',<381>,1:27], [@35,182:194='target_amount',<381>,4:33]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query1={query_dictionary={jan_sales=[[@3,14:22='jan_sales',<381>,1:14], [@31,167:175='jan_sales',<381>,4:18]], empid=[[@1,7:11='empid',<381>,1:7]], target_amount=[[@7,27:39='target_amount',<381>,1:27], [@35,182:194='target_amount',<381>,4:33]]}, table_dictionary={targets={month_name=[[@17,94:103='month_name',<381>,3:29]], target_amount=[[@5,25:25='p',<381>,1:25], [@33,180:180='p',<381>,4:31]], sales_amount=[[@14,76:87='sales_amount',<381>,3:11]]}, monthly_sales_long={month_name=[[@17,94:103='month_name',<381>,3:29]], sales_amount=[[@14,76:87='sales_amount',<381>,3:11]]}}, derivation={source_columns={tuple_0=[{name=month_name, table_ref=monthly_sales_long}, {name=sales_amount, table_ref=monthly_sales_long}]}, derived_columns={tuple_0={jan_sales_SUM=[[@12,72:74='SUM',<141>,3:7], [@20,109:119=''jan_sales'',<389>,3:44]], feb_sales_SUM=[[@12,72:74='SUM',<141>,3:7], [@22,122:132=''feb_sales'',<389>,3:57]], mar_sales_SUM=[[@12,72:74='SUM',<141>,3:7], [@24,135:145=''mar_sales'',<389>,3:70]]}}}, filters=[{name=jan_sales, table_ref=null}, {name=target_amount, table_ref=p}], interface={jan_sales=[{name=jan_sales, table_ref=null}], empid=[{name=empid, table_ref=null}], target_amount=[{name=target_amount, table_ref=p}]}, table_alias={p=targets, tuple_0=monthly_sales_long}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	// IN-list alias in QUALIFY (clause probe): jan_sales resolves to monthly_sales_long.
	public void pivotTableWithQualifyJanSalesProbeTest() {
		final String query =
			"SELECT empid, jan_sales, feb_sales, mar_sales\n" +
			"FROM monthly_sales_long\n" +
			"PIVOT (SUM(sales_amount) FOR month_name IN ('jan_sales', 'feb_sales', 'mar_sales'))\n" +
			"QUALIFY jan_sales > 100;";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoFatalErrors(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=empid, table_ref=null}}, 2={column={name=jan_sales, table_ref=null}}, 3={column={name=feb_sales, table_ref=null}}, 4={column={name=mar_sales, table_ref=null}}}, from={pivot={value={function={function_name=SUM, parameters={column={name=sales_amount, table_ref=null}}}}, for={column={name=month_name, table_ref=null}}, in={1={pivot_literal='jan_sales'}, 2={pivot_literal='feb_sales'}, 3={pivot_literal='mar_sales'}}}, table={alias=null, table=monthly_sales_long}}, qualify={condition={left={column={name=jan_sales, table_ref=null}}, right={literal=100}, operator=>}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[jan_sales, empid, mar_sales, feb_sales]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{monthly_sales_long={jan_sales=[[@3,14:22='jan_sales',<381>,1:14], [@28,162:170='jan_sales',<381>,4:8]], month_name=[[@17,99:108='month_name',<381>,3:29]], empid=[[@1,7:11='empid',<381>,1:7]], mar_sales=[[@7,36:44='mar_sales',<381>,1:36]], sales_amount=[[@14,81:92='sales_amount',<381>,3:11]], feb_sales=[[@5,25:33='feb_sales',<381>,1:25]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query1={jan_sales=[[@3,14:22='jan_sales',<381>,1:14], [@28,162:170='jan_sales',<381>,4:8]], empid=[[@1,7:11='empid',<381>,1:7]], mar_sales=[[@7,36:44='mar_sales',<381>,1:36]], feb_sales=[[@5,25:33='feb_sales',<381>,1:25]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query1={query_dictionary={jan_sales=[[@3,14:22='jan_sales',<381>,1:14], [@28,162:170='jan_sales',<381>,4:8]], empid=[[@1,7:11='empid',<381>,1:7]], mar_sales=[[@7,36:44='mar_sales',<381>,1:36]], feb_sales=[[@5,25:33='feb_sales',<381>,1:25]]}, table_dictionary={monthly_sales_long={jan_sales=[[@3,14:22='jan_sales',<381>,1:14], [@28,162:170='jan_sales',<381>,4:8]], month_name=[[@17,99:108='month_name',<381>,3:29]], empid=[[@1,7:11='empid',<381>,1:7]], mar_sales=[[@7,36:44='mar_sales',<381>,1:36]], sales_amount=[[@14,81:92='sales_amount',<381>,3:11]], feb_sales=[[@5,25:33='feb_sales',<381>,1:25]]}}, derivation={source_columns={tuple_0=[{name=month_name, table_ref=monthly_sales_long}, {name=sales_amount, table_ref=monthly_sales_long}]}, derived_columns={tuple_0={jan_sales_SUM=[[@12,77:79='SUM',<141>,3:7], [@20,114:124=''jan_sales'',<389>,3:44]], feb_sales_SUM=[[@12,77:79='SUM',<141>,3:7], [@22,127:137=''feb_sales'',<389>,3:57]], mar_sales_SUM=[[@12,77:79='SUM',<141>,3:7], [@24,140:150=''mar_sales'',<389>,3:70]]}}}, filters=[{name=jan_sales, table_ref=null}], interface={jan_sales=[{name=jan_sales, table_ref=monthly_sales_long}], empid=[{name=empid, table_ref=monthly_sales_long}], mar_sales=[{name=mar_sales, table_ref=monthly_sales_long}], feb_sales=[{name=feb_sales, table_ref=monthly_sales_long}]}, table_alias={tuple_0=monthly_sales_long}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	// IN-list alias in ORDER BY expression: jan_sales/feb_sales resolve to monthly_sales_long.
	public void pivotTableWithOrderByExpressionJanFebProbeTest() {
		final String query =
			"SELECT empid, jan_sales, feb_sales, mar_sales\n" +
			"FROM monthly_sales_long\n" +
			"PIVOT (SUM(sales_amount) FOR month_name IN ('jan_sales', 'feb_sales', 'mar_sales'))\n" +
			"ORDER BY jan_sales / feb_sales;";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoFatalErrors(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=empid, table_ref=null}}, 2={column={name=jan_sales, table_ref=null}}, 3={column={name=feb_sales, table_ref=null}}, 4={column={name=mar_sales, table_ref=null}}}, orderby={1={null_order=null, predicand={calc={left={column={name=jan_sales, table_ref=null}}, right={column={name=feb_sales, table_ref=null}}, operator=/}}, sort_order=ASC}}, from={pivot={value={function={function_name=SUM, parameters={column={name=sales_amount, table_ref=null}}}}, for={column={name=month_name, table_ref=null}}, in={1={pivot_literal='jan_sales'}, 2={pivot_literal='feb_sales'}, 3={pivot_literal='mar_sales'}}}, table={alias=null, table=monthly_sales_long}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[jan_sales, empid, mar_sales, feb_sales]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{monthly_sales_long={jan_sales=[[@3,14:22='jan_sales',<381>,1:14], [@29,163:171='jan_sales',<381>,4:9]], month_name=[[@17,99:108='month_name',<381>,3:29]], empid=[[@1,7:11='empid',<381>,1:7]], mar_sales=[[@7,36:44='mar_sales',<381>,1:36]], sales_amount=[[@14,81:92='sales_amount',<381>,3:11]], feb_sales=[[@5,25:33='feb_sales',<381>,1:25], [@31,175:183='feb_sales',<381>,4:21]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query1={jan_sales=[[@3,14:22='jan_sales',<381>,1:14], [@29,163:171='jan_sales',<381>,4:9]], empid=[[@1,7:11='empid',<381>,1:7]], mar_sales=[[@7,36:44='mar_sales',<381>,1:36]], feb_sales=[[@5,25:33='feb_sales',<381>,1:25], [@31,175:183='feb_sales',<381>,4:21]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query1={query_dictionary={jan_sales=[[@3,14:22='jan_sales',<381>,1:14], [@29,163:171='jan_sales',<381>,4:9]], empid=[[@1,7:11='empid',<381>,1:7]], mar_sales=[[@7,36:44='mar_sales',<381>,1:36]], feb_sales=[[@5,25:33='feb_sales',<381>,1:25], [@31,175:183='feb_sales',<381>,4:21]]}, table_dictionary={monthly_sales_long={jan_sales=[[@3,14:22='jan_sales',<381>,1:14], [@29,163:171='jan_sales',<381>,4:9]], month_name=[[@17,99:108='month_name',<381>,3:29]], empid=[[@1,7:11='empid',<381>,1:7]], mar_sales=[[@7,36:44='mar_sales',<381>,1:36]], sales_amount=[[@14,81:92='sales_amount',<381>,3:11]], feb_sales=[[@5,25:33='feb_sales',<381>,1:25], [@31,175:183='feb_sales',<381>,4:21]]}}, derivation={source_columns={tuple_0=[{name=month_name, table_ref=monthly_sales_long}, {name=sales_amount, table_ref=monthly_sales_long}]}, derived_columns={tuple_0={jan_sales_SUM=[[@12,77:79='SUM',<141>,3:7], [@20,114:124=''jan_sales'',<389>,3:44]], feb_sales_SUM=[[@12,77:79='SUM',<141>,3:7], [@22,127:137=''feb_sales'',<389>,3:57]], mar_sales_SUM=[[@12,77:79='SUM',<141>,3:7], [@24,140:150=''mar_sales'',<389>,3:70]]}}}, ordered_by=[{name=jan_sales, table_ref=null}, {name=feb_sales, table_ref=null}], interface={jan_sales=[{name=jan_sales, table_ref=monthly_sales_long}], empid=[{name=empid, table_ref=monthly_sales_long}], mar_sales=[{name=mar_sales, table_ref=monthly_sales_long}], feb_sales=[{name=feb_sales, table_ref=monthly_sales_long}]}, table_alias={tuple_0=monthly_sales_long}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void pivotFromDerivedAdjustedColumnsV3Test() {
		final String query =
			"SELECT empid, month_name,  jan_adjusted_SUM, feb_adjusted_SUM\n" +
			"FROM (SELECT empid, month_name, sales_amount * 1.10 AS adjusted_sales FROM monthly_sales_long)\n" +
			"PIVOT (SUM(adjusted_sales) FOR month_name IN ('jan_adjusted', 'feb_adjusted'));";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoFatalErrors(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=empid, table_ref=null}}, 2={column={name=month_name, table_ref=null}}, 3={column={name=jan_adjusted_SUM, table_ref=null}}, 4={column={name=feb_adjusted_SUM, table_ref=null}}}, from={select={1={column={name=empid, table_ref=null}}, 2={column={name=month_name, table_ref=null}}, 3={alias=adjusted_sales, calc={left={column={name=sales_amount, table_ref=null}}, right={literal=1.10}, operator=*}}}, pivot={value={function={function_name=SUM, parameters={column={name=adjusted_sales, table_ref=null}}}}, for={column={name=month_name, table_ref=null}}, in={1={pivot_literal='jan_adjusted'}, 2={pivot_literal='feb_adjusted'}}}, from={table={alias=null, table=monthly_sales_long}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[empid, month_name, jan_adjusted_SUM, feb_adjusted_SUM]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
		 "{monthly_sales_long={empid=[[@11,75:79='empid',<381>,2:13]], month_name=[[@13,82:91='month_name',<381>,2:20]], sales_amount=[[@15,94:105='sales_amount',<381>,2:32]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
		 "{query0={empid=[[@11,75:79='empid',<381>,2:13], [@1,7:11='empid',<381>,1:7]], month_name=[[@13,82:91='month_name',<381>,2:20], [@32,188:197='month_name',<381>,3:31]], adjusted_sales=[[@21,117:130='adjusted_sales',<381>,2:55], [@29,168:181='adjusted_sales',<381>,3:11]]}, query2={empid=[[@1,7:11='empid',<381>,1:7]], month_name=[[@3,14:23='month_name',<381>,1:14]], jan_adjusted_SUM=[[@5,27:42='jan_adjusted_SUM',<381>,1:27], [@27,164:166='SUM',<141>,3:7], [@35,203:216=''jan_adjusted'',<389>,3:46]], feb_adjusted_SUM=[[@7,45:60='feb_adjusted_SUM',<381>,1:45], [@27,164:166='SUM',<141>,3:7], [@37,219:232=''feb_adjusted'',<389>,3:62]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query2={query_dictionary={empid=[[@1,7:11='empid',<381>,1:7]], month_name=[[@3,14:23='month_name',<381>,1:14]], jan_adjusted_SUM=[[@5,27:42='jan_adjusted_SUM',<381>,1:27], [@27,164:166='SUM',<141>,3:7], [@35,203:216=''jan_adjusted'',<389>,3:46]], feb_adjusted_SUM=[[@7,45:60='feb_adjusted_SUM',<381>,1:45], [@27,164:166='SUM',<141>,3:7], [@37,219:232=''feb_adjusted'',<389>,3:62]]}, def_query0={query_dictionary={empid=[[@11,75:79='empid',<381>,2:13], [@1,7:11='empid',<381>,1:7]], month_name=[[@13,82:91='month_name',<381>,2:20], [@32,188:197='month_name',<381>,3:31]], adjusted_sales=[[@21,117:130='adjusted_sales',<381>,2:55], [@29,168:181='adjusted_sales',<381>,3:11]]}, table_dictionary={monthly_sales_long={empid=[[@11,75:79='empid',<381>,2:13]], month_name=[[@13,82:91='month_name',<381>,2:20]], sales_amount=[[@15,94:105='sales_amount',<381>,2:32]]}}, interface={empid=[{name=empid, table_ref=monthly_sales_long}], month_name=[{name=month_name, table_ref=monthly_sales_long}], adjusted_sales=[{name=sales_amount, table_ref=monthly_sales_long}]}}, derivation={derived_columns={tuple_0={jan_adjusted_SUM=[[@27,164:166='SUM',<141>,3:7], [@35,203:216=''jan_adjusted'',<389>,3:46]], feb_adjusted_SUM=[[@27,164:166='SUM',<141>,3:7], [@37,219:232=''feb_adjusted'',<389>,3:62]]}}}, interface={empid=[{name=empid, table_ref=query0}], month_name=[{name=month_name, table_ref=query0}], jan_adjusted_SUM=[{name=jan_adjusted_SUM, table_ref=null}], feb_adjusted_SUM=[{name=feb_adjusted_SUM, table_ref=null}]}, table_alias={query0=query0}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	// IN-list alias in SELECT expression (tax) and WHERE: jan_sales binds to physical source.
	public void pivotWithTaxAndWhereV4Test() {
		final String query =
			"SELECT empid, jan_sales, feb_sales_SUM feb_tot, mar_sales_SUM, jan_sales * 0.07 AS tax, sales_amount/jan_sales_SUM AS jan_sales_ratio\n" +
			"FROM monthly_sales_long\n" +
			"PIVOT (SUM(sales_amount) FOR month_name IN ('jan_sales', 'feb_sales', 'mar_sales')) WHERE empid > 100 and mar_sales_SUM > 10000\n" +
			"group by sales_amount/jan_sales_SUM;";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoFatalErrors(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=empid, table_ref=null}}, 2={column={name=jan_sales, table_ref=null}}, 3={column={name=feb_sales_SUM, table_ref=null}, alias=feb_tot}, 4={column={name=mar_sales_SUM, table_ref=null}}, 5={alias=tax, calc={left={column={name=jan_sales, table_ref=null}}, right={literal=0.07}, operator=*}}, 6={alias=jan_sales_ratio, calc={left={column={name=sales_amount, table_ref=null}}, right={column={name=jan_sales_SUM, table_ref=null}}, operator=/}}}, from={pivot={value={function={function_name=SUM, parameters={column={name=sales_amount, table_ref=null}}}}, for={column={name=month_name, table_ref=null}}, in={1={pivot_literal='jan_sales'}, 2={pivot_literal='feb_sales'}, 3={pivot_literal='mar_sales'}}}, table={alias=null, table=monthly_sales_long}}, where={and={1={condition={left={column={name=empid, table_ref=null}}, right={literal=100}, operator=>}}, 2={condition={left={column={name=mar_sales_SUM, table_ref=null}}, right={literal=10000}, operator=>}}}}, groupby={1={calc={left={column={name=sales_amount, table_ref=null}}, right={column={name=jan_sales_SUM, table_ref=null}}, operator=/}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[jan_sales, empid, feb_tot, mar_sales_SUM, tax, jan_sales_ratio]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{monthly_sales_long={jan_sales=[[@3,14:22='jan_sales',<381>,1:14], [@10,63:71='jan_sales',<381>,1:63]], month_name=[[@32,187:196='month_name',<381>,3:29]], empid=[[@1,7:11='empid',<381>,1:7], [@43,248:252='empid',<381>,3:90]], sales_amount=[[@29,169:180='sales_amount',<381>,3:11], [@52,295:306='sales_amount',<381>,4:9]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query1={jan_sales=[[@3,14:22='jan_sales',<381>,1:14]], empid=[[@1,7:11='empid',<381>,1:7], [@43,248:252='empid',<381>,3:90]], feb_tot=[[@6,39:45='feb_tot',<381>,1:39]], mar_sales_SUM=[[@8,48:60='mar_sales_SUM',<381>,1:48], [@47,264:276='mar_sales_SUM',<381>,3:106], [@27,165:167='SUM',<141>,3:7], [@39,228:238=''mar_sales'',<389>,3:70]], tax=[[@16,83:85='tax',<381>,1:83]], jan_sales_ratio=[[@22,118:132='jan_sales_ratio',<381>,1:118]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query1={query_dictionary={jan_sales=[[@3,14:22='jan_sales',<381>,1:14]], empid=[[@1,7:11='empid',<381>,1:7], [@43,248:252='empid',<381>,3:90]], feb_tot=[[@6,39:45='feb_tot',<381>,1:39]], mar_sales_SUM=[[@8,48:60='mar_sales_SUM',<381>,1:48], [@47,264:276='mar_sales_SUM',<381>,3:106], [@27,165:167='SUM',<141>,3:7], [@39,228:238=''mar_sales'',<389>,3:70]], tax=[[@16,83:85='tax',<381>,1:83]], jan_sales_ratio=[[@22,118:132='jan_sales_ratio',<381>,1:118]]}, table_dictionary={monthly_sales_long={jan_sales=[[@3,14:22='jan_sales',<381>,1:14], [@10,63:71='jan_sales',<381>,1:63]], month_name=[[@32,187:196='month_name',<381>,3:29]], empid=[[@1,7:11='empid',<381>,1:7], [@43,248:252='empid',<381>,3:90]], sales_amount=[[@29,169:180='sales_amount',<381>,3:11], [@52,295:306='sales_amount',<381>,4:9]]}}, grouped_by=[{name=sales_amount, table_ref=null}, {name=jan_sales_SUM, table_ref=tuple_0}, {name=month_name, table_ref=monthly_sales_long}, {name=sales_amount, table_ref=monthly_sales_long}], derivation={source_columns={tuple_0=[{name=month_name, table_ref=monthly_sales_long}, {name=sales_amount, table_ref=monthly_sales_long}]}, derived_columns={tuple_0={jan_sales_SUM=[[@27,165:167='SUM',<141>,3:7], [@35,202:212=''jan_sales'',<389>,3:44]], feb_sales_SUM=[[@27,165:167='SUM',<141>,3:7], [@37,215:225=''feb_sales'',<389>,3:57]], mar_sales_SUM=[[@27,165:167='SUM',<141>,3:7], [@39,228:238=''mar_sales'',<389>,3:70]]}}}, filters=[{name=empid, table_ref=null}, {name=mar_sales_SUM, table_ref=tuple_0}, {name=month_name, table_ref=monthly_sales_long}, {name=sales_amount, table_ref=monthly_sales_long}], interface={jan_sales=[{name=jan_sales, table_ref=monthly_sales_long}], empid=[{name=empid, table_ref=monthly_sales_long}], feb_tot=[{name=feb_sales_SUM, table_ref=tuple_0}, {name=month_name, table_ref=monthly_sales_long}, {name=sales_amount, table_ref=monthly_sales_long}], mar_sales_SUM=[{name=mar_sales_SUM, table_ref=tuple_0}, {name=month_name, table_ref=monthly_sales_long}, {name=sales_amount, table_ref=monthly_sales_long}], tax=[{name=jan_sales, table_ref=monthly_sales_long}], jan_sales_ratio=[{name=sales_amount, table_ref=monthly_sales_long}, {name=jan_sales_SUM, table_ref=tuple_0}, {name=month_name, table_ref=monthly_sales_long}]}, table_alias={tuple_0=monthly_sales_long}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	// IN-list alias JOIN ON with pivot alias u + WHERE: jan_sales is physical lineage, not derived.
	public void pivotJoinTargetsWithFilterV5Test() {
		final String query =
			"SELECT empid, jan_sales, feb_sales, jan_sales * 0.07 AS tax\n" +
			"FROM monthly_sales_long PIVOT (SUM(sales_amount) FOR month_name IN ('jan_sales', 'feb_sales')) u\n" +
			"JOIN targets t ON u.empid = t.empid AND u.jan_sales >= t.target_amount WHERE jan_sales > 100;";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoFatalErrors(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=empid, table_ref=null}}, 2={column={name=jan_sales, table_ref=null}}, 3={column={name=feb_sales, table_ref=null}}, 4={alias=tax, calc={left={column={name=jan_sales, table_ref=null}}, right={literal=0.07}, operator=*}}}, from={join={1={pivot={value={function={function_name=SUM, parameters={column={name=sales_amount, table_ref=null}}}}, for={column={name=month_name, table_ref=null}}, in={1={pivot_literal='jan_sales'}, 2={pivot_literal='feb_sales'}}}, alias=u, table={alias=null, table=monthly_sales_long}}, 2={join=JOIN, on={and={1={condition={left={column={name=empid, table_ref=u}}, right={column={name=empid, table_ref=t}}, operator==}}, 2={condition={left={column={name=jan_sales, table_ref=u}}, right={column={name=target_amount, table_ref=t}}, operator=>=}}}}}, 3={table={alias=t, table=targets}}}}, where={condition={left={column={name=jan_sales, table_ref=null}}, right={literal=100}, operator=>}}}}",
			extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[jan_sales, empid, tax, feb_sales]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{targets={empid=[[@40,185:185='t',<381>,3:28]], month_name=[[@23,113:122='month_name',<381>,2:53]], target_amount=[[@48,212:212='t',<381>,3:55]], sales_amount=[[@20,95:106='sales_amount',<381>,2:35]]}, monthly_sales_long={jan_sales=[[@44,197:197='u',<381>,3:40]], month_name=[[@23,113:122='month_name',<381>,2:53]], empid=[[@36,175:175='u',<381>,3:18]], sales_amount=[[@20,95:106='sales_amount',<381>,2:35]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query1={jan_sales=[[@3,14:22='jan_sales',<381>,1:14], [@46,199:207='jan_sales',<381>,3:42], [@52,234:242='jan_sales',<381>,3:77]], empid=[[@1,7:11='empid',<381>,1:7], [@38,177:181='empid',<381>,3:20], [@42,187:191='empid',<381>,3:30]], tax=[[@13,56:58='tax',<381>,1:56]], feb_sales=[[@5,25:33='feb_sales',<381>,1:25]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query1={query_dictionary={jan_sales=[[@3,14:22='jan_sales',<381>,1:14], [@46,199:207='jan_sales',<381>,3:42], [@52,234:242='jan_sales',<381>,3:77]], empid=[[@1,7:11='empid',<381>,1:7], [@38,177:181='empid',<381>,3:20], [@42,187:191='empid',<381>,3:30]], tax=[[@13,56:58='tax',<381>,1:56]], feb_sales=[[@5,25:33='feb_sales',<381>,1:25]]}, table_dictionary={targets={month_name=[[@23,113:122='month_name',<381>,2:53]], empid=[[@40,185:185='t',<381>,3:28]], target_amount=[[@48,212:212='t',<381>,3:55]], sales_amount=[[@20,95:106='sales_amount',<381>,2:35]]}, monthly_sales_long={jan_sales=[[@44,197:197='u',<381>,3:40]], month_name=[[@23,113:122='month_name',<381>,2:53]], empid=[[@36,175:175='u',<381>,3:18]], sales_amount=[[@20,95:106='sales_amount',<381>,2:35]]}}, derivation={source_columns={u=[{name=month_name, table_ref=monthly_sales_long}, {name=sales_amount, table_ref=monthly_sales_long}]}, derived_columns={u={jan_sales_SUM=[[@18,91:93='SUM',<141>,2:31], [@26,128:138=''jan_sales'',<389>,2:68]], feb_sales_SUM=[[@18,91:93='SUM',<141>,2:31], [@28,141:151=''feb_sales'',<389>,2:81]]}}}, filters=[{name=empid, table_ref=u}, {name=empid, table_ref=t}, {name=jan_sales, table_ref=u}, {name=target_amount, table_ref=t}, {name=jan_sales, table_ref=null}], interface={jan_sales=[{name=jan_sales, table_ref=null}], empid=[{name=empid, table_ref=null}], tax=[{name=jan_sales, table_ref=null}], feb_sales=[{name=feb_sales, table_ref=null}]}, table_alias={t=targets, u=monthly_sales_long}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	// IN-list alias SELECT with FOR column (month_name) retained alongside pivot output aliases.
	public void pivotKeepingForColumnV6Test() {
		final String query =
			"SELECT empid, month_name, jan_sales, feb_sales, mar_sales\n" +
			"FROM monthly_sales_long\n" +
			"PIVOT (SUM(sales_amount) FOR month_name IN ('jan_sales', 'feb_sales', 'mar_sales'));";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoFatalErrors(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=empid, table_ref=null}}, 2={column={name=month_name, table_ref=null}}, 3={column={name=jan_sales, table_ref=null}}, 4={column={name=feb_sales, table_ref=null}}, 5={column={name=mar_sales, table_ref=null}}}, from={pivot={value={function={function_name=SUM, parameters={column={name=sales_amount, table_ref=null}}}}, for={column={name=month_name, table_ref=null}}, in={1={pivot_literal='jan_sales'}, 2={pivot_literal='feb_sales'}, 3={pivot_literal='mar_sales'}}}, table={alias=null, table=monthly_sales_long}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[jan_sales, empid, month_name, mar_sales, feb_sales]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{monthly_sales_long={jan_sales=[[@5,26:34='jan_sales',<381>,1:26]], month_name=[[@19,111:120='month_name',<381>,3:29]], empid=[[@1,7:11='empid',<381>,1:7]], mar_sales=[[@9,48:56='mar_sales',<381>,1:48]], sales_amount=[[@16,93:104='sales_amount',<381>,3:11]], feb_sales=[[@7,37:45='feb_sales',<381>,1:37]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query1={jan_sales=[[@5,26:34='jan_sales',<381>,1:26]], empid=[[@1,7:11='empid',<381>,1:7]], month_name=[[@3,14:23='month_name',<381>,1:14]], mar_sales=[[@9,48:56='mar_sales',<381>,1:48]], feb_sales=[[@7,37:45='feb_sales',<381>,1:37]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query1={query_dictionary={jan_sales=[[@5,26:34='jan_sales',<381>,1:26]], empid=[[@1,7:11='empid',<381>,1:7]], month_name=[[@3,14:23='month_name',<381>,1:14]], mar_sales=[[@9,48:56='mar_sales',<381>,1:48]], feb_sales=[[@7,37:45='feb_sales',<381>,1:37]]}, table_dictionary={monthly_sales_long={jan_sales=[[@5,26:34='jan_sales',<381>,1:26]], month_name=[[@19,111:120='month_name',<381>,3:29]], empid=[[@1,7:11='empid',<381>,1:7]], mar_sales=[[@9,48:56='mar_sales',<381>,1:48]], sales_amount=[[@16,93:104='sales_amount',<381>,3:11]], feb_sales=[[@7,37:45='feb_sales',<381>,1:37]]}}, derivation={source_columns={tuple_0=[{name=month_name, table_ref=monthly_sales_long}, {name=sales_amount, table_ref=monthly_sales_long}]}, derived_columns={tuple_0={jan_sales_SUM=[[@14,89:91='SUM',<141>,3:7], [@22,126:136=''jan_sales'',<389>,3:44]], feb_sales_SUM=[[@14,89:91='SUM',<141>,3:7], [@24,139:149=''feb_sales'',<389>,3:57]], mar_sales_SUM=[[@14,89:91='SUM',<141>,3:7], [@26,152:162=''mar_sales'',<389>,3:70]]}}}, interface={jan_sales=[{name=jan_sales, table_ref=monthly_sales_long}], empid=[{name=empid, table_ref=monthly_sales_long}], month_name=[{name=month_name, table_ref=monthly_sales_long}], mar_sales=[{name=mar_sales, table_ref=monthly_sales_long}], feb_sales=[{name=feb_sales, table_ref=monthly_sales_long}]}, table_alias={tuple_0=monthly_sales_long}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	// Derived column SELECT: jan_sales_SUM/feb_sales_SUM/mar_sales_SUM have table_ref=null.
	public void pivotBasicMonthSalesV7Test() {
		final String query =
			"SELECT empid, jan_sales_SUM, feb_sales_SUM, mar_sales_SUM\n" +
			"FROM monthly_sales_long\n" +
			"PIVOT (SUM(sales_amount) FOR month_name IN ('jan_sales', 'feb_sales', 'mar_sales'));";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoFatalErrors(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=empid, table_ref=null}}, 2={column={name=jan_sales_SUM, table_ref=null}}, 3={column={name=feb_sales_SUM, table_ref=null}}, 4={column={name=mar_sales_SUM, table_ref=null}}}, from={pivot={value={function={function_name=SUM, parameters={column={name=sales_amount, table_ref=null}}}}, for={column={name=month_name, table_ref=null}}, in={1={pivot_literal='jan_sales'}, 2={pivot_literal='feb_sales'}, 3={pivot_literal='mar_sales'}}}, table={alias=null, table=monthly_sales_long}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[empid, jan_sales_SUM, mar_sales_SUM, feb_sales_SUM]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{monthly_sales_long={month_name=[[@17,111:120='month_name',<381>,3:29]], empid=[[@1,7:11='empid',<381>,1:7]], sales_amount=[[@14,93:104='sales_amount',<381>,3:11]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query1={empid=[[@1,7:11='empid',<381>,1:7]], jan_sales_SUM=[[@3,14:26='jan_sales_SUM',<381>,1:14], [@12,89:91='SUM',<141>,3:7], [@20,126:136=''jan_sales'',<389>,3:44]], mar_sales_SUM=[[@7,44:56='mar_sales_SUM',<381>,1:44], [@12,89:91='SUM',<141>,3:7], [@24,152:162=''mar_sales'',<389>,3:70]], feb_sales_SUM=[[@5,29:41='feb_sales_SUM',<381>,1:29], [@12,89:91='SUM',<141>,3:7], [@22,139:149=''feb_sales'',<389>,3:57]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query1={query_dictionary={empid=[[@1,7:11='empid',<381>,1:7]], jan_sales_SUM=[[@3,14:26='jan_sales_SUM',<381>,1:14], [@12,89:91='SUM',<141>,3:7], [@20,126:136=''jan_sales'',<389>,3:44]], mar_sales_SUM=[[@7,44:56='mar_sales_SUM',<381>,1:44], [@12,89:91='SUM',<141>,3:7], [@24,152:162=''mar_sales'',<389>,3:70]], feb_sales_SUM=[[@5,29:41='feb_sales_SUM',<381>,1:29], [@12,89:91='SUM',<141>,3:7], [@22,139:149=''feb_sales'',<389>,3:57]]}, table_dictionary={monthly_sales_long={month_name=[[@17,111:120='month_name',<381>,3:29]], empid=[[@1,7:11='empid',<381>,1:7]], sales_amount=[[@14,93:104='sales_amount',<381>,3:11]]}}, derivation={source_columns={tuple_0=[{name=month_name, table_ref=monthly_sales_long}, {name=sales_amount, table_ref=monthly_sales_long}]}, derived_columns={tuple_0={jan_sales_SUM=[[@12,89:91='SUM',<141>,3:7], [@20,126:136=''jan_sales'',<389>,3:44]], feb_sales_SUM=[[@12,89:91='SUM',<141>,3:7], [@22,139:149=''feb_sales'',<389>,3:57]], mar_sales_SUM=[[@12,89:91='SUM',<141>,3:7], [@24,152:162=''mar_sales'',<389>,3:70]]}}}, interface={empid=[{name=empid, table_ref=monthly_sales_long}], jan_sales_SUM=[{name=jan_sales_SUM, table_ref=tuple_0}, {name=month_name, table_ref=monthly_sales_long}, {name=sales_amount, table_ref=monthly_sales_long}], mar_sales_SUM=[{name=mar_sales_SUM, table_ref=tuple_0}, {name=month_name, table_ref=monthly_sales_long}, {name=sales_amount, table_ref=monthly_sales_long}], feb_sales_SUM=[{name=feb_sales_SUM, table_ref=tuple_0}, {name=month_name, table_ref=monthly_sales_long}, {name=sales_amount, table_ref=monthly_sales_long}]}, table_alias={tuple_0=monthly_sales_long}}}",
				extractor.getSymbolTable().toString());
	}

	// PIVOT DERIVED COLUMN TESTS (monthly_sales_long fixture)
	//
	// Complement the IN-list alias tests above: these use real derived names
	// (jan_sales_SUM = IN-value + aggregate) in subclauses on monthly_sales_long.

	@Test
	// Derived column in JOIN ON (E0e probe): jan_sales_SUM has table_ref=null in filters.
	public void pivotMonthlySalesLongJoinOnDerivedSumProbeTest() {
		final String query =
			"SELECT empid, jan_sales_SUM, p.target_amount\n" +
			"FROM monthly_sales_long\n" +
			"PIVOT (SUM(sales_amount) FOR month_name IN ('jan_sales', 'feb_sales', 'mar_sales'))\n" +
			"JOIN targets p ON jan_sales_SUM >= p.target_amount;";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoFatalErrors(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=empid, table_ref=null}}, 2={column={name=jan_sales_SUM, table_ref=null}}, 3={column={name=target_amount, table_ref=p}}}, from={join={1={pivot={value={function={function_name=SUM, parameters={column={name=sales_amount, table_ref=null}}}}, for={column={name=month_name, table_ref=null}}, in={1={pivot_literal='jan_sales'}, 2={pivot_literal='feb_sales'}, 3={pivot_literal='mar_sales'}}}, table={alias=null, table=monthly_sales_long}}, 2={join=JOIN, on={condition={left={column={name=jan_sales_SUM, table_ref=null}}, right={column={name=target_amount, table_ref=p}}, operator=>=}}}, 3={table={alias=p, table=targets}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[empid, jan_sales_SUM, target_amount]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{targets={month_name=[[@17,98:107='month_name',<381>,3:29]], target_amount=[[@5,29:29='p',<381>,1:29], [@33,188:188='p',<381>,4:35]], sales_amount=[[@14,80:91='sales_amount',<381>,3:11]]}, monthly_sales_long={month_name=[[@17,98:107='month_name',<381>,3:29]], sales_amount=[[@14,80:91='sales_amount',<381>,3:11]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query1={empid=[[@1,7:11='empid',<381>,1:7]], jan_sales_SUM=[[@3,14:26='jan_sales_SUM',<381>,1:14], [@31,171:183='jan_sales_SUM',<381>,4:18], [@12,76:78='SUM',<141>,3:7], [@20,113:123=''jan_sales'',<389>,3:44]], target_amount=[[@7,31:43='target_amount',<381>,1:31], [@35,190:202='target_amount',<381>,4:37]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query1={query_dictionary={empid=[[@1,7:11='empid',<381>,1:7]], jan_sales_SUM=[[@3,14:26='jan_sales_SUM',<381>,1:14], [@31,171:183='jan_sales_SUM',<381>,4:18], [@12,76:78='SUM',<141>,3:7], [@20,113:123=''jan_sales'',<389>,3:44]], target_amount=[[@7,31:43='target_amount',<381>,1:31], [@35,190:202='target_amount',<381>,4:37]]}, table_dictionary={targets={month_name=[[@17,98:107='month_name',<381>,3:29]], target_amount=[[@5,29:29='p',<381>,1:29], [@33,188:188='p',<381>,4:35]], sales_amount=[[@14,80:91='sales_amount',<381>,3:11]]}, monthly_sales_long={month_name=[[@17,98:107='month_name',<381>,3:29]], sales_amount=[[@14,80:91='sales_amount',<381>,3:11]]}}, derivation={source_columns={tuple_0=[{name=month_name, table_ref=monthly_sales_long}, {name=sales_amount, table_ref=monthly_sales_long}]}, derived_columns={tuple_0={jan_sales_SUM=[[@12,76:78='SUM',<141>,3:7], [@20,113:123=''jan_sales'',<389>,3:44]], feb_sales_SUM=[[@12,76:78='SUM',<141>,3:7], [@22,126:136=''feb_sales'',<389>,3:57]], mar_sales_SUM=[[@12,76:78='SUM',<141>,3:7], [@24,139:149=''mar_sales'',<389>,3:70]]}}}, filters=[{name=jan_sales_SUM, table_ref=tuple_0}, {name=month_name, table_ref=monthly_sales_long}, {name=sales_amount, table_ref=monthly_sales_long}, {name=target_amount, table_ref=p}], interface={empid=[{name=empid, table_ref=null}], jan_sales_SUM=[{name=jan_sales_SUM, table_ref=tuple_0}, {name=month_name, table_ref=monthly_sales_long}, {name=sales_amount, table_ref=monthly_sales_long}], target_amount=[{name=target_amount, table_ref=p}]}, table_alias={p=targets, tuple_0=monthly_sales_long}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	// Derived column JOIN ON with pivot alias u + WHERE + SELECT expression (tax).
	public void pivotMonthlySalesLongJoinFilterDerivedSumTest() {
		final String query =
			"SELECT empid, jan_sales_SUM, feb_sales_SUM, jan_sales_SUM * 0.07 AS tax\n" +
			"FROM monthly_sales_long PIVOT (SUM(sales_amount) FOR month_name IN ('jan_sales', 'feb_sales')) u\n" +
			"JOIN targets t ON u.empid = t.empid AND u.jan_sales_SUM >= t.target_amount WHERE jan_sales_SUM > 100;";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoFatalErrors(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=empid, table_ref=null}}, 2={column={name=jan_sales_SUM, table_ref=null}}, 3={column={name=feb_sales_SUM, table_ref=null}}, 4={alias=tax, calc={left={column={name=jan_sales_SUM, table_ref=null}}, right={literal=0.07}, operator=*}}}, from={join={1={pivot={value={function={function_name=SUM, parameters={column={name=sales_amount, table_ref=null}}}}, for={column={name=month_name, table_ref=null}}, in={1={pivot_literal='jan_sales'}, 2={pivot_literal='feb_sales'}}}, alias=u, table={alias=null, table=monthly_sales_long}}, 2={join=JOIN, on={and={1={condition={left={column={name=empid, table_ref=u}}, right={column={name=empid, table_ref=t}}, operator==}}, 2={condition={left={column={name=jan_sales_SUM, table_ref=u}}, right={column={name=target_amount, table_ref=t}}, operator=>=}}}}}, 3={table={alias=t, table=targets}}}}, where={condition={left={column={name=jan_sales_SUM, table_ref=null}}, right={literal=100}, operator=>}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[empid, jan_sales_SUM, tax, feb_sales_SUM]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{targets={empid=[[@40,197:197='t',<381>,3:28]], month_name=[[@23,125:134='month_name',<381>,2:53]], target_amount=[[@48,228:228='t',<381>,3:59]], sales_amount=[[@20,107:118='sales_amount',<381>,2:35]]}, monthly_sales_long={month_name=[[@23,125:134='month_name',<381>,2:53]], empid=[[@36,187:187='u',<381>,3:18]], sales_amount=[[@20,107:118='sales_amount',<381>,2:35]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query1={empid=[[@1,7:11='empid',<381>,1:7], [@38,189:193='empid',<381>,3:20], [@42,199:203='empid',<381>,3:30]], jan_sales_SUM=[[@3,14:26='jan_sales_SUM',<381>,1:14], [@46,211:223='jan_sales_SUM',<381>,3:42], [@52,250:262='jan_sales_SUM',<381>,3:81], [@18,103:105='SUM',<141>,2:31], [@26,140:150=''jan_sales'',<389>,2:68]], tax=[[@13,68:70='tax',<381>,1:68]], feb_sales_SUM=[[@5,29:41='feb_sales_SUM',<381>,1:29], [@18,103:105='SUM',<141>,2:31], [@28,153:163=''feb_sales'',<389>,2:81]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query1={query_dictionary={empid=[[@1,7:11='empid',<381>,1:7], [@38,189:193='empid',<381>,3:20], [@42,199:203='empid',<381>,3:30]], jan_sales_SUM=[[@3,14:26='jan_sales_SUM',<381>,1:14], [@46,211:223='jan_sales_SUM',<381>,3:42], [@52,250:262='jan_sales_SUM',<381>,3:81], [@18,103:105='SUM',<141>,2:31], [@26,140:150=''jan_sales'',<389>,2:68]], tax=[[@13,68:70='tax',<381>,1:68]], feb_sales_SUM=[[@5,29:41='feb_sales_SUM',<381>,1:29], [@18,103:105='SUM',<141>,2:31], [@28,153:163=''feb_sales'',<389>,2:81]]}, table_dictionary={targets={month_name=[[@23,125:134='month_name',<381>,2:53]], empid=[[@40,197:197='t',<381>,3:28]], target_amount=[[@48,228:228='t',<381>,3:59]], sales_amount=[[@20,107:118='sales_amount',<381>,2:35]]}, monthly_sales_long={month_name=[[@23,125:134='month_name',<381>,2:53]], empid=[[@36,187:187='u',<381>,3:18]], sales_amount=[[@20,107:118='sales_amount',<381>,2:35]]}}, derivation={source_columns={u=[{name=month_name, table_ref=monthly_sales_long}, {name=sales_amount, table_ref=monthly_sales_long}]}, derived_columns={u={jan_sales_SUM=[[@18,103:105='SUM',<141>,2:31], [@26,140:150=''jan_sales'',<389>,2:68]], feb_sales_SUM=[[@18,103:105='SUM',<141>,2:31], [@28,153:163=''feb_sales'',<389>,2:81]]}}}, filters=[{name=empid, table_ref=u}, {name=empid, table_ref=t}, {name=jan_sales_SUM, table_ref=u}, {name=target_amount, table_ref=t}, {name=month_name, table_ref=monthly_sales_long}, {name=sales_amount, table_ref=monthly_sales_long}], interface={empid=[{name=empid, table_ref=null}], jan_sales_SUM=[{name=jan_sales_SUM, table_ref=u}, {name=month_name, table_ref=monthly_sales_long}, {name=sales_amount, table_ref=monthly_sales_long}], tax=[{name=jan_sales_SUM, table_ref=u}, {name=month_name, table_ref=monthly_sales_long}, {name=sales_amount, table_ref=monthly_sales_long}], feb_sales_SUM=[{name=feb_sales_SUM, table_ref=u}, {name=month_name, table_ref=monthly_sales_long}, {name=sales_amount, table_ref=monthly_sales_long}]}, table_alias={t=targets, u=monthly_sales_long}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	// Derived column in SELECT expression (tax) and WHERE on monthly_sales_long.
	public void pivotMonthlySalesLongTaxWhereDerivedSumTest() {
		final String query =
			"SELECT empid, jan_sales_SUM, feb_sales_SUM, mar_sales_SUM, jan_sales_SUM * 0.07 AS tax\n" +
			"FROM monthly_sales_long\n" +
			"PIVOT (SUM(sales_amount) FOR month_name IN ('jan_sales', 'feb_sales', 'mar_sales')) WHERE empid > 100;";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=empid, table_ref=null}}, 2={column={name=jan_sales_SUM, table_ref=null}}, 3={column={name=feb_sales_SUM, table_ref=null}}, 4={column={name=mar_sales_SUM, table_ref=null}}, 5={alias=tax, calc={left={column={name=jan_sales_SUM, table_ref=null}}, right={literal=0.07}, operator=*}}}, from={pivot={value={function={function_name=SUM, parameters={column={name=sales_amount, table_ref=null}}}}, for={column={name=month_name, table_ref=null}}, in={1={pivot_literal='jan_sales'}, 2={pivot_literal='feb_sales'}, 3={pivot_literal='mar_sales'}}}, table={alias=null, table=monthly_sales_long}}, where={condition={left={column={name=empid, table_ref=null}}, right={literal=100}, operator=>}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[empid, jan_sales_SUM, mar_sales_SUM, tax, feb_sales_SUM]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{monthly_sales_long={month_name=[[@25,140:149='month_name',<381>,3:29]], empid=[[@1,7:11='empid',<381>,1:7], [@36,201:205='empid',<381>,3:90]], sales_amount=[[@22,122:133='sales_amount',<381>,3:11]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query1={empid=[[@1,7:11='empid',<381>,1:7], [@36,201:205='empid',<381>,3:90]], jan_sales_SUM=[[@3,14:26='jan_sales_SUM',<381>,1:14], [@20,118:120='SUM',<141>,3:7], [@28,155:165=''jan_sales'',<389>,3:44]], mar_sales_SUM=[[@7,44:56='mar_sales_SUM',<381>,1:44], [@20,118:120='SUM',<141>,3:7], [@32,181:191=''mar_sales'',<389>,3:70]], tax=[[@15,83:85='tax',<381>,1:83]], feb_sales_SUM=[[@5,29:41='feb_sales_SUM',<381>,1:29], [@20,118:120='SUM',<141>,3:7], [@30,168:178=''feb_sales'',<389>,3:57]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query1={query_dictionary={empid=[[@1,7:11='empid',<381>,1:7], [@36,201:205='empid',<381>,3:90]], jan_sales_SUM=[[@3,14:26='jan_sales_SUM',<381>,1:14], [@20,118:120='SUM',<141>,3:7], [@28,155:165=''jan_sales'',<389>,3:44]], mar_sales_SUM=[[@7,44:56='mar_sales_SUM',<381>,1:44], [@20,118:120='SUM',<141>,3:7], [@32,181:191=''mar_sales'',<389>,3:70]], tax=[[@15,83:85='tax',<381>,1:83]], feb_sales_SUM=[[@5,29:41='feb_sales_SUM',<381>,1:29], [@20,118:120='SUM',<141>,3:7], [@30,168:178=''feb_sales'',<389>,3:57]]}, table_dictionary={monthly_sales_long={month_name=[[@25,140:149='month_name',<381>,3:29]], empid=[[@1,7:11='empid',<381>,1:7], [@36,201:205='empid',<381>,3:90]], sales_amount=[[@22,122:133='sales_amount',<381>,3:11]]}}, derivation={source_columns={tuple_0=[{name=month_name, table_ref=monthly_sales_long}, {name=sales_amount, table_ref=monthly_sales_long}]}, derived_columns={tuple_0={jan_sales_SUM=[[@20,118:120='SUM',<141>,3:7], [@28,155:165=''jan_sales'',<389>,3:44]], feb_sales_SUM=[[@20,118:120='SUM',<141>,3:7], [@30,168:178=''feb_sales'',<389>,3:57]], mar_sales_SUM=[[@20,118:120='SUM',<141>,3:7], [@32,181:191=''mar_sales'',<389>,3:70]]}}}, filters=[{name=empid, table_ref=null}], interface={empid=[{name=empid, table_ref=monthly_sales_long}], jan_sales_SUM=[{name=jan_sales_SUM, table_ref=tuple_0}, {name=month_name, table_ref=monthly_sales_long}, {name=sales_amount, table_ref=monthly_sales_long}], mar_sales_SUM=[{name=mar_sales_SUM, table_ref=tuple_0}, {name=month_name, table_ref=monthly_sales_long}, {name=sales_amount, table_ref=monthly_sales_long}], tax=[{name=jan_sales_SUM, table_ref=tuple_0}, {name=month_name, table_ref=monthly_sales_long}, {name=sales_amount, table_ref=monthly_sales_long}], feb_sales_SUM=[{name=feb_sales_SUM, table_ref=tuple_0}, {name=month_name, table_ref=monthly_sales_long}, {name=sales_amount, table_ref=monthly_sales_long}]}, table_alias={tuple_0=monthly_sales_long}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	// Derived column in ORDER BY expression: jan_sales_SUM/feb_sales_SUM have table_ref=null.
	public void pivotMonthlySalesLongOrderByExpressionDerivedSumProbeTest() {
		final String query =
			"SELECT empid, jan_sales_SUM, feb_sales_SUM, mar_sales_SUM\n" +
			"FROM monthly_sales_long\n" +
			"PIVOT (SUM(sales_amount) FOR month_name IN ('jan_sales', 'feb_sales', 'mar_sales'))\n" +
			"ORDER BY jan_sales_SUM / feb_sales_SUM;";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=empid, table_ref=null}}, 2={column={name=jan_sales_SUM, table_ref=null}}, 3={column={name=feb_sales_SUM, table_ref=null}}, 4={column={name=mar_sales_SUM, table_ref=null}}}, orderby={1={null_order=null, predicand={calc={left={column={name=jan_sales_SUM, table_ref=null}}, right={column={name=feb_sales_SUM, table_ref=null}}, operator=/}}, sort_order=ASC}}, from={pivot={value={function={function_name=SUM, parameters={column={name=sales_amount, table_ref=null}}}}, for={column={name=month_name, table_ref=null}}, in={1={pivot_literal='jan_sales'}, 2={pivot_literal='feb_sales'}, 3={pivot_literal='mar_sales'}}}, table={alias=null, table=monthly_sales_long}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[empid, jan_sales_SUM, mar_sales_SUM, feb_sales_SUM]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{monthly_sales_long={month_name=[[@17,111:120='month_name',<381>,3:29]], empid=[[@1,7:11='empid',<381>,1:7]], sales_amount=[[@14,93:104='sales_amount',<381>,3:11]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query1={empid=[[@1,7:11='empid',<381>,1:7]], jan_sales_SUM=[[@3,14:26='jan_sales_SUM',<381>,1:14], [@29,175:187='jan_sales_SUM',<381>,4:9], [@12,89:91='SUM',<141>,3:7], [@20,126:136=''jan_sales'',<389>,3:44]], mar_sales_SUM=[[@7,44:56='mar_sales_SUM',<381>,1:44], [@12,89:91='SUM',<141>,3:7], [@24,152:162=''mar_sales'',<389>,3:70]], feb_sales_SUM=[[@5,29:41='feb_sales_SUM',<381>,1:29], [@31,191:203='feb_sales_SUM',<381>,4:25], [@12,89:91='SUM',<141>,3:7], [@22,139:149=''feb_sales'',<389>,3:57]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query1={query_dictionary={empid=[[@1,7:11='empid',<381>,1:7]], jan_sales_SUM=[[@3,14:26='jan_sales_SUM',<381>,1:14], [@29,175:187='jan_sales_SUM',<381>,4:9], [@12,89:91='SUM',<141>,3:7], [@20,126:136=''jan_sales'',<389>,3:44]], mar_sales_SUM=[[@7,44:56='mar_sales_SUM',<381>,1:44], [@12,89:91='SUM',<141>,3:7], [@24,152:162=''mar_sales'',<389>,3:70]], feb_sales_SUM=[[@5,29:41='feb_sales_SUM',<381>,1:29], [@31,191:203='feb_sales_SUM',<381>,4:25], [@12,89:91='SUM',<141>,3:7], [@22,139:149=''feb_sales'',<389>,3:57]]}, table_dictionary={monthly_sales_long={month_name=[[@17,111:120='month_name',<381>,3:29]], empid=[[@1,7:11='empid',<381>,1:7]], sales_amount=[[@14,93:104='sales_amount',<381>,3:11]]}}, derivation={source_columns={tuple_0=[{name=month_name, table_ref=monthly_sales_long}, {name=sales_amount, table_ref=monthly_sales_long}]}, derived_columns={tuple_0={jan_sales_SUM=[[@12,89:91='SUM',<141>,3:7], [@20,126:136=''jan_sales'',<389>,3:44]], feb_sales_SUM=[[@12,89:91='SUM',<141>,3:7], [@22,139:149=''feb_sales'',<389>,3:57]], mar_sales_SUM=[[@12,89:91='SUM',<141>,3:7], [@24,152:162=''mar_sales'',<389>,3:70]]}}}, ordered_by=[{name=jan_sales_SUM, table_ref=tuple_0}, {name=month_name, table_ref=monthly_sales_long}, {name=sales_amount, table_ref=monthly_sales_long}, {name=feb_sales_SUM, table_ref=tuple_0}], interface={empid=[{name=empid, table_ref=monthly_sales_long}], jan_sales_SUM=[{name=jan_sales_SUM, table_ref=tuple_0}, {name=month_name, table_ref=monthly_sales_long}, {name=sales_amount, table_ref=monthly_sales_long}], mar_sales_SUM=[{name=mar_sales_SUM, table_ref=tuple_0}, {name=month_name, table_ref=monthly_sales_long}, {name=sales_amount, table_ref=monthly_sales_long}], feb_sales_SUM=[{name=feb_sales_SUM, table_ref=tuple_0}, {name=month_name, table_ref=monthly_sales_long}, {name=sales_amount, table_ref=monthly_sales_long}]}, table_alias={tuple_0=monthly_sales_long}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	// IN-list alias SELECT + JOIN on physical empid; pivot output aliases have table_ref=null.
	public void pivotBasicMonthSalesJoinV8Test() {
		final String query =
			"SELECT empid, jan_sales, feb_sales, mar_sales, t2.a1, t2.a2\n" +
			"FROM monthly_sales_long\n" +
			"PIVOT (SUM(sales_amount) FOR month_name IN ('jan_sales', 'feb_sales', 'mar_sales'))\n" +
			"JOIN metrics_table t2 ON empid = t2.metric_label;";
		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);
		assertNoFatalErrors(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=empid, table_ref=null}}, 2={column={name=jan_sales, table_ref=null}}, 3={column={name=feb_sales, table_ref=null}}, 4={column={name=mar_sales, table_ref=null}}, 5={column={name=a1, table_ref=t2}}, 6={column={name=a2, table_ref=t2}}}, from={join={1={pivot={value={function={function_name=SUM, parameters={column={name=sales_amount, table_ref=null}}}}, for={column={name=month_name, table_ref=null}}, in={1={pivot_literal='jan_sales'}, 2={pivot_literal='feb_sales'}, 3={pivot_literal='mar_sales'}}}, table={alias=null, table=monthly_sales_long}}, 2={join=JOIN, on={condition={left={column={name=empid, table_ref=null}}, right={column={name=metric_label, table_ref=t2}}, operator==}}}, 3={table={alias=t2, table=metrics_table}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[jan_sales, a1, empid, mar_sales, a2, feb_sales]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{metrics_table={a1=[[@9,47:48='t2',<381>,1:47]], a2=[[@13,54:55='t2',<381>,1:54]], month_name=[[@25,113:122='month_name',<381>,3:29]], sales_amount=[[@22,95:106='sales_amount',<381>,3:11]], metric_label=[[@41,201:202='t2',<381>,4:33]]}, monthly_sales_long={month_name=[[@25,113:122='month_name',<381>,3:29]], sales_amount=[[@22,95:106='sales_amount',<381>,3:11]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query1={jan_sales=[[@3,14:22='jan_sales',<381>,1:14]], a1=[[@11,50:51='a1',<381>,1:50]], empid=[[@1,7:11='empid',<381>,1:7], [@39,193:197='empid',<381>,4:25]], mar_sales=[[@7,36:44='mar_sales',<381>,1:36]], a2=[[@15,57:58='a2',<381>,1:57]], feb_sales=[[@5,25:33='feb_sales',<381>,1:25]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query1={query_dictionary={jan_sales=[[@3,14:22='jan_sales',<381>,1:14]], a1=[[@11,50:51='a1',<381>,1:50]], empid=[[@1,7:11='empid',<381>,1:7], [@39,193:197='empid',<381>,4:25]], mar_sales=[[@7,36:44='mar_sales',<381>,1:36]], a2=[[@15,57:58='a2',<381>,1:57]], feb_sales=[[@5,25:33='feb_sales',<381>,1:25]]}, table_dictionary={metrics_table={a1=[[@9,47:48='t2',<381>,1:47]], month_name=[[@25,113:122='month_name',<381>,3:29]], a2=[[@13,54:55='t2',<381>,1:54]], sales_amount=[[@22,95:106='sales_amount',<381>,3:11]], metric_label=[[@41,201:202='t2',<381>,4:33]]}, monthly_sales_long={month_name=[[@25,113:122='month_name',<381>,3:29]], sales_amount=[[@22,95:106='sales_amount',<381>,3:11]]}}, derivation={source_columns={tuple_0=[{name=month_name, table_ref=monthly_sales_long}, {name=sales_amount, table_ref=monthly_sales_long}]}, derived_columns={tuple_0={jan_sales_SUM=[[@20,91:93='SUM',<141>,3:7], [@28,128:138=''jan_sales'',<389>,3:44]], feb_sales_SUM=[[@20,91:93='SUM',<141>,3:7], [@30,141:151=''feb_sales'',<389>,3:57]], mar_sales_SUM=[[@20,91:93='SUM',<141>,3:7], [@32,154:164=''mar_sales'',<389>,3:70]]}}}, filters=[{name=empid, table_ref=null}, {name=metric_label, table_ref=t2}], interface={jan_sales=[{name=jan_sales, table_ref=null}], a1=[{name=a1, table_ref=t2}], empid=[{name=empid, table_ref=null}], mar_sales=[{name=mar_sales, table_ref=null}], a2=[{name=a2, table_ref=t2}], feb_sales=[{name=feb_sales, table_ref=null}]}, table_alias={t2=metrics_table, tuple_0=monthly_sales_long}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void pivotSameQuerySelectDerivedColumnFromTableTest() {
		final String query =
			"SELECT src, A_sum\n" +
			"FROM tab1\n" +
			"PIVOT (SUM(col1) sum FOR col2 IN ('A'));";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=src, table_ref=null}}, 2={column={name=A_sum, table_ref=null}}}, from={pivot={value={function={function_name=SUM, parameters={column={name=col1, table_ref=null}}}, alias=sum}, for={column={name=col2, table_ref=null}}, in={1={pivot_literal='A'}}}, table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[src, A_sum]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={src=[[@1,7:9='src',<381>,1:7]], col2=[[@14,53:56='col2',<381>,3:25]], col1=[[@10,39:42='col1',<381>,3:11]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query1={src=[[@1,7:9='src',<381>,1:7]], A_sum=[[@3,12:16='A_sum',<381>,1:12], [@8,35:37='SUM',<141>,3:7], [@17,62:64=''A'',<389>,3:34]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query1={query_dictionary={src=[[@1,7:9='src',<381>,1:7]], A_sum=[[@3,12:16='A_sum',<381>,1:12], [@8,35:37='SUM',<141>,3:7], [@17,62:64=''A'',<389>,3:34]]}, table_dictionary={tab1={src=[[@1,7:9='src',<381>,1:7]], col2=[[@14,53:56='col2',<381>,3:25]], col1=[[@10,39:42='col1',<381>,3:11]]}}, derivation={source_columns={tuple_0=[{name=col2, table_ref=tab1}, {name=col1, table_ref=tab1}]}, derived_columns={tuple_0={A_sum=[[@8,35:37='SUM',<141>,3:7], [@17,62:64=''A'',<389>,3:34]]}}}, interface={src=[{name=src, table_ref=tab1}], A_sum=[{name=A_sum, table_ref=tuple_0}, {name=col2, table_ref=tab1}, {name=col1, table_ref=tab1}]}, table_alias={tuple_0=tab1}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void pivotSameQueryWhereDerivedColumnFromTableTest() {
		final String query =
			"SELECT src\n" +
			"FROM tab1\n" +
			"PIVOT (SUM(col1) sum FOR col2 IN ('A'))\n" +
			"WHERE A_sum > 0;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=src, table_ref=null}}}, from={pivot={value={function={function_name=SUM, parameters={column={name=col1, table_ref=null}}}, alias=sum}, for={column={name=col2, table_ref=null}}, in={1={pivot_literal='A'}}}, table={alias=null, table=tab1}}, where={condition={left={column={name=A_sum, table_ref=null}}, right={literal=0}, operator=>}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[src]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={src=[[@1,7:9='src',<381>,1:7]], col2=[[@12,46:49='col2',<381>,3:25]], col1=[[@8,32:35='col1',<381>,3:11]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query1={src=[[@1,7:9='src',<381>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query1={query_dictionary={src=[[@1,7:9='src',<381>,1:7]]}, table_dictionary={tab1={src=[[@1,7:9='src',<381>,1:7]], col2=[[@12,46:49='col2',<381>,3:25]], col1=[[@8,32:35='col1',<381>,3:11]]}}, derivation={source_columns={tuple_0=[{name=col2, table_ref=tab1}, {name=col1, table_ref=tab1}]}, derived_columns={tuple_0={A_sum=[[@6,28:30='SUM',<141>,3:7], [@15,55:57=''A'',<389>,3:34]]}}}, filters=[{name=A_sum, table_ref=tuple_0}, {name=col2, table_ref=tab1}, {name=col1, table_ref=tab1}], interface={src=[{name=src, table_ref=tab1}]}, table_alias={tuple_0=tab1}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void pivotSameQueryGroupByDerivedColumnFromTableTest() {
		final String query =
			"SELECT src\n" +
			"FROM tab1\n" +
			"PIVOT (SUM(col1) sum FOR col2 IN ('A'))\n" +
			"GROUP BY src, A_sum;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=src, table_ref=null}}}, from={pivot={value={function={function_name=SUM, parameters={column={name=col1, table_ref=null}}}, alias=sum}, for={column={name=col2, table_ref=null}}, in={1={pivot_literal='A'}}}, table={alias=null, table=tab1}}, groupby={1={column={name=src, table_ref=null}}, 2={column={name=A_sum, table_ref=null}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[src]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={src=[[@1,7:9='src',<381>,1:7], [@20,70:72='src',<381>,4:9]], col2=[[@12,46:49='col2',<381>,3:25]], col1=[[@8,32:35='col1',<381>,3:11]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query1={src=[[@1,7:9='src',<381>,1:7], [@20,70:72='src',<381>,4:9]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query1={query_dictionary={src=[[@1,7:9='src',<381>,1:7], [@20,70:72='src',<381>,4:9]]}, table_dictionary={tab1={src=[[@1,7:9='src',<381>,1:7], [@20,70:72='src',<381>,4:9]], col2=[[@12,46:49='col2',<381>,3:25]], col1=[[@8,32:35='col1',<381>,3:11]]}}, grouped_by=[{name=src, table_ref=null}, {name=A_sum, table_ref=tuple_0}, {name=col2, table_ref=tab1}, {name=col1, table_ref=tab1}], derivation={source_columns={tuple_0=[{name=col2, table_ref=tab1}, {name=col1, table_ref=tab1}]}, derived_columns={tuple_0={A_sum=[[@6,28:30='SUM',<141>,3:7], [@15,55:57=''A'',<389>,3:34]]}}}, interface={src=[{name=src, table_ref=tab1}]}, table_alias={tuple_0=tab1}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void pivotSameQueryHavingDerivedColumnFromTableTest() {
		final String query =
			"SELECT src\n" +
			"FROM tab1\n" +
			"PIVOT (SUM(col1) sum FOR col2 IN ('A'))\n" +
			"GROUP BY src, A_sum\n" +
			"HAVING A_sum > 0;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=src, table_ref=null}}}, having={condition={left={column={name=A_sum, table_ref=null}}, right={literal=0}, operator=>}}, from={pivot={value={function={function_name=SUM, parameters={column={name=col1, table_ref=null}}}, alias=sum}, for={column={name=col2, table_ref=null}}, in={1={pivot_literal='A'}}}, table={alias=null, table=tab1}}, groupby={1={column={name=src, table_ref=null}}, 2={column={name=A_sum, table_ref=null}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[src]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={src=[[@1,7:9='src',<381>,1:7], [@20,70:72='src',<381>,4:9]], col2=[[@12,46:49='col2',<381>,3:25]], col1=[[@8,32:35='col1',<381>,3:11]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query1={src=[[@1,7:9='src',<381>,1:7], [@20,70:72='src',<381>,4:9]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query1={query_dictionary={src=[[@1,7:9='src',<381>,1:7], [@20,70:72='src',<381>,4:9]]}, table_dictionary={tab1={src=[[@1,7:9='src',<381>,1:7], [@20,70:72='src',<381>,4:9]], col2=[[@12,46:49='col2',<381>,3:25]], col1=[[@8,32:35='col1',<381>,3:11]]}}, grouped_by=[{name=src, table_ref=null}, {name=A_sum, table_ref=tuple_0}, {name=col2, table_ref=tab1}, {name=col1, table_ref=tab1}], derivation={source_columns={tuple_0=[{name=col2, table_ref=tab1}, {name=col1, table_ref=tab1}]}, derived_columns={tuple_0={A_sum=[[@6,28:30='SUM',<141>,3:7], [@15,55:57=''A'',<389>,3:34]]}}}, filters=[{name=A_sum, table_ref=tuple_0}, {name=col2, table_ref=tab1}, {name=col1, table_ref=tab1}], interface={src=[{name=src, table_ref=tab1}]}, table_alias={tuple_0=tab1}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void pivotSameQueryQualifyDerivedColumnFromTableTest() {
		final String query =
			"SELECT src\n" +
			"FROM tab1\n" +
			"PIVOT (SUM(col1) sum FOR col2 IN ('A'))\n" +
			"QUALIFY A_sum > 0;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=src, table_ref=null}}}, from={pivot={value={function={function_name=SUM, parameters={column={name=col1, table_ref=null}}}, alias=sum}, for={column={name=col2, table_ref=null}}, in={1={pivot_literal='A'}}}, table={alias=null, table=tab1}}, qualify={condition={left={column={name=A_sum, table_ref=null}}, right={literal=0}, operator=>}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[src]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={src=[[@1,7:9='src',<381>,1:7]], col2=[[@12,46:49='col2',<381>,3:25]], col1=[[@8,32:35='col1',<381>,3:11]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query1={src=[[@1,7:9='src',<381>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query1={query_dictionary={src=[[@1,7:9='src',<381>,1:7]]}, table_dictionary={tab1={src=[[@1,7:9='src',<381>,1:7]], col2=[[@12,46:49='col2',<381>,3:25]], col1=[[@8,32:35='col1',<381>,3:11]]}}, derivation={source_columns={tuple_0=[{name=col2, table_ref=tab1}, {name=col1, table_ref=tab1}]}, derived_columns={tuple_0={A_sum=[[@6,28:30='SUM',<141>,3:7], [@15,55:57=''A'',<389>,3:34]]}}}, filters=[{name=A_sum, table_ref=tuple_0}, {name=col2, table_ref=tab1}, {name=col1, table_ref=tab1}], interface={src=[{name=src, table_ref=tab1}]}, table_alias={tuple_0=tab1}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void pivotSameQueryOrderByDerivedColumnFromTableTest() {
		final String query =
			"SELECT src\n" +
			"FROM tab1\n" +
			"PIVOT (SUM(col1) sum FOR col2 IN ('A'))\n" +
			"ORDER BY A_sum;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=src, table_ref=null}}}, orderby={1={null_order=null, predicand={column={name=A_sum, table_ref=null}}, sort_order=ASC}}, from={pivot={value={function={function_name=SUM, parameters={column={name=col1, table_ref=null}}}, alias=sum}, for={column={name=col2, table_ref=null}}, in={1={pivot_literal='A'}}}, table={alias=null, table=tab1}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[src]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{tab1={src=[[@1,7:9='src',<381>,1:7]], col2=[[@12,46:49='col2',<381>,3:25]], col1=[[@8,32:35='col1',<381>,3:11]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query1={src=[[@1,7:9='src',<381>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query1={query_dictionary={src=[[@1,7:9='src',<381>,1:7]]}, table_dictionary={tab1={src=[[@1,7:9='src',<381>,1:7]], col2=[[@12,46:49='col2',<381>,3:25]], col1=[[@8,32:35='col1',<381>,3:11]]}}, derivation={source_columns={tuple_0=[{name=col2, table_ref=tab1}, {name=col1, table_ref=tab1}]}, derived_columns={tuple_0={A_sum=[[@6,28:30='SUM',<141>,3:7], [@15,55:57=''A'',<389>,3:34]]}}}, ordered_by=[{name=A_sum, table_ref=tuple_0}, {name=col2, table_ref=tab1}, {name=col1, table_ref=tab1}], interface={src=[{name=src, table_ref=tab1}]}, table_alias={tuple_0=tab1}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void pivotSameQueryJoinDerivedColumnFromTableTest() {
		final String query =
			"SELECT A_sum, t.target_amount\n" +
			"FROM (SELECT col1, col2 FROM tab1) q\n" +
			"PIVOT (SUM(col1) sum FOR col2 IN ('A'))\n" +
			"JOIN targets t ON A_sum >= t.target_amount;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=A_sum, table_ref=null}}, 2={column={name=target_amount, table_ref=t}}}, from={join={1={pivot={value={function={function_name=SUM, parameters={column={name=col1, table_ref=null}}}, alias=sum}, for={column={name=col2, table_ref=null}}, in={1={pivot_literal='A'}}}, table={alias=q, query={select={1={column={name=col1, table_ref=null}}, 2={column={name=col2, table_ref=null}}}, from={table={alias=null, table=tab1}}}}}, 2={join=JOIN, on={condition={left={column={name=A_sum, table_ref=null}}, right={column={name=target_amount, table_ref=t}}, operator=>=}}}, 3={table={alias=t, table=targets}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[target_amount, A_sum]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{q={col2=[[@24,92:95='col2',<381>,3:25]], col1=[[@20,78:81='col1',<381>,3:11]]}, tab1={col2=[[@11,49:52='col2',<381>,2:19]], col1=[[@9,43:46='col1',<381>,2:13]]}, targets={target_amount=[[@3,14:14='t',<381>,1:14], [@36,134:134='t',<381>,4:27]], col2=[[@24,92:95='col2',<381>,3:25]], col1=[[@20,78:81='col1',<381>,3:11]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={col2=[[@11,49:52='col2',<381>,2:19]], col1=[[@9,43:46='col1',<381>,2:13]]}, query2={A_sum=[[@1,7:11='A_sum',<381>,1:7], [@34,125:129='A_sum',<381>,4:18], [@18,74:76='SUM',<141>,3:7], [@27,101:103=''A'',<389>,3:34]], target_amount=[[@5,16:28='target_amount',<381>,1:16], [@38,136:148='target_amount',<381>,4:29]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query2={query_dictionary={target_amount=[[@5,16:28='target_amount',<381>,1:16], [@38,136:148='target_amount',<381>,4:29]], A_sum=[[@1,7:11='A_sum',<381>,1:7], [@34,125:129='A_sum',<381>,4:18], [@18,74:76='SUM',<141>,3:7], [@27,101:103=''A'',<389>,3:34]]}, table_dictionary={q={col2=[[@24,92:95='col2',<381>,3:25]], col1=[[@20,78:81='col1',<381>,3:11]]}, targets={target_amount=[[@3,14:14='t',<381>,1:14], [@36,134:134='t',<381>,4:27]], col2=[[@24,92:95='col2',<381>,3:25]], col1=[[@20,78:81='col1',<381>,3:11]]}}, def_query0={query_dictionary={col2=[[@11,49:52='col2',<381>,2:19]], col1=[[@9,43:46='col1',<381>,2:13]]}, table_dictionary={tab1={col2=[[@11,49:52='col2',<381>,2:19]], col1=[[@9,43:46='col1',<381>,2:13]]}}, interface={col2=[{name=col2, table_ref=tab1}], col1=[{name=col1, table_ref=tab1}]}}, derivation={source_columns={tuple_0=[{name=col2, table_ref=q}, {name=col1, table_ref=q}]}, derived_columns={tuple_0={A_sum=[[@18,74:76='SUM',<141>,3:7], [@27,101:103=''A'',<389>,3:34]]}}}, filters=[{name=A_sum, table_ref=tuple_0}, {name=col2, table_ref=q}, {name=col1, table_ref=q}, {name=target_amount, table_ref=t}], interface={target_amount=[{name=target_amount, table_ref=t}], A_sum=[{name=A_sum, table_ref=tuple_0}, {name=col2, table_ref=q}, {name=col1, table_ref=q}]}, table_alias={q=query0, t=targets, tuple_0=q}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void pivotSameQueryDerivedColumnsFromSubqueryAcrossClausesTest() {
		final String query =
			"SELECT q.src\n" +
			"FROM (SELECT src, col1, col2 FROM tab1) q\n" +
			"PIVOT (SUM(col1) sum FOR col2 IN ('A'))\n" +
			"JOIN targets t ON A_sum >= t.target_amount\n" +
			"WHERE A_sum > 0\n" +
			"GROUP BY q.src, A_sum\n" +
			"HAVING A_sum > 0\n" +
			"QUALIFY A_sum > 0\n" +
			"ORDER BY A_sum;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=src, table_ref=q}}}, having={condition={left={column={name=A_sum, table_ref=null}}, right={literal=0}, operator=>}}, orderby={1={null_order=null, predicand={column={name=A_sum, table_ref=null}}, sort_order=ASC}}, from={join={1={pivot={value={function={function_name=SUM, parameters={column={name=col1, table_ref=null}}}, alias=sum}, for={column={name=col2, table_ref=null}}, in={1={pivot_literal='A'}}}, table={alias=q, query={select={1={column={name=src, table_ref=null}}, 2={column={name=col1, table_ref=null}}, 3={column={name=col2, table_ref=null}}}, from={table={alias=null, table=tab1}}}}}, 2={join=JOIN, on={condition={left={column={name=A_sum, table_ref=null}}, right={column={name=target_amount, table_ref=t}}, operator=>=}}}, 3={table={alias=t, table=targets}}}}, where={condition={left={column={name=A_sum, table_ref=null}}, right={literal=0}, operator=>}}, groupby={1={column={name=src, table_ref=q}}, 2={column={name=A_sum, table_ref=null}}}, qualify={condition={left={column={name=A_sum, table_ref=null}}, right={literal=0}, operator=>}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[src]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{q={col2=[[@24,80:83='col2',<381>,3:25]], col1=[[@20,66:69='col1',<381>,3:11]]}, tab1={src=[[@7,26:28='src',<381>,2:13]], col2=[[@11,37:40='col2',<381>,2:24]], col1=[[@9,31:34='col1',<381>,2:18]]}, targets={target_amount=[[@36,122:122='t',<381>,4:27]], col2=[[@24,80:83='col2',<381>,3:25]], col1=[[@20,66:69='col1',<381>,3:11]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
		 "{query0={col2=[[@11,37:40='col2',<381>,2:24]], src=[[@7,26:28='src',<381>,2:13], [@1,7:7='q',<381>,1:7], [@45,163:163='q',<381>,6:9]], col1=[[@9,31:34='col1',<381>,2:18]]}, query2={src=[[@3,9:11='src',<381>,1:9], [@47,165:167='src',<381>,6:11]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query2={query_dictionary={src=[[@3,9:11='src',<381>,1:9], [@47,165:167='src',<381>,6:11]]}, table_dictionary={q={col2=[[@24,80:83='col2',<381>,3:25]], col1=[[@20,66:69='col1',<381>,3:11]]}, targets={target_amount=[[@36,122:122='t',<381>,4:27]], col2=[[@24,80:83='col2',<381>,3:25]], col1=[[@20,66:69='col1',<381>,3:11]]}}, grouped_by=[{name=src, table_ref=q}, {name=A_sum, table_ref=tuple_0}, {name=col2, table_ref=q}, {name=col1, table_ref=q}], def_query0={query_dictionary={src=[[@7,26:28='src',<381>,2:13], [@1,7:7='q',<381>,1:7], [@45,163:163='q',<381>,6:9]], col2=[[@11,37:40='col2',<381>,2:24]], col1=[[@9,31:34='col1',<381>,2:18]]}, table_dictionary={tab1={src=[[@7,26:28='src',<381>,2:13]], col2=[[@11,37:40='col2',<381>,2:24]], col1=[[@9,31:34='col1',<381>,2:18]]}}, interface={src=[{name=src, table_ref=tab1}], col2=[{name=col2, table_ref=tab1}], col1=[{name=col1, table_ref=tab1}]}}, derivation={source_columns={tuple_0=[{name=col2, table_ref=q}, {name=col1, table_ref=q}]}, derived_columns={tuple_0={A_sum=[[@18,62:64='SUM',<141>,3:7], [@27,89:91=''A'',<389>,3:34]]}}}, ordered_by=[{name=A_sum, table_ref=tuple_0}, {name=col2, table_ref=q}, {name=col1, table_ref=q}], filters=[{name=A_sum, table_ref=tuple_0}, {name=col2, table_ref=q}, {name=col1, table_ref=q}, {name=target_amount, table_ref=t}], interface={src=[{name=src, table_ref=q}]}, table_alias={q=query0, t=targets, tuple_0=q}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void pivotNestedTableDerivedColumnsResolveInOuterClausesV1Test() {
		final String query =
			"SELECT p.empid, p.jan_sum, t.target_amount\n" +
			"FROM (\n" +
			"  SELECT *\n" +
			"  FROM monthly_sales_long\n" +
			"  PIVOT (SUM(sales_amount) sum FOR month_name IN ('jan', 'feb'))\n" +
			") p\n" +
			"JOIN targets t ON p.jan_sum >= t.target_amount\n" +
			"WHERE p.feb_sum > 0\n" +
			"GROUP BY p.empid, p.jan_sum, p.feb_sum, t.target_amount\n" +
			"HAVING p.jan_sum > 10\n" +
			"QUALIFY p.feb_sum > 0\n" +
			"ORDER BY p.jan_sum;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=empid, table_ref=p}}, 2={column={name=jan_sum, table_ref=p}}, 3={column={name=target_amount, table_ref=t}}}, having={condition={left={column={name=jan_sum, table_ref=p}}, right={literal=10}, operator=>}}, orderby={1={null_order=null, predicand={column={name=jan_sum, table_ref=p}}, sort_order=ASC}}, from={join={1={table={alias=p, query={select={1={column={name=*, table_ref=*}}}, from={pivot={value={function={function_name=SUM, parameters={column={name=sales_amount, table_ref=null}}}, alias=sum}, for={column={name=month_name, table_ref=null}}, in={1={pivot_literal='jan'}, 2={pivot_literal='feb'}}}, table={alias=null, table=monthly_sales_long}}}}}, 2={join=JOIN, on={condition={left={column={name=jan_sum, table_ref=p}}, right={column={name=target_amount, table_ref=t}}, operator=>=}}}, 3={table={alias=t, table=targets}}}}, where={condition={left={column={name=feb_sum, table_ref=p}}, right={literal=0}, operator=>}}, groupby={1={column={name=empid, table_ref=p}}, 2={column={name=jan_sum, table_ref=p}}, 3={column={name=feb_sum, table_ref=p}}, 4={column={name=target_amount, table_ref=t}}}, qualify={condition={left={column={name=feb_sum, table_ref=p}}, right={literal=0}, operator=>}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[empid, target_amount, jan_sum]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{targets={target_amount=[[@9,27:27='t',<381>,1:27], [@44,187:187='t',<381>,7:31], [@67,263:263='t',<381>,9:40]]}, monthly_sales_long={month_name=[[@26,122:131='month_name',<381>,5:35]], sales_amount=[[@22,100:111='sales_amount',<381>,5:13]], *=[[@15,59:59='*',<291>,3:9]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query1={feb_sum=[[@48,209:209='p',<381>,8:6], [@63,252:252='p',<381>,9:29], [@77,309:309='p',<381>,11:8]], empid=[[@1,7:7='p',<381>,1:7], [@55,232:232='p',<381>,9:9]], *=[[@15,59:59='*',<291>,3:9]], jan_sum=[[@5,16:16='p',<381>,1:16], [@40,174:174='p',<381>,7:18], [@59,241:241='p',<381>,9:18], [@71,286:286='p',<381>,10:7], [@84,332:332='p',<381>,12:9]]}, query2={empid=[[@3,9:13='empid',<381>,1:9]], target_amount=[[@11,29:41='target_amount',<381>,1:29]], jan_sum=[[@7,18:24='jan_sum',<381>,1:18]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query2={query_dictionary={empid=[[@3,9:13='empid',<381>,1:9]], target_amount=[[@11,29:41='target_amount',<381>,1:29]], jan_sum=[[@7,18:24='jan_sum',<381>,1:18]]}, table_dictionary={targets={target_amount=[[@9,27:27='t',<381>,1:27], [@44,187:187='t',<381>,7:31], [@67,263:263='t',<381>,9:40]]}}, grouped_by=[{name=empid, table_ref=p}, {name=jan_sum, table_ref=p}, {name=feb_sum, table_ref=p}, {name=target_amount, table_ref=t}], def_query1={query_dictionary={empid=[[@1,7:7='p',<381>,1:7], [@55,232:232='p',<381>,9:9]], jan_sum=[[@5,16:16='p',<381>,1:16], [@40,174:174='p',<381>,7:18], [@59,241:241='p',<381>,9:18], [@71,286:286='p',<381>,10:7], [@84,332:332='p',<381>,12:9]], feb_sum=[[@48,209:209='p',<381>,8:6], [@63,252:252='p',<381>,9:29], [@77,309:309='p',<381>,11:8]], *=[[@15,59:59='*',<291>,3:9]]}, table_dictionary={monthly_sales_long={month_name=[[@26,122:131='month_name',<381>,5:35]], sales_amount=[[@22,100:111='sales_amount',<381>,5:13]], *=[[@15,59:59='*',<291>,3:9]]}}, derivation={source_columns={tuple_0=[{name=month_name, table_ref=monthly_sales_long}, {name=sales_amount, table_ref=monthly_sales_long}]}, derived_columns={tuple_0={jan_sum=[[@20,96:98='SUM',<141>,5:9], [@29,137:141=''jan'',<389>,5:50]], feb_sum=[[@20,96:98='SUM',<141>,5:9], [@31,144:148=''feb'',<389>,5:57]]}}}, interface={*=[{name=*, table_ref=*}]}, table_alias={tuple_0=monthly_sales_long}}, ordered_by=[{name=jan_sum, table_ref=p}], filters=[{name=jan_sum, table_ref=p}, {name=target_amount, table_ref=t}, {name=feb_sum, table_ref=p}], interface={empid=[{name=empid, table_ref=p}], target_amount=[{name=target_amount, table_ref=t}], jan_sum=[{name=jan_sum, table_ref=p}]}, table_alias={p=query1, t=targets}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void pivotNestedSubqueryDerivedColumnsResolveInOuterClausesV1Test() {
		final String query =
			"SELECT p.empid, p.jan_sum, t.target_amount\n" +
			"FROM (\n" +
			"  SELECT *\n" +
			"  FROM (SELECT empid, month_name, sales_amount FROM monthly_sales_long) src\n" +
			"  PIVOT (SUM(sales_amount) sum FOR month_name IN ('jan', 'feb'))\n" +
			") p\n" +
			"JOIN targets t ON p.jan_sum >= t.target_amount\n" +
			"WHERE p.feb_sum > 0\n" +
			"GROUP BY p.empid, p.jan_sum, p.feb_sum, t.target_amount\n" +
			"HAVING p.jan_sum > 10\n" +
			"QUALIFY p.feb_sum > 0\n" +
			"ORDER BY p.jan_sum;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=empid, table_ref=p}}, 2={column={name=jan_sum, table_ref=p}}, 3={column={name=target_amount, table_ref=t}}}, having={condition={left={column={name=jan_sum, table_ref=p}}, right={literal=10}, operator=>}}, orderby={1={null_order=null, predicand={column={name=jan_sum, table_ref=p}}, sort_order=ASC}}, from={join={1={table={alias=p, query={select={1={column={name=*, table_ref=*}}}, from={pivot={value={function={function_name=SUM, parameters={column={name=sales_amount, table_ref=null}}}, alias=sum}, for={column={name=month_name, table_ref=null}}, in={1={pivot_literal='jan'}, 2={pivot_literal='feb'}}}, table={alias=src, query={select={1={column={name=empid, table_ref=null}}, 2={column={name=month_name, table_ref=null}}, 3={column={name=sales_amount, table_ref=null}}}, from={table={alias=null, table=monthly_sales_long}}}}}}}}, 2={join=JOIN, on={condition={left={column={name=jan_sum, table_ref=p}}, right={column={name=target_amount, table_ref=t}}, operator=>=}}}, 3={table={alias=t, table=targets}}}}, where={condition={left={column={name=feb_sum, table_ref=p}}, right={literal=0}, operator=>}}, groupby={1={column={name=empid, table_ref=p}}, 2={column={name=jan_sum, table_ref=p}}, 3={column={name=feb_sum, table_ref=p}}, 4={column={name=target_amount, table_ref=t}}}, qualify={condition={left={column={name=feb_sum, table_ref=p}}, right={literal=0}, operator=>}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[empid, target_amount, jan_sum]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{src={month_name=[[@36,172:181='month_name',<381>,5:35]], sales_amount=[[@32,150:161='sales_amount',<381>,5:13]], *=[[@15,59:59='*',<291>,3:9]]}, targets={target_amount=[[@9,27:27='t',<381>,1:27], [@54,237:237='t',<381>,7:31], [@77,313:313='t',<381>,9:40]]}, monthly_sales_long={empid=[[@19,76:80='empid',<381>,4:15]], month_name=[[@21,83:92='month_name',<381>,4:22]], sales_amount=[[@23,95:106='sales_amount',<381>,4:34]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={empid=[[@19,76:80='empid',<381>,4:15]], month_name=[[@21,83:92='month_name',<381>,4:22]], sales_amount=[[@23,95:106='sales_amount',<381>,4:34]], *=[[@15,59:59='*',<291>,3:9]]}, query2={feb_sum=[[@58,259:259='p',<381>,8:6], [@73,302:302='p',<381>,9:29], [@87,359:359='p',<381>,11:8]], empid=[[@1,7:7='p',<381>,1:7], [@65,282:282='p',<381>,9:9]], *=[[@15,59:59='*',<291>,3:9]], jan_sum=[[@5,16:16='p',<381>,1:16], [@50,224:224='p',<381>,7:18], [@69,291:291='p',<381>,9:18], [@81,336:336='p',<381>,10:7], [@94,382:382='p',<381>,12:9]]}, query3={empid=[[@3,9:13='empid',<381>,1:9]], target_amount=[[@11,29:41='target_amount',<381>,1:29]], jan_sum=[[@7,18:24='jan_sum',<381>,1:18]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query3={query_dictionary={empid=[[@3,9:13='empid',<381>,1:9]], target_amount=[[@11,29:41='target_amount',<381>,1:29]], jan_sum=[[@7,18:24='jan_sum',<381>,1:18]]}, table_dictionary={targets={target_amount=[[@9,27:27='t',<381>,1:27], [@54,237:237='t',<381>,7:31], [@77,313:313='t',<381>,9:40]]}}, grouped_by=[{name=empid, table_ref=p}, {name=jan_sum, table_ref=p}, {name=feb_sum, table_ref=p}, {name=target_amount, table_ref=t}], ordered_by=[{name=jan_sum, table_ref=p}], filters=[{name=jan_sum, table_ref=p}, {name=target_amount, table_ref=t}, {name=feb_sum, table_ref=p}], interface={empid=[{name=empid, table_ref=p}], target_amount=[{name=target_amount, table_ref=t}], jan_sum=[{name=jan_sum, table_ref=p}]}, table_alias={p=query2, t=targets}, def_query2={query_dictionary={empid=[[@1,7:7='p',<381>,1:7], [@65,282:282='p',<381>,9:9]], jan_sum=[[@5,16:16='p',<381>,1:16], [@50,224:224='p',<381>,7:18], [@69,291:291='p',<381>,9:18], [@81,336:336='p',<381>,10:7], [@94,382:382='p',<381>,12:9]], feb_sum=[[@58,259:259='p',<381>,8:6], [@73,302:302='p',<381>,9:29], [@87,359:359='p',<381>,11:8]], *=[[@15,59:59='*',<291>,3:9]]}, table_dictionary={src={month_name=[[@36,172:181='month_name',<381>,5:35]], sales_amount=[[@32,150:161='sales_amount',<381>,5:13]], *=[[@15,59:59='*',<291>,3:9]]}}, def_query0={query_dictionary={empid=[[@19,76:80='empid',<381>,4:15]], month_name=[[@21,83:92='month_name',<381>,4:22]], sales_amount=[[@23,95:106='sales_amount',<381>,4:34]], *=[[@15,59:59='*',<291>,3:9]]}, table_dictionary={monthly_sales_long={empid=[[@19,76:80='empid',<381>,4:15]], month_name=[[@21,83:92='month_name',<381>,4:22]], sales_amount=[[@23,95:106='sales_amount',<381>,4:34]]}}, interface={empid=[{name=empid, table_ref=monthly_sales_long}], month_name=[{name=month_name, table_ref=monthly_sales_long}], sales_amount=[{name=sales_amount, table_ref=monthly_sales_long}]}}, derivation={source_columns={tuple_0=[{name=month_name, table_ref=src}, {name=sales_amount, table_ref=src}]}, derived_columns={tuple_0={jan_sum=[[@30,146:148='SUM',<141>,5:9], [@39,187:191=''jan'',<389>,5:50]], feb_sum=[[@30,146:148='SUM',<141>,5:9], [@41,194:198=''feb'',<389>,5:57]]}}}, interface={*=[{name=*, table_ref=*}]}, table_alias={src=query0, tuple_0=src}}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void pivotUpdateFromRhsUnqualifiedDerivedColumnReentryE0gTest() {
		final String query =
			"UPDATE targets t\n" +
			"SET target_amount = jan_sales_SUM\n" +
			"FROM monthly_sales_long\n" +
			"PIVOT (SUM(sales_amount) FOR month_name IN ('jan_sales', 'feb_sales')) u\n" +
			"WHERE t.empid = u.empid;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={update={from={pivot={value={function={function_name=SUM, parameters={column={name=sales_amount, table_ref=null}}}}, for={column={name=month_name, table_ref=null}}, in={1={pivot_literal='jan_sales'}, 2={pivot_literal='feb_sales'}}}, alias=u, table={alias=null, table=monthly_sales_long}}, where={condition={left={column={name=empid, table_ref=t}}, right={column={name=empid, table_ref=u}}, operator==}}, assignments={1={set={column={name=target_amount, table_ref=null}}, to={column={name=jan_sales_SUM, table_ref=null}}}}, table={alias=t, table=targets}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[target_amount]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{targets={empid=[[@26,154:154='t',<381>,5:6]], month_name=[[@16,104:113='month_name',<381>,4:29]], target_amount=[[@4,21:33='target_amount',<381>,2:4]], sales_amount=[[@13,86:97='sales_amount',<381>,4:11]]}, monthly_sales_long={month_name=[[@16,104:113='month_name',<381>,4:29]], empid=[[@30,164:164='u',<381>,5:16]], sales_amount=[[@13,86:97='sales_amount',<381>,4:11]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{update1={target_amount=[[@4,21:33='target_amount',<381>,2:4]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_update1={assignments={target_amount=[{name=jan_sales_SUM, table_ref=u}, {name=month_name, table_ref=monthly_sales_long}, {name=sales_amount, table_ref=monthly_sales_long}]}, table_dictionary={targets={month_name=[[@16,104:113='month_name',<381>,4:29]], empid=[[@26,154:154='t',<381>,5:6]], sales_amount=[[@13,86:97='sales_amount',<381>,4:11]], target_amount=[[@4,21:33='target_amount',<381>,2:4]]}, monthly_sales_long={month_name=[[@16,104:113='month_name',<381>,4:29]], empid=[[@30,164:164='u',<381>,5:16]], sales_amount=[[@13,86:97='sales_amount',<381>,4:11]]}}, update_dictionary={target_amount=[[@4,21:33='target_amount',<381>,2:4]]}, derivation={source_columns={u=[{name=month_name, table_ref=monthly_sales_long}, {name=sales_amount, table_ref=monthly_sales_long}]}, derived_columns={u={jan_sales_SUM=[[@11,82:84='SUM',<141>,4:7], [@19,119:129=''jan_sales'',<389>,4:44]], feb_sales_SUM=[[@11,82:84='SUM',<141>,4:7], [@21,132:142=''feb_sales'',<389>,4:57]]}}}, filters=[{name=empid, table_ref=t}, {name=empid, table_ref=u}], table_alias={t=targets, u=monthly_sales_long}}}",
				extractor.getSymbolTable().toString());
	}

	/*
	 * Phase 16 / 17 operand qualifier coverage:
	 * - Phase 16.4 — PIVOT physical operands: redundant correct prefix → WARNING; wrong prefix → FATAL.
	 * - Phase 17.0b — UNPIVOT VALUE/FOR derived columns: any prefix → FATAL (DERIVED_OPERAND_QUALIFIED).
	 * - UNPIVOT IN-list physical operands follow the Phase 16 redundant/invalid policy.
	 * Unqualified outer references to pivot output columns (jan_sales, empid) remain
	 * Phase 18 IN-list / join-resolution territory and are tested separately below.
	 */
	@Test
	public void pivotQualifiedOperandsJoinOnQualifiedTest() {
		final String query =
			"SELECT t2.a1, t2.a2\n"
				+ "FROM monthly_sales_long msl\n"
				+ "PIVOT (SUM(msl.sales_amount) FOR msl.month_name IN ('jan_sales', 'feb_sales', 'mar_sales'))\n"
				+ "JOIN metrics_table t2 ON msl.empid = t2.metric_label;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertDiagnosticAtPosition(
				extractor.getSnippet(),
				"RELATIONAL_MODIFIER_QUALIFIED_OPERAND_REDUNDANT",
				ParseDiagnostic.Severity.WARNING,
				"Qualified PIVOT operand 'msl.sales_amount'",
				"msl.sales_amount",
				3,
				11);
		assertDiagnosticAtPosition(
				extractor.getSnippet(),
				"RELATIONAL_MODIFIER_QUALIFIED_OPERAND_REDUNDANT",
				ParseDiagnostic.Severity.WARNING,
				"Qualified PIVOT operand 'msl.month_name'",
				"msl.month_name",
				3,
				33);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=a1, table_ref=t2}}, 2={column={name=a2, table_ref=t2}}}, from={join={1={pivot={value={function={function_name=SUM, parameters={column={name=sales_amount, table_ref=msl}}}}, for={column={name=month_name, table_ref=msl}}, in={1={pivot_literal='jan_sales'}, 2={pivot_literal='feb_sales'}, 3={pivot_literal='mar_sales'}}}, table={alias=msl, table=monthly_sales_long}}, 2={join=JOIN, on={condition={left={column={name=empid, table_ref=msl}}, right={column={name=metric_label, table_ref=t2}}, operator==}}}, 3={table={alias=t2, table=metrics_table}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[a1, a2]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{metrics_table={a1=[[@1,7:8='t2',<381>,1:7]], a2=[[@5,14:15='t2',<381>,1:14]], metric_label=[[@40,177:178='t2',<381>,4:37]]}, monthly_sales_long={empid=[[@36,165:167='msl',<381>,4:25]], month_name=[[@22,85:94='month_name',<381>,3:37], [@20,81:83='msl',<381>,3:33]], sales_amount=[[@17,63:74='sales_amount',<381>,3:15], [@15,59:61='msl',<381>,3:11]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query1={a1=[[@3,10:11='a1',<381>,1:10]], a2=[[@7,17:18='a2',<381>,1:17]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query1={query_dictionary={a1=[[@3,10:11='a1',<381>,1:10]], a2=[[@7,17:18='a2',<381>,1:17]]}, table_dictionary={metrics_table={a1=[[@1,7:8='t2',<381>,1:7]], a2=[[@5,14:15='t2',<381>,1:14]], metric_label=[[@40,177:178='t2',<381>,4:37]]}, monthly_sales_long={month_name=[[@22,85:94='month_name',<381>,3:37], [@20,81:83='msl',<381>,3:33]], empid=[[@36,165:167='msl',<381>,4:25]], sales_amount=[[@17,63:74='sales_amount',<381>,3:15], [@15,59:61='msl',<381>,3:11]]}}, derivation={source_columns={tuple_0=[{name=month_name, table_ref=msl}, {name=sales_amount, table_ref=msl}]}, derived_columns={tuple_0={jan_sales_SUM=[[@13,55:57='SUM',<141>,3:7], [@25,100:110=''jan_sales'',<389>,3:52]], feb_sales_SUM=[[@13,55:57='SUM',<141>,3:7], [@27,113:123=''feb_sales'',<389>,3:65]], mar_sales_SUM=[[@13,55:57='SUM',<141>,3:7], [@29,126:136=''mar_sales'',<389>,3:78]]}}}, filters=[{name=empid, table_ref=msl}, {name=metric_label, table_ref=t2}], interface={a1=[{name=a1, table_ref=t2}], a2=[{name=a2, table_ref=t2}]}, table_alias={msl=monthly_sales_long, t2=metrics_table, tuple_0=msl}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void pivotQualifiedOperandsWhereWithPivotAliasTest() {
		final String query =
			"SELECT msl.empid\n"
				+ "FROM monthly_sales_long msl\n"
				+ "PIVOT (SUM(msl.sales_amount) FOR msl.month_name IN ('jan_sales', 'feb_sales')) u\n"
				+ "JOIN targets t ON u.empid = t.empid\n"
				+ "WHERE msl.sales_amount > 0;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertDiagnosticAtPosition(
				extractor.getSnippet(),
				"RELATIONAL_MODIFIER_QUALIFIED_OPERAND_REDUNDANT",
				ParseDiagnostic.Severity.WARNING,
				"Qualified PIVOT operand 'msl.sales_amount'",
				"msl.sales_amount",
				3,
				11);
		assertDiagnosticAtPosition(
				extractor.getSnippet(),
				"RELATIONAL_MODIFIER_QUALIFIED_OPERAND_REDUNDANT",
				ParseDiagnostic.Severity.WARNING,
				"Qualified PIVOT operand 'msl.month_name'",
				"msl.month_name",
				3,
				33);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=empid, table_ref=msl}}}, from={join={1={pivot={value={function={function_name=SUM, parameters={column={name=sales_amount, table_ref=msl}}}}, for={column={name=month_name, table_ref=msl}}, in={1={pivot_literal='jan_sales'}, 2={pivot_literal='feb_sales'}}}, alias=u, table={alias=msl, table=monthly_sales_long}}, 2={join=JOIN, on={condition={left={column={name=empid, table_ref=u}}, right={column={name=empid, table_ref=t}}, operator==}}}, 3={table={alias=t, table=targets}}}}, where={condition={left={column={name=sales_amount, table_ref=msl}}, right={literal=0}, operator=>}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[empid]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{targets={empid=[[@35,154:154='t',<381>,4:28]]}, monthly_sales_long={empid=[[@1,7:9='msl',<381>,1:7], [@31,144:144='u',<381>,4:18]], month_name=[[@18,82:91='month_name',<381>,3:37], [@16,78:80='msl',<381>,3:33]], sales_amount=[[@11,56:58='msl',<381>,3:11], [@39,168:170='msl',<381>,5:6], [@13,60:71='sales_amount',<381>,3:15]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query1={empid=[[@3,11:15='empid',<381>,1:11], [@33,146:150='empid',<381>,4:20], [@37,156:160='empid',<381>,4:30]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query1={query_dictionary={empid=[[@3,11:15='empid',<381>,1:11], [@33,146:150='empid',<381>,4:20], [@37,156:160='empid',<381>,4:30]]}, table_dictionary={targets={empid=[[@35,154:154='t',<381>,4:28]]}, monthly_sales_long={month_name=[[@18,82:91='month_name',<381>,3:37], [@16,78:80='msl',<381>,3:33]], empid=[[@1,7:9='msl',<381>,1:7], [@31,144:144='u',<381>,4:18]], sales_amount=[[@13,60:71='sales_amount',<381>,3:15], [@11,56:58='msl',<381>,3:11], [@39,168:170='msl',<381>,5:6]]}}, derivation={source_columns={u=[{name=month_name, table_ref=msl}, {name=sales_amount, table_ref=msl}]}, derived_columns={u={jan_sales_SUM=[[@9,52:54='SUM',<141>,3:7], [@21,97:107=''jan_sales'',<389>,3:52]], feb_sales_SUM=[[@9,52:54='SUM',<141>,3:7], [@23,110:120=''feb_sales'',<389>,3:65]]}}}, filters=[{name=empid, table_ref=u}, {name=empid, table_ref=t}, {name=sales_amount, table_ref=msl}], interface={empid=[{name=empid, table_ref=msl}]}, table_alias={t=targets, u=msl, msl=monthly_sales_long}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void pivotQualifiedOperandsGroupByHavingOrderByTest() {
		final String query =
			"SELECT empid, jan_sales\n"
				+ "FROM monthly_sales_long msl\n"
				+ "PIVOT (SUM(msl.sales_amount) FOR msl.month_name IN ('jan_sales', 'feb_sales'))\n"
				+ "GROUP BY msl.month_name, jan_sales\n"
				+ "HAVING msl.sales_amount > 100\n"
				+ "ORDER BY msl.sales_amount;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertDiagnosticAtPosition(
				extractor.getSnippet(),
				"RELATIONAL_MODIFIER_QUALIFIED_OPERAND_REDUNDANT",
				ParseDiagnostic.Severity.WARNING,
				"Qualified PIVOT operand 'msl.sales_amount'",
				"msl.sales_amount",
				3,
				11);
		assertDiagnosticAtPosition(
				extractor.getSnippet(),
				"RELATIONAL_MODIFIER_QUALIFIED_OPERAND_REDUNDANT",
				ParseDiagnostic.Severity.WARNING,
				"Qualified PIVOT operand 'msl.month_name'",
				"msl.month_name",
				3,
				33);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=empid, table_ref=null}}, 2={column={name=jan_sales, table_ref=null}}}, having={condition={left={column={name=sales_amount, table_ref=msl}}, right={literal=100}, operator=>}}, orderby={1={null_order=null, predicand={column={name=sales_amount, table_ref=msl}}, sort_order=ASC}}, from={pivot={value={function={function_name=SUM, parameters={column={name=sales_amount, table_ref=msl}}}}, for={column={name=month_name, table_ref=msl}}, in={1={pivot_literal='jan_sales'}, 2={pivot_literal='feb_sales'}}}, table={alias=msl, table=monthly_sales_long}}, groupby={1={column={name=month_name, table_ref=msl}}, 2={column={name=jan_sales, table_ref=null}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[jan_sales, empid]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{monthly_sales_long={jan_sales=[[@3,14:22='jan_sales',<381>,1:14], [@32,156:164='jan_sales',<381>,4:25]], month_name=[[@16,85:87='msl',<381>,3:33], [@28,140:142='msl',<381>,4:9], [@18,89:98='month_name',<381>,3:37]], empid=[[@1,7:11='empid',<381>,1:7]], sales_amount=[[@11,63:65='msl',<381>,3:11], [@34,173:175='msl',<381>,5:7], [@41,205:207='msl',<381>,6:9], [@13,67:78='sales_amount',<381>,3:15]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query1={jan_sales=[[@3,14:22='jan_sales',<381>,1:14], [@32,156:164='jan_sales',<381>,4:25]], empid=[[@1,7:11='empid',<381>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query1={query_dictionary={jan_sales=[[@3,14:22='jan_sales',<381>,1:14], [@32,156:164='jan_sales',<381>,4:25]], empid=[[@1,7:11='empid',<381>,1:7]]}, table_dictionary={monthly_sales_long={jan_sales=[[@3,14:22='jan_sales',<381>,1:14], [@32,156:164='jan_sales',<381>,4:25]], month_name=[[@18,89:98='month_name',<381>,3:37], [@16,85:87='msl',<381>,3:33], [@28,140:142='msl',<381>,4:9]], empid=[[@1,7:11='empid',<381>,1:7]], sales_amount=[[@13,67:78='sales_amount',<381>,3:15], [@11,63:65='msl',<381>,3:11], [@34,173:175='msl',<381>,5:7], [@41,205:207='msl',<381>,6:9]]}}, grouped_by=[{name=month_name, table_ref=msl}, {name=jan_sales, table_ref=null}], derivation={source_columns={tuple_0=[{name=month_name, table_ref=msl}, {name=sales_amount, table_ref=msl}]}, derived_columns={tuple_0={jan_sales_SUM=[[@9,59:61='SUM',<141>,3:7], [@21,104:114=''jan_sales'',<389>,3:52]], feb_sales_SUM=[[@9,59:61='SUM',<141>,3:7], [@23,117:127=''feb_sales'',<389>,3:65]]}}}, ordered_by=[{name=sales_amount, table_ref=msl}], filters=[{name=sales_amount, table_ref=msl}], interface={jan_sales=[{name=jan_sales, table_ref=monthly_sales_long}], empid=[{name=empid, table_ref=monthly_sales_long}]}, table_alias={msl=monthly_sales_long, tuple_0=msl}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void pivotQualifiedOperandsUpdateSetTest() {
		final String query =
			"UPDATE targets t\n"
				+ "SET target_amount = sales_amount\n"
				+ "FROM monthly_sales_long msl\n"
				+ "PIVOT (SUM(msl.sales_amount) FOR msl.month_name IN ('jan_sales', 'feb_sales')) u\n"
				+ "WHERE t.empid = u.empid;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertDiagnosticAtPosition(
				extractor.getSnippet(),
				"RELATIONAL_MODIFIER_QUALIFIED_OPERAND_REDUNDANT",
				ParseDiagnostic.Severity.WARNING,
				"Qualified PIVOT operand 'msl.sales_amount'",
				"msl.sales_amount",
				4,
				11);
		assertDiagnosticAtPosition(
				extractor.getSnippet(),
				"RELATIONAL_MODIFIER_QUALIFIED_OPERAND_REDUNDANT",
				ParseDiagnostic.Severity.WARNING,
				"Qualified PIVOT operand 'msl.month_name'",
				"msl.month_name",
				4,
				33);
		Assert.assertEquals("AST is wrong",
				"{SQL={update={from={pivot={value={function={function_name=SUM, parameters={column={name=sales_amount, table_ref=msl}}}}, for={column={name=month_name, table_ref=msl}}, in={1={pivot_literal='jan_sales'}, 2={pivot_literal='feb_sales'}}}, alias=u, table={alias=msl, table=monthly_sales_long}}, where={condition={left={column={name=empid, table_ref=t}}, right={column={name=empid, table_ref=u}}, operator==}}, assignments={1={set={column={name=target_amount, table_ref=null}}, to={column={name=sales_amount, table_ref=null}}}}, table={alias=t, table=targets}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[target_amount]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{targets={empid=[[@31,165:165='t',<381>,5:6]], target_amount=[[@4,21:33='target_amount',<381>,2:4]], sales_amount=[[@6,37:48='sales_amount',<381>,2:20]]}, monthly_sales_long={month_name=[[@21,115:124='month_name',<381>,4:37], [@19,111:113='msl',<381>,4:33]], empid=[[@35,175:175='u',<381>,5:16]], sales_amount=[[@16,93:104='sales_amount',<381>,4:15], [@14,89:91='msl',<381>,4:11]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{update1={target_amount=[[@4,21:33='target_amount',<381>,2:4]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_update1={assignments={target_amount=[{name=sales_amount, table_ref=null}]}, table_dictionary={targets={empid=[[@31,165:165='t',<381>,5:6]], sales_amount=[[@6,37:48='sales_amount',<381>,2:20]], target_amount=[[@4,21:33='target_amount',<381>,2:4]]}, monthly_sales_long={month_name=[[@21,115:124='month_name',<381>,4:37], [@19,111:113='msl',<381>,4:33]], empid=[[@35,175:175='u',<381>,5:16]], sales_amount=[[@16,93:104='sales_amount',<381>,4:15], [@14,89:91='msl',<381>,4:11]]}}, update_dictionary={target_amount=[[@4,21:33='target_amount',<381>,2:4]]}, derivation={source_columns={u=[{name=month_name, table_ref=msl}, {name=sales_amount, table_ref=msl}]}, derived_columns={u={jan_sales_SUM=[[@12,85:87='SUM',<141>,4:7], [@24,130:140=''jan_sales'',<389>,4:52]], feb_sales_SUM=[[@12,85:87='SUM',<141>,4:7], [@26,143:153=''feb_sales'',<389>,4:65]]}}}, filters=[{name=empid, table_ref=t}, {name=empid, table_ref=u}], table_alias={t=targets, u=msl, msl=monthly_sales_long}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void pivotQualifiedOperandsUpdateWhereTest() {
		final String query =
			"UPDATE targets t\n"
				+ "SET target_amount = jan_sales_SUM\n"
				+ "FROM monthly_sales_long msl\n"
				+ "PIVOT (SUM(msl.sales_amount) FOR msl.month_name IN ('jan_sales', 'feb_sales')) u\n"
				+ "WHERE sales_amount > 0 AND t.empid = u.empid;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertDiagnosticAtPosition(
				extractor.getSnippet(),
				"RELATIONAL_MODIFIER_QUALIFIED_OPERAND_REDUNDANT",
				ParseDiagnostic.Severity.WARNING,
				"Qualified PIVOT operand 'msl.sales_amount'",
				"msl.sales_amount",
				4,
				11);
		assertDiagnosticAtPosition(
				extractor.getSnippet(),
				"RELATIONAL_MODIFIER_QUALIFIED_OPERAND_REDUNDANT",
				ParseDiagnostic.Severity.WARNING,
				"Qualified PIVOT operand 'msl.month_name'",
				"msl.month_name",
				4,
				33);
		Assert.assertEquals("AST is wrong",
				"{SQL={update={from={pivot={value={function={function_name=SUM, parameters={column={name=sales_amount, table_ref=msl}}}}, for={column={name=month_name, table_ref=msl}}, in={1={pivot_literal='jan_sales'}, 2={pivot_literal='feb_sales'}}}, alias=u, table={alias=msl, table=monthly_sales_long}}, where={and={1={condition={left={column={name=sales_amount, table_ref=null}}, right={literal=0}, operator=>}}, 2={condition={left={column={name=empid, table_ref=t}}, right={column={name=empid, table_ref=u}}, operator==}}}}, assignments={1={set={column={name=target_amount, table_ref=null}}, to={column={name=jan_sales_SUM, table_ref=null}}}}, table={alias=t, table=targets}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[target_amount]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{targets={empid=[[@35,187:187='t',<381>,5:27]], target_amount=[[@4,21:33='target_amount',<381>,2:4]], sales_amount=[[@31,166:177='sales_amount',<381>,5:6]]}, monthly_sales_long={month_name=[[@21,116:125='month_name',<381>,4:37], [@19,112:114='msl',<381>,4:33]], empid=[[@39,197:197='u',<381>,5:37]], sales_amount=[[@16,94:105='sales_amount',<381>,4:15], [@14,90:92='msl',<381>,4:11]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{update1={target_amount=[[@4,21:33='target_amount',<381>,2:4]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_update1={assignments={target_amount=[{name=jan_sales_SUM, table_ref=u}, {name=month_name, table_ref=msl}, {name=sales_amount, table_ref=msl}]}, table_dictionary={targets={empid=[[@35,187:187='t',<381>,5:27]], sales_amount=[[@31,166:177='sales_amount',<381>,5:6]], target_amount=[[@4,21:33='target_amount',<381>,2:4]]}, monthly_sales_long={month_name=[[@21,116:125='month_name',<381>,4:37], [@19,112:114='msl',<381>,4:33]], empid=[[@39,197:197='u',<381>,5:37]], sales_amount=[[@16,94:105='sales_amount',<381>,4:15], [@14,90:92='msl',<381>,4:11]]}}, update_dictionary={target_amount=[[@4,21:33='target_amount',<381>,2:4]]}, derivation={source_columns={u=[{name=month_name, table_ref=msl}, {name=sales_amount, table_ref=msl}]}, derived_columns={u={jan_sales_SUM=[[@12,86:88='SUM',<141>,4:7], [@24,131:141=''jan_sales'',<389>,4:52]], feb_sales_SUM=[[@12,86:88='SUM',<141>,4:7], [@26,144:154=''feb_sales'',<389>,4:65]]}}}, filters=[{name=sales_amount, table_ref=null}, {name=empid, table_ref=t}, {name=empid, table_ref=u}], table_alias={t=targets, u=msl, msl=monthly_sales_long}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void pivotQualifiedOperandsThreeWayJoinTest() {
		final String query =
			"SELECT d.dim_label\n"
				+ "FROM monthly_sales_long msl\n"
				+ "PIVOT (SUM(msl.sales_amount) FOR msl.month_name IN ('jan_sales'))\n"
				+ "JOIN metrics_table t2 ON msl.empid = t2.metric_label\n"
				+ "JOIN dim_table d ON t2.a1 = d.dim_key;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertDiagnosticAtPosition(
				extractor.getSnippet(),
				"RELATIONAL_MODIFIER_QUALIFIED_OPERAND_REDUNDANT",
				ParseDiagnostic.Severity.WARNING,
				"Qualified PIVOT operand 'msl.sales_amount'",
				"msl.sales_amount",
				3,
				11);
		assertDiagnosticAtPosition(
				extractor.getSnippet(),
				"RELATIONAL_MODIFIER_QUALIFIED_OPERAND_REDUNDANT",
				ParseDiagnostic.Severity.WARNING,
				"Qualified PIVOT operand 'msl.month_name'",
				"msl.month_name",
				3,
				33);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=dim_label, table_ref=d}}}, from={join={1={pivot={value={function={function_name=SUM, parameters={column={name=sales_amount, table_ref=msl}}}}, for={column={name=month_name, table_ref=msl}}, in={1={pivot_literal='jan_sales'}}}, table={alias=msl, table=monthly_sales_long}}, 2={join=JOIN, on={condition={left={column={name=empid, table_ref=msl}}, right={column={name=metric_label, table_ref=t2}}, operator==}}}, 3={table={alias=t2, table=metrics_table}}, 4={join=JOIN, on={condition={left={column={name=a1, table_ref=t2}}, right={column={name=dim_key, table_ref=d}}, operator==}}}, 5={table={alias=d, table=dim_table}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[dim_label]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{metrics_table={a1=[[@39,186:187='t2',<381>,5:20]], metric_label=[[@32,150:151='t2',<381>,4:37]]}, dim_table={dim_key=[[@43,194:194='d',<381>,5:28]], dim_label=[[@1,7:7='d',<381>,1:7]]}, monthly_sales_long={empid=[[@28,138:140='msl',<381>,4:25]], month_name=[[@18,84:93='month_name',<381>,3:37], [@16,80:82='msl',<381>,3:33]], sales_amount=[[@13,62:73='sales_amount',<381>,3:15], [@11,58:60='msl',<381>,3:11]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query1={dim_label=[[@3,9:17='dim_label',<381>,1:9]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query1={query_dictionary={dim_label=[[@3,9:17='dim_label',<381>,1:9]]}, table_dictionary={metrics_table={a1=[[@39,186:187='t2',<381>,5:20]], metric_label=[[@32,150:151='t2',<381>,4:37]]}, dim_table={dim_key=[[@43,194:194='d',<381>,5:28]], dim_label=[[@1,7:7='d',<381>,1:7]]}, monthly_sales_long={month_name=[[@18,84:93='month_name',<381>,3:37], [@16,80:82='msl',<381>,3:33]], empid=[[@28,138:140='msl',<381>,4:25]], sales_amount=[[@13,62:73='sales_amount',<381>,3:15], [@11,58:60='msl',<381>,3:11]]}}, derivation={source_columns={tuple_0=[{name=month_name, table_ref=msl}, {name=sales_amount, table_ref=msl}]}, derived_columns={tuple_0={jan_sales_SUM=[[@9,54:56='SUM',<141>,3:7], [@21,99:109=''jan_sales'',<389>,3:52]]}}}, filters=[{name=empid, table_ref=msl}, {name=metric_label, table_ref=t2}, {name=a1, table_ref=t2}, {name=dim_key, table_ref=d}], interface={dim_label=[{name=dim_label, table_ref=d}]}, table_alias={d=dim_table, msl=monthly_sales_long, t2=metrics_table, tuple_0=msl}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void pivotQualifiedOperandsMultiAggregateTest() {
		final String query =
			"SELECT t2.a1\n"
				+ "FROM monthly_sales_long msl\n"
				+ "PIVOT (SUM(msl.sales_amount) AS sales_sum, SUM(msl.units) AS units_sum FOR msl.month_name IN ('jan_sales'))\n"
				+ "JOIN metrics_table t2 ON msl.empid = t2.metric_label;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		Snippet snippet = extractor.getSnippet();
		assertDiagnosticAtPosition(
				snippet,
				"RELATIONAL_MODIFIER_QUALIFIED_OPERAND_REDUNDANT",
				ParseDiagnostic.Severity.WARNING,
				"Qualified PIVOT operand 'msl.sales_amount'",
				"msl.sales_amount",
				3,
				11);
		assertDiagnosticAtPosition(
				snippet,
				"RELATIONAL_MODIFIER_QUALIFIED_OPERAND_REDUNDANT",
				ParseDiagnostic.Severity.WARNING,
				"Qualified PIVOT operand 'msl.units'",
				"msl.units",
				3,
				47);
		assertDiagnosticAtPosition(
				snippet,
				"RELATIONAL_MODIFIER_QUALIFIED_OPERAND_REDUNDANT",
				ParseDiagnostic.Severity.WARNING,
				"Qualified PIVOT operand 'msl.month_name'",
				"msl.month_name",
				3,
				75);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=a1, table_ref=t2}}}, from={join={1={pivot={value={1={function={function_name=SUM, parameters={column={name=sales_amount, table_ref=msl}}}, alias=sales_sum}, 2={function={function_name=SUM, parameters={column={name=units, table_ref=msl}}}, alias=units_sum}}, for={column={name=month_name, table_ref=msl}}, in={1={pivot_literal='jan_sales'}}}, table={alias=msl, table=monthly_sales_long}}, 2={join=JOIN, on={condition={left={column={name=empid, table_ref=msl}}, right={column={name=metric_label, table_ref=t2}}, operator==}}}, 3={table={alias=t2, table=metrics_table}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[a1]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{metrics_table={a1=[[@1,7:8='t2',<381>,1:7]], metric_label=[[@43,186:187='t2',<381>,4:37]]}, monthly_sales_long={empid=[[@39,174:176='msl',<381>,4:25]], month_name=[[@29,120:129='month_name',<381>,3:79], [@27,116:118='msl',<381>,3:75]], sales_amount=[[@13,56:67='sales_amount',<381>,3:15], [@11,52:54='msl',<381>,3:11]], units=[[@22,92:96='units',<381>,3:51], [@20,88:90='msl',<381>,3:47]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query1={a1=[[@3,10:11='a1',<381>,1:10]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query1={query_dictionary={a1=[[@3,10:11='a1',<381>,1:10]]}, table_dictionary={metrics_table={a1=[[@1,7:8='t2',<381>,1:7]], metric_label=[[@43,186:187='t2',<381>,4:37]]}, monthly_sales_long={month_name=[[@29,120:129='month_name',<381>,3:79], [@27,116:118='msl',<381>,3:75]], empid=[[@39,174:176='msl',<381>,4:25]], sales_amount=[[@13,56:67='sales_amount',<381>,3:15], [@11,52:54='msl',<381>,3:11]], units=[[@22,92:96='units',<381>,3:51], [@20,88:90='msl',<381>,3:47]]}}, derivation={source_columns={tuple_0=[{name=month_name, table_ref=msl}, {name=sales_amount, table_ref=msl}, {name=units, table_ref=msl}]}, derived_columns={tuple_0={jan_sales_sales_sum=[[@9,48:50='SUM',<141>,3:7], [@32,135:145=''jan_sales'',<389>,3:94]], jan_sales_units_sum=[[@18,84:86='SUM',<141>,3:43], [@32,135:145=''jan_sales'',<389>,3:94]]}}}, filters=[{name=empid, table_ref=msl}, {name=metric_label, table_ref=t2}], interface={a1=[{name=a1, table_ref=t2}]}, table_alias={msl=monthly_sales_long, t2=metrics_table, tuple_0=msl}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void pivotQualifiedOperandsQualifiedSelectListTest() {
		final String query =
			"SELECT msl.empid, msl.month_name\n"
				+ "FROM monthly_sales_long msl\n"
				+ "PIVOT (SUM(msl.sales_amount) FOR msl.month_name IN ('jan_sales', 'feb_sales'));";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertDiagnosticAtPosition(
				extractor.getSnippet(),
				"RELATIONAL_MODIFIER_QUALIFIED_OPERAND_REDUNDANT",
				ParseDiagnostic.Severity.WARNING,
				"Qualified PIVOT operand 'msl.sales_amount'",
				"msl.sales_amount",
				3,
				11);
		assertDiagnosticAtPosition(
				extractor.getSnippet(),
				"RELATIONAL_MODIFIER_QUALIFIED_OPERAND_REDUNDANT",
				ParseDiagnostic.Severity.WARNING,
				"Qualified PIVOT operand 'msl.month_name'",
				"msl.month_name",
				3,
				33);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=empid, table_ref=msl}}, 2={column={name=month_name, table_ref=msl}}}, from={pivot={value={function={function_name=SUM, parameters={column={name=sales_amount, table_ref=msl}}}}, for={column={name=month_name, table_ref=msl}}, in={1={pivot_literal='jan_sales'}, 2={pivot_literal='feb_sales'}}}, table={alias=msl, table=monthly_sales_long}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[empid, month_name]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{monthly_sales_long={month_name=[[@20,94:96='msl',<381>,3:33], [@22,98:107='month_name',<381>,3:37]], empid=[[@1,7:9='msl',<381>,1:7]], sales_amount=[[@17,76:87='sales_amount',<381>,3:15], [@15,72:74='msl',<381>,3:11]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query1={empid=[[@3,11:15='empid',<381>,1:11]], month_name=[[@7,22:31='month_name',<381>,1:22]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query1={query_dictionary={empid=[[@3,11:15='empid',<381>,1:11]], month_name=[[@7,22:31='month_name',<381>,1:22]]}, table_dictionary={monthly_sales_long={month_name=[[@22,98:107='month_name',<381>,3:37], [@20,94:96='msl',<381>,3:33]], empid=[[@1,7:9='msl',<381>,1:7]], sales_amount=[[@17,76:87='sales_amount',<381>,3:15], [@15,72:74='msl',<381>,3:11]]}}, derivation={source_columns={tuple_0=[{name=month_name, table_ref=msl}, {name=sales_amount, table_ref=msl}]}, derived_columns={tuple_0={jan_sales_SUM=[[@13,68:70='SUM',<141>,3:7], [@25,113:123=''jan_sales'',<389>,3:52]], feb_sales_SUM=[[@13,68:70='SUM',<141>,3:7], [@27,126:136=''feb_sales'',<389>,3:65]]}}}, interface={empid=[{name=empid, table_ref=msl}], month_name=[{name=month_name, table_ref=msl}]}, table_alias={msl=monthly_sales_long, tuple_0=msl}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void pivotWrongQualifierOperandFatalTest() {
		final String query =
			"SELECT t2.a1\n"
				+ "FROM monthly_sales_long msl\n"
				+ "PIVOT (SUM(wrong.sales_amount) FOR msl.month_name IN ('jan_sales'))\n"
				+ "JOIN metrics_table t2 ON msl.empid = t2.metric_label;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertDiagnosticAtPosition(
				extractor.getSnippet(),
				"RELATIONAL_MODIFIER_QUALIFIED_OPERAND_INVALID",
				ParseDiagnostic.Severity.FATAL,
				"Qualified PIVOT operand 'wrong.sales_amount'",
				"wrong.sales_amount",
				3,
				11);
		assertFatalDiagnosticAtPosition(
				extractor.getSnippet(),
				"QUALIFIED_COLUMN_NOT_FOUND_IN_TABLE",
				"Source Table not found for Column 'sales_amount' at (l:3 c:11). No alias or table called 'wrong'.",
				"sales_amount",
				3,
				11);
		assertDiagnosticAtPosition(
				extractor.getSnippet(),
				"RELATIONAL_MODIFIER_QUALIFIED_OPERAND_REDUNDANT",
				ParseDiagnostic.Severity.WARNING,
				"Qualified PIVOT operand 'msl.month_name'",
				"msl.month_name",
				3,
				35);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=a1, table_ref=t2}}}, from={join={1={pivot={value={function={function_name=SUM, parameters={column={name=sales_amount, table_ref=wrong}}}}, for={column={name=month_name, table_ref=msl}}, in={1={pivot_literal='jan_sales'}}}, table={alias=msl, table=monthly_sales_long}}, 2={join=JOIN, on={condition={left={column={name=empid, table_ref=msl}}, right={column={name=metric_label, table_ref=t2}}, operator==}}}, 3={table={alias=t2, table=metrics_table}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[a1]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{metrics_table={a1=[[@1,7:8='t2',<381>,1:7]], metric_label=[[@32,146:147='t2',<381>,4:37]]}, monthly_sales_long={empid=[[@28,134:136='msl',<381>,4:25]], month_name=[[@18,80:89='month_name',<381>,3:39], [@16,76:78='msl',<381>,3:35]], sales_amount=[[@13,58:69='sales_amount',<381>,3:17]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query1={a1=[[@3,10:11='a1',<381>,1:10]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query1={query_dictionary={a1=[[@3,10:11='a1',<381>,1:10]]}, table_dictionary={metrics_table={a1=[[@1,7:8='t2',<381>,1:7]], metric_label=[[@32,146:147='t2',<381>,4:37]]}, monthly_sales_long={month_name=[[@18,80:89='month_name',<381>,3:39], [@16,76:78='msl',<381>,3:35]], empid=[[@28,134:136='msl',<381>,4:25]], sales_amount=[[@13,58:69='sales_amount',<381>,3:17]]}}, derivation={source_columns={tuple_0=[{name=month_name, table_ref=msl}, {name=sales_amount, table_ref=msl}]}, derived_columns={tuple_0={jan_sales_SUM=[[@9,48:50='SUM',<141>,3:7], [@21,95:105=''jan_sales'',<389>,3:54]]}}}, filters=[{name=empid, table_ref=msl}, {name=metric_label, table_ref=t2}], interface={a1=[{name=a1, table_ref=t2}]}, table_alias={msl=monthly_sales_long, t2=metrics_table, tuple_0=msl}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void pivotUnqualifiedOuterOutputsAfterJoinAmbiguousTest() {
		final String query =
			"SELECT empid, jan_sales, feb_sales, mar_sales, t2.a1, t2.a2\n"
				+ "FROM monthly_sales_long msl\n"
				+ "PIVOT (SUM(msl.sales_amount) FOR msl.month_name IN ('jan_sales', 'feb_sales', 'mar_sales'))\n"
				+ "JOIN metrics_table t2 ON empid = t2.metric_label;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertDiagnosticAtPosition(
				extractor.getSnippet(),
				"RELATIONAL_MODIFIER_QUALIFIED_OPERAND_REDUNDANT",
				ParseDiagnostic.Severity.WARNING,
				"Qualified PIVOT operand 'msl.sales_amount'",
				"msl.sales_amount",
				3,
				11);
		assertDiagnosticAtPosition(
				extractor.getSnippet(),
				"RELATIONAL_MODIFIER_QUALIFIED_OPERAND_REDUNDANT",
				ParseDiagnostic.Severity.WARNING,
				"Qualified PIVOT operand 'msl.month_name'",
				"msl.month_name",
				3,
				33);
		assertDiagnosticAtPosition(
				extractor.getSnippet(),
				"AMBIGUOUS_COLUMN_REFERENCE",
				ParseDiagnostic.Severity.SEVERE_WARNING,
				"Ambiguous column reference 'jan_sales' at (l:1 c:14). Possible sources: [metrics_table, monthly_sales_long]",
				"jan_sales",
				1,
				14);
		assertDiagnosticAtPosition(
				extractor.getSnippet(),
				"AMBIGUOUS_COLUMN_REFERENCE",
				ParseDiagnostic.Severity.SEVERE_WARNING,
				"Ambiguous column reference 'empid' at (l:1 c:7). Possible sources: [metrics_table, monthly_sales_long]",
				"empid",
				1,
				7);
		assertDiagnosticAtPosition(
				extractor.getSnippet(),
				"AMBIGUOUS_COLUMN_REFERENCE",
				ParseDiagnostic.Severity.SEVERE_WARNING,
				"Ambiguous column reference 'feb_sales' at (l:1 c:25). Possible sources: [metrics_table, monthly_sales_long]",
				"feb_sales",
				1,
				25);
		assertDiagnosticAtPosition(
				extractor.getSnippet(),
				"AMBIGUOUS_COLUMN_REFERENCE",
				ParseDiagnostic.Severity.SEVERE_WARNING,
				"Ambiguous column reference 'mar_sales' at (l:1 c:36). Possible sources: [metrics_table, monthly_sales_long]",
				"mar_sales",
				1,
				36);
		assertDiagnosticAtPosition(
				extractor.getSnippet(),
				"UNRESOLVED_UNQUALIFIED_COLUMNS",
				ParseDiagnostic.Severity.ERROR,
				null,
				"jan_sales",
				1,
				14);
		assertDiagnosticAtPosition(
				extractor.getSnippet(),
				"UNRESOLVED_UNQUALIFIED_COLUMNS",
				ParseDiagnostic.Severity.ERROR,
				null,
				"empid",
				1,
				7);
		assertDiagnosticAtPosition(
				extractor.getSnippet(),
				"UNRESOLVED_UNQUALIFIED_COLUMNS",
				ParseDiagnostic.Severity.ERROR,
				null,
				"empid",
				4,
				25);
		assertDiagnosticAtPosition(
				extractor.getSnippet(),
				"UNRESOLVED_UNQUALIFIED_COLUMNS",
				ParseDiagnostic.Severity.ERROR,
				null,
				"mar_sales",
				1,
				36);
		assertDiagnosticAtPosition(
				extractor.getSnippet(),
				"UNRESOLVED_UNQUALIFIED_COLUMNS",
				ParseDiagnostic.Severity.ERROR,
				null,
				"feb_sales",
				1,
				25);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=empid, table_ref=null}}, 2={column={name=jan_sales, table_ref=null}}, 3={column={name=feb_sales, table_ref=null}}, 4={column={name=mar_sales, table_ref=null}}, 5={column={name=a1, table_ref=t2}}, 6={column={name=a2, table_ref=t2}}}, from={join={1={pivot={value={function={function_name=SUM, parameters={column={name=sales_amount, table_ref=msl}}}}, for={column={name=month_name, table_ref=msl}}, in={1={pivot_literal='jan_sales'}, 2={pivot_literal='feb_sales'}, 3={pivot_literal='mar_sales'}}}, table={alias=msl, table=monthly_sales_long}}, 2={join=JOIN, on={condition={left={column={name=empid, table_ref=null}}, right={column={name=metric_label, table_ref=t2}}, operator==}}}, 3={table={alias=t2, table=metrics_table}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[jan_sales, a1, empid, mar_sales, a2, feb_sales]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{metrics_table={a1=[[@9,47:48='t2',<381>,1:47]], a2=[[@13,54:55='t2',<381>,1:54]], metric_label=[[@46,213:214='t2',<381>,4:33]]}, monthly_sales_long={month_name=[[@30,125:134='month_name',<381>,3:37], [@28,121:123='msl',<381>,3:33]], sales_amount=[[@25,103:114='sales_amount',<381>,3:15], [@23,99:101='msl',<381>,3:11]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query1={jan_sales=[[@3,14:22='jan_sales',<381>,1:14]], a1=[[@11,50:51='a1',<381>,1:50]], empid=[[@1,7:11='empid',<381>,1:7], [@44,205:209='empid',<381>,4:25]], mar_sales=[[@7,36:44='mar_sales',<381>,1:36]], a2=[[@15,57:58='a2',<381>,1:57]], feb_sales=[[@5,25:33='feb_sales',<381>,1:25]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query1={query_dictionary={jan_sales=[[@3,14:22='jan_sales',<381>,1:14]], a1=[[@11,50:51='a1',<381>,1:50]], empid=[[@1,7:11='empid',<381>,1:7], [@44,205:209='empid',<381>,4:25]], mar_sales=[[@7,36:44='mar_sales',<381>,1:36]], a2=[[@15,57:58='a2',<381>,1:57]], feb_sales=[[@5,25:33='feb_sales',<381>,1:25]]}, table_dictionary={metrics_table={a1=[[@9,47:48='t2',<381>,1:47]], a2=[[@13,54:55='t2',<381>,1:54]], metric_label=[[@46,213:214='t2',<381>,4:33]]}, monthly_sales_long={month_name=[[@30,125:134='month_name',<381>,3:37], [@28,121:123='msl',<381>,3:33]], sales_amount=[[@25,103:114='sales_amount',<381>,3:15], [@23,99:101='msl',<381>,3:11]]}}, derivation={source_columns={tuple_0=[{name=month_name, table_ref=msl}, {name=sales_amount, table_ref=msl}]}, derived_columns={tuple_0={jan_sales_SUM=[[@21,95:97='SUM',<141>,3:7], [@33,140:150=''jan_sales'',<389>,3:52]], feb_sales_SUM=[[@21,95:97='SUM',<141>,3:7], [@35,153:163=''feb_sales'',<389>,3:65]], mar_sales_SUM=[[@21,95:97='SUM',<141>,3:7], [@37,166:176=''mar_sales'',<389>,3:78]]}}}, filters=[{name=empid, table_ref=null}, {name=metric_label, table_ref=t2}], interface={jan_sales=[{name=jan_sales, table_ref=null}], a1=[{name=a1, table_ref=t2}], empid=[{name=empid, table_ref=null}], mar_sales=[{name=mar_sales, table_ref=null}], a2=[{name=a2, table_ref=t2}], feb_sales=[{name=feb_sales, table_ref=null}]}, table_alias={msl=monthly_sales_long, t2=metrics_table, tuple_0=msl}}}",
				extractor.getSymbolTable().toString());
	}

	/*
	 * Unqualified operand parity: same scenarios as the qualified-operand tests above with
	 * redundant phrase prefixes removed. Resolution artifacts match; only pivot/unpivot
	 * operand AST table_ref values and operand token dictionary entries differ.
	 */
	@Test
	public void pivotQualifiedOperandsJoinOnQualifiedUnqualifiedParityTest() {
		final String query =
			"SELECT t2.a1, t2.a2\n"
				+ "FROM monthly_sales_long msl\n"
				+ "PIVOT (SUM(sales_amount) FOR month_name IN ('jan_sales', 'feb_sales', 'mar_sales'))\n"
				+ "JOIN metrics_table t2 ON msl.empid = t2.metric_label;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=a1, table_ref=t2}}, 2={column={name=a2, table_ref=t2}}}, from={join={1={pivot={value={function={function_name=SUM, parameters={column={name=sales_amount, table_ref=null}}}}, for={column={name=month_name, table_ref=null}}, in={1={pivot_literal='jan_sales'}, 2={pivot_literal='feb_sales'}, 3={pivot_literal='mar_sales'}}}, table={alias=msl, table=monthly_sales_long}}, 2={join=JOIN, on={condition={left={column={name=empid, table_ref=msl}}, right={column={name=metric_label, table_ref=t2}}, operator==}}}, 3={table={alias=t2, table=metrics_table}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[a1, a2]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{metrics_table={a1=[[@1,7:8='t2',<381>,1:7]], a2=[[@5,14:15='t2',<381>,1:14]], month_name=[[@18,77:86='month_name',<381>,3:29]], sales_amount=[[@15,59:70='sales_amount',<381>,3:11]], metric_label=[[@36,169:170='t2',<381>,4:37]]}, monthly_sales_long={empid=[[@32,157:159='msl',<381>,4:25]], month_name=[[@18,77:86='month_name',<381>,3:29]], sales_amount=[[@15,59:70='sales_amount',<381>,3:11]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query1={a1=[[@3,10:11='a1',<381>,1:10]], a2=[[@7,17:18='a2',<381>,1:17]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query1={query_dictionary={a1=[[@3,10:11='a1',<381>,1:10]], a2=[[@7,17:18='a2',<381>,1:17]]}, table_dictionary={metrics_table={a1=[[@1,7:8='t2',<381>,1:7]], month_name=[[@18,77:86='month_name',<381>,3:29]], a2=[[@5,14:15='t2',<381>,1:14]], sales_amount=[[@15,59:70='sales_amount',<381>,3:11]], metric_label=[[@36,169:170='t2',<381>,4:37]]}, monthly_sales_long={month_name=[[@18,77:86='month_name',<381>,3:29]], empid=[[@32,157:159='msl',<381>,4:25]], sales_amount=[[@15,59:70='sales_amount',<381>,3:11]]}}, derivation={source_columns={tuple_0=[{name=month_name, table_ref=msl}, {name=sales_amount, table_ref=msl}]}, derived_columns={tuple_0={jan_sales_SUM=[[@13,55:57='SUM',<141>,3:7], [@21,92:102=''jan_sales'',<389>,3:44]], feb_sales_SUM=[[@13,55:57='SUM',<141>,3:7], [@23,105:115=''feb_sales'',<389>,3:57]], mar_sales_SUM=[[@13,55:57='SUM',<141>,3:7], [@25,118:128=''mar_sales'',<389>,3:70]]}}}, filters=[{name=empid, table_ref=msl}, {name=metric_label, table_ref=t2}], interface={a1=[{name=a1, table_ref=t2}], a2=[{name=a2, table_ref=t2}]}, table_alias={msl=monthly_sales_long, t2=metrics_table, tuple_0=msl}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void pivotQualifiedOperandsWhereWithPivotAliasUnqualifiedParityTest() {
		final String query =
			"SELECT msl.empid\n"
				+ "FROM monthly_sales_long msl\n"
				+ "PIVOT (SUM(sales_amount) FOR month_name IN ('jan_sales', 'feb_sales')) u\n"
				+ "JOIN targets t ON u.empid = t.empid\n"
				+ "WHERE msl.sales_amount > 0;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=empid, table_ref=msl}}}, from={join={1={pivot={value={function={function_name=SUM, parameters={column={name=sales_amount, table_ref=null}}}}, for={column={name=month_name, table_ref=null}}, in={1={pivot_literal='jan_sales'}, 2={pivot_literal='feb_sales'}}}, alias=u, table={alias=msl, table=monthly_sales_long}}, 2={join=JOIN, on={condition={left={column={name=empid, table_ref=u}}, right={column={name=empid, table_ref=t}}, operator==}}}, 3={table={alias=t, table=targets}}}}, where={condition={left={column={name=sales_amount, table_ref=msl}}, right={literal=0}, operator=>}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[empid]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{targets={empid=[[@31,146:146='t',<381>,4:28]], month_name=[[@14,74:83='month_name',<381>,3:29]], sales_amount=[[@11,56:67='sales_amount',<381>,3:11]]}, monthly_sales_long={empid=[[@1,7:9='msl',<381>,1:7], [@27,136:136='u',<381>,4:18]], month_name=[[@14,74:83='month_name',<381>,3:29]], sales_amount=[[@35,160:162='msl',<381>,5:6], [@11,56:67='sales_amount',<381>,3:11]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query1={empid=[[@3,11:15='empid',<381>,1:11], [@29,138:142='empid',<381>,4:20], [@33,148:152='empid',<381>,4:30]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query1={query_dictionary={empid=[[@3,11:15='empid',<381>,1:11], [@29,138:142='empid',<381>,4:20], [@33,148:152='empid',<381>,4:30]]}, table_dictionary={targets={month_name=[[@14,74:83='month_name',<381>,3:29]], empid=[[@31,146:146='t',<381>,4:28]], sales_amount=[[@11,56:67='sales_amount',<381>,3:11]]}, monthly_sales_long={month_name=[[@14,74:83='month_name',<381>,3:29]], empid=[[@1,7:9='msl',<381>,1:7], [@27,136:136='u',<381>,4:18]], sales_amount=[[@11,56:67='sales_amount',<381>,3:11], [@35,160:162='msl',<381>,5:6]]}}, derivation={source_columns={u=[{name=month_name, table_ref=msl}, {name=sales_amount, table_ref=msl}]}, derived_columns={u={jan_sales_SUM=[[@9,52:54='SUM',<141>,3:7], [@17,89:99=''jan_sales'',<389>,3:44]], feb_sales_SUM=[[@9,52:54='SUM',<141>,3:7], [@19,102:112=''feb_sales'',<389>,3:57]]}}}, filters=[{name=empid, table_ref=u}, {name=empid, table_ref=t}, {name=sales_amount, table_ref=msl}], interface={empid=[{name=empid, table_ref=msl}]}, table_alias={t=targets, u=msl, msl=monthly_sales_long}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void pivotQualifiedOperandsGroupByHavingOrderByUnqualifiedParityTest() {
		final String query =
			"SELECT empid, jan_sales\n"
				+ "FROM monthly_sales_long msl\n"
				+ "PIVOT (SUM(sales_amount) FOR month_name IN ('jan_sales', 'feb_sales'))\n"
				+ "GROUP BY msl.month_name, jan_sales\n"
				+ "HAVING msl.sales_amount > 100\n"
				+ "ORDER BY msl.sales_amount;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=empid, table_ref=null}}, 2={column={name=jan_sales, table_ref=null}}}, having={condition={left={column={name=sales_amount, table_ref=msl}}, right={literal=100}, operator=>}}, orderby={1={null_order=null, predicand={column={name=sales_amount, table_ref=msl}}, sort_order=ASC}}, from={pivot={value={function={function_name=SUM, parameters={column={name=sales_amount, table_ref=null}}}}, for={column={name=month_name, table_ref=null}}, in={1={pivot_literal='jan_sales'}, 2={pivot_literal='feb_sales'}}}, table={alias=msl, table=monthly_sales_long}}, groupby={1={column={name=month_name, table_ref=msl}}, 2={column={name=jan_sales, table_ref=null}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[jan_sales, empid]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{monthly_sales_long={jan_sales=[[@3,14:22='jan_sales',<381>,1:14], [@28,148:156='jan_sales',<381>,4:25]], month_name=[[@24,132:134='msl',<381>,4:9], [@14,81:90='month_name',<381>,3:29]], empid=[[@1,7:11='empid',<381>,1:7]], sales_amount=[[@30,165:167='msl',<381>,5:7], [@37,197:199='msl',<381>,6:9], [@11,63:74='sales_amount',<381>,3:11]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query1={jan_sales=[[@3,14:22='jan_sales',<381>,1:14], [@28,148:156='jan_sales',<381>,4:25]], empid=[[@1,7:11='empid',<381>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query1={query_dictionary={jan_sales=[[@3,14:22='jan_sales',<381>,1:14], [@28,148:156='jan_sales',<381>,4:25]], empid=[[@1,7:11='empid',<381>,1:7]]}, table_dictionary={monthly_sales_long={jan_sales=[[@3,14:22='jan_sales',<381>,1:14], [@28,148:156='jan_sales',<381>,4:25]], month_name=[[@14,81:90='month_name',<381>,3:29], [@24,132:134='msl',<381>,4:9]], empid=[[@1,7:11='empid',<381>,1:7]], sales_amount=[[@11,63:74='sales_amount',<381>,3:11], [@30,165:167='msl',<381>,5:7], [@37,197:199='msl',<381>,6:9]]}}, grouped_by=[{name=month_name, table_ref=msl}, {name=jan_sales, table_ref=null}], derivation={source_columns={tuple_0=[{name=month_name, table_ref=msl}, {name=sales_amount, table_ref=msl}]}, derived_columns={tuple_0={jan_sales_SUM=[[@9,59:61='SUM',<141>,3:7], [@17,96:106=''jan_sales'',<389>,3:44]], feb_sales_SUM=[[@9,59:61='SUM',<141>,3:7], [@19,109:119=''feb_sales'',<389>,3:57]]}}}, ordered_by=[{name=sales_amount, table_ref=msl}], filters=[{name=sales_amount, table_ref=msl}], interface={jan_sales=[{name=jan_sales, table_ref=monthly_sales_long}], empid=[{name=empid, table_ref=monthly_sales_long}]}, table_alias={msl=monthly_sales_long, tuple_0=msl}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void pivotQualifiedOperandsUpdateSetUnqualifiedParityTest() {
		final String query =
			"UPDATE targets t\n"
				+ "SET target_amount = sales_amount\n"
				+ "FROM monthly_sales_long msl\n"
				+ "PIVOT (SUM(sales_amount) FOR month_name IN ('jan_sales', 'feb_sales')) u\n"
				+ "WHERE t.empid = u.empid;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={update={from={pivot={value={function={function_name=SUM, parameters={column={name=sales_amount, table_ref=null}}}}, for={column={name=month_name, table_ref=null}}, in={1={pivot_literal='jan_sales'}, 2={pivot_literal='feb_sales'}}}, alias=u, table={alias=msl, table=monthly_sales_long}}, where={condition={left={column={name=empid, table_ref=t}}, right={column={name=empid, table_ref=u}}, operator==}}, assignments={1={set={column={name=target_amount, table_ref=null}}, to={column={name=sales_amount, table_ref=null}}}}, table={alias=t, table=targets}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[target_amount]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{targets={empid=[[@27,157:157='t',<381>,5:6]], month_name=[[@17,107:116='month_name',<381>,4:29]], target_amount=[[@4,21:33='target_amount',<381>,2:4]], sales_amount=[[@14,89:100='sales_amount',<381>,4:11]]}, monthly_sales_long={month_name=[[@17,107:116='month_name',<381>,4:29]], empid=[[@31,167:167='u',<381>,5:16]], sales_amount=[[@14,89:100='sales_amount',<381>,4:11]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{update1={target_amount=[[@4,21:33='target_amount',<381>,2:4]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_update1={assignments={target_amount=[{name=sales_amount, table_ref=null}]}, table_dictionary={targets={month_name=[[@17,107:116='month_name',<381>,4:29]], empid=[[@27,157:157='t',<381>,5:6]], sales_amount=[[@14,89:100='sales_amount',<381>,4:11]], target_amount=[[@4,21:33='target_amount',<381>,2:4]]}, monthly_sales_long={month_name=[[@17,107:116='month_name',<381>,4:29]], empid=[[@31,167:167='u',<381>,5:16]], sales_amount=[[@14,89:100='sales_amount',<381>,4:11]]}}, update_dictionary={target_amount=[[@4,21:33='target_amount',<381>,2:4]]}, derivation={source_columns={u=[{name=month_name, table_ref=msl}, {name=sales_amount, table_ref=msl}]}, derived_columns={u={jan_sales_SUM=[[@12,85:87='SUM',<141>,4:7], [@20,122:132=''jan_sales'',<389>,4:44]], feb_sales_SUM=[[@12,85:87='SUM',<141>,4:7], [@22,135:145=''feb_sales'',<389>,4:57]]}}}, filters=[{name=empid, table_ref=t}, {name=empid, table_ref=u}], table_alias={t=targets, u=msl, msl=monthly_sales_long}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void pivotQualifiedOperandsUpdateWhereUnqualifiedParityTest() {
		final String query =
			"UPDATE targets t\n"
				+ "SET target_amount = jan_sales_SUM\n"
				+ "FROM monthly_sales_long msl\n"
				+ "PIVOT (SUM(sales_amount) FOR month_name IN ('jan_sales', 'feb_sales')) u\n"
				+ "WHERE sales_amount > 0 AND t.empid = u.empid;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={update={from={pivot={value={function={function_name=SUM, parameters={column={name=sales_amount, table_ref=null}}}}, for={column={name=month_name, table_ref=null}}, in={1={pivot_literal='jan_sales'}, 2={pivot_literal='feb_sales'}}}, alias=u, table={alias=msl, table=monthly_sales_long}}, where={and={1={condition={left={column={name=sales_amount, table_ref=null}}, right={literal=0}, operator=>}}, 2={condition={left={column={name=empid, table_ref=t}}, right={column={name=empid, table_ref=u}}, operator==}}}}, assignments={1={set={column={name=target_amount, table_ref=null}}, to={column={name=jan_sales_SUM, table_ref=null}}}}, table={alias=t, table=targets}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[target_amount]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{targets={empid=[[@31,179:179='t',<381>,5:27]], month_name=[[@17,108:117='month_name',<381>,4:29]], target_amount=[[@4,21:33='target_amount',<381>,2:4]], sales_amount=[[@14,90:101='sales_amount',<381>,4:11], [@27,158:169='sales_amount',<381>,5:6]]}, monthly_sales_long={month_name=[[@17,108:117='month_name',<381>,4:29]], empid=[[@35,189:189='u',<381>,5:37]], sales_amount=[[@14,90:101='sales_amount',<381>,4:11]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{update1={target_amount=[[@4,21:33='target_amount',<381>,2:4]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_update1={assignments={target_amount=[{name=jan_sales_SUM, table_ref=u}, {name=month_name, table_ref=msl}, {name=sales_amount, table_ref=msl}]}, table_dictionary={targets={month_name=[[@17,108:117='month_name',<381>,4:29]], empid=[[@31,179:179='t',<381>,5:27]], sales_amount=[[@14,90:101='sales_amount',<381>,4:11], [@27,158:169='sales_amount',<381>,5:6]], target_amount=[[@4,21:33='target_amount',<381>,2:4]]}, monthly_sales_long={month_name=[[@17,108:117='month_name',<381>,4:29]], empid=[[@35,189:189='u',<381>,5:37]], sales_amount=[[@14,90:101='sales_amount',<381>,4:11]]}}, update_dictionary={target_amount=[[@4,21:33='target_amount',<381>,2:4]]}, derivation={source_columns={u=[{name=month_name, table_ref=msl}, {name=sales_amount, table_ref=msl}]}, derived_columns={u={jan_sales_SUM=[[@12,86:88='SUM',<141>,4:7], [@20,123:133=''jan_sales'',<389>,4:44]], feb_sales_SUM=[[@12,86:88='SUM',<141>,4:7], [@22,136:146=''feb_sales'',<389>,4:57]]}}}, filters=[{name=sales_amount, table_ref=null}, {name=empid, table_ref=t}, {name=empid, table_ref=u}], table_alias={t=targets, u=msl, msl=monthly_sales_long}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void pivotQualifiedOperandsThreeWayJoinUnqualifiedParityTest() {
		final String query =
			"SELECT d.dim_label\n"
				+ "FROM monthly_sales_long msl\n"
				+ "PIVOT (SUM(sales_amount) FOR month_name IN ('jan_sales'))\n"
				+ "JOIN metrics_table t2 ON msl.empid = t2.metric_label\n"
				+ "JOIN dim_table d ON t2.a1 = d.dim_key;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=dim_label, table_ref=d}}}, from={join={1={pivot={value={function={function_name=SUM, parameters={column={name=sales_amount, table_ref=null}}}}, for={column={name=month_name, table_ref=null}}, in={1={pivot_literal='jan_sales'}}}, table={alias=msl, table=monthly_sales_long}}, 2={join=JOIN, on={condition={left={column={name=empid, table_ref=msl}}, right={column={name=metric_label, table_ref=t2}}, operator==}}}, 3={table={alias=t2, table=metrics_table}}, 4={join=JOIN, on={condition={left={column={name=a1, table_ref=t2}}, right={column={name=dim_key, table_ref=d}}, operator==}}}, 5={table={alias=d, table=dim_table}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[dim_label]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{metrics_table={a1=[[@35,178:179='t2',<381>,5:20]], month_name=[[@14,76:85='month_name',<381>,3:29]], sales_amount=[[@11,58:69='sales_amount',<381>,3:11]], metric_label=[[@28,142:143='t2',<381>,4:37]]}, dim_table={dim_key=[[@39,186:186='d',<381>,5:28]], dim_label=[[@1,7:7='d',<381>,1:7]]}, monthly_sales_long={empid=[[@24,130:132='msl',<381>,4:25]], month_name=[[@14,76:85='month_name',<381>,3:29]], sales_amount=[[@11,58:69='sales_amount',<381>,3:11]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query1={dim_label=[[@3,9:17='dim_label',<381>,1:9]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query1={query_dictionary={dim_label=[[@3,9:17='dim_label',<381>,1:9]]}, table_dictionary={metrics_table={a1=[[@35,178:179='t2',<381>,5:20]], month_name=[[@14,76:85='month_name',<381>,3:29]], sales_amount=[[@11,58:69='sales_amount',<381>,3:11]], metric_label=[[@28,142:143='t2',<381>,4:37]]}, dim_table={dim_key=[[@39,186:186='d',<381>,5:28]], dim_label=[[@1,7:7='d',<381>,1:7]]}, monthly_sales_long={month_name=[[@14,76:85='month_name',<381>,3:29]], empid=[[@24,130:132='msl',<381>,4:25]], sales_amount=[[@11,58:69='sales_amount',<381>,3:11]]}}, derivation={source_columns={tuple_0=[{name=month_name, table_ref=msl}, {name=sales_amount, table_ref=msl}]}, derived_columns={tuple_0={jan_sales_SUM=[[@9,54:56='SUM',<141>,3:7], [@17,91:101=''jan_sales'',<389>,3:44]]}}}, filters=[{name=empid, table_ref=msl}, {name=metric_label, table_ref=t2}, {name=a1, table_ref=t2}, {name=dim_key, table_ref=d}], interface={dim_label=[{name=dim_label, table_ref=d}]}, table_alias={d=dim_table, msl=monthly_sales_long, t2=metrics_table, tuple_0=msl}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void pivotQualifiedOperandsMultiAggregateUnqualifiedParityTest() {
		final String query =
			"SELECT t2.a1\n"
				+ "FROM monthly_sales_long msl\n"
				+ "PIVOT (SUM(sales_amount) AS sales_sum, SUM(units) AS units_sum FOR month_name IN ('jan_sales'))\n"
				+ "JOIN metrics_table t2 ON msl.empid = t2.metric_label;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=a1, table_ref=t2}}}, from={join={1={pivot={value={1={function={function_name=SUM, parameters={column={name=sales_amount, table_ref=null}}}, alias=sales_sum}, 2={function={function_name=SUM, parameters={column={name=units, table_ref=null}}}, alias=units_sum}}, for={column={name=month_name, table_ref=null}}, in={1={pivot_literal='jan_sales'}}}, table={alias=msl, table=monthly_sales_long}}, 2={join=JOIN, on={condition={left={column={name=empid, table_ref=msl}}, right={column={name=metric_label, table_ref=t2}}, operator==}}}, 3={table={alias=t2, table=metrics_table}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[a1]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{metrics_table={a1=[[@1,7:8='t2',<381>,1:7]], month_name=[[@23,108:117='month_name',<381>,3:67]], sales_amount=[[@11,52:63='sales_amount',<381>,3:11]], metric_label=[[@37,174:175='t2',<381>,4:37]], units=[[@18,84:88='units',<381>,3:43]]}, monthly_sales_long={empid=[[@33,162:164='msl',<381>,4:25]], month_name=[[@23,108:117='month_name',<381>,3:67]], sales_amount=[[@11,52:63='sales_amount',<381>,3:11]], units=[[@18,84:88='units',<381>,3:43]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query1={a1=[[@3,10:11='a1',<381>,1:10]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query1={query_dictionary={a1=[[@3,10:11='a1',<381>,1:10]]}, table_dictionary={metrics_table={a1=[[@1,7:8='t2',<381>,1:7]], month_name=[[@23,108:117='month_name',<381>,3:67]], sales_amount=[[@11,52:63='sales_amount',<381>,3:11]], units=[[@18,84:88='units',<381>,3:43]], metric_label=[[@37,174:175='t2',<381>,4:37]]}, monthly_sales_long={month_name=[[@23,108:117='month_name',<381>,3:67]], empid=[[@33,162:164='msl',<381>,4:25]], sales_amount=[[@11,52:63='sales_amount',<381>,3:11]], units=[[@18,84:88='units',<381>,3:43]]}}, derivation={source_columns={tuple_0=[{name=month_name, table_ref=msl}, {name=sales_amount, table_ref=msl}, {name=units, table_ref=msl}]}, derived_columns={tuple_0={jan_sales_sales_sum=[[@9,48:50='SUM',<141>,3:7], [@26,123:133=''jan_sales'',<389>,3:82]], jan_sales_units_sum=[[@16,80:82='SUM',<141>,3:39], [@26,123:133=''jan_sales'',<389>,3:82]]}}}, filters=[{name=empid, table_ref=msl}, {name=metric_label, table_ref=t2}], interface={a1=[{name=a1, table_ref=t2}]}, table_alias={msl=monthly_sales_long, t2=metrics_table, tuple_0=msl}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void pivotQualifiedOperandsQualifiedSelectListUnqualifiedParityTest() {
		final String query =
			"SELECT msl.empid, msl.month_name\n"
				+ "FROM monthly_sales_long msl\n"
				+ "PIVOT (SUM(sales_amount) FOR month_name IN ('jan_sales', 'feb_sales'));";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=empid, table_ref=msl}}, 2={column={name=month_name, table_ref=msl}}}, from={pivot={value={function={function_name=SUM, parameters={column={name=sales_amount, table_ref=null}}}}, for={column={name=month_name, table_ref=null}}, in={1={pivot_literal='jan_sales'}, 2={pivot_literal='feb_sales'}}}, table={alias=msl, table=monthly_sales_long}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[empid, month_name]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{monthly_sales_long={month_name=[[@5,18:20='msl',<381>,1:18], [@18,90:99='month_name',<381>,3:29]], empid=[[@1,7:9='msl',<381>,1:7]], sales_amount=[[@15,72:83='sales_amount',<381>,3:11]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query1={empid=[[@3,11:15='empid',<381>,1:11]], month_name=[[@7,22:31='month_name',<381>,1:22]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query1={query_dictionary={empid=[[@3,11:15='empid',<381>,1:11]], month_name=[[@7,22:31='month_name',<381>,1:22]]}, table_dictionary={monthly_sales_long={month_name=[[@18,90:99='month_name',<381>,3:29], [@5,18:20='msl',<381>,1:18]], empid=[[@1,7:9='msl',<381>,1:7]], sales_amount=[[@15,72:83='sales_amount',<381>,3:11]]}}, derivation={source_columns={tuple_0=[{name=month_name, table_ref=msl}, {name=sales_amount, table_ref=msl}]}, derived_columns={tuple_0={jan_sales_SUM=[[@13,68:70='SUM',<141>,3:7], [@21,105:115=''jan_sales'',<389>,3:44]], feb_sales_SUM=[[@13,68:70='SUM',<141>,3:7], [@23,118:128=''feb_sales'',<389>,3:57]]}}}, interface={empid=[{name=empid, table_ref=msl}], month_name=[{name=month_name, table_ref=msl}]}, table_alias={msl=monthly_sales_long, tuple_0=msl}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void pivotWrongQualifierOperandUnqualifiedParityTest() {
		final String query =
			"SELECT t2.a1\n"
				+ "FROM monthly_sales_long msl\n"
				+ "PIVOT (SUM(sales_amount) FOR month_name IN ('jan_sales'))\n"
				+ "JOIN metrics_table t2 ON msl.empid = t2.metric_label;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=a1, table_ref=t2}}}, from={join={1={pivot={value={function={function_name=SUM, parameters={column={name=sales_amount, table_ref=null}}}}, for={column={name=month_name, table_ref=null}}, in={1={pivot_literal='jan_sales'}}}, table={alias=msl, table=monthly_sales_long}}, 2={join=JOIN, on={condition={left={column={name=empid, table_ref=msl}}, right={column={name=metric_label, table_ref=t2}}, operator==}}}, 3={table={alias=t2, table=metrics_table}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[a1]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{metrics_table={a1=[[@1,7:8='t2',<381>,1:7]], month_name=[[@14,70:79='month_name',<381>,3:29]], sales_amount=[[@11,52:63='sales_amount',<381>,3:11]], metric_label=[[@28,136:137='t2',<381>,4:37]]}, monthly_sales_long={empid=[[@24,124:126='msl',<381>,4:25]], month_name=[[@14,70:79='month_name',<381>,3:29]], sales_amount=[[@11,52:63='sales_amount',<381>,3:11]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query1={a1=[[@3,10:11='a1',<381>,1:10]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query1={query_dictionary={a1=[[@3,10:11='a1',<381>,1:10]]}, table_dictionary={metrics_table={a1=[[@1,7:8='t2',<381>,1:7]], month_name=[[@14,70:79='month_name',<381>,3:29]], sales_amount=[[@11,52:63='sales_amount',<381>,3:11]], metric_label=[[@28,136:137='t2',<381>,4:37]]}, monthly_sales_long={month_name=[[@14,70:79='month_name',<381>,3:29]], empid=[[@24,124:126='msl',<381>,4:25]], sales_amount=[[@11,52:63='sales_amount',<381>,3:11]]}}, derivation={source_columns={tuple_0=[{name=month_name, table_ref=msl}, {name=sales_amount, table_ref=msl}]}, derived_columns={tuple_0={jan_sales_SUM=[[@9,48:50='SUM',<141>,3:7], [@17,85:95=''jan_sales'',<389>,3:44]]}}}, filters=[{name=empid, table_ref=msl}, {name=metric_label, table_ref=t2}], interface={a1=[{name=a1, table_ref=t2}]}, table_alias={msl=monthly_sales_long, t2=metrics_table, tuple_0=msl}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void pivotUnqualifiedOuterOutputsAfterJoinAmbiguousUnqualifiedOperandsParityTest() {
		final String query =
			"SELECT empid, jan_sales, feb_sales, mar_sales, t2.a1, t2.a2\n"
				+ "FROM monthly_sales_long msl\n"
				+ "PIVOT (SUM(sales_amount) FOR month_name IN ('jan_sales', 'feb_sales', 'mar_sales'))\n"
				+ "JOIN metrics_table t2 ON empid = t2.metric_label;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		Assert.assertEquals("Redundant qualifier warnings should be absent",
				0,
				extractor.getSnippet().getDiagnosticCountBySeverity(ParseDiagnostic.Severity.WARNING));
		assertDiagnosticAtPosition(
				extractor.getSnippet(),
				"AMBIGUOUS_COLUMN_REFERENCE",
				ParseDiagnostic.Severity.SEVERE_WARNING,
				"Ambiguous column reference 'jan_sales' at (l:1 c:14). Possible sources: [metrics_table, monthly_sales_long]",
				"jan_sales",
				1,
				14);
		assertDiagnosticAtPosition(
				extractor.getSnippet(),
				"AMBIGUOUS_COLUMN_REFERENCE",
				ParseDiagnostic.Severity.SEVERE_WARNING,
				"Ambiguous column reference 'empid' at (l:1 c:7). Possible sources: [metrics_table, monthly_sales_long]",
				"empid",
				1,
				7);
		assertDiagnosticAtPosition(
				extractor.getSnippet(),
				"AMBIGUOUS_COLUMN_REFERENCE",
				ParseDiagnostic.Severity.SEVERE_WARNING,
				"Ambiguous column reference 'feb_sales' at (l:1 c:25). Possible sources: [metrics_table, monthly_sales_long]",
				"feb_sales",
				1,
				25);
		assertDiagnosticAtPosition(
				extractor.getSnippet(),
				"AMBIGUOUS_COLUMN_REFERENCE",
				ParseDiagnostic.Severity.SEVERE_WARNING,
				"Ambiguous column reference 'mar_sales' at (l:1 c:36). Possible sources: [metrics_table, monthly_sales_long]",
				"mar_sales",
				1,
				36);
		assertDiagnosticAtPosition(
				extractor.getSnippet(),
				"UNRESOLVED_UNQUALIFIED_COLUMNS",
				ParseDiagnostic.Severity.ERROR,
				null,
				"jan_sales",
				1,
				14);
		assertDiagnosticAtPosition(
				extractor.getSnippet(),
				"UNRESOLVED_UNQUALIFIED_COLUMNS",
				ParseDiagnostic.Severity.ERROR,
				null,
				"empid",
				1,
				7);
		assertDiagnosticAtPosition(
				extractor.getSnippet(),
				"UNRESOLVED_UNQUALIFIED_COLUMNS",
				ParseDiagnostic.Severity.ERROR,
				null,
				"empid",
				4,
				25);
		assertDiagnosticAtPosition(
				extractor.getSnippet(),
				"UNRESOLVED_UNQUALIFIED_COLUMNS",
				ParseDiagnostic.Severity.ERROR,
				null,
				"mar_sales",
				1,
				36);
		assertDiagnosticAtPosition(
				extractor.getSnippet(),
				"UNRESOLVED_UNQUALIFIED_COLUMNS",
				ParseDiagnostic.Severity.ERROR,
				null,
				"feb_sales",
				1,
				25);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=empid, table_ref=null}}, 2={column={name=jan_sales, table_ref=null}}, 3={column={name=feb_sales, table_ref=null}}, 4={column={name=mar_sales, table_ref=null}}, 5={column={name=a1, table_ref=t2}}, 6={column={name=a2, table_ref=t2}}}, from={join={1={pivot={value={function={function_name=SUM, parameters={column={name=sales_amount, table_ref=null}}}}, for={column={name=month_name, table_ref=null}}, in={1={pivot_literal='jan_sales'}, 2={pivot_literal='feb_sales'}, 3={pivot_literal='mar_sales'}}}, table={alias=msl, table=monthly_sales_long}}, 2={join=JOIN, on={condition={left={column={name=empid, table_ref=null}}, right={column={name=metric_label, table_ref=t2}}, operator==}}}, 3={table={alias=t2, table=metrics_table}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[jan_sales, a1, empid, mar_sales, a2, feb_sales]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{metrics_table={a1=[[@9,47:48='t2',<381>,1:47]], a2=[[@13,54:55='t2',<381>,1:54]], month_name=[[@26,117:126='month_name',<381>,3:29]], sales_amount=[[@23,99:110='sales_amount',<381>,3:11]], metric_label=[[@42,205:206='t2',<381>,4:33]]}, monthly_sales_long={month_name=[[@26,117:126='month_name',<381>,3:29]], sales_amount=[[@23,99:110='sales_amount',<381>,3:11]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query1={jan_sales=[[@3,14:22='jan_sales',<381>,1:14]], a1=[[@11,50:51='a1',<381>,1:50]], empid=[[@1,7:11='empid',<381>,1:7], [@40,197:201='empid',<381>,4:25]], mar_sales=[[@7,36:44='mar_sales',<381>,1:36]], a2=[[@15,57:58='a2',<381>,1:57]], feb_sales=[[@5,25:33='feb_sales',<381>,1:25]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query1={query_dictionary={jan_sales=[[@3,14:22='jan_sales',<381>,1:14]], a1=[[@11,50:51='a1',<381>,1:50]], empid=[[@1,7:11='empid',<381>,1:7], [@40,197:201='empid',<381>,4:25]], mar_sales=[[@7,36:44='mar_sales',<381>,1:36]], a2=[[@15,57:58='a2',<381>,1:57]], feb_sales=[[@5,25:33='feb_sales',<381>,1:25]]}, table_dictionary={metrics_table={a1=[[@9,47:48='t2',<381>,1:47]], month_name=[[@26,117:126='month_name',<381>,3:29]], a2=[[@13,54:55='t2',<381>,1:54]], sales_amount=[[@23,99:110='sales_amount',<381>,3:11]], metric_label=[[@42,205:206='t2',<381>,4:33]]}, monthly_sales_long={month_name=[[@26,117:126='month_name',<381>,3:29]], sales_amount=[[@23,99:110='sales_amount',<381>,3:11]]}}, derivation={source_columns={tuple_0=[{name=month_name, table_ref=msl}, {name=sales_amount, table_ref=msl}]}, derived_columns={tuple_0={jan_sales_SUM=[[@21,95:97='SUM',<141>,3:7], [@29,132:142=''jan_sales'',<389>,3:44]], feb_sales_SUM=[[@21,95:97='SUM',<141>,3:7], [@31,145:155=''feb_sales'',<389>,3:57]], mar_sales_SUM=[[@21,95:97='SUM',<141>,3:7], [@33,158:168=''mar_sales'',<389>,3:70]]}}}, filters=[{name=empid, table_ref=null}, {name=metric_label, table_ref=t2}], interface={jan_sales=[{name=jan_sales, table_ref=null}], a1=[{name=a1, table_ref=t2}], empid=[{name=empid, table_ref=null}], mar_sales=[{name=mar_sales, table_ref=null}], a2=[{name=a2, table_ref=t2}], feb_sales=[{name=feb_sales, table_ref=null}]}, table_alias={msl=monthly_sales_long, t2=metrics_table, tuple_0=msl}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void unpivotQualifiedDerivedOperandsFatalTest() {
		final String query =
			"SELECT empid, month_name, sales_amount\n"
				+ "FROM monthly_sales msl\n"
				+ "UNPIVOT (msl.sales_amount FOR msl.month_name IN (jan_sales, feb_sales));";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertDiagnosticAtPosition(
				extractor.getSnippet(),
				"RELATIONAL_MODIFIER_DERIVED_OPERAND_QUALIFIED",
				ParseDiagnostic.Severity.FATAL,
				"derived output columns in VALUE and FOR positions must be unqualified",
				"msl.sales_amount",
				3,
				9);
		assertDiagnosticAtPosition(
				extractor.getSnippet(),
				"RELATIONAL_MODIFIER_DERIVED_OPERAND_QUALIFIED",
				ParseDiagnostic.Severity.FATAL,
				"Qualified UNPIVOT operand 'msl.month_name'",
				"msl.month_name",
				3,
				30);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=empid, table_ref=null}}, 2={column={name=month_name, table_ref=null}}, 3={column={name=sales_amount, table_ref=null}}}, from={unpivot={value={column={name=sales_amount, table_ref=msl}}, for={column={name=month_name, table_ref=msl}}, in={1={name=jan_sales, table_ref=null}, 2={name=feb_sales, table_ref=null}}}, table={alias=msl, table=monthly_sales}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[empid, month_name, sales_amount]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{monthly_sales={jan_sales=[[@20,111:119='jan_sales',<381>,3:49]], empid=[[@1,7:11='empid',<381>,1:7]], feb_sales=[[@22,122:130='feb_sales',<381>,3:60]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={empid=[[@1,7:11='empid',<381>,1:7]], month_name=[[@3,14:23='month_name',<381>,1:14], [@17,96:105='month_name',<381>,3:34]], sales_amount=[[@5,26:37='sales_amount',<381>,1:26], [@13,75:86='sales_amount',<381>,3:13]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={empid=[[@1,7:11='empid',<381>,1:7]], month_name=[[@3,14:23='month_name',<381>,1:14], [@17,96:105='month_name',<381>,3:34]], sales_amount=[[@5,26:37='sales_amount',<381>,1:26], [@13,75:86='sales_amount',<381>,3:13]]}, table_dictionary={monthly_sales={jan_sales=[[@20,111:119='jan_sales',<381>,3:49]], empid=[[@1,7:11='empid',<381>,1:7]], feb_sales=[[@22,122:130='feb_sales',<381>,3:60]]}}, derivation={source_columns={tuple_0=[{name=jan_sales, table_ref=msl}, {name=feb_sales, table_ref=msl}]}, derived_columns={tuple_0={sales_amount=[[@13,75:86='sales_amount',<381>,3:13]], month_name=[[@17,96:105='month_name',<381>,3:34]]}}}, interface={empid=[{name=empid, table_ref=monthly_sales}], month_name=[{name=month_name, table_ref=tuple_0}, {name=jan_sales, table_ref=msl}, {name=feb_sales, table_ref=msl}], sales_amount=[{name=jan_sales, table_ref=msl}, {name=feb_sales, table_ref=msl}]}, table_alias={msl=monthly_sales, tuple_0=msl}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void unpivotWrongQualifierOperandFatalTest() {
		final String query =
			"SELECT empid\n"
				+ "FROM monthly_sales msl\n"
				+ "UNPIVOT (wrong.sales_amount FOR msl.month_name IN (jan_sales));";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertDiagnosticAtPosition(
				extractor.getSnippet(),
				"RELATIONAL_MODIFIER_DERIVED_OPERAND_QUALIFIED",
				ParseDiagnostic.Severity.FATAL,
				"Qualified UNPIVOT operand 'wrong.sales_amount'",
				"wrong.sales_amount",
				3,
				9);
		assertDiagnosticAtPosition(
				extractor.getSnippet(),
				"RELATIONAL_MODIFIER_DERIVED_OPERAND_QUALIFIED",
				ParseDiagnostic.Severity.FATAL,
				"Qualified UNPIVOT operand 'msl.month_name'",
				"msl.month_name",
				3,
				32);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=empid, table_ref=null}}}, from={unpivot={value={column={name=sales_amount, table_ref=wrong}}, for={column={name=month_name, table_ref=msl}}, in={1={name=jan_sales, table_ref=null}}}, table={alias=msl, table=monthly_sales}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[empid]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{monthly_sales={jan_sales=[[@16,87:95='jan_sales',<381>,3:51]], empid=[[@1,7:11='empid',<381>,1:7]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={empid=[[@1,7:11='empid',<381>,1:7]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={empid=[[@1,7:11='empid',<381>,1:7]]}, table_dictionary={monthly_sales={jan_sales=[[@16,87:95='jan_sales',<381>,3:51]], empid=[[@1,7:11='empid',<381>,1:7]]}}, derivation={source_columns={tuple_0=[{name=jan_sales, table_ref=msl}]}, derived_columns={tuple_0={sales_amount=[[@9,51:62='sales_amount',<381>,3:15]], month_name=[[@13,72:81='month_name',<381>,3:36]]}}}, interface={empid=[{name=empid, table_ref=monthly_sales}]}, table_alias={msl=monthly_sales, tuple_0=msl}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void unpivotQualifiedValueDerivedOperandFatalTest() {
		final String query =
			"SELECT empid, month_name, sales_amount\n"
				+ "FROM monthly_sales msl\n"
				+ "UNPIVOT (msl.sales_amount FOR month_name IN (jan_sales, feb_sales));";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertDiagnosticAtPosition(
				extractor.getSnippet(),
				"RELATIONAL_MODIFIER_DERIVED_OPERAND_QUALIFIED",
				ParseDiagnostic.Severity.FATAL,
				"Qualified UNPIVOT operand 'msl.sales_amount'",
				"msl.sales_amount",
				3,
				9);
		Assert.assertEquals("Redundant qualifier warnings should be absent",
				0,
				extractor.getSnippet().getDiagnosticCountBySeverity(ParseDiagnostic.Severity.WARNING));
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=empid, table_ref=null}}, 2={column={name=month_name, table_ref=null}}, 3={column={name=sales_amount, table_ref=null}}}, from={unpivot={value={column={name=sales_amount, table_ref=msl}}, for={column={name=month_name, table_ref=null}}, in={1={name=jan_sales, table_ref=null}, 2={name=feb_sales, table_ref=null}}}, table={alias=msl, table=monthly_sales}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[empid, month_name, sales_amount]",
				extractor.getInterface().toString());
	}

	@Test
	public void unpivotQualifiedInListOperandsRedundantWarningTest() {
		final String query =
			"SELECT empid, month_name, sales_amount\n"
				+ "FROM monthly_sales msl\n"
				+ "UNPIVOT (sales_amount FOR month_name IN (msl.jan_sales, msl.feb_sales));";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertDiagnosticAtPosition(
				extractor.getSnippet(),
				"RELATIONAL_MODIFIER_QUALIFIED_OPERAND_REDUNDANT",
				ParseDiagnostic.Severity.WARNING,
				"Qualified UNPIVOT operand 'msl.jan_sales'",
				"msl.jan_sales",
				3,
				41);
		assertDiagnosticAtPosition(
				extractor.getSnippet(),
				"RELATIONAL_MODIFIER_QUALIFIED_OPERAND_REDUNDANT",
				ParseDiagnostic.Severity.WARNING,
				"Qualified UNPIVOT operand 'msl.feb_sales'",
				"msl.feb_sales",
				3,
				56);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=empid, table_ref=null}}, 2={column={name=month_name, table_ref=null}}, 3={column={name=sales_amount, table_ref=null}}}, from={unpivot={value={column={name=sales_amount, table_ref=null}}, for={column={name=month_name, table_ref=null}}, in={1={name=jan_sales, table_ref=msl}, 2={name=feb_sales, table_ref=msl}}}, table={alias=msl, table=monthly_sales}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[empid, month_name, sales_amount]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{monthly_sales={jan_sales=[[@18,107:115='jan_sales',<381>,3:45], [@16,103:105='msl',<381>,3:41]], empid=[[@1,7:11='empid',<381>,1:7]], feb_sales=[[@22,122:130='feb_sales',<381>,3:60], [@20,118:120='msl',<381>,3:56]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={empid=[[@1,7:11='empid',<381>,1:7]], month_name=[[@3,14:23='month_name',<381>,1:14], [@13,88:97='month_name',<381>,3:26]], sales_amount=[[@5,26:37='sales_amount',<381>,1:26], [@11,71:82='sales_amount',<381>,3:9]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={empid=[[@1,7:11='empid',<381>,1:7]], month_name=[[@3,14:23='month_name',<381>,1:14], [@13,88:97='month_name',<381>,3:26]], sales_amount=[[@5,26:37='sales_amount',<381>,1:26], [@11,71:82='sales_amount',<381>,3:9]]}, table_dictionary={monthly_sales={jan_sales=[[@18,107:115='jan_sales',<381>,3:45], [@16,103:105='msl',<381>,3:41]], empid=[[@1,7:11='empid',<381>,1:7]], feb_sales=[[@22,122:130='feb_sales',<381>,3:60], [@20,118:120='msl',<381>,3:56]]}}, derivation={source_columns={tuple_0=[{name=jan_sales, table_ref=msl}, {name=feb_sales, table_ref=msl}]}, derived_columns={tuple_0={sales_amount=[[@11,71:82='sales_amount',<381>,3:9]], month_name=[[@13,88:97='month_name',<381>,3:26]]}}}, interface={empid=[{name=empid, table_ref=monthly_sales}], month_name=[{name=month_name, table_ref=tuple_0}, {name=jan_sales, table_ref=msl}, {name=feb_sales, table_ref=msl}], sales_amount=[{name=jan_sales, table_ref=msl}, {name=feb_sales, table_ref=msl}]}, table_alias={msl=monthly_sales, tuple_0=msl}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void unpivotQualifiedOperandsUnqualifiedParityTest() {
		final String query =
			"SELECT empid, month_name, sales_amount\n"
				+ "FROM monthly_sales msl\n"
				+ "UNPIVOT (sales_amount FOR month_name IN (jan_sales, feb_sales));";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=empid, table_ref=null}}, 2={column={name=month_name, table_ref=null}}, 3={column={name=sales_amount, table_ref=null}}}, from={unpivot={value={column={name=sales_amount, table_ref=null}}, for={column={name=month_name, table_ref=null}}, in={1={name=jan_sales, table_ref=null}, 2={name=feb_sales, table_ref=null}}}, table={alias=msl, table=monthly_sales}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[empid, month_name, sales_amount]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{monthly_sales={jan_sales=[[@16,103:111='jan_sales',<381>,3:41]], empid=[[@1,7:11='empid',<381>,1:7]], feb_sales=[[@18,114:122='feb_sales',<381>,3:52]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={empid=[[@1,7:11='empid',<381>,1:7]], month_name=[[@3,14:23='month_name',<381>,1:14], [@13,88:97='month_name',<381>,3:26]], sales_amount=[[@5,26:37='sales_amount',<381>,1:26], [@11,71:82='sales_amount',<381>,3:9]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={empid=[[@1,7:11='empid',<381>,1:7]], month_name=[[@3,14:23='month_name',<381>,1:14], [@13,88:97='month_name',<381>,3:26]], sales_amount=[[@5,26:37='sales_amount',<381>,1:26], [@11,71:82='sales_amount',<381>,3:9]]}, table_dictionary={monthly_sales={jan_sales=[[@16,103:111='jan_sales',<381>,3:41]], empid=[[@1,7:11='empid',<381>,1:7]], feb_sales=[[@18,114:122='feb_sales',<381>,3:52]]}}, derivation={source_columns={tuple_0=[{name=jan_sales, table_ref=msl}, {name=feb_sales, table_ref=msl}]}, derived_columns={tuple_0={sales_amount=[[@11,71:82='sales_amount',<381>,3:9]], month_name=[[@13,88:97='month_name',<381>,3:26]]}}}, interface={empid=[{name=empid, table_ref=monthly_sales}], month_name=[{name=month_name, table_ref=tuple_0}, {name=jan_sales, table_ref=msl}, {name=feb_sales, table_ref=msl}], sales_amount=[{name=jan_sales, table_ref=msl}, {name=feb_sales, table_ref=msl}]}, table_alias={msl=monthly_sales, tuple_0=msl}}}",
				extractor.getSymbolTable().toString());
	}

	// CTE source + derived-column clause surfaces (Category D): PIVOT on WITH body, all five probes.
	@Test
	public void pivotCteSourceDerivedColumnClauseSurfacesV1Test() {
		final String query =
			"WITH src_rows AS (\n" +
			"  SELECT src, col1, col2 FROM tab1\n" +
			")\n" +
			"SELECT q.src\n" +
			"FROM src_rows q\n" +
			"PIVOT (SUM(col1) sum FOR col2 IN ('A'))\n" +
			"JOIN targets t ON A_sum >= t.target_amount\n" +
			"WHERE A_sum > 0\n" +
			"GROUP BY q.src, A_sum\n" +
			"HAVING A_sum > 0\n" +
			"QUALIFY A_sum > 0\n" +
			"ORDER BY A_sum;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={with={1={cte={select={1={column={name=src, table_ref=null}}, 2={column={name=col1, table_ref=null}}, 3={column={name=col2, table_ref=null}}}, from={table={alias=null, table=tab1}}}, alias=src_rows}}, query={select={1={column={name=src, table_ref=q}}}, having={condition={left={column={name=A_sum, table_ref=null}}, right={literal=0}, operator=>}}, orderby={1={null_order=null, predicand={column={name=A_sum, table_ref=null}}, sort_order=ASC}}, from={join={1={pivot={value={function={function_name=SUM, parameters={column={name=col1, table_ref=null}}}, alias=sum}, for={column={name=col2, table_ref=null}}, in={1={pivot_literal='A'}}}, table={alias=q, table=src_rows}}, 2={join=JOIN, on={condition={left={column={name=A_sum, table_ref=null}}, right={column={name=target_amount, table_ref=t}}, operator=>=}}}, 3={table={alias=t, table=targets}}}}, where={condition={left={column={name=A_sum, table_ref=null}}, right={literal=0}, operator=>}}, groupby={1={column={name=src, table_ref=q}}, 2={column={name=A_sum, table_ref=null}}}, qualify={condition={left={column={name=A_sum, table_ref=null}}, right={literal=0}, operator=>}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[src]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{q={col2=[[@28,110:113='col2',<381>,6:25]], col1=[[@24,96:99='col1',<381>,6:11]]}, tab1={src=[[@5,28:30='src',<381>,2:9]], col2=[[@9,39:42='col2',<381>,2:20]], col1=[[@7,33:36='col1',<381>,2:14]]}, targets={target_amount=[[@40,152:152='t',<381>,7:27]], col2=[[@28,110:113='col2',<381>,6:25]], col1=[[@24,96:99='col1',<381>,6:11]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={col2=[[@9,39:42='col2',<381>,2:20]], src=[[@5,28:30='src',<381>,2:9], [@14,63:63='q',<381>,4:7], [@49,193:193='q',<381>,9:9]], col1=[[@7,33:36='col1',<381>,2:14]]}, query2={src=[[@16,65:67='src',<381>,4:9], [@51,195:197='src',<381>,9:11]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query2={context_list={src_rows=query0, q=query0}, query_dictionary={src=[[@16,65:67='src',<381>,4:9], [@51,195:197='src',<381>,9:11]]}, table_dictionary={q={col2=[[@28,110:113='col2',<381>,6:25]], col1=[[@24,96:99='col1',<381>,6:11]]}, targets={target_amount=[[@40,152:152='t',<381>,7:27]], col2=[[@28,110:113='col2',<381>,6:25]], col1=[[@24,96:99='col1',<381>,6:11]]}}, grouped_by=[{name=src, table_ref=q}, {name=A_sum, table_ref=tuple_0}, {name=col2, table_ref=q}, {name=col1, table_ref=q}], def_query0={query_dictionary={src=[[@5,28:30='src',<381>,2:9], [@14,63:63='q',<381>,4:7], [@49,193:193='q',<381>,9:9]], col2=[[@9,39:42='col2',<381>,2:20]], col1=[[@7,33:36='col1',<381>,2:14]]}, table_dictionary={tab1={src=[[@5,28:30='src',<381>,2:9]], col2=[[@9,39:42='col2',<381>,2:20]], col1=[[@7,33:36='col1',<381>,2:14]]}}, interface={src=[{name=src, table_ref=tab1}], col2=[{name=col2, table_ref=tab1}], col1=[{name=col1, table_ref=tab1}]}}, derivation={source_columns={tuple_0=[{name=col2, table_ref=q}, {name=col1, table_ref=q}]}, derived_columns={tuple_0={A_sum=[[@22,92:94='SUM',<141>,6:7], [@31,119:121=''A'',<389>,6:34]]}}}, ordered_by=[{name=A_sum, table_ref=tuple_0}, {name=col2, table_ref=q}, {name=col1, table_ref=q}], filters=[{name=A_sum, table_ref=tuple_0}, {name=col2, table_ref=q}, {name=col1, table_ref=q}, {name=target_amount, table_ref=t}], interface={src=[{name=src, table_ref=q}]}, table_alias={q=query0, t=targets, src_rows=query0, tuple_0=q}}}",
				extractor.getSymbolTable().toString());
	}

	// CTE source + derived-column clause surfaces (Category D): UNPIVOT on WITH body, VALUE/FOR in all five probes.
	@Test
	public void unpivotCteSourceDerivedColumnClauseSurfacesV1Test() {
		final String query =
			"WITH wide_rows AS (\n" +
			"  SELECT empid, jan_sales, feb_sales, mar_sales FROM monthly_sales\n" +
			")\n" +
			"SELECT month_name, sales_amount\n" +
			"FROM wide_rows\n" +
			"UNPIVOT (sales_amount FOR month_name IN (jan_sales, feb_sales, mar_sales))\n" +
			"JOIN targets t ON sales_amount >= t.target_amount AND month_name = t.month_name\n" +
			"WHERE sales_amount > 10\n" +
			"GROUP BY month_name, sales_amount\n" +
			"HAVING sales_amount > 100\n" +
			"QUALIFY month_name IS NOT NULL\n" +
			"ORDER BY sales_amount;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		Snippet snippet = extractor.getSnippet();
		assertDiagnosticAtPosition(
				snippet,
				"AMBIGUOUS_COLUMN_REFERENCE",
				ParseDiagnostic.Severity.SEVERE_WARNING,
				"Ambiguous column reference 'month_name' at (l:4 c:7). Possible sources: [wide_rows, targets]",
				"month_name",
				4,
				7);
		assertDiagnosticAtPosition(
				snippet,
				"AMBIGUOUS_COLUMN_REFERENCE",
				ParseDiagnostic.Severity.SEVERE_WARNING,
				"Ambiguous column reference 'sales_amount' at (l:4 c:19). Possible sources: [wide_rows, targets]",
				"sales_amount",
				4,
				19);
		Assert.assertEquals("AST is wrong",
				"{SQL={with={1={cte={select={1={column={name=empid, table_ref=null}}, 2={column={name=jan_sales, table_ref=null}}, 3={column={name=feb_sales, table_ref=null}}, 4={column={name=mar_sales, table_ref=null}}}, from={table={alias=null, table=monthly_sales}}}, alias=wide_rows}}, query={select={1={column={name=month_name, table_ref=null}}, 2={column={name=sales_amount, table_ref=null}}}, having={condition={left={column={name=sales_amount, table_ref=null}}, right={literal=100}, operator=>}}, orderby={1={null_order=null, predicand={column={name=sales_amount, table_ref=null}}, sort_order=ASC}}, from={join={1={unpivot={value={column={name=sales_amount, table_ref=null}}, for={column={name=month_name, table_ref=null}}, in={1={name=jan_sales, table_ref=null}, 2={name=feb_sales, table_ref=null}, 3={name=mar_sales, table_ref=null}}}, table={alias=null, table=wide_rows}}, 2={join=JOIN, on={and={1={condition={left={column={name=sales_amount, table_ref=null}}, right={column={name=target_amount, table_ref=t}}, operator=>=}}, 2={condition={left={column={name=month_name, table_ref=null}}, right={column={name=month_name, table_ref=t}}, operator==}}}}}, 3={table={alias=t, table=targets}}}}, where={condition={left={column={name=sales_amount, table_ref=null}}, right={literal=10}, operator=>}}, groupby={1={column={name=month_name, table_ref=null}}, 2={column={name=sales_amount, table_ref=null}}}, qualify={condition={left={column={name=month_name, table_ref=null}}, operator=IS NOT NULL}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[month_name, sales_amount]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{monthly_sales={jan_sales=[[@7,36:44='jan_sales',<381>,2:16]], empid=[[@5,29:33='empid',<381>,2:9]], mar_sales=[[@11,58:66='mar_sales',<381>,2:38]], feb_sales=[[@9,47:55='feb_sales',<381>,2:27]]}, wide_rows={jan_sales=[[@28,177:185='jan_sales',<381>,6:41]], mar_sales=[[@32,199:207='mar_sales',<381>,6:63]], feb_sales=[[@30,188:196='feb_sales',<381>,6:52]]}, targets={target_amount=[[@41,245:245='t',<381>,7:34]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={jan_sales=[[@7,36:44='jan_sales',<381>,2:16]], empid=[[@5,29:33='empid',<381>,2:9]], mar_sales=[[@11,58:66='mar_sales',<381>,2:38]], feb_sales=[[@9,47:55='feb_sales',<381>,2:27]]}, query1={month_name=[[@16,96:105='month_name',<381>,4:7], [@45,265:274='month_name',<381>,7:54], [@49,280:289='month_name',<381>,7:69], [@64,383:392='month_name',<381>,11:8], [@56,324:333='month_name',<381>,9:9], [@25,162:171='month_name',<381>,6:26]], sales_amount=[[@18,108:119='sales_amount',<381>,4:19], [@39,229:240='sales_amount',<381>,7:18], [@51,297:308='sales_amount',<381>,8:6], [@60,356:367='sales_amount',<381>,10:7], [@58,336:347='sales_amount',<381>,9:21], [@70,415:426='sales_amount',<381>,12:9], [@23,145:156='sales_amount',<381>,6:9]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query1={context_list={wide_rows=query0}, query_dictionary={month_name=[[@16,96:105='month_name',<381>,4:7], [@45,265:274='month_name',<381>,7:54], [@49,280:289='month_name',<381>,7:69], [@64,383:392='month_name',<381>,11:8], [@56,324:333='month_name',<381>,9:9], [@25,162:171='month_name',<381>,6:26]], sales_amount=[[@18,108:119='sales_amount',<381>,4:19], [@39,229:240='sales_amount',<381>,7:18], [@51,297:308='sales_amount',<381>,8:6], [@60,356:367='sales_amount',<381>,10:7], [@58,336:347='sales_amount',<381>,9:21], [@70,415:426='sales_amount',<381>,12:9], [@23,145:156='sales_amount',<381>,6:9]]}, table_dictionary={wide_rows={jan_sales=[[@28,177:185='jan_sales',<381>,6:41]], mar_sales=[[@32,199:207='mar_sales',<381>,6:63]], feb_sales=[[@30,188:196='feb_sales',<381>,6:52]]}, targets={target_amount=[[@41,245:245='t',<381>,7:34]]}}, grouped_by=[{name=month_name, table_ref=null}, {name=sales_amount, table_ref=null}], def_query0={query_dictionary={jan_sales=[[@7,36:44='jan_sales',<381>,2:16]], empid=[[@5,29:33='empid',<381>,2:9]], mar_sales=[[@11,58:66='mar_sales',<381>,2:38]], feb_sales=[[@9,47:55='feb_sales',<381>,2:27]]}, table_dictionary={monthly_sales={jan_sales=[[@7,36:44='jan_sales',<381>,2:16]], empid=[[@5,29:33='empid',<381>,2:9]], mar_sales=[[@11,58:66='mar_sales',<381>,2:38]], feb_sales=[[@9,47:55='feb_sales',<381>,2:27]]}}, interface={jan_sales=[{name=jan_sales, table_ref=monthly_sales}], empid=[{name=empid, table_ref=monthly_sales}], mar_sales=[{name=mar_sales, table_ref=monthly_sales}], feb_sales=[{name=feb_sales, table_ref=monthly_sales}]}}, derivation={derived_columns={tuple_0={sales_amount=[[@23,145:156='sales_amount',<381>,6:9]], month_name=[[@25,162:171='month_name',<381>,6:26]]}}}, ordered_by=[{name=sales_amount, table_ref=null}], filters=[{name=sales_amount, table_ref=null}, {name=target_amount, table_ref=t}, {name=month_name, table_ref=null}, {name=month_name, table_ref=t}], interface={month_name=[{name=month_name, table_ref=null}], sales_amount=[{name=sales_amount, table_ref=null}]}, table_alias={t=targets, wide_rows=query0}}}",
				extractor.getSymbolTable().toString());
	}

	// Three-tuple join chains: each FROM arm is table + relational operator.
	// Derived columns appear in SELECT (unqualified or source-table qualified), JOIN ON, and WHERE.
	// Modifier aliases (p, q, u1, …) may qualify derived operands in JOIN/WHERE but not in SELECT.

	// Three-tuple PIVOT join: distinct derived output per modifier (happy path).
	@Test
	public void triplePivotJoinDerivedColumnsAcrossTuplesV1Test() {
		final String query =
			"SELECT jan_sales_SUM, feb_sales_SUM, mar_sales_SUM\n" +
			"FROM monthly_sales_long p_src\n" +
			"PIVOT (SUM(sales_amount) FOR month_name IN ('jan_sales')) p\n" +
			"JOIN monthly_sales_long q_src\n" +
			"PIVOT (SUM(sales_amount) FOR month_name IN ('feb_sales')) q\n" +
			"  ON p.jan_sales_SUM = q.feb_sales_SUM\n" +
			"JOIN monthly_sales_long r_src\n" +
			"PIVOT (SUM(sales_amount) FOR month_name IN ('mar_sales')) r\n" +
			"  ON q.feb_sales_SUM = r.mar_sales_SUM\n" +
			"WHERE p.jan_sales_SUM > 0\n" +
			"  AND q.feb_sales_SUM > 0\n" +
			"  AND r.mar_sales_SUM > 0;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=jan_sales_SUM, table_ref=null}}, 2={column={name=feb_sales_SUM, table_ref=null}}, 3={column={name=mar_sales_SUM, table_ref=null}}}, from={join={1={pivot={value={function={function_name=SUM, parameters={column={name=sales_amount, table_ref=null}}}}, for={column={name=month_name, table_ref=null}}, in={1={pivot_literal='jan_sales'}}}, alias=p, table={alias=p_src, table=monthly_sales_long}}, 2={join=JOIN, on={condition={left={column={name=jan_sales_SUM, table_ref=p}}, right={column={name=feb_sales_SUM, table_ref=q}}, operator==}}}, 3={pivot={value={function={function_name=SUM, parameters={column={name=sales_amount, table_ref=null}}}}, for={column={name=month_name, table_ref=null}}, in={1={pivot_literal='feb_sales'}}}, alias=q, table={alias=q_src, table=monthly_sales_long}}, 4={join=JOIN, on={condition={left={column={name=feb_sales_SUM, table_ref=q}}, right={column={name=mar_sales_SUM, table_ref=r}}, operator==}}}, 5={pivot={value={function={function_name=SUM, parameters={column={name=sales_amount, table_ref=null}}}}, for={column={name=month_name, table_ref=null}}, in={1={pivot_literal='mar_sales'}}}, alias=r, table={alias=r_src, table=monthly_sales_long}}}}, where={and={1={condition={left={column={name=jan_sales_SUM, table_ref=p}}, right={literal=0}, operator=>}}, 2={condition={left={column={name=feb_sales_SUM, table_ref=q}}, right={literal=0}, operator=>}}, 3={condition={left={column={name=mar_sales_SUM, table_ref=r}}, right={literal=0}, operator=>}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[jan_sales_SUM, mar_sales_SUM, feb_sales_SUM]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{monthly_sales_long={month_name=[[@16,110:119='month_name',<381>,3:29], [@33,200:209='month_name',<381>,5:29], [@58,329:338='month_name',<381>,8:29]], sales_amount=[[@13,92:103='sales_amount',<381>,3:11], [@30,182:193='sales_amount',<381>,5:11], [@55,311:322='sales_amount',<381>,8:11]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query3={jan_sales_SUM=[[@1,7:19='jan_sales_SUM',<381>,1:7], [@43,238:250='jan_sales_SUM',<381>,6:7], [@76,407:419='jan_sales_SUM',<381>,10:8], [@11,88:90='SUM',<141>,3:7], [@19,125:135=''jan_sales'',<389>,3:44]], mar_sales_SUM=[[@5,37:49='mar_sales_SUM',<381>,1:37], [@72,385:397='mar_sales_SUM',<381>,9:25], [@88,459:471='mar_sales_SUM',<381>,12:8], [@53,307:309='SUM',<141>,8:7], [@61,344:354=''mar_sales'',<389>,8:44]], feb_sales_SUM=[[@3,22:34='feb_sales_SUM',<381>,1:22], [@47,256:268='feb_sales_SUM',<381>,6:25], [@68,367:379='feb_sales_SUM',<381>,9:7], [@82,433:445='feb_sales_SUM',<381>,11:8], [@28,178:180='SUM',<141>,5:7], [@36,215:225=''feb_sales'',<389>,5:44]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query3={query_dictionary={jan_sales_SUM=[[@1,7:19='jan_sales_SUM',<381>,1:7], [@43,238:250='jan_sales_SUM',<381>,6:7], [@76,407:419='jan_sales_SUM',<381>,10:8], [@11,88:90='SUM',<141>,3:7], [@19,125:135=''jan_sales'',<389>,3:44]], mar_sales_SUM=[[@5,37:49='mar_sales_SUM',<381>,1:37], [@72,385:397='mar_sales_SUM',<381>,9:25], [@88,459:471='mar_sales_SUM',<381>,12:8], [@53,307:309='SUM',<141>,8:7], [@61,344:354=''mar_sales'',<389>,8:44]], feb_sales_SUM=[[@3,22:34='feb_sales_SUM',<381>,1:22], [@47,256:268='feb_sales_SUM',<381>,6:25], [@68,367:379='feb_sales_SUM',<381>,9:7], [@82,433:445='feb_sales_SUM',<381>,11:8], [@28,178:180='SUM',<141>,5:7], [@36,215:225=''feb_sales'',<389>,5:44]]}, table_dictionary={monthly_sales_long={month_name=[[@16,110:119='month_name',<381>,3:29], [@33,200:209='month_name',<381>,5:29], [@58,329:338='month_name',<381>,8:29]], sales_amount=[[@13,92:103='sales_amount',<381>,3:11], [@30,182:193='sales_amount',<381>,5:11], [@55,311:322='sales_amount',<381>,8:11]]}}, derivation={source_columns={p=[{name=month_name, table_ref=p_src}, {name=sales_amount, table_ref=p_src}], q=[{name=month_name, table_ref=q_src}, {name=sales_amount, table_ref=q_src}], r=[{name=month_name, table_ref=r_src}, {name=sales_amount, table_ref=r_src}]}, derived_columns={p={jan_sales_SUM=[[@11,88:90='SUM',<141>,3:7], [@19,125:135=''jan_sales'',<389>,3:44]]}, q={feb_sales_SUM=[[@28,178:180='SUM',<141>,5:7], [@36,215:225=''feb_sales'',<389>,5:44]]}, r={mar_sales_SUM=[[@53,307:309='SUM',<141>,8:7], [@61,344:354=''mar_sales'',<389>,8:44]]}}}, filters=[{name=jan_sales_SUM, table_ref=p}, {name=feb_sales_SUM, table_ref=q}, {name=mar_sales_SUM, table_ref=r}], interface={jan_sales_SUM=[{name=jan_sales_SUM, table_ref=p}, {name=month_name, table_ref=p_src}, {name=sales_amount, table_ref=p_src}], mar_sales_SUM=[{name=mar_sales_SUM, table_ref=r}, {name=month_name, table_ref=r_src}, {name=sales_amount, table_ref=r_src}], feb_sales_SUM=[{name=feb_sales_SUM, table_ref=q}, {name=month_name, table_ref=q_src}, {name=sales_amount, table_ref=q_src}]}, table_alias={p=p_src, q=q_src, p_src=monthly_sales_long, r=r_src, q_src=monthly_sales_long, r_src=monthly_sales_long}}}",
				extractor.getSymbolTable().toString());
	}

	/**
	 * Phase 17.6.3: triple sibling PIVOTs with the same derived registry key — unqualified
	 * {@code jan_sales_SUM} in SELECT must fatal like {@link #tripleUnpivotJoinDerivedColumnsAcrossTuplesV1Test}.
	 */
	@Test
	public void triplePivotJoinDerivedColumnsSameOutputSelectAmbiguousV17_6_3Test() {
		final String query =
				"SELECT p_src.empid AS e1, q_src.empid AS e2, r_src.empid AS e3, jan_sales_SUM\n"
						+ "FROM monthly_sales_long p_src\n"
						+ "PIVOT (SUM(sales_amount) FOR month_name IN ('jan_sales')) p\n"
						+ "JOIN monthly_sales_long q_src\n"
						+ "PIVOT (SUM(sales_amount) FOR month_name IN ('jan_sales')) q\n"
						+ "  ON p.jan_sales_SUM = q.jan_sales_SUM\n"
						+ "JOIN monthly_sales_long r_src\n"
						+ "PIVOT (SUM(sales_amount) FOR month_name IN ('jan_sales')) r\n"
						+ "  ON q.jan_sales_SUM = r.jan_sales_SUM\n"
						+ "WHERE p.jan_sales_SUM > 0\n"
						+ "  AND q.jan_sales_SUM > 0\n"
						+ "  AND r.jan_sales_SUM > 0;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		Snippet snippet = extractor.getSnippet();
		assertFatalDiagnosticAtPosition(
				snippet,
				"AMBIGUOUS_DERIVED_COLUMN_REFERENCE",
				"Ambiguous derived column reference 'jan_sales_SUM' at (l:1 c:64). Possible sources: [p, q, r]",
				"jan_sales_SUM",
				1,
				64);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=empid, table_ref=p_src}, alias=e1}, 2={column={name=empid, table_ref=q_src}, alias=e2}, 3={column={name=empid, table_ref=r_src}, alias=e3}, 4={column={name=jan_sales_SUM, table_ref=null}}}, from={join={1={pivot={value={function={function_name=SUM, parameters={column={name=sales_amount, table_ref=null}}}}, for={column={name=month_name, table_ref=null}}, in={1={pivot_literal='jan_sales'}}}, alias=p, table={alias=p_src, table=monthly_sales_long}}, 2={join=JOIN, on={condition={left={column={name=jan_sales_SUM, table_ref=p}}, right={column={name=jan_sales_SUM, table_ref=q}}, operator==}}}, 3={pivot={value={function={function_name=SUM, parameters={column={name=sales_amount, table_ref=null}}}}, for={column={name=month_name, table_ref=null}}, in={1={pivot_literal='jan_sales'}}}, alias=q, table={alias=q_src, table=monthly_sales_long}}, 4={join=JOIN, on={condition={left={column={name=jan_sales_SUM, table_ref=q}}, right={column={name=jan_sales_SUM, table_ref=r}}, operator==}}}, 5={pivot={value={function={function_name=SUM, parameters={column={name=sales_amount, table_ref=null}}}}, for={column={name=month_name, table_ref=null}}, in={1={pivot_literal='jan_sales'}}}, alias=r, table={alias=r_src, table=monthly_sales_long}}}}, where={and={1={condition={left={column={name=jan_sales_SUM, table_ref=p}}, right={literal=0}, operator=>}}, 2={condition={left={column={name=jan_sales_SUM, table_ref=q}}, right={literal=0}, operator=>}}, 3={condition={left={column={name=jan_sales_SUM, table_ref=r}}, right={literal=0}, operator=>}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[jan_sales_SUM, e1, e2, e3]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{monthly_sales_long={empid=[[@1,7:11='p_src',<381>,1:7], [@7,26:30='q_src',<381>,1:26], [@13,45:49='r_src',<381>,1:45]], month_name=[[@30,137:146='month_name',<381>,3:29], [@47,227:236='month_name',<381>,5:29], [@72,356:365='month_name',<381>,8:29]], sales_amount=[[@27,119:130='sales_amount',<381>,3:11], [@44,209:220='sales_amount',<381>,5:11], [@69,338:349='sales_amount',<381>,8:11]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query3={jan_sales_SUM=[[@19,64:76='jan_sales_SUM',<381>,1:64], [@57,265:277='jan_sales_SUM',<381>,6:7], [@61,283:295='jan_sales_SUM',<381>,6:25], [@82,394:406='jan_sales_SUM',<381>,9:7], [@86,412:424='jan_sales_SUM',<381>,9:25], [@90,434:446='jan_sales_SUM',<381>,10:8], [@96,460:472='jan_sales_SUM',<381>,11:8], [@102,486:498='jan_sales_SUM',<381>,12:8]], e1=[[@5,22:23='e1',<381>,1:22]], e2=[[@11,41:42='e2',<381>,1:41]], e3=[[@17,60:61='e3',<381>,1:60]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query3={query_dictionary={jan_sales_SUM=[[@19,64:76='jan_sales_SUM',<381>,1:64], [@57,265:277='jan_sales_SUM',<381>,6:7], [@61,283:295='jan_sales_SUM',<381>,6:25], [@82,394:406='jan_sales_SUM',<381>,9:7], [@86,412:424='jan_sales_SUM',<381>,9:25], [@90,434:446='jan_sales_SUM',<381>,10:8], [@96,460:472='jan_sales_SUM',<381>,11:8], [@102,486:498='jan_sales_SUM',<381>,12:8]], e1=[[@5,22:23='e1',<381>,1:22]], e2=[[@11,41:42='e2',<381>,1:41]], e3=[[@17,60:61='e3',<381>,1:60]]}, table_dictionary={monthly_sales_long={month_name=[[@30,137:146='month_name',<381>,3:29], [@47,227:236='month_name',<381>,5:29], [@72,356:365='month_name',<381>,8:29]], empid=[[@1,7:11='p_src',<381>,1:7], [@7,26:30='q_src',<381>,1:26], [@13,45:49='r_src',<381>,1:45]], sales_amount=[[@27,119:130='sales_amount',<381>,3:11], [@44,209:220='sales_amount',<381>,5:11], [@69,338:349='sales_amount',<381>,8:11]]}}, derivation={source_columns={p=[{name=month_name, table_ref=p_src}, {name=sales_amount, table_ref=p_src}], q=[{name=month_name, table_ref=q_src}, {name=sales_amount, table_ref=q_src}], r=[{name=month_name, table_ref=r_src}, {name=sales_amount, table_ref=r_src}]}, derived_columns={p={jan_sales_SUM=[[@25,115:117='SUM',<141>,3:7], [@33,152:162=''jan_sales'',<389>,3:44]]}, q={jan_sales_SUM=[[@42,205:207='SUM',<141>,5:7], [@50,242:252=''jan_sales'',<389>,5:44]]}, r={jan_sales_SUM=[[@67,334:336='SUM',<141>,8:7], [@75,371:381=''jan_sales'',<389>,8:44]]}}}, filters=[{name=jan_sales_SUM, table_ref=p}, {name=jan_sales_SUM, table_ref=q}, {name=jan_sales_SUM, table_ref=r}], interface={jan_sales_SUM=[{name=jan_sales_SUM, table_ref=null}], e1=[{name=empid, table_ref=p_src}], e2=[{name=empid, table_ref=q_src}], e3=[{name=empid, table_ref=r_src}]}, table_alias={p=p_src, q=q_src, p_src=monthly_sales_long, r=r_src, q_src=monthly_sales_long, r_src=monthly_sales_long}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void tripleUnpivotJoinDerivedColumnsAcrossTuplesV1Test() {
		final String query =
			"SELECT m1.empid AS e1, m2.empid AS e2, m3.empid AS e3, sales_amount, month_name\n" +
			"FROM monthly_sales m1\n" +
			"UNPIVOT (sales_amount FOR month_name IN (jan_sales, feb_sales)) u1\n" +
			"JOIN monthly_sales m2\n" +
			"UNPIVOT (sales_amount FOR month_name IN (jan_sales, feb_sales)) u2\n" +
			"  ON u1.month_name = u2.month_name AND u1.sales_amount = u2.sales_amount\n" +
			"JOIN monthly_sales m3\n" +
			"UNPIVOT (sales_amount FOR month_name IN (jan_sales, mar_sales)) u3\n" +
			"  ON u2.month_name = u3.month_name\n" +
			"WHERE u1.sales_amount > 10\n" +
			"  AND u2.sales_amount > 10\n" +
			"  AND u3.sales_amount > 10;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		Snippet snippet = extractor.getSnippet();
		assertFatalDiagnosticAtPosition(
				snippet,
				"AMBIGUOUS_DERIVED_COLUMN_REFERENCE",
				"Ambiguous derived column reference 'month_name' at (l:1 c:69). Possible sources: [u1, u2, u3]",
				"month_name",
				1,
				69);
		assertFatalDiagnosticAtPosition(
				snippet,
				"AMBIGUOUS_DERIVED_COLUMN_REFERENCE",
				"Ambiguous derived column reference 'sales_amount' at (l:1 c:55). Possible sources: [u1, u2, u3]",
				"sales_amount",
				1,
				55);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=empid, table_ref=m1}, alias=e1}, 2={column={name=empid, table_ref=m2}, alias=e2}, 3={column={name=empid, table_ref=m3}, alias=e3}, 4={column={name=sales_amount, table_ref=null}}, 5={column={name=month_name, table_ref=null}}}, from={join={1={unpivot={value={column={name=sales_amount, table_ref=null}}, for={column={name=month_name, table_ref=null}}, in={1={name=jan_sales, table_ref=null}, 2={name=feb_sales, table_ref=null}}}, alias=u1, table={alias=m1, table=monthly_sales}}, 2={join=JOIN, on={and={1={condition={left={column={name=month_name, table_ref=u1}}, right={column={name=month_name, table_ref=u2}}, operator==}}, 2={condition={left={column={name=sales_amount, table_ref=u1}}, right={column={name=sales_amount, table_ref=u2}}, operator==}}}}}, 3={unpivot={value={column={name=sales_amount, table_ref=null}}, for={column={name=month_name, table_ref=null}}, in={1={name=jan_sales, table_ref=null}, 2={name=feb_sales, table_ref=null}}}, alias=u2, table={alias=m2, table=monthly_sales}}, 4={join=JOIN, on={condition={left={column={name=month_name, table_ref=u2}}, right={column={name=month_name, table_ref=u3}}, operator==}}}, 5={unpivot={value={column={name=sales_amount, table_ref=null}}, for={column={name=month_name, table_ref=null}}, in={1={name=jan_sales, table_ref=null}, 2={name=mar_sales, table_ref=null}}}, alias=u3, table={alias=m3, table=monthly_sales}}}}, where={and={1={condition={left={column={name=sales_amount, table_ref=u1}}, right={literal=10}, operator=>}}, 2={condition={left={column={name=sales_amount, table_ref=u2}}, right={literal=10}, operator=>}}, 3={condition={left={column={name=sales_amount, table_ref=u3}}, right={literal=10}, operator=>}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[month_name, sales_amount, e1, e2, e3]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{monthly_sales={jan_sales=[[@32,143:151='jan_sales',<381>,3:41], [@48,232:240='jan_sales',<381>,5:41], [@80,394:402='jan_sales',<381>,8:41]], empid=[[@7,23:24='m2',<381>,1:23], [@13,39:40='m3',<381>,1:39], [@1,7:8='m1',<381>,1:7]], mar_sales=[[@82,405:413='mar_sales',<381>,8:52]], feb_sales=[[@34,154:162='feb_sales',<381>,3:52], [@50,243:251='feb_sales',<381>,5:52]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query0={month_name=[[@21,69:78='month_name',<381>,1:69], [@57,266:275='month_name',<381>,6:8], [@61,282:291='month_name',<381>,6:24], [@89,428:437='month_name',<381>,9:8], [@93,444:453='month_name',<381>,9:24]], sales_amount=[[@19,55:66='sales_amount',<381>,1:55], [@65,300:311='sales_amount',<381>,6:42], [@69,318:329='sales_amount',<381>,6:60], [@97,464:475='sales_amount',<381>,10:9], [@103,491:502='sales_amount',<381>,11:9], [@109,518:529='sales_amount',<381>,12:9]], e1=[[@5,19:20='e1',<381>,1:19]], e2=[[@11,35:36='e2',<381>,1:35]], e3=[[@17,51:52='e3',<381>,1:51]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query0={query_dictionary={month_name=[[@21,69:78='month_name',<381>,1:69], [@57,266:275='month_name',<381>,6:8], [@61,282:291='month_name',<381>,6:24], [@89,428:437='month_name',<381>,9:8], [@93,444:453='month_name',<381>,9:24]], sales_amount=[[@19,55:66='sales_amount',<381>,1:55], [@65,300:311='sales_amount',<381>,6:42], [@69,318:329='sales_amount',<381>,6:60], [@97,464:475='sales_amount',<381>,10:9], [@103,491:502='sales_amount',<381>,11:9], [@109,518:529='sales_amount',<381>,12:9]], e1=[[@5,19:20='e1',<381>,1:19]], e2=[[@11,35:36='e2',<381>,1:35]], e3=[[@17,51:52='e3',<381>,1:51]]}, table_dictionary={monthly_sales={jan_sales=[[@32,143:151='jan_sales',<381>,3:41], [@48,232:240='jan_sales',<381>,5:41], [@80,394:402='jan_sales',<381>,8:41]], mar_sales=[[@82,405:413='mar_sales',<381>,8:52]], empid=[[@7,23:24='m2',<381>,1:23], [@13,39:40='m3',<381>,1:39], [@1,7:8='m1',<381>,1:7]], feb_sales=[[@34,154:162='feb_sales',<381>,3:52], [@50,243:251='feb_sales',<381>,5:52]]}}, derivation={source_columns={u1=[{name=jan_sales, table_ref=m1}, {name=feb_sales, table_ref=m1}], u2=[{name=jan_sales, table_ref=m2}, {name=feb_sales, table_ref=m2}], u3=[{name=jan_sales, table_ref=m3}, {name=mar_sales, table_ref=m3}]}, derived_columns={u1={sales_amount=[[@27,111:122='sales_amount',<381>,3:9]], month_name=[[@29,128:137='month_name',<381>,3:26]]}, u2={sales_amount=[[@43,200:211='sales_amount',<381>,5:9]], month_name=[[@45,217:226='month_name',<381>,5:26]]}, u3={sales_amount=[[@75,362:373='sales_amount',<381>,8:9]], month_name=[[@77,379:388='month_name',<381>,8:26]]}}}, filters=[{name=month_name, table_ref=u1}, {name=month_name, table_ref=u2}, {name=sales_amount, table_ref=u1}, {name=sales_amount, table_ref=u2}, {name=month_name, table_ref=u3}, {name=sales_amount, table_ref=u3}], interface={month_name=[{name=month_name, table_ref=null}], sales_amount=[{name=sales_amount, table_ref=null}], e1=[{name=empid, table_ref=m1}], e2=[{name=empid, table_ref=m2}], e3=[{name=empid, table_ref=m3}]}, table_alias={m1=monthly_sales, m2=monthly_sales, m3=monthly_sales, u1=m1, u2=m2, u3=m3}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void triplePivotUnpivotPivotJoinDerivedColumnsV1Test() {
		final String query =
			"SELECT jan_sales_SUM, sales_amount, month_name, feb_sales_SUM\n" +
			"FROM monthly_sales_long p_src\n" +
			"PIVOT (SUM(sales_amount) FOR month_name IN ('jan_sales', 'feb_sales')) p\n" +
			"JOIN monthly_sales u_src\n" +
			"UNPIVOT (sales_amount FOR month_name IN (jan_sales, feb_sales)) u\n" +
			"  ON p.jan_sales_SUM = u.sales_amount AND u.month_name = 'jan_sales'\n" +
			"JOIN monthly_sales_long q_src\n" +
			"PIVOT (SUM(sales_amount) FOR month_name IN ('feb_sales')) q\n" +
			"  ON u.sales_amount = q.feb_sales_SUM\n" +
			"WHERE p.jan_sales_SUM > 0\n" +
			"  AND u.sales_amount > 0 or sales_amount < 10\n" +
			"  AND feb_sales_SUM > 0 and month_name != 'jan_sales'\n" +
			"ORDER BY p.jan_sales_SUM, q.feb_sales_SUM, month_name, sales_amount;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		Snippet snippet = extractor.getSnippet();
		assertFatalDiagnosticAtPosition(
				snippet,
				"AMBIGUOUS_DERIVED_COLUMN_REFERENCE",
				"Ambiguous derived column reference 'feb_sales_SUM' at (l:1 c:48). Possible sources: [p, q]",
				null,
				1,
				48);
		assertFatalDiagnosticAtPosition(
				snippet,
				"AMBIGUOUS_DERIVED_COLUMN_REFERENCE",
				"Ambiguous derived column reference 'feb_sales_SUM' at (l:12 c:6). Possible sources: [p, q]",
				null,
				12,
				6);
		assertDiagnosticAtPosition(
				snippet,
				"AMBIGUOUS_COLUMN_REFERENCE",
				ParseDiagnostic.Severity.SEVERE_WARNING,
				"Ambiguous column reference 'sales_amount' at (l:1 c:22). Possible sources: [p, q]",
				"sales_amount",
				1,
				22);
		assertDiagnosticAtPosition(
				snippet,
				"AMBIGUOUS_COLUMN_REFERENCE",
				ParseDiagnostic.Severity.SEVERE_WARNING,
				"Ambiguous column reference 'month_name' at (l:1 c:36). Possible sources: [p, q]",
				"month_name",
				1,
				36);
		assertDiagnosticAtPosition(
				snippet,
				"AMBIGUOUS_COLUMN_REFERENCE",
				ParseDiagnostic.Severity.SEVERE_WARNING,
				"Ambiguous column reference 'sales_amount' at (l:11 c:28). Possible sources: [p, q]",
				null,
				11,
				28);
		assertDiagnosticAtPosition(
				snippet,
				"AMBIGUOUS_COLUMN_REFERENCE",
				ParseDiagnostic.Severity.SEVERE_WARNING,
				"Ambiguous column reference 'month_name' at (l:12 c:28). Possible sources: [p, q]",
				null,
				12,
				28);
		assertDiagnosticAtPosition(
				snippet,
				"AMBIGUOUS_COLUMN_REFERENCE",
				ParseDiagnostic.Severity.SEVERE_WARNING,
				"Ambiguous column reference 'month_name' at (l:13 c:43). Possible sources: [p, q]",
				null,
				13,
				43);
		assertDiagnosticAtPosition(
				snippet,
				"AMBIGUOUS_COLUMN_REFERENCE",
				ParseDiagnostic.Severity.SEVERE_WARNING,
				"Ambiguous column reference 'sales_amount' at (l:13 c:55). Possible sources: [p, q]",
				null,
				13,
				55);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=jan_sales_SUM, table_ref=null}}, 2={column={name=sales_amount, table_ref=null}}, 3={column={name=month_name, table_ref=null}}, 4={column={name=feb_sales_SUM, table_ref=null}}}, orderby={1={null_order=null, predicand={column={name=jan_sales_SUM, table_ref=p}}, sort_order=ASC}, 2={null_order=null, predicand={column={name=feb_sales_SUM, table_ref=q}}, sort_order=ASC}, 3={null_order=null, predicand={column={name=month_name, table_ref=null}}, sort_order=ASC}, 4={null_order=null, predicand={column={name=sales_amount, table_ref=null}}, sort_order=ASC}}, from={join={1={pivot={value={function={function_name=SUM, parameters={column={name=sales_amount, table_ref=null}}}}, for={column={name=month_name, table_ref=null}}, in={1={pivot_literal='jan_sales'}, 2={pivot_literal='feb_sales'}}}, alias=p, table={alias=p_src, table=monthly_sales_long}}, 2={join=JOIN, on={and={1={condition={left={column={name=jan_sales_SUM, table_ref=p}}, right={column={name=sales_amount, table_ref=u}}, operator==}}, 2={condition={left={column={name=month_name, table_ref=u}}, right={literal='jan_sales'}, operator==}}}}}, 3={unpivot={value={column={name=sales_amount, table_ref=null}}, for={column={name=month_name, table_ref=null}}, in={1={name=jan_sales, table_ref=null}, 2={name=feb_sales, table_ref=null}}}, alias=u, table={alias=u_src, table=monthly_sales}}, 4={join=JOIN, on={condition={left={column={name=sales_amount, table_ref=u}}, right={column={name=feb_sales_SUM, table_ref=q}}, operator==}}}, 5={pivot={value={function={function_name=SUM, parameters={column={name=sales_amount, table_ref=null}}}}, for={column={name=month_name, table_ref=null}}, in={1={pivot_literal='feb_sales'}}}, alias=q, table={alias=q_src, table=monthly_sales_long}}}}, where={or={1={and={1={condition={left={column={name=jan_sales_SUM, table_ref=p}}, right={literal=0}, operator=>}}, 2={condition={left={column={name=sales_amount, table_ref=u}}, right={literal=0}, operator=>}}}}, 2={and={1={condition={left={column={name=sales_amount, table_ref=null}}, right={literal=10}, operator=<}}, 2={condition={left={column={name=feb_sales_SUM, table_ref=null}}, right={literal=0}, operator=>}}, 3={condition={left={column={name=month_name, table_ref=null}}, right={literal='jan_sales'}, operator=!=}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[jan_sales_SUM, month_name, sales_amount, feb_sales_SUM]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{monthly_sales={jan_sales=[[@37,231:239='jan_sales',<381>,5:41]], feb_sales=[[@39,242:250='feb_sales',<381>,5:52]]}, monthly_sales_long={month_name=[[@18,121:130='month_name',<381>,3:29], [@67,384:393='month_name',<381>,8:29]], sales_amount=[[@15,103:114='sales_amount',<381>,3:11], [@64,366:377='sales_amount',<381>,8:11]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query2={jan_sales_SUM=[[@1,7:19='jan_sales_SUM',<381>,1:7], [@46,263:275='jan_sales_SUM',<381>,6:7], [@85,461:473='jan_sales_SUM',<381>,10:8], [@110,590:602='jan_sales_SUM',<381>,13:11], [@13,99:101='SUM',<141>,3:7], [@21,136:146=''jan_sales'',<389>,3:44]], month_name=[[@5,36:45='month_name',<381>,1:36], [@54,300:309='month_name',<381>,6:44], [@103,553:562='month_name',<381>,12:28], [@116,622:631='month_name',<381>,13:43], [@34,216:225='month_name',<381>,5:26]], sales_amount=[[@3,22:33='sales_amount',<381>,1:22], [@50,281:292='sales_amount',<381>,6:25], [@77,422:433='sales_amount',<381>,9:7], [@91,487:498='sales_amount',<381>,11:8], [@95,507:518='sales_amount',<381>,11:28], [@118,634:645='sales_amount',<381>,13:55], [@32,199:210='sales_amount',<381>,5:9]], feb_sales_SUM=[[@7,48:60='feb_sales_SUM',<381>,1:48], [@81,439:451='feb_sales_SUM',<381>,9:24], [@99,531:543='feb_sales_SUM',<381>,12:6], [@114,607:619='feb_sales_SUM',<381>,13:28]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query2={query_dictionary={jan_sales_SUM=[[@1,7:19='jan_sales_SUM',<381>,1:7], [@46,263:275='jan_sales_SUM',<381>,6:7], [@85,461:473='jan_sales_SUM',<381>,10:8], [@110,590:602='jan_sales_SUM',<381>,13:11], [@13,99:101='SUM',<141>,3:7], [@21,136:146=''jan_sales'',<389>,3:44]], month_name=[[@5,36:45='month_name',<381>,1:36], [@54,300:309='month_name',<381>,6:44], [@103,553:562='month_name',<381>,12:28], [@116,622:631='month_name',<381>,13:43], [@34,216:225='month_name',<381>,5:26]], sales_amount=[[@3,22:33='sales_amount',<381>,1:22], [@50,281:292='sales_amount',<381>,6:25], [@77,422:433='sales_amount',<381>,9:7], [@91,487:498='sales_amount',<381>,11:8], [@95,507:518='sales_amount',<381>,11:28], [@118,634:645='sales_amount',<381>,13:55], [@32,199:210='sales_amount',<381>,5:9]], feb_sales_SUM=[[@7,48:60='feb_sales_SUM',<381>,1:48], [@81,439:451='feb_sales_SUM',<381>,9:24], [@99,531:543='feb_sales_SUM',<381>,12:6], [@114,607:619='feb_sales_SUM',<381>,13:28]]}, table_dictionary={monthly_sales={jan_sales=[[@37,231:239='jan_sales',<381>,5:41]], feb_sales=[[@39,242:250='feb_sales',<381>,5:52]]}, monthly_sales_long={month_name=[[@18,121:130='month_name',<381>,3:29], [@67,384:393='month_name',<381>,8:29]], sales_amount=[[@15,103:114='sales_amount',<381>,3:11], [@64,366:377='sales_amount',<381>,8:11]]}}, derivation={source_columns={p=[{name=month_name, table_ref=p_src}, {name=sales_amount, table_ref=p_src}], q=[{name=month_name, table_ref=q_src}, {name=sales_amount, table_ref=q_src}], u=[{name=jan_sales, table_ref=u_src}, {name=feb_sales, table_ref=u_src}]}, derived_columns={p={jan_sales_SUM=[[@13,99:101='SUM',<141>,3:7], [@21,136:146=''jan_sales'',<389>,3:44]], feb_sales_SUM=[[@13,99:101='SUM',<141>,3:7], [@23,149:159=''feb_sales'',<389>,3:57]]}, q={feb_sales_SUM=[[@62,362:364='SUM',<141>,8:7], [@70,399:409=''feb_sales'',<389>,8:44]]}, u={sales_amount=[[@32,199:210='sales_amount',<381>,5:9]], month_name=[[@34,216:225='month_name',<381>,5:26]]}}}, ordered_by=[{name=jan_sales_SUM, table_ref=p}, {name=feb_sales_SUM, table_ref=q}, {name=month_name, table_ref=u}, {name=jan_sales, table_ref=u_src}, {name=feb_sales, table_ref=u_src}, {name=sales_amount, table_ref=u}], filters=[{name=jan_sales_SUM, table_ref=p}, {name=sales_amount, table_ref=u}, {name=month_name, table_ref=u}, {name=feb_sales_SUM, table_ref=q}, {name=jan_sales, table_ref=u_src}, {name=feb_sales, table_ref=u_src}, {name=feb_sales_SUM, table_ref=null}], interface={jan_sales_SUM=[{name=jan_sales_SUM, table_ref=p}, {name=month_name, table_ref=p_src}, {name=sales_amount, table_ref=p_src}], month_name=[{name=month_name, table_ref=u}, {name=jan_sales, table_ref=u_src}, {name=feb_sales, table_ref=u_src}], sales_amount=[{name=sales_amount, table_ref=u}, {name=jan_sales, table_ref=u_src}, {name=feb_sales, table_ref=u_src}], feb_sales_SUM=[{name=feb_sales_SUM, table_ref=null}]}, table_alias={p=p_src, q=q_src, p_src=monthly_sales_long, q_src=monthly_sales_long, u=u_src, u_src=monthly_sales}}}",
				extractor.getSymbolTable().toString());
	}

	@Test
	public void tripleUnpivotPivotUnpivotJoinDerivedColumnsV1Test() {
		final String query =
			"SELECT u1_src.empid AS e1, jan_sales_SUM, u2_src.empid AS e2, sales_amount, month_name\n" +
			"FROM monthly_sales u1_src\n" +
			"UNPIVOT (sales_amount FOR month_name IN (jan_sales, feb_sales)) u1\n" +
			"JOIN monthly_sales_long p_src\n" +
			"PIVOT (SUM(sales_amount) FOR month_name IN ('jan_sales')) p\n" +
			"  ON u1.sales_amount = p.jan_sales_SUM\n" +
			"JOIN monthly_sales u2_src\n" +
			"UNPIVOT (sales_amount FOR month_name IN (feb_sales, mar_sales)) u2\n" +
			"  ON p.jan_sales_SUM = u2.sales_amount\n" +
			"WHERE u1.sales_amount > 10\n" +
			"  AND p.jan_sales_SUM > 0\n" +
			"  AND u2.sales_amount > 10;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		Snippet snippet = extractor.getSnippet();
		assertFatalDiagnosticAtPosition(
				snippet,
				"AMBIGUOUS_DERIVED_COLUMN_REFERENCE",
				"Ambiguous derived column reference 'sales_amount' at (l:1 c:62). Possible sources: [u1, u2]",
				"sales_amount",
				1,
				62);
		assertFatalDiagnosticAtPosition(
				snippet,
				"AMBIGUOUS_DERIVED_COLUMN_REFERENCE",
				"Ambiguous derived column reference 'month_name' at (l:1 c:76). Possible sources: [u1, u2]",
				"month_name",
				1,
				76);
		Assert.assertEquals("AST is wrong",
				"{SQL={select={1={column={name=empid, table_ref=u1_src}, alias=e1}, 2={column={name=jan_sales_SUM, table_ref=null}}, 3={column={name=empid, table_ref=u2_src}, alias=e2}, 4={column={name=sales_amount, table_ref=null}}, 5={column={name=month_name, table_ref=null}}}, from={join={1={unpivot={value={column={name=sales_amount, table_ref=null}}, for={column={name=month_name, table_ref=null}}, in={1={name=jan_sales, table_ref=null}, 2={name=feb_sales, table_ref=null}}}, alias=u1, table={alias=u1_src, table=monthly_sales}}, 2={join=JOIN, on={condition={left={column={name=sales_amount, table_ref=u1}}, right={column={name=jan_sales_SUM, table_ref=p}}, operator==}}}, 3={pivot={value={function={function_name=SUM, parameters={column={name=sales_amount, table_ref=null}}}}, for={column={name=month_name, table_ref=null}}, in={1={pivot_literal='jan_sales'}}}, alias=p, table={alias=p_src, table=monthly_sales_long}}, 4={join=JOIN, on={condition={left={column={name=jan_sales_SUM, table_ref=p}}, right={column={name=sales_amount, table_ref=u2}}, operator==}}}, 5={unpivot={value={column={name=sales_amount, table_ref=null}}, for={column={name=month_name, table_ref=null}}, in={1={name=feb_sales, table_ref=null}, 2={name=mar_sales, table_ref=null}}}, alias=u2, table={alias=u2_src, table=monthly_sales}}}}, where={and={1={condition={left={column={name=sales_amount, table_ref=u1}}, right={literal=10}, operator=>}}, 2={condition={left={column={name=jan_sales_SUM, table_ref=p}}, right={literal=0}, operator=>}}, 3={condition={left={column={name=sales_amount, table_ref=u2}}, right={literal=10}, operator=>}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[jan_sales_SUM, month_name, sales_amount, e1, e2]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong",
				"{monthly_sales={jan_sales=[[@28,154:162='jan_sales',<381>,3:41]], empid=[[@1,7:12='u1_src',<381>,1:7], [@9,42:47='u2_src',<381>,1:42]], mar_sales=[[@71,387:395='mar_sales',<381>,8:52]], feb_sales=[[@69,376:384='feb_sales',<381>,8:41], [@30,165:173='feb_sales',<381>,3:52]]}, monthly_sales_long={month_name=[[@44,239:248='month_name',<381>,5:29]], sales_amount=[[@41,221:232='sales_amount',<381>,5:11]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong",
				"{query1={jan_sales_SUM=[[@7,27:39='jan_sales_SUM',<381>,1:27], [@58,295:307='jan_sales_SUM',<381>,6:25], [@78,409:421='jan_sales_SUM',<381>,9:7], [@92,476:488='jan_sales_SUM',<381>,11:8], [@39,217:219='SUM',<141>,5:7], [@47,254:264=''jan_sales'',<389>,5:44]], month_name=[[@17,76:85='month_name',<381>,1:76]], sales_amount=[[@15,62:73='sales_amount',<381>,1:62], [@54,278:289='sales_amount',<381>,6:8], [@82,428:439='sales_amount',<381>,9:26], [@86,450:461='sales_amount',<381>,10:9], [@98,503:514='sales_amount',<381>,12:9]], e1=[[@5,23:24='e1',<381>,1:23]], e2=[[@13,58:59='e2',<381>,1:58]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong",
				"{def_query1={query_dictionary={jan_sales_SUM=[[@7,27:39='jan_sales_SUM',<381>,1:27], [@58,295:307='jan_sales_SUM',<381>,6:25], [@78,409:421='jan_sales_SUM',<381>,9:7], [@92,476:488='jan_sales_SUM',<381>,11:8], [@39,217:219='SUM',<141>,5:7], [@47,254:264=''jan_sales'',<389>,5:44]], month_name=[[@17,76:85='month_name',<381>,1:76]], sales_amount=[[@15,62:73='sales_amount',<381>,1:62], [@54,278:289='sales_amount',<381>,6:8], [@82,428:439='sales_amount',<381>,9:26], [@86,450:461='sales_amount',<381>,10:9], [@98,503:514='sales_amount',<381>,12:9]], e1=[[@5,23:24='e1',<381>,1:23]], e2=[[@13,58:59='e2',<381>,1:58]]}, table_dictionary={monthly_sales={jan_sales=[[@28,154:162='jan_sales',<381>,3:41]], mar_sales=[[@71,387:395='mar_sales',<381>,8:52]], empid=[[@1,7:12='u1_src',<381>,1:7], [@9,42:47='u2_src',<381>,1:42]], feb_sales=[[@69,376:384='feb_sales',<381>,8:41], [@30,165:173='feb_sales',<381>,3:52]]}, monthly_sales_long={month_name=[[@44,239:248='month_name',<381>,5:29]], sales_amount=[[@41,221:232='sales_amount',<381>,5:11]]}}, derivation={source_columns={p=[{name=month_name, table_ref=p_src}, {name=sales_amount, table_ref=p_src}], u1=[{name=jan_sales, table_ref=u1_src}, {name=feb_sales, table_ref=u1_src}], u2=[{name=feb_sales, table_ref=u2_src}, {name=mar_sales, table_ref=u2_src}]}, derived_columns={p={jan_sales_SUM=[[@39,217:219='SUM',<141>,5:7], [@47,254:264=''jan_sales'',<389>,5:44]]}, u1={sales_amount=[[@23,122:133='sales_amount',<381>,3:9]], month_name=[[@25,139:148='month_name',<381>,3:26]]}, u2={sales_amount=[[@64,344:355='sales_amount',<381>,8:9]], month_name=[[@66,361:370='month_name',<381>,8:26]]}}}, filters=[{name=sales_amount, table_ref=u1}, {name=jan_sales_SUM, table_ref=p}, {name=sales_amount, table_ref=u2}], interface={jan_sales_SUM=[{name=jan_sales_SUM, table_ref=p}, {name=month_name, table_ref=p_src}, {name=sales_amount, table_ref=p_src}], month_name=[{name=month_name, table_ref=null}], sales_amount=[{name=sales_amount, table_ref=null}], e1=[{name=empid, table_ref=u1_src}], e2=[{name=empid, table_ref=u2_src}]}, table_alias={p=p_src, p_src=monthly_sales_long, u2_src=monthly_sales, u1_src=monthly_sales, u1=u1_src, u2=u2_src}}}",
				extractor.getSymbolTable().toString());
	}


	// Phase 17.6.7: triple-tuple joins with subquery-backed FROM arms (paired with physical-table triple tests above).

	private static final String V17_6_7_PIVOT_LONG_SRC =
			"(SELECT empid, month_name, sales_amount FROM monthly_sales_long)";
	private static final String V17_6_7_UNPIVOT_WIDE_SRC =
			"(SELECT empid, jan_sales, feb_sales, mar_sales FROM monthly_sales)";

	/** Phase 17.6.7 paired variant of {@link #triplePivotJoinDerivedColumnsAcrossTuplesV1Test}. */
	@Test
	public void triplePivotJoinDerivedColumnsAcrossTuplesSubqueryFromV17_6_7Test() {
		final String query =
				"SELECT jan_sales_SUM, feb_sales_SUM, mar_sales_SUM\n"
						+ "FROM " + V17_6_7_PIVOT_LONG_SRC + " p_src\n"
						+ "PIVOT (SUM(sales_amount) FOR month_name IN ('jan_sales')) p\n"
						+ "JOIN " + V17_6_7_PIVOT_LONG_SRC + " q_src\n"
						+ "PIVOT (SUM(sales_amount) FOR month_name IN ('feb_sales')) q\n"
						+ "  ON p.jan_sales_SUM = q.feb_sales_SUM\n"
						+ "JOIN " + V17_6_7_PIVOT_LONG_SRC + " r_src\n"
						+ "PIVOT (SUM(sales_amount) FOR month_name IN ('mar_sales')) r\n"
						+ "  ON q.feb_sales_SUM = r.mar_sales_SUM\n"
						+ "WHERE p.jan_sales_SUM > 0\n"
						+ "  AND q.feb_sales_SUM > 0\n"
						+ "  AND r.mar_sales_SUM > 0;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=jan_sales_SUM, table_ref=null}}, 2={column={name=feb_sales_SUM, table_ref=null}}, 3={column={name=mar_sales_SUM, table_ref=null}}}, from={join={1={pivot={value={function={function_name=SUM, parameters={column={name=sales_amount, table_ref=null}}}}, for={column={name=month_name, table_ref=null}}, in={1={pivot_literal='jan_sales'}}}, alias=p, table={alias=p_src, query={select={1={column={name=empid, table_ref=null}}, 2={column={name=month_name, table_ref=null}}, 3={column={name=sales_amount, table_ref=null}}}, from={table={alias=null, table=monthly_sales_long}}}}}, 2={join=JOIN, on={condition={left={column={name=jan_sales_SUM, table_ref=p}}, right={column={name=feb_sales_SUM, table_ref=q}}, operator==}}}, 3={pivot={value={function={function_name=SUM, parameters={column={name=sales_amount, table_ref=null}}}}, for={column={name=month_name, table_ref=null}}, in={1={pivot_literal='feb_sales'}}}, alias=q, table={alias=q_src, query={select={1={column={name=empid, table_ref=null}}, 2={column={name=month_name, table_ref=null}}, 3={column={name=sales_amount, table_ref=null}}}, from={table={alias=null, table=monthly_sales_long}}}}}, 4={join=JOIN, on={condition={left={column={name=feb_sales_SUM, table_ref=q}}, right={column={name=mar_sales_SUM, table_ref=r}}, operator==}}}, 5={pivot={value={function={function_name=SUM, parameters={column={name=sales_amount, table_ref=null}}}}, for={column={name=month_name, table_ref=null}}, in={1={pivot_literal='mar_sales'}}}, alias=r, table={alias=r_src, query={select={1={column={name=empid, table_ref=null}}, 2={column={name=month_name, table_ref=null}}, 3={column={name=sales_amount, table_ref=null}}}, from={table={alias=null, table=monthly_sales_long}}}}}}}, where={and={1={condition={left={column={name=jan_sales_SUM, table_ref=p}}, right={literal=0}, operator=>}}, 2={condition={left={column={name=feb_sales_SUM, table_ref=q}}, right={literal=0}, operator=>}}, 3={condition={left={column={name=mar_sales_SUM, table_ref=r}}, right={literal=0}, operator=>}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[jan_sales_SUM, mar_sales_SUM, feb_sales_SUM]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{p_src={month_name=[[@25,156:165='month_name',<381>,3:29], [@85,467:476='month_name',<381>,8:29]], sales_amount=[[@22,138:149='sales_amount',<381>,3:11], [@82,449:460='sales_amount',<381>,8:11]]}, q_src={month_name=[[@51,292:301='month_name',<381>,5:29]], sales_amount=[[@48,274:285='sales_amount',<381>,5:11]]}, r_src={month_name=[[@85,467:476='month_name',<381>,8:29]], sales_amount=[[@82,449:460='sales_amount',<381>,8:11]]}, monthly_sales_long={empid=[[@9,64:68='empid',<381>,2:13], [@35,200:204='empid',<381>,4:13], [@69,375:379='empid',<381>,7:13]], month_name=[[@11,71:80='month_name',<381>,2:20], [@37,207:216='month_name',<381>,4:20], [@71,382:391='month_name',<381>,7:20]], sales_amount=[[@13,83:94='sales_amount',<381>,2:32], [@39,219:230='sales_amount',<381>,4:32], [@73,394:405='sales_amount',<381>,7:32]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query4={empid=[[@69,375:379='empid',<381>,7:13]], month_name=[[@71,382:391='month_name',<381>,7:20]], sales_amount=[[@73,394:405='sales_amount',<381>,7:32]]}, query6={jan_sales_SUM=[[@1,7:19='jan_sales_SUM',<381>,1:7], [@61,330:342='jan_sales_SUM',<381>,6:7], [@103,545:557='jan_sales_SUM',<381>,10:8], [@20,134:136='SUM',<141>,3:7], [@28,171:181=''jan_sales'',<389>,3:44]], mar_sales_SUM=[[@5,37:49='mar_sales_SUM',<381>,1:37], [@99,523:535='mar_sales_SUM',<381>,9:25], [@115,597:609='mar_sales_SUM',<381>,12:8], [@80,445:447='SUM',<141>,8:7], [@88,482:492=''mar_sales'',<389>,8:44]], feb_sales_SUM=[[@3,22:34='feb_sales_SUM',<381>,1:22], [@65,348:360='feb_sales_SUM',<381>,6:25], [@95,505:517='feb_sales_SUM',<381>,9:7], [@109,571:583='feb_sales_SUM',<381>,11:8], [@46,270:272='SUM',<141>,5:7], [@54,307:317=''feb_sales'',<389>,5:44]]}, query0={empid=[[@9,64:68='empid',<381>,2:13]], month_name=[[@11,71:80='month_name',<381>,2:20]], sales_amount=[[@13,83:94='sales_amount',<381>,2:32]]}, query2={empid=[[@35,200:204='empid',<381>,4:13]], month_name=[[@37,207:216='month_name',<381>,4:20]], sales_amount=[[@39,219:230='sales_amount',<381>,4:32]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query6={query_dictionary={jan_sales_SUM=[[@1,7:19='jan_sales_SUM',<381>,1:7], [@61,330:342='jan_sales_SUM',<381>,6:7], [@103,545:557='jan_sales_SUM',<381>,10:8], [@20,134:136='SUM',<141>,3:7], [@28,171:181=''jan_sales'',<389>,3:44]], mar_sales_SUM=[[@5,37:49='mar_sales_SUM',<381>,1:37], [@99,523:535='mar_sales_SUM',<381>,9:25], [@115,597:609='mar_sales_SUM',<381>,12:8], [@80,445:447='SUM',<141>,8:7], [@88,482:492=''mar_sales'',<389>,8:44]], feb_sales_SUM=[[@3,22:34='feb_sales_SUM',<381>,1:22], [@65,348:360='feb_sales_SUM',<381>,6:25], [@95,505:517='feb_sales_SUM',<381>,9:7], [@109,571:583='feb_sales_SUM',<381>,11:8], [@46,270:272='SUM',<141>,5:7], [@54,307:317=''feb_sales'',<389>,5:44]]}, table_dictionary={p_src={month_name=[[@25,156:165='month_name',<381>,3:29], [@85,467:476='month_name',<381>,8:29]], sales_amount=[[@22,138:149='sales_amount',<381>,3:11], [@82,449:460='sales_amount',<381>,8:11]]}, q_src={month_name=[[@51,292:301='month_name',<381>,5:29]], sales_amount=[[@48,274:285='sales_amount',<381>,5:11]]}, r_src={month_name=[[@85,467:476='month_name',<381>,8:29]], sales_amount=[[@82,449:460='sales_amount',<381>,8:11]]}}, def_query0={query_dictionary={empid=[[@9,64:68='empid',<381>,2:13]], month_name=[[@11,71:80='month_name',<381>,2:20]], sales_amount=[[@13,83:94='sales_amount',<381>,2:32]]}, table_dictionary={monthly_sales_long={empid=[[@9,64:68='empid',<381>,2:13], [@35,200:204='empid',<381>,4:13], [@69,375:379='empid',<381>,7:13]], month_name=[[@11,71:80='month_name',<381>,2:20], [@37,207:216='month_name',<381>,4:20], [@71,382:391='month_name',<381>,7:20]], sales_amount=[[@13,83:94='sales_amount',<381>,2:32], [@39,219:230='sales_amount',<381>,4:32], [@73,394:405='sales_amount',<381>,7:32]]}}, interface={empid=[{name=empid, table_ref=monthly_sales_long}], month_name=[{name=month_name, table_ref=monthly_sales_long}], sales_amount=[{name=sales_amount, table_ref=monthly_sales_long}]}}, derivation={source_columns={p=[{name=month_name, table_ref=p_src}, {name=sales_amount, table_ref=p_src}], q=[{name=month_name, table_ref=q_src}, {name=sales_amount, table_ref=q_src}], r=[{name=month_name, table_ref=r_src}, {name=sales_amount, table_ref=r_src}]}, derived_columns={p={jan_sales_SUM=[[@20,134:136='SUM',<141>,3:7], [@28,171:181=''jan_sales'',<389>,3:44]]}, q={feb_sales_SUM=[[@46,270:272='SUM',<141>,5:7], [@54,307:317=''feb_sales'',<389>,5:44]]}, r={mar_sales_SUM=[[@80,445:447='SUM',<141>,8:7], [@88,482:492=''mar_sales'',<389>,8:44]]}}}, filters=[{name=jan_sales_SUM, table_ref=p}, {name=feb_sales_SUM, table_ref=q}, {name=mar_sales_SUM, table_ref=r}], interface={jan_sales_SUM=[{name=jan_sales_SUM, table_ref=p}, {name=month_name, table_ref=p_src}, {name=sales_amount, table_ref=p_src}], mar_sales_SUM=[{name=mar_sales_SUM, table_ref=r}, {name=month_name, table_ref=r_src}, {name=sales_amount, table_ref=r_src}], feb_sales_SUM=[{name=feb_sales_SUM, table_ref=q}, {name=month_name, table_ref=q_src}, {name=sales_amount, table_ref=q_src}]}, def_query4={query_dictionary={empid=[[@69,375:379='empid',<381>,7:13]], month_name=[[@71,382:391='month_name',<381>,7:20]], sales_amount=[[@73,394:405='sales_amount',<381>,7:32]]}, table_dictionary={monthly_sales_long={empid=[[@69,375:379='empid',<381>,7:13]], month_name=[[@71,382:391='month_name',<381>,7:20]], sales_amount=[[@73,394:405='sales_amount',<381>,7:32]]}}, interface={empid=[{name=empid, table_ref=monthly_sales_long}], month_name=[{name=month_name, table_ref=monthly_sales_long}], sales_amount=[{name=sales_amount, table_ref=monthly_sales_long}]}}, table_alias={p_src=query0, q_src=query2, r_src=query4, p=p_src, q=q_src, r=r_src}, def_query2={query_dictionary={empid=[[@35,200:204='empid',<381>,4:13]], month_name=[[@37,207:216='month_name',<381>,4:20]], sales_amount=[[@39,219:230='sales_amount',<381>,4:32]]}, table_dictionary={monthly_sales_long={empid=[[@35,200:204='empid',<381>,4:13]], month_name=[[@37,207:216='month_name',<381>,4:20]], sales_amount=[[@39,219:230='sales_amount',<381>,4:32]]}}, interface={empid=[{name=empid, table_ref=monthly_sales_long}], month_name=[{name=month_name, table_ref=monthly_sales_long}], sales_amount=[{name=sales_amount, table_ref=monthly_sales_long}]}}}}",
				extractor.getSymbolTable().toString());
	}

	/** Phase 17.6.7 paired variant of {@link #triplePivotJoinDerivedColumnsSameOutputSelectAmbiguousV17_6_3Test}. */
	@Test
	public void triplePivotJoinDerivedColumnsSameOutputSelectAmbiguousSubqueryFromV17_6_7Test() {
		final String query =
				"SELECT p_src.empid AS e1, q_src.empid AS e2, r_src.empid AS e3, jan_sales_SUM\n"
						+ "FROM " + V17_6_7_PIVOT_LONG_SRC + " p_src\n"
						+ "PIVOT (SUM(sales_amount) FOR month_name IN ('jan_sales')) p\n"
						+ "JOIN " + V17_6_7_PIVOT_LONG_SRC + " q_src\n"
						+ "PIVOT (SUM(sales_amount) FOR month_name IN ('jan_sales')) q\n"
						+ "  ON p.jan_sales_SUM = q.jan_sales_SUM\n"
						+ "JOIN " + V17_6_7_PIVOT_LONG_SRC + " r_src\n"
						+ "PIVOT (SUM(sales_amount) FOR month_name IN ('jan_sales')) r\n"
						+ "  ON q.jan_sales_SUM = r.jan_sales_SUM\n"
						+ "WHERE p.jan_sales_SUM > 0\n"
						+ "  AND q.jan_sales_SUM > 0\n"
						+ "  AND r.jan_sales_SUM > 0;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		Snippet snippet = extractor.getSnippet();
		assertFatalDiagnosticAtPosition(
				snippet,
				"AMBIGUOUS_DERIVED_COLUMN_REFERENCE",
				"Ambiguous derived column reference 'jan_sales_SUM' at (l:1 c:64). Possible sources: [p, q, r]",
				"jan_sales_SUM",
				1,
				64);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=empid, table_ref=p_src}, alias=e1}, 2={column={name=empid, table_ref=q_src}, alias=e2}, 3={column={name=empid, table_ref=r_src}, alias=e3}, 4={column={name=jan_sales_SUM, table_ref=null}}}, from={join={1={pivot={value={function={function_name=SUM, parameters={column={name=sales_amount, table_ref=null}}}}, for={column={name=month_name, table_ref=null}}, in={1={pivot_literal='jan_sales'}}}, alias=p, table={alias=p_src, query={select={1={column={name=empid, table_ref=null}}, 2={column={name=month_name, table_ref=null}}, 3={column={name=sales_amount, table_ref=null}}}, from={table={alias=null, table=monthly_sales_long}}}}}, 2={join=JOIN, on={condition={left={column={name=jan_sales_SUM, table_ref=p}}, right={column={name=jan_sales_SUM, table_ref=q}}, operator==}}}, 3={pivot={value={function={function_name=SUM, parameters={column={name=sales_amount, table_ref=null}}}}, for={column={name=month_name, table_ref=null}}, in={1={pivot_literal='jan_sales'}}}, alias=q, table={alias=q_src, query={select={1={column={name=empid, table_ref=null}}, 2={column={name=month_name, table_ref=null}}, 3={column={name=sales_amount, table_ref=null}}}, from={table={alias=null, table=monthly_sales_long}}}}}, 4={join=JOIN, on={condition={left={column={name=jan_sales_SUM, table_ref=q}}, right={column={name=jan_sales_SUM, table_ref=r}}, operator==}}}, 5={pivot={value={function={function_name=SUM, parameters={column={name=sales_amount, table_ref=null}}}}, for={column={name=month_name, table_ref=null}}, in={1={pivot_literal='jan_sales'}}}, alias=r, table={alias=r_src, query={select={1={column={name=empid, table_ref=null}}, 2={column={name=month_name, table_ref=null}}, 3={column={name=sales_amount, table_ref=null}}}, from={table={alias=null, table=monthly_sales_long}}}}}}}, where={and={1={condition={left={column={name=jan_sales_SUM, table_ref=p}}, right={literal=0}, operator=>}}, 2={condition={left={column={name=jan_sales_SUM, table_ref=q}}, right={literal=0}, operator=>}}, 3={condition={left={column={name=jan_sales_SUM, table_ref=r}}, right={literal=0}, operator=>}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[jan_sales_SUM, e1, e2, e3]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{p_src={month_name=[[@39,183:192='month_name',<381>,3:29], [@99,494:503='month_name',<381>,8:29]], sales_amount=[[@36,165:176='sales_amount',<381>,3:11], [@96,476:487='sales_amount',<381>,8:11]]}, q_src={month_name=[[@65,319:328='month_name',<381>,5:29]], sales_amount=[[@62,301:312='sales_amount',<381>,5:11]]}, r_src={month_name=[[@99,494:503='month_name',<381>,8:29]], sales_amount=[[@96,476:487='sales_amount',<381>,8:11]]}, monthly_sales_long={empid=[[@23,91:95='empid',<381>,2:13], [@49,227:231='empid',<381>,4:13], [@83,402:406='empid',<381>,7:13]], month_name=[[@25,98:107='month_name',<381>,2:20], [@51,234:243='month_name',<381>,4:20], [@85,409:418='month_name',<381>,7:20]], sales_amount=[[@27,110:121='sales_amount',<381>,2:32], [@53,246:257='sales_amount',<381>,4:32], [@87,421:432='sales_amount',<381>,7:32]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query4={empid=[[@83,402:406='empid',<381>,7:13], [@13,45:49='r_src',<381>,1:45]], month_name=[[@85,409:418='month_name',<381>,7:20]], sales_amount=[[@87,421:432='sales_amount',<381>,7:32]]}, query6={jan_sales_SUM=[[@19,64:76='jan_sales_SUM',<381>,1:64], [@75,357:369='jan_sales_SUM',<381>,6:7], [@79,375:387='jan_sales_SUM',<381>,6:25], [@109,532:544='jan_sales_SUM',<381>,9:7], [@113,550:562='jan_sales_SUM',<381>,9:25], [@117,572:584='jan_sales_SUM',<381>,10:8], [@123,598:610='jan_sales_SUM',<381>,11:8], [@129,624:636='jan_sales_SUM',<381>,12:8]], e1=[[@5,22:23='e1',<381>,1:22]], e2=[[@11,41:42='e2',<381>,1:41]], e3=[[@17,60:61='e3',<381>,1:60]]}, query0={empid=[[@23,91:95='empid',<381>,2:13], [@1,7:11='p_src',<381>,1:7]], month_name=[[@25,98:107='month_name',<381>,2:20]], sales_amount=[[@27,110:121='sales_amount',<381>,2:32]]}, query2={empid=[[@49,227:231='empid',<381>,4:13], [@7,26:30='q_src',<381>,1:26]], month_name=[[@51,234:243='month_name',<381>,4:20]], sales_amount=[[@53,246:257='sales_amount',<381>,4:32]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query6={query_dictionary={jan_sales_SUM=[[@19,64:76='jan_sales_SUM',<381>,1:64], [@75,357:369='jan_sales_SUM',<381>,6:7], [@79,375:387='jan_sales_SUM',<381>,6:25], [@109,532:544='jan_sales_SUM',<381>,9:7], [@113,550:562='jan_sales_SUM',<381>,9:25], [@117,572:584='jan_sales_SUM',<381>,10:8], [@123,598:610='jan_sales_SUM',<381>,11:8], [@129,624:636='jan_sales_SUM',<381>,12:8]], e1=[[@5,22:23='e1',<381>,1:22]], e2=[[@11,41:42='e2',<381>,1:41]], e3=[[@17,60:61='e3',<381>,1:60]]}, table_dictionary={p_src={month_name=[[@39,183:192='month_name',<381>,3:29], [@99,494:503='month_name',<381>,8:29]], sales_amount=[[@36,165:176='sales_amount',<381>,3:11], [@96,476:487='sales_amount',<381>,8:11]]}, q_src={month_name=[[@65,319:328='month_name',<381>,5:29]], sales_amount=[[@62,301:312='sales_amount',<381>,5:11]]}, r_src={month_name=[[@99,494:503='month_name',<381>,8:29]], sales_amount=[[@96,476:487='sales_amount',<381>,8:11]]}}, def_query0={query_dictionary={empid=[[@23,91:95='empid',<381>,2:13], [@1,7:11='p_src',<381>,1:7]], month_name=[[@25,98:107='month_name',<381>,2:20]], sales_amount=[[@27,110:121='sales_amount',<381>,2:32]]}, table_dictionary={monthly_sales_long={empid=[[@23,91:95='empid',<381>,2:13], [@49,227:231='empid',<381>,4:13], [@83,402:406='empid',<381>,7:13]], month_name=[[@25,98:107='month_name',<381>,2:20], [@51,234:243='month_name',<381>,4:20], [@85,409:418='month_name',<381>,7:20]], sales_amount=[[@27,110:121='sales_amount',<381>,2:32], [@53,246:257='sales_amount',<381>,4:32], [@87,421:432='sales_amount',<381>,7:32]]}}, interface={empid=[{name=empid, table_ref=monthly_sales_long}], month_name=[{name=month_name, table_ref=monthly_sales_long}], sales_amount=[{name=sales_amount, table_ref=monthly_sales_long}]}}, derivation={source_columns={p=[{name=month_name, table_ref=p_src}, {name=sales_amount, table_ref=p_src}], q=[{name=month_name, table_ref=q_src}, {name=sales_amount, table_ref=q_src}], r=[{name=month_name, table_ref=r_src}, {name=sales_amount, table_ref=r_src}]}, derived_columns={p={jan_sales_SUM=[[@34,161:163='SUM',<141>,3:7], [@42,198:208=''jan_sales'',<389>,3:44]]}, q={jan_sales_SUM=[[@60,297:299='SUM',<141>,5:7], [@68,334:344=''jan_sales'',<389>,5:44]]}, r={jan_sales_SUM=[[@94,472:474='SUM',<141>,8:7], [@102,509:519=''jan_sales'',<389>,8:44]]}}}, filters=[{name=jan_sales_SUM, table_ref=p}, {name=jan_sales_SUM, table_ref=q}, {name=jan_sales_SUM, table_ref=r}], interface={jan_sales_SUM=[{name=jan_sales_SUM, table_ref=null}], e1=[{name=empid, table_ref=p_src}], e2=[{name=empid, table_ref=q_src}], e3=[{name=empid, table_ref=r_src}]}, def_query4={query_dictionary={empid=[[@83,402:406='empid',<381>,7:13], [@13,45:49='r_src',<381>,1:45]], month_name=[[@85,409:418='month_name',<381>,7:20]], sales_amount=[[@87,421:432='sales_amount',<381>,7:32]]}, table_dictionary={monthly_sales_long={empid=[[@83,402:406='empid',<381>,7:13]], month_name=[[@85,409:418='month_name',<381>,7:20]], sales_amount=[[@87,421:432='sales_amount',<381>,7:32]]}}, interface={empid=[{name=empid, table_ref=monthly_sales_long}], month_name=[{name=month_name, table_ref=monthly_sales_long}], sales_amount=[{name=sales_amount, table_ref=monthly_sales_long}]}}, table_alias={p_src=query0, q_src=query2, r_src=query4, p=p_src, q=q_src, r=r_src}, def_query2={query_dictionary={empid=[[@49,227:231='empid',<381>,4:13], [@7,26:30='q_src',<381>,1:26]], month_name=[[@51,234:243='month_name',<381>,4:20]], sales_amount=[[@53,246:257='sales_amount',<381>,4:32]]}, table_dictionary={monthly_sales_long={empid=[[@49,227:231='empid',<381>,4:13]], month_name=[[@51,234:243='month_name',<381>,4:20]], sales_amount=[[@53,246:257='sales_amount',<381>,4:32]]}}, interface={empid=[{name=empid, table_ref=monthly_sales_long}], month_name=[{name=month_name, table_ref=monthly_sales_long}], sales_amount=[{name=sales_amount, table_ref=monthly_sales_long}]}}}}",
				extractor.getSymbolTable().toString());
	}

	/** Phase 17.6.7 paired variant of {@link #tripleUnpivotJoinDerivedColumnsAcrossTuplesV1Test}. */
	@Test
	public void tripleUnpivotJoinDerivedColumnsAcrossTuplesSubqueryFromV17_6_7Test() {
		final String query =
				"SELECT m1.empid AS e1, m2.empid AS e2, m3.empid AS e3, sales_amount, month_name\n"
						+ "FROM " + V17_6_7_UNPIVOT_WIDE_SRC + " m1\n"
						+ "UNPIVOT (sales_amount FOR month_name IN (jan_sales, feb_sales)) u1\n"
						+ "JOIN " + V17_6_7_UNPIVOT_WIDE_SRC + " m2\n"
						+ "UNPIVOT (sales_amount FOR month_name IN (jan_sales, feb_sales)) u2\n"
						+ "  ON u1.month_name = u2.month_name AND u1.sales_amount = u2.sales_amount\n"
						+ "JOIN " + V17_6_7_UNPIVOT_WIDE_SRC + " m3\n"
						+ "UNPIVOT (sales_amount FOR month_name IN (jan_sales, mar_sales)) u3\n"
						+ "  ON u2.month_name = u3.month_name\n"
						+ "WHERE u1.sales_amount > 10\n"
						+ "  AND u2.sales_amount > 10\n"
						+ "  AND u3.sales_amount > 10;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		Snippet snippet = extractor.getSnippet();
		assertFatalDiagnosticAtPosition(
				snippet,
				"AMBIGUOUS_DERIVED_COLUMN_REFERENCE",
				"Ambiguous derived column reference 'month_name' at (l:1 c:69). Possible sources: [u1, u2, u3]",
				"month_name",
				1,
				69);
		assertFatalDiagnosticAtPosition(
				snippet,
				"AMBIGUOUS_DERIVED_COLUMN_REFERENCE",
				"Ambiguous derived column reference 'sales_amount' at (l:1 c:55). Possible sources: [u1, u2, u3]",
				"sales_amount",
				1,
				55);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=empid, table_ref=m1}, alias=e1}, 2={column={name=empid, table_ref=m2}, alias=e2}, 3={column={name=empid, table_ref=m3}, alias=e3}, 4={column={name=sales_amount, table_ref=null}}, 5={column={name=month_name, table_ref=null}}}, from={join={1={unpivot={value={column={name=sales_amount, table_ref=null}}, for={column={name=month_name, table_ref=null}}, in={1={name=jan_sales, table_ref=null}, 2={name=feb_sales, table_ref=null}}}, alias=u1, table={alias=m1, query={select={1={column={name=empid, table_ref=null}}, 2={column={name=jan_sales, table_ref=null}}, 3={column={name=feb_sales, table_ref=null}}, 4={column={name=mar_sales, table_ref=null}}}, from={table={alias=null, table=monthly_sales}}}}}, 2={join=JOIN, on={and={1={condition={left={column={name=month_name, table_ref=u1}}, right={column={name=month_name, table_ref=u2}}, operator==}}, 2={condition={left={column={name=sales_amount, table_ref=u1}}, right={column={name=sales_amount, table_ref=u2}}, operator==}}}}}, 3={unpivot={value={column={name=sales_amount, table_ref=null}}, for={column={name=month_name, table_ref=null}}, in={1={name=jan_sales, table_ref=null}, 2={name=feb_sales, table_ref=null}}}, alias=u2, table={alias=m2, query={select={1={column={name=empid, table_ref=null}}, 2={column={name=jan_sales, table_ref=null}}, 3={column={name=feb_sales, table_ref=null}}, 4={column={name=mar_sales, table_ref=null}}}, from={table={alias=null, table=monthly_sales}}}}}, 4={join=JOIN, on={condition={left={column={name=month_name, table_ref=u2}}, right={column={name=month_name, table_ref=u3}}, operator==}}}, 5={unpivot={value={column={name=sales_amount, table_ref=null}}, for={column={name=month_name, table_ref=null}}, in={1={name=jan_sales, table_ref=null}, 2={name=mar_sales, table_ref=null}}}, alias=u3, table={alias=m3, query={select={1={column={name=empid, table_ref=null}}, 2={column={name=jan_sales, table_ref=null}}, 3={column={name=feb_sales, table_ref=null}}, 4={column={name=mar_sales, table_ref=null}}}, from={table={alias=null, table=monthly_sales}}}}}}}, where={and={1={condition={left={column={name=sales_amount, table_ref=u1}}, right={literal=10}, operator=>}}, 2={condition={left={column={name=sales_amount, table_ref=u2}}, right={literal=10}, operator=>}}, 3={condition={left={column={name=sales_amount, table_ref=u3}}, right={literal=10}, operator=>}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[month_name, sales_amount, e1, e2, e3]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{monthly_sales={jan_sales=[[@27,100:108='jan_sales',<381>,2:20], [@54,242:250='jan_sales',<381>,4:20], [@97,457:465='jan_sales',<381>,7:20]], empid=[[@25,93:97='empid',<381>,2:13], [@52,235:239='empid',<381>,4:13], [@95,450:454='empid',<381>,7:13]], mar_sales=[[@31,122:130='mar_sales',<381>,2:42], [@58,264:272='mar_sales',<381>,4:42], [@101,479:487='mar_sales',<381>,7:42]], feb_sales=[[@29,111:119='feb_sales',<381>,2:31], [@56,253:261='feb_sales',<381>,4:31], [@99,468:476='feb_sales',<381>,7:31]]}, m1={jan_sales=[[@43,196:204='jan_sales',<381>,3:41]], feb_sales=[[@45,207:215='feb_sales',<381>,3:52]]}, m2={jan_sales=[[@70,338:346='jan_sales',<381>,5:41]], feb_sales=[[@72,349:357='feb_sales',<381>,5:52]]}, m3={jan_sales=[[@113,553:561='jan_sales',<381>,8:41]], mar_sales=[[@115,564:572='mar_sales',<381>,8:52]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={jan_sales=[[@27,100:108='jan_sales',<381>,2:20]], empid=[[@25,93:97='empid',<381>,2:13], [@1,7:8='m1',<381>,1:7]], mar_sales=[[@31,122:130='mar_sales',<381>,2:42]], feb_sales=[[@29,111:119='feb_sales',<381>,2:31]]}, query1={jan_sales=[[@54,242:250='jan_sales',<381>,4:20]], empid=[[@52,235:239='empid',<381>,4:13], [@7,23:24='m2',<381>,1:23]], mar_sales=[[@58,264:272='mar_sales',<381>,4:42]], feb_sales=[[@56,253:261='feb_sales',<381>,4:31]]}, query2={jan_sales=[[@97,457:465='jan_sales',<381>,7:20]], empid=[[@95,450:454='empid',<381>,7:13], [@13,39:40='m3',<381>,1:39]], mar_sales=[[@101,479:487='mar_sales',<381>,7:42]], feb_sales=[[@99,468:476='feb_sales',<381>,7:31]]}, query3={month_name=[[@21,69:78='month_name',<381>,1:69], [@79,372:381='month_name',<381>,6:8], [@83,388:397='month_name',<381>,6:24], [@122,587:596='month_name',<381>,9:8], [@126,603:612='month_name',<381>,9:24]], sales_amount=[[@19,55:66='sales_amount',<381>,1:55], [@87,406:417='sales_amount',<381>,6:42], [@91,424:435='sales_amount',<381>,6:60], [@130,623:634='sales_amount',<381>,10:9], [@136,650:661='sales_amount',<381>,11:9], [@142,677:688='sales_amount',<381>,12:9]], e1=[[@5,19:20='e1',<381>,1:19]], e2=[[@11,35:36='e2',<381>,1:35]], e3=[[@17,51:52='e3',<381>,1:51]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query3={query_dictionary={month_name=[[@21,69:78='month_name',<381>,1:69], [@79,372:381='month_name',<381>,6:8], [@83,388:397='month_name',<381>,6:24], [@122,587:596='month_name',<381>,9:8], [@126,603:612='month_name',<381>,9:24]], sales_amount=[[@19,55:66='sales_amount',<381>,1:55], [@87,406:417='sales_amount',<381>,6:42], [@91,424:435='sales_amount',<381>,6:60], [@130,623:634='sales_amount',<381>,10:9], [@136,650:661='sales_amount',<381>,11:9], [@142,677:688='sales_amount',<381>,12:9]], e1=[[@5,19:20='e1',<381>,1:19]], e2=[[@11,35:36='e2',<381>,1:35]], e3=[[@17,51:52='e3',<381>,1:51]]}, table_dictionary={m1={jan_sales=[[@43,196:204='jan_sales',<381>,3:41]], feb_sales=[[@45,207:215='feb_sales',<381>,3:52]]}, m2={jan_sales=[[@70,338:346='jan_sales',<381>,5:41]], feb_sales=[[@72,349:357='feb_sales',<381>,5:52]]}, m3={jan_sales=[[@113,553:561='jan_sales',<381>,8:41]], mar_sales=[[@115,564:572='mar_sales',<381>,8:52]]}}, def_query1={query_dictionary={jan_sales=[[@54,242:250='jan_sales',<381>,4:20]], empid=[[@52,235:239='empid',<381>,4:13], [@7,23:24='m2',<381>,1:23]], mar_sales=[[@58,264:272='mar_sales',<381>,4:42]], feb_sales=[[@56,253:261='feb_sales',<381>,4:31]]}, table_dictionary={monthly_sales={jan_sales=[[@54,242:250='jan_sales',<381>,4:20]], empid=[[@52,235:239='empid',<381>,4:13]], mar_sales=[[@58,264:272='mar_sales',<381>,4:42]], feb_sales=[[@56,253:261='feb_sales',<381>,4:31]]}}, interface={jan_sales=[{name=jan_sales, table_ref=monthly_sales}], empid=[{name=empid, table_ref=monthly_sales}], mar_sales=[{name=mar_sales, table_ref=monthly_sales}], feb_sales=[{name=feb_sales, table_ref=monthly_sales}]}}, def_query0={query_dictionary={jan_sales=[[@27,100:108='jan_sales',<381>,2:20]], empid=[[@25,93:97='empid',<381>,2:13], [@1,7:8='m1',<381>,1:7]], mar_sales=[[@31,122:130='mar_sales',<381>,2:42]], feb_sales=[[@29,111:119='feb_sales',<381>,2:31]]}, table_dictionary={monthly_sales={jan_sales=[[@27,100:108='jan_sales',<381>,2:20], [@54,242:250='jan_sales',<381>,4:20], [@97,457:465='jan_sales',<381>,7:20]], empid=[[@25,93:97='empid',<381>,2:13], [@52,235:239='empid',<381>,4:13], [@95,450:454='empid',<381>,7:13]], mar_sales=[[@31,122:130='mar_sales',<381>,2:42], [@58,264:272='mar_sales',<381>,4:42], [@101,479:487='mar_sales',<381>,7:42]], feb_sales=[[@29,111:119='feb_sales',<381>,2:31], [@56,253:261='feb_sales',<381>,4:31], [@99,468:476='feb_sales',<381>,7:31]]}}, interface={jan_sales=[{name=jan_sales, table_ref=monthly_sales}], empid=[{name=empid, table_ref=monthly_sales}], mar_sales=[{name=mar_sales, table_ref=monthly_sales}], feb_sales=[{name=feb_sales, table_ref=monthly_sales}]}}, derivation={source_columns={u1=[{name=jan_sales, table_ref=m1}, {name=feb_sales, table_ref=m1}], u2=[{name=jan_sales, table_ref=m2}, {name=feb_sales, table_ref=m2}], u3=[{name=jan_sales, table_ref=m3}, {name=mar_sales, table_ref=m3}]}, derived_columns={u1={sales_amount=[[@38,164:175='sales_amount',<381>,3:9]], month_name=[[@40,181:190='month_name',<381>,3:26]]}, u2={sales_amount=[[@65,306:317='sales_amount',<381>,5:9]], month_name=[[@67,323:332='month_name',<381>,5:26]]}, u3={sales_amount=[[@108,521:532='sales_amount',<381>,8:9]], month_name=[[@110,538:547='month_name',<381>,8:26]]}}}, filters=[{name=month_name, table_ref=u1}, {name=month_name, table_ref=u2}, {name=sales_amount, table_ref=u1}, {name=sales_amount, table_ref=u2}, {name=month_name, table_ref=u3}, {name=sales_amount, table_ref=u3}], interface={month_name=[{name=month_name, table_ref=null}], sales_amount=[{name=sales_amount, table_ref=null}], e1=[{name=empid, table_ref=m1}], e2=[{name=empid, table_ref=m2}], e3=[{name=empid, table_ref=m3}]}, table_alias={m1=query0, m2=query1, m3=query2, u1=m1, u2=m2, u3=m3}, def_query2={query_dictionary={jan_sales=[[@97,457:465='jan_sales',<381>,7:20]], empid=[[@95,450:454='empid',<381>,7:13], [@13,39:40='m3',<381>,1:39]], mar_sales=[[@101,479:487='mar_sales',<381>,7:42]], feb_sales=[[@99,468:476='feb_sales',<381>,7:31]]}, table_dictionary={monthly_sales={jan_sales=[[@97,457:465='jan_sales',<381>,7:20]], empid=[[@95,450:454='empid',<381>,7:13]], mar_sales=[[@101,479:487='mar_sales',<381>,7:42]], feb_sales=[[@99,468:476='feb_sales',<381>,7:31]]}}, interface={jan_sales=[{name=jan_sales, table_ref=monthly_sales}], empid=[{name=empid, table_ref=monthly_sales}], mar_sales=[{name=mar_sales, table_ref=monthly_sales}], feb_sales=[{name=feb_sales, table_ref=monthly_sales}]}}}}",
				extractor.getSymbolTable().toString());
	}

	/** Phase 17.6.7 paired variant of {@link #triplePivotUnpivotPivotJoinDerivedColumnsV1Test}. */
	@Test
	public void triplePivotUnpivotPivotJoinDerivedColumnsSubqueryFromV17_6_7Test() {
		final String query =
				"SELECT jan_sales_SUM, sales_amount, month_name, feb_sales_SUM\n"
						+ "FROM " + V17_6_7_PIVOT_LONG_SRC + " p_src\n"
						+ "PIVOT (SUM(sales_amount) FOR month_name IN ('jan_sales', 'feb_sales')) p\n"
						+ "JOIN " + V17_6_7_UNPIVOT_WIDE_SRC + " u_src\n"
						+ "UNPIVOT (sales_amount FOR month_name IN (jan_sales, feb_sales)) u\n"
						+ "  ON p.jan_sales_SUM = u.sales_amount AND u.month_name = 'jan_sales'\n"
						+ "JOIN " + V17_6_7_PIVOT_LONG_SRC + " q_src\n"
						+ "PIVOT (SUM(sales_amount) FOR month_name IN ('feb_sales')) q\n"
						+ "  ON u.sales_amount = q.feb_sales_SUM\n"
						+ "WHERE p.jan_sales_SUM > 0\n"
						+ "  AND u.sales_amount > 0 or sales_amount < 10\n"
						+ "  AND feb_sales_SUM > 0 and month_name != 'jan_sales'\n"
						+ "ORDER BY p.jan_sales_SUM, q.feb_sales_SUM, month_name, sales_amount;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		Snippet snippet = extractor.getSnippet();
		assertFatalDiagnosticAtPosition(
				snippet,
				"AMBIGUOUS_DERIVED_COLUMN_REFERENCE",
				"Ambiguous derived column reference 'feb_sales_SUM' at (l:1 c:48). Possible sources: [p, q]",
				null,
				1,
				48);
		assertFatalDiagnosticAtPosition(
				snippet,
				"AMBIGUOUS_DERIVED_COLUMN_REFERENCE",
				"Ambiguous derived column reference 'feb_sales_SUM' at (l:12 c:6). Possible sources: [p, q]",
				null,
				12,
				6);
		assertDiagnosticAtPosition(
				snippet,
				"AMBIGUOUS_COLUMN_REFERENCE",
				ParseDiagnostic.Severity.SEVERE_WARNING,
				"Ambiguous column reference 'sales_amount' at (l:1 c:22). Possible sources: [p, q]",
				"sales_amount",
				1,
				22);
		assertDiagnosticAtPosition(
				snippet,
				"AMBIGUOUS_COLUMN_REFERENCE",
				ParseDiagnostic.Severity.SEVERE_WARNING,
				"Ambiguous column reference 'month_name' at (l:1 c:36). Possible sources: [p, q]",
				"month_name",
				1,
				36);
		assertDiagnosticAtPosition(
				snippet,
				"AMBIGUOUS_COLUMN_REFERENCE",
				ParseDiagnostic.Severity.SEVERE_WARNING,
				"Ambiguous column reference 'sales_amount' at (l:11 c:28). Possible sources: [p, q]",
				null,
				11,
				28);
		assertDiagnosticAtPosition(
				snippet,
				"AMBIGUOUS_COLUMN_REFERENCE",
				ParseDiagnostic.Severity.SEVERE_WARNING,
				"Ambiguous column reference 'month_name' at (l:12 c:28). Possible sources: [p, q]",
				null,
				12,
				28);
		assertDiagnosticAtPosition(
				snippet,
				"AMBIGUOUS_COLUMN_REFERENCE",
				ParseDiagnostic.Severity.SEVERE_WARNING,
				"Ambiguous column reference 'month_name' at (l:13 c:43). Possible sources: [p, q]",
				null,
				13,
				43);
		assertDiagnosticAtPosition(
				snippet,
				"AMBIGUOUS_COLUMN_REFERENCE",
				ParseDiagnostic.Severity.SEVERE_WARNING,
				"Ambiguous column reference 'sales_amount' at (l:13 c:55). Possible sources: [p, q]",
				null,
				13,
				55);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=jan_sales_SUM, table_ref=null}}, 2={column={name=sales_amount, table_ref=null}}, 3={column={name=month_name, table_ref=null}}, 4={column={name=feb_sales_SUM, table_ref=null}}}, orderby={1={null_order=null, predicand={column={name=jan_sales_SUM, table_ref=p}}, sort_order=ASC}, 2={null_order=null, predicand={column={name=feb_sales_SUM, table_ref=q}}, sort_order=ASC}, 3={null_order=null, predicand={column={name=month_name, table_ref=null}}, sort_order=ASC}, 4={null_order=null, predicand={column={name=sales_amount, table_ref=null}}, sort_order=ASC}}, from={join={1={pivot={value={function={function_name=SUM, parameters={column={name=sales_amount, table_ref=null}}}}, for={column={name=month_name, table_ref=null}}, in={1={pivot_literal='jan_sales'}, 2={pivot_literal='feb_sales'}}}, alias=p, table={alias=p_src, query={select={1={column={name=empid, table_ref=null}}, 2={column={name=month_name, table_ref=null}}, 3={column={name=sales_amount, table_ref=null}}}, from={table={alias=null, table=monthly_sales_long}}}}}, 2={join=JOIN, on={and={1={condition={left={column={name=jan_sales_SUM, table_ref=p}}, right={column={name=sales_amount, table_ref=u}}, operator==}}, 2={condition={left={column={name=month_name, table_ref=u}}, right={literal='jan_sales'}, operator==}}}}}, 3={unpivot={value={column={name=sales_amount, table_ref=null}}, for={column={name=month_name, table_ref=null}}, in={1={name=jan_sales, table_ref=null}, 2={name=feb_sales, table_ref=null}}}, alias=u, table={alias=u_src, query={select={1={column={name=empid, table_ref=null}}, 2={column={name=jan_sales, table_ref=null}}, 3={column={name=feb_sales, table_ref=null}}, 4={column={name=mar_sales, table_ref=null}}}, from={table={alias=null, table=monthly_sales}}}}}, 4={join=JOIN, on={condition={left={column={name=sales_amount, table_ref=u}}, right={column={name=feb_sales_SUM, table_ref=q}}, operator==}}}, 5={pivot={value={function={function_name=SUM, parameters={column={name=sales_amount, table_ref=null}}}}, for={column={name=month_name, table_ref=null}}, in={1={pivot_literal='feb_sales'}}}, alias=q, table={alias=q_src, query={select={1={column={name=empid, table_ref=null}}, 2={column={name=month_name, table_ref=null}}, 3={column={name=sales_amount, table_ref=null}}}, from={table={alias=null, table=monthly_sales_long}}}}}}}, where={or={1={and={1={condition={left={column={name=jan_sales_SUM, table_ref=p}}, right={literal=0}, operator=>}}, 2={condition={left={column={name=sales_amount, table_ref=u}}, right={literal=0}, operator=>}}}}, 2={and={1={condition={left={column={name=sales_amount, table_ref=null}}, right={literal=10}, operator=<}}, 2={condition={left={column={name=feb_sales_SUM, table_ref=null}}, right={literal=0}, operator=>}}, 3={condition={left={column={name=month_name, table_ref=null}}, right={literal='jan_sales'}, operator=!=}}}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[jan_sales_SUM, month_name, sales_amount, feb_sales_SUM]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{p_src={month_name=[[@27,167:176='month_name',<381>,3:29], [@96,529:538='month_name',<381>,8:29], [@132,698:707='month_name',<381>,12:28], [@145,767:776='month_name',<381>,13:43]], sales_amount=[[@24,149:160='sales_amount',<381>,3:11], [@93,511:522='sales_amount',<381>,8:11], [@124,652:663='sales_amount',<381>,11:28], [@147,779:790='sales_amount',<381>,13:55]]}, monthly_sales={jan_sales=[[@41,231:239='jan_sales',<381>,4:20]], empid=[[@39,224:228='empid',<381>,4:13]], mar_sales=[[@45,253:261='mar_sales',<381>,4:42]], feb_sales=[[@43,242:250='feb_sales',<381>,4:31]]}, q_src={month_name=[[@96,529:538='month_name',<381>,8:29]], sales_amount=[[@93,511:522='sales_amount',<381>,8:11]]}, u_src={jan_sales=[[@57,330:338='jan_sales',<381>,5:41]], feb_sales=[[@59,341:349='feb_sales',<381>,5:52]]}, monthly_sales_long={empid=[[@11,75:79='empid',<381>,2:13], [@80,437:441='empid',<381>,7:13]], month_name=[[@13,82:91='month_name',<381>,2:20], [@82,444:453='month_name',<381>,7:20]], sales_amount=[[@15,94:105='sales_amount',<381>,2:32], [@84,456:467='sales_amount',<381>,7:32]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query5={jan_sales_SUM=[[@1,7:19='jan_sales_SUM',<381>,1:7], [@66,362:374='jan_sales_SUM',<381>,6:7], [@114,606:618='jan_sales_SUM',<381>,10:8], [@139,735:747='jan_sales_SUM',<381>,13:11], [@22,145:147='SUM',<141>,3:7], [@30,182:192=''jan_sales'',<389>,3:44]], month_name=[[@5,36:45='month_name',<381>,1:36], [@74,399:408='month_name',<381>,6:44], [@132,698:707='month_name',<381>,12:28], [@145,767:776='month_name',<381>,13:43], [@54,315:324='month_name',<381>,5:26]], sales_amount=[[@3,22:33='sales_amount',<381>,1:22], [@70,380:391='sales_amount',<381>,6:25], [@106,567:578='sales_amount',<381>,9:7], [@120,632:643='sales_amount',<381>,11:8], [@124,652:663='sales_amount',<381>,11:28], [@147,779:790='sales_amount',<381>,13:55], [@52,298:309='sales_amount',<381>,5:9]], feb_sales_SUM=[[@7,48:60='feb_sales_SUM',<381>,1:48], [@110,584:596='feb_sales_SUM',<381>,9:24], [@128,676:688='feb_sales_SUM',<381>,12:6], [@143,752:764='feb_sales_SUM',<381>,13:28]]}, query0={empid=[[@11,75:79='empid',<381>,2:13]], month_name=[[@13,82:91='month_name',<381>,2:20]], sales_amount=[[@15,94:105='sales_amount',<381>,2:32]]}, query2={jan_sales=[[@41,231:239='jan_sales',<381>,4:20]], empid=[[@39,224:228='empid',<381>,4:13]], mar_sales=[[@45,253:261='mar_sales',<381>,4:42]], feb_sales=[[@43,242:250='feb_sales',<381>,4:31]]}, query3={empid=[[@80,437:441='empid',<381>,7:13]], month_name=[[@82,444:453='month_name',<381>,7:20]], sales_amount=[[@84,456:467='sales_amount',<381>,7:32]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query5={query_dictionary={jan_sales_SUM=[[@1,7:19='jan_sales_SUM',<381>,1:7], [@66,362:374='jan_sales_SUM',<381>,6:7], [@114,606:618='jan_sales_SUM',<381>,10:8], [@139,735:747='jan_sales_SUM',<381>,13:11], [@22,145:147='SUM',<141>,3:7], [@30,182:192=''jan_sales'',<389>,3:44]], month_name=[[@5,36:45='month_name',<381>,1:36], [@74,399:408='month_name',<381>,6:44], [@132,698:707='month_name',<381>,12:28], [@145,767:776='month_name',<381>,13:43], [@54,315:324='month_name',<381>,5:26]], sales_amount=[[@3,22:33='sales_amount',<381>,1:22], [@70,380:391='sales_amount',<381>,6:25], [@106,567:578='sales_amount',<381>,9:7], [@120,632:643='sales_amount',<381>,11:8], [@124,652:663='sales_amount',<381>,11:28], [@147,779:790='sales_amount',<381>,13:55], [@52,298:309='sales_amount',<381>,5:9]], feb_sales_SUM=[[@7,48:60='feb_sales_SUM',<381>,1:48], [@110,584:596='feb_sales_SUM',<381>,9:24], [@128,676:688='feb_sales_SUM',<381>,12:6], [@143,752:764='feb_sales_SUM',<381>,13:28]]}, table_dictionary={p_src={month_name=[[@27,167:176='month_name',<381>,3:29], [@96,529:538='month_name',<381>,8:29], [@132,698:707='month_name',<381>,12:28], [@145,767:776='month_name',<381>,13:43]], sales_amount=[[@24,149:160='sales_amount',<381>,3:11], [@93,511:522='sales_amount',<381>,8:11], [@124,652:663='sales_amount',<381>,11:28], [@147,779:790='sales_amount',<381>,13:55]]}, q_src={month_name=[[@96,529:538='month_name',<381>,8:29]], sales_amount=[[@93,511:522='sales_amount',<381>,8:11]]}, u_src={jan_sales=[[@57,330:338='jan_sales',<381>,5:41]], feb_sales=[[@59,341:349='feb_sales',<381>,5:52]]}}, def_query0={query_dictionary={empid=[[@11,75:79='empid',<381>,2:13]], month_name=[[@13,82:91='month_name',<381>,2:20]], sales_amount=[[@15,94:105='sales_amount',<381>,2:32]]}, table_dictionary={monthly_sales_long={empid=[[@11,75:79='empid',<381>,2:13], [@80,437:441='empid',<381>,7:13]], month_name=[[@13,82:91='month_name',<381>,2:20], [@82,444:453='month_name',<381>,7:20]], sales_amount=[[@15,94:105='sales_amount',<381>,2:32], [@84,456:467='sales_amount',<381>,7:32]]}}, interface={empid=[{name=empid, table_ref=monthly_sales_long}], month_name=[{name=month_name, table_ref=monthly_sales_long}], sales_amount=[{name=sales_amount, table_ref=monthly_sales_long}]}}, derivation={source_columns={p=[{name=month_name, table_ref=p_src}, {name=sales_amount, table_ref=p_src}], q=[{name=month_name, table_ref=q_src}, {name=sales_amount, table_ref=q_src}], u=[{name=jan_sales, table_ref=u_src}, {name=feb_sales, table_ref=u_src}]}, derived_columns={p={jan_sales_SUM=[[@22,145:147='SUM',<141>,3:7], [@30,182:192=''jan_sales'',<389>,3:44]], feb_sales_SUM=[[@22,145:147='SUM',<141>,3:7], [@32,195:205=''feb_sales'',<389>,3:57]]}, q={feb_sales_SUM=[[@91,507:509='SUM',<141>,8:7], [@99,544:554=''feb_sales'',<389>,8:44]]}, u={sales_amount=[[@52,298:309='sales_amount',<381>,5:9]], month_name=[[@54,315:324='month_name',<381>,5:26]]}}}, ordered_by=[{name=jan_sales_SUM, table_ref=p}, {name=feb_sales_SUM, table_ref=q}, {name=month_name, table_ref=u}, {name=jan_sales, table_ref=u_src}, {name=feb_sales, table_ref=u_src}, {name=sales_amount, table_ref=u}], filters=[{name=jan_sales_SUM, table_ref=p}, {name=sales_amount, table_ref=u}, {name=month_name, table_ref=u}, {name=feb_sales_SUM, table_ref=q}, {name=jan_sales, table_ref=u_src}, {name=feb_sales, table_ref=u_src}, {name=feb_sales_SUM, table_ref=null}], interface={jan_sales_SUM=[{name=jan_sales_SUM, table_ref=p}, {name=month_name, table_ref=p_src}, {name=sales_amount, table_ref=p_src}], month_name=[{name=month_name, table_ref=u}, {name=jan_sales, table_ref=u_src}, {name=feb_sales, table_ref=u_src}], sales_amount=[{name=sales_amount, table_ref=u}, {name=jan_sales, table_ref=u_src}, {name=feb_sales, table_ref=u_src}], feb_sales_SUM=[{name=feb_sales_SUM, table_ref=null}]}, def_query3={query_dictionary={empid=[[@80,437:441='empid',<381>,7:13]], month_name=[[@82,444:453='month_name',<381>,7:20]], sales_amount=[[@84,456:467='sales_amount',<381>,7:32]]}, table_dictionary={monthly_sales_long={empid=[[@80,437:441='empid',<381>,7:13]], month_name=[[@82,444:453='month_name',<381>,7:20]], sales_amount=[[@84,456:467='sales_amount',<381>,7:32]]}}, interface={empid=[{name=empid, table_ref=monthly_sales_long}], month_name=[{name=month_name, table_ref=monthly_sales_long}], sales_amount=[{name=sales_amount, table_ref=monthly_sales_long}]}}, table_alias={p_src=query0, u_src=query2, q_src=query3, p=p_src, q=q_src, u=u_src}, def_query2={query_dictionary={jan_sales=[[@41,231:239='jan_sales',<381>,4:20]], empid=[[@39,224:228='empid',<381>,4:13]], mar_sales=[[@45,253:261='mar_sales',<381>,4:42]], feb_sales=[[@43,242:250='feb_sales',<381>,4:31]]}, table_dictionary={monthly_sales={jan_sales=[[@41,231:239='jan_sales',<381>,4:20]], empid=[[@39,224:228='empid',<381>,4:13]], mar_sales=[[@45,253:261='mar_sales',<381>,4:42]], feb_sales=[[@43,242:250='feb_sales',<381>,4:31]]}}, interface={jan_sales=[{name=jan_sales, table_ref=monthly_sales}], empid=[{name=empid, table_ref=monthly_sales}], mar_sales=[{name=mar_sales, table_ref=monthly_sales}], feb_sales=[{name=feb_sales, table_ref=monthly_sales}]}}}}",
				extractor.getSymbolTable().toString());
	}

	/** Phase 17.6.7 paired variant of {@link #tripleUnpivotPivotUnpivotJoinDerivedColumnsV1Test}. */
	@Test
	public void tripleUnpivotPivotUnpivotJoinDerivedColumnsSubqueryFromV17_6_7Test() {
		final String query =
				"SELECT u1_src.empid AS e1, jan_sales_SUM, u2_src.empid AS e2, sales_amount, month_name\n"
						+ "FROM " + V17_6_7_UNPIVOT_WIDE_SRC + " u1_src\n"
						+ "UNPIVOT (sales_amount FOR month_name IN (jan_sales, feb_sales)) u1\n"
						+ "JOIN " + V17_6_7_PIVOT_LONG_SRC + " p_src\n"
						+ "PIVOT (SUM(sales_amount) FOR month_name IN ('jan_sales')) p\n"
						+ "  ON u1.sales_amount = p.jan_sales_SUM\n"
						+ "JOIN " + V17_6_7_UNPIVOT_WIDE_SRC + " u2_src\n"
						+ "UNPIVOT (sales_amount FOR month_name IN (feb_sales, mar_sales)) u2\n"
						+ "  ON p.jan_sales_SUM = u2.sales_amount\n"
						+ "WHERE u1.sales_amount > 10\n"
						+ "  AND p.jan_sales_SUM > 0\n"
						+ "  AND u2.sales_amount > 10;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		Snippet snippet = extractor.getSnippet();
		assertFatalDiagnosticAtPosition(
				snippet,
				"AMBIGUOUS_DERIVED_COLUMN_REFERENCE",
				"Ambiguous derived column reference 'sales_amount' at (l:1 c:62). Possible sources: [u1, u2]",
				"sales_amount",
				1,
				62);
		assertFatalDiagnosticAtPosition(
				snippet,
				"AMBIGUOUS_DERIVED_COLUMN_REFERENCE",
				"Ambiguous derived column reference 'month_name' at (l:1 c:76). Possible sources: [u1, u2]",
				"month_name",
				1,
				76);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=empid, table_ref=u1_src}, alias=e1}, 2={column={name=jan_sales_SUM, table_ref=null}}, 3={column={name=empid, table_ref=u2_src}, alias=e2}, 4={column={name=sales_amount, table_ref=null}}, 5={column={name=month_name, table_ref=null}}}, from={join={1={unpivot={value={column={name=sales_amount, table_ref=null}}, for={column={name=month_name, table_ref=null}}, in={1={name=jan_sales, table_ref=null}, 2={name=feb_sales, table_ref=null}}}, alias=u1, table={alias=u1_src, query={select={1={column={name=empid, table_ref=null}}, 2={column={name=jan_sales, table_ref=null}}, 3={column={name=feb_sales, table_ref=null}}, 4={column={name=mar_sales, table_ref=null}}}, from={table={alias=null, table=monthly_sales}}}}}, 2={join=JOIN, on={condition={left={column={name=sales_amount, table_ref=u1}}, right={column={name=jan_sales_SUM, table_ref=p}}, operator==}}}, 3={pivot={value={function={function_name=SUM, parameters={column={name=sales_amount, table_ref=null}}}}, for={column={name=month_name, table_ref=null}}, in={1={pivot_literal='jan_sales'}}}, alias=p, table={alias=p_src, query={select={1={column={name=empid, table_ref=null}}, 2={column={name=month_name, table_ref=null}}, 3={column={name=sales_amount, table_ref=null}}}, from={table={alias=null, table=monthly_sales_long}}}}}, 4={join=JOIN, on={condition={left={column={name=jan_sales_SUM, table_ref=p}}, right={column={name=sales_amount, table_ref=u2}}, operator==}}}, 5={unpivot={value={column={name=sales_amount, table_ref=null}}, for={column={name=month_name, table_ref=null}}, in={1={name=feb_sales, table_ref=null}, 2={name=mar_sales, table_ref=null}}}, alias=u2, table={alias=u2_src, query={select={1={column={name=empid, table_ref=null}}, 2={column={name=jan_sales, table_ref=null}}, 3={column={name=feb_sales, table_ref=null}}, 4={column={name=mar_sales, table_ref=null}}}, from={table={alias=null, table=monthly_sales}}}}}}}, where={and={1={condition={left={column={name=sales_amount, table_ref=u1}}, right={literal=10}, operator=>}}, 2={condition={left={column={name=jan_sales_SUM, table_ref=p}}, right={literal=0}, operator=>}}, 3={condition={left={column={name=sales_amount, table_ref=u2}}, right={literal=10}, operator=>}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[jan_sales_SUM, month_name, sales_amount, e1, e2]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{p_src={month_name=[[@64,338:347='month_name',<381>,5:29]], sales_amount=[[@61,320:331='sales_amount',<381>,5:11]]}, monthly_sales={jan_sales=[[@23,107:115='jan_sales',<381>,2:20], [@84,428:436='jan_sales',<381>,7:20]], empid=[[@21,100:104='empid',<381>,2:13], [@82,421:425='empid',<381>,7:13]], mar_sales=[[@27,129:137='mar_sales',<381>,2:42], [@88,450:458='mar_sales',<381>,7:42]], feb_sales=[[@25,118:126='feb_sales',<381>,2:31], [@86,439:447='feb_sales',<381>,7:31]]}, u2_src={mar_sales=[[@102,539:547='mar_sales',<381>,8:52]], feb_sales=[[@100,528:536='feb_sales',<381>,8:41]]}, u1_src={jan_sales=[[@39,207:215='jan_sales',<381>,3:41]], feb_sales=[[@41,218:226='feb_sales',<381>,3:52]]}, monthly_sales_long={empid=[[@48,246:250='empid',<381>,4:13]], month_name=[[@50,253:262='month_name',<381>,4:20]], sales_amount=[[@52,265:276='sales_amount',<381>,4:32]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query4={jan_sales_SUM=[[@7,27:39='jan_sales_SUM',<381>,1:27], [@78,394:406='jan_sales_SUM',<381>,6:25], [@109,561:573='jan_sales_SUM',<381>,9:7], [@123,628:640='jan_sales_SUM',<381>,11:8], [@59,316:318='SUM',<141>,5:7], [@67,353:363=''jan_sales'',<389>,5:44]], month_name=[[@17,76:85='month_name',<381>,1:76]], sales_amount=[[@15,62:73='sales_amount',<381>,1:62], [@74,377:388='sales_amount',<381>,6:8], [@113,580:591='sales_amount',<381>,9:26], [@117,602:613='sales_amount',<381>,10:9], [@129,655:666='sales_amount',<381>,12:9]], e1=[[@5,23:24='e1',<381>,1:23]], e2=[[@13,58:59='e2',<381>,1:58]]}, query0={jan_sales=[[@23,107:115='jan_sales',<381>,2:20]], empid=[[@21,100:104='empid',<381>,2:13], [@1,7:12='u1_src',<381>,1:7]], mar_sales=[[@27,129:137='mar_sales',<381>,2:42]], feb_sales=[[@25,118:126='feb_sales',<381>,2:31]]}, query1={empid=[[@48,246:250='empid',<381>,4:13]], month_name=[[@50,253:262='month_name',<381>,4:20]], sales_amount=[[@52,265:276='sales_amount',<381>,4:32]]}, query3={jan_sales=[[@84,428:436='jan_sales',<381>,7:20]], empid=[[@82,421:425='empid',<381>,7:13], [@9,42:47='u2_src',<381>,1:42]], mar_sales=[[@88,450:458='mar_sales',<381>,7:42]], feb_sales=[[@86,439:447='feb_sales',<381>,7:31]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query4={query_dictionary={jan_sales_SUM=[[@7,27:39='jan_sales_SUM',<381>,1:27], [@78,394:406='jan_sales_SUM',<381>,6:25], [@109,561:573='jan_sales_SUM',<381>,9:7], [@123,628:640='jan_sales_SUM',<381>,11:8], [@59,316:318='SUM',<141>,5:7], [@67,353:363=''jan_sales'',<389>,5:44]], month_name=[[@17,76:85='month_name',<381>,1:76]], sales_amount=[[@15,62:73='sales_amount',<381>,1:62], [@74,377:388='sales_amount',<381>,6:8], [@113,580:591='sales_amount',<381>,9:26], [@117,602:613='sales_amount',<381>,10:9], [@129,655:666='sales_amount',<381>,12:9]], e1=[[@5,23:24='e1',<381>,1:23]], e2=[[@13,58:59='e2',<381>,1:58]]}, table_dictionary={p_src={month_name=[[@64,338:347='month_name',<381>,5:29]], sales_amount=[[@61,320:331='sales_amount',<381>,5:11]]}, u2_src={mar_sales=[[@102,539:547='mar_sales',<381>,8:52]], feb_sales=[[@100,528:536='feb_sales',<381>,8:41]]}, u1_src={jan_sales=[[@39,207:215='jan_sales',<381>,3:41]], feb_sales=[[@41,218:226='feb_sales',<381>,3:52]]}}, def_query1={query_dictionary={empid=[[@48,246:250='empid',<381>,4:13]], month_name=[[@50,253:262='month_name',<381>,4:20]], sales_amount=[[@52,265:276='sales_amount',<381>,4:32]]}, table_dictionary={monthly_sales_long={empid=[[@48,246:250='empid',<381>,4:13]], month_name=[[@50,253:262='month_name',<381>,4:20]], sales_amount=[[@52,265:276='sales_amount',<381>,4:32]]}}, interface={empid=[{name=empid, table_ref=monthly_sales_long}], month_name=[{name=month_name, table_ref=monthly_sales_long}], sales_amount=[{name=sales_amount, table_ref=monthly_sales_long}]}}, def_query0={query_dictionary={jan_sales=[[@23,107:115='jan_sales',<381>,2:20]], empid=[[@21,100:104='empid',<381>,2:13], [@1,7:12='u1_src',<381>,1:7]], mar_sales=[[@27,129:137='mar_sales',<381>,2:42]], feb_sales=[[@25,118:126='feb_sales',<381>,2:31]]}, table_dictionary={monthly_sales={jan_sales=[[@23,107:115='jan_sales',<381>,2:20], [@84,428:436='jan_sales',<381>,7:20]], empid=[[@21,100:104='empid',<381>,2:13], [@82,421:425='empid',<381>,7:13]], mar_sales=[[@27,129:137='mar_sales',<381>,2:42], [@88,450:458='mar_sales',<381>,7:42]], feb_sales=[[@25,118:126='feb_sales',<381>,2:31], [@86,439:447='feb_sales',<381>,7:31]]}}, interface={jan_sales=[{name=jan_sales, table_ref=monthly_sales}], empid=[{name=empid, table_ref=monthly_sales}], mar_sales=[{name=mar_sales, table_ref=monthly_sales}], feb_sales=[{name=feb_sales, table_ref=monthly_sales}]}}, derivation={source_columns={p=[{name=month_name, table_ref=p_src}, {name=sales_amount, table_ref=p_src}], u1=[{name=jan_sales, table_ref=u1_src}, {name=feb_sales, table_ref=u1_src}], u2=[{name=feb_sales, table_ref=u2_src}, {name=mar_sales, table_ref=u2_src}]}, derived_columns={p={jan_sales_SUM=[[@59,316:318='SUM',<141>,5:7], [@67,353:363=''jan_sales'',<389>,5:44]]}, u1={sales_amount=[[@34,175:186='sales_amount',<381>,3:9]], month_name=[[@36,192:201='month_name',<381>,3:26]]}, u2={sales_amount=[[@95,496:507='sales_amount',<381>,8:9]], month_name=[[@97,513:522='month_name',<381>,8:26]]}}}, filters=[{name=sales_amount, table_ref=u1}, {name=jan_sales_SUM, table_ref=p}, {name=sales_amount, table_ref=u2}], interface={jan_sales_SUM=[{name=jan_sales_SUM, table_ref=p}, {name=month_name, table_ref=p_src}, {name=sales_amount, table_ref=p_src}], month_name=[{name=month_name, table_ref=null}], sales_amount=[{name=sales_amount, table_ref=null}], e1=[{name=empid, table_ref=u1_src}], e2=[{name=empid, table_ref=u2_src}]}, def_query3={query_dictionary={jan_sales=[[@84,428:436='jan_sales',<381>,7:20]], empid=[[@82,421:425='empid',<381>,7:13], [@9,42:47='u2_src',<381>,1:42]], mar_sales=[[@88,450:458='mar_sales',<381>,7:42]], feb_sales=[[@86,439:447='feb_sales',<381>,7:31]]}, table_dictionary={monthly_sales={jan_sales=[[@84,428:436='jan_sales',<381>,7:20]], empid=[[@82,421:425='empid',<381>,7:13]], mar_sales=[[@88,450:458='mar_sales',<381>,7:42]], feb_sales=[[@86,439:447='feb_sales',<381>,7:31]]}}, interface={jan_sales=[{name=jan_sales, table_ref=monthly_sales}], empid=[{name=empid, table_ref=monthly_sales}], mar_sales=[{name=mar_sales, table_ref=monthly_sales}], feb_sales=[{name=feb_sales, table_ref=monthly_sales}]}}, table_alias={u1_src=query0, p_src=query1, u2_src=query3, p=p_src, u1=u1_src, u2=u2_src}}}",
				extractor.getSymbolTable().toString());
	}

	// Phase 17.7.11: single PIVOT/UNPIVOT over each non-db_object tuple_source_primary (sign-off gate for query-backed operand lineage).

	/** Phase 17.7.11 — Subquery-backed long-format FROM before a single PIVOT. */
	@Test
	public void singlePivotSubqueryFromV17_7_11Test() {
		final String query =
				"SELECT jan_sales_SUM, p_src.empid\n"
				+ "FROM (SELECT empid, month_name, sales_amount FROM monthly_sales_long) p_src\n"
				+ "PIVOT (SUM(sales_amount) FOR month_name IN ('jan_sales')) p\n"
				+ "WHERE p.jan_sales_SUM > 0;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=jan_sales_SUM, table_ref=null}}, 2={column={name=empid, table_ref=p_src}}}, from={pivot={value={function={function_name=SUM, parameters={column={name=sales_amount, table_ref=null}}}}, for={column={name=month_name, table_ref=null}}, in={1={pivot_literal='jan_sales'}}}, alias=p, table={alias=p_src, query={select={1={column={name=empid, table_ref=null}}, 2={column={name=month_name, table_ref=null}}, 3={column={name=sales_amount, table_ref=null}}}, from={table={alias=null, table=monthly_sales_long}}}}}, where={condition={left={column={name=jan_sales_SUM, table_ref=p}}, right={literal=0}, operator=>}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[jan_sales_SUM, empid]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{p_src={month_name=[[@25,139:148='month_name',<381>,3:29]], sales_amount=[[@22,121:132='sales_amount',<381>,3:11]]}, monthly_sales_long={empid=[[@9,47:51='empid',<381>,2:13]], month_name=[[@11,54:63='month_name',<381>,2:20]], sales_amount=[[@13,66:77='sales_amount',<381>,2:32]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={empid=[[@9,47:51='empid',<381>,2:13], [@3,22:26='p_src',<381>,1:22]], month_name=[[@11,54:63='month_name',<381>,2:20]], sales_amount=[[@13,66:77='sales_amount',<381>,2:32]]}, query2={jan_sales_SUM=[[@1,7:19='jan_sales_SUM',<381>,1:7], [@35,178:190='jan_sales_SUM',<381>,4:8], [@20,117:119='SUM',<141>,3:7], [@28,154:164=''jan_sales'',<389>,3:44]], empid=[[@5,28:32='empid',<381>,1:28]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query2={query_dictionary={jan_sales_SUM=[[@1,7:19='jan_sales_SUM',<381>,1:7], [@35,178:190='jan_sales_SUM',<381>,4:8], [@20,117:119='SUM',<141>,3:7], [@28,154:164=''jan_sales'',<389>,3:44]], empid=[[@5,28:32='empid',<381>,1:28]]}, table_dictionary={p_src={month_name=[[@25,139:148='month_name',<381>,3:29]], sales_amount=[[@22,121:132='sales_amount',<381>,3:11]]}}, def_query0={query_dictionary={empid=[[@9,47:51='empid',<381>,2:13], [@3,22:26='p_src',<381>,1:22]], month_name=[[@11,54:63='month_name',<381>,2:20]], sales_amount=[[@13,66:77='sales_amount',<381>,2:32]]}, table_dictionary={monthly_sales_long={empid=[[@9,47:51='empid',<381>,2:13]], month_name=[[@11,54:63='month_name',<381>,2:20]], sales_amount=[[@13,66:77='sales_amount',<381>,2:32]]}}, interface={empid=[{name=empid, table_ref=monthly_sales_long}], month_name=[{name=month_name, table_ref=monthly_sales_long}], sales_amount=[{name=sales_amount, table_ref=monthly_sales_long}]}}, derivation={source_columns={p=[{name=month_name, table_ref=p_src}, {name=sales_amount, table_ref=p_src}]}, derived_columns={p={jan_sales_SUM=[[@20,117:119='SUM',<141>,3:7], [@28,154:164=''jan_sales'',<389>,3:44]]}}}, filters=[{name=jan_sales_SUM, table_ref=p}], interface={jan_sales_SUM=[{name=jan_sales_SUM, table_ref=p}, {name=month_name, table_ref=p_src}, {name=sales_amount, table_ref=p_src}], empid=[{name=empid, table_ref=p_src}]}, table_alias={p_src=query0, p=p_src}}}",
				extractor.getSymbolTable().toString());
	}

	/** Phase 17.7.11 — Tuple substitution variable long-format FROM before a single PIVOT. */
	@Test
	public void singlePivotVariableFromV17_7_11Test() {
		final String query =
				"SELECT jan_sales_SUM, p_src.empid\n"
				+ "FROM <monthly_sales_long pivot source> AS p_src\n"
				+ "PIVOT (SUM(sales_amount) FOR month_name IN ('jan_sales')) p\n"
				+ "WHERE p.jan_sales_SUM > 0;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=jan_sales_SUM, table_ref=null}}, 2={column={name=empid, table_ref=p_src}}}, from={pivot={value={function={function_name=SUM, parameters={column={name=sales_amount, table_ref=null}}}}, for={column={name=month_name, table_ref=null}}, in={1={pivot_literal='jan_sales'}}}, alias=p, table={alias=p_src, substitution={name=<monthly_sales_long pivot source>, type=tuple}}}, where={condition={left={column={name=jan_sales_SUM, table_ref=p}}, right={literal=0}, operator=>}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[jan_sales_SUM, empid]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<monthly_sales_long pivot source>=tuple}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{<monthly_sales_long pivot source>={empid=[[@3,22:26='p_src',<381>,1:22]], month_name=[[@17,111:120='month_name',<381>,3:29]], sales_amount=[[@14,93:104='sales_amount',<381>,3:11]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query1={jan_sales_SUM=[[@1,7:19='jan_sales_SUM',<381>,1:7], [@27,150:162='jan_sales_SUM',<381>,4:8], [@12,89:91='SUM',<141>,3:7], [@20,126:136=''jan_sales'',<389>,3:44]], empid=[[@5,28:32='empid',<381>,1:28]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query1={query_dictionary={jan_sales_SUM=[[@1,7:19='jan_sales_SUM',<381>,1:7], [@27,150:162='jan_sales_SUM',<381>,4:8], [@12,89:91='SUM',<141>,3:7], [@20,126:136=''jan_sales'',<389>,3:44]], empid=[[@5,28:32='empid',<381>,1:28]]}, table_dictionary={<monthly_sales_long pivot source>={month_name=[[@17,111:120='month_name',<381>,3:29]], empid=[[@3,22:26='p_src',<381>,1:22]], sales_amount=[[@14,93:104='sales_amount',<381>,3:11]]}}, derivation={source_columns={p=[{name=month_name, table_ref=p_src}, {name=sales_amount, table_ref=p_src}]}, derived_columns={p={jan_sales_SUM=[[@12,89:91='SUM',<141>,3:7], [@20,126:136=''jan_sales'',<389>,3:44]]}}}, filters=[{name=jan_sales_SUM, table_ref=p}], interface={jan_sales_SUM=[{name=jan_sales_SUM, table_ref=p}, {name=month_name, table_ref=p_src}, {name=sales_amount, table_ref=p_src}], empid=[{name=empid, table_ref=p_src}]}, table_alias={p=p_src, p_src=<monthly_sales_long pivot source>}}}",
				extractor.getSymbolTable().toString());
	}

	/** Phase 17.7.11 — Jinja tuple FROM before a single PIVOT. */
	@Test
	public void singlePivotJinjaFromV17_7_11Test() {
		final String query =
				"SELECT jan_sales_SUM, p_src.empid\n"
				+ "FROM {{ ref('monthly_sales_long') }} p_src\n"
				+ "PIVOT (SUM(sales_amount) FOR month_name IN ('jan_sales')) p\n"
				+ "WHERE p.jan_sales_SUM > 0;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=jan_sales_SUM, table_ref=null}}, 2={column={name=empid, table_ref=p_src}}}, from={pivot={value={function={function_name=SUM, parameters={column={name=sales_amount, table_ref=null}}}}, for={column={name=month_name, table_ref=null}}, in={1={pivot_literal='jan_sales'}}}, alias=p, table={alias=p_src, substitution={name={{ ref('monthly_sales_long') }}, parts={jinja_table={function_name=ref, parameters={1={literal='monthly_sales_long'}}}}, type=tuple}}}, where={condition={left={column={name=jan_sales_SUM, table_ref=p}}, right={literal=0}, operator=>}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[jan_sales_SUM, empid]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{{{ ref('monthly_sales_long') }}=tuple}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{{{ ref('monthly_sales_long') }}={empid=[[@3,22:26='p_src',<381>,1:22]], month_name=[[@21,106:115='month_name',<381>,3:29]], sales_amount=[[@18,88:99='sales_amount',<381>,3:11]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query1={jan_sales_SUM=[[@1,7:19='jan_sales_SUM',<381>,1:7], [@31,145:157='jan_sales_SUM',<381>,4:8], [@16,84:86='SUM',<141>,3:7], [@24,121:131=''jan_sales'',<389>,3:44]], empid=[[@5,28:32='empid',<381>,1:28]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query1={query_dictionary={jan_sales_SUM=[[@1,7:19='jan_sales_SUM',<381>,1:7], [@31,145:157='jan_sales_SUM',<381>,4:8], [@16,84:86='SUM',<141>,3:7], [@24,121:131=''jan_sales'',<389>,3:44]], empid=[[@5,28:32='empid',<381>,1:28]]}, table_dictionary={{{ ref('monthly_sales_long') }}={month_name=[[@21,106:115='month_name',<381>,3:29]], empid=[[@3,22:26='p_src',<381>,1:22]], sales_amount=[[@18,88:99='sales_amount',<381>,3:11]]}}, derivation={source_columns={p=[{name=month_name, table_ref=p_src}, {name=sales_amount, table_ref=p_src}]}, derived_columns={p={jan_sales_SUM=[[@16,84:86='SUM',<141>,3:7], [@24,121:131=''jan_sales'',<389>,3:44]]}}}, filters=[{name=jan_sales_SUM, table_ref=p}], interface={jan_sales_SUM=[{name=jan_sales_SUM, table_ref=p}, {name=month_name, table_ref=p_src}, {name=sales_amount, table_ref=p_src}], empid=[{name=empid, table_ref=p_src}]}, table_alias={p=p_src, p_src={{ ref('monthly_sales_long') }}}}}",
				extractor.getSymbolTable().toString());
	}

	/** Phase 17.7.11 — VALUES primary long-format FROM before a single PIVOT. */
	@Test
	public void singlePivotValuesFromV17_7_11Test() {
		final String query =
				"SELECT jan_sales_SUM, p_src.empid\n"
				+ "FROM (VALUES (1, 'jan_sales', 100.0), (1, 'feb_sales', 200.0), (2, 'jan_sales', 150.0)) AS p_src (empid, month_name, sales_amount)\n"
				+ "PIVOT (SUM(p_src.sales_amount) FOR p_src.month_name IN ('jan_sales')) p\n"
				+ "WHERE p.jan_sales_SUM > 0;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=jan_sales_SUM, table_ref=null}}, 2={column={name=empid, table_ref=p_src}}}, from={values={columns={1={column={name=empid, table_ref=null}}, 2={column={name=month_name, table_ref=null}}, 3={column={name=sales_amount, table_ref=null}}}, alias=p_src, matrix={1={row={1={literal=1}, 2={literal='jan_sales'}, 3={literal=100.0}}}, 2={row={1={literal=1}, 2={literal='feb_sales'}, 3={literal=200.0}}}, 3={row={1={literal=2}, 2={literal='jan_sales'}, 3={literal=150.0}}}}}, pivot={value={function={function_name=SUM, parameters={column={name=sales_amount, table_ref=p_src}}}}, for={column={name=month_name, table_ref=p_src}}, in={1={pivot_literal='jan_sales'}}}, alias=p}, where={condition={left={column={name=jan_sales_SUM, table_ref=p}}, right={literal=0}, operator=>}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[jan_sales_SUM, empid]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{pivot={}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{values0={empid=[[@42,132:136='empid',<381>,2:98], [@3,22:26='p_src',<381>,1:22]], month_name=[[@44,139:148='month_name',<381>,2:105], [@57,200:204='p_src',<381>,3:35]], sales_amount=[[@46,151:162='sales_amount',<381>,2:117], [@52,176:180='p_src',<381>,3:11]]}, query2={jan_sales_SUM=[[@1,7:19='jan_sales_SUM',<381>,1:7], [@69,245:257='jan_sales_SUM',<381>,4:8], [@50,172:174='SUM',<141>,3:7], [@62,221:231=''jan_sales'',<389>,3:56]], empid=[[@5,28:32='empid',<381>,1:28]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query2={query_dictionary={jan_sales_SUM=[[@1,7:19='jan_sales_SUM',<381>,1:7], [@69,245:257='jan_sales_SUM',<381>,4:8], [@50,172:174='SUM',<141>,3:7], [@62,221:231=''jan_sales'',<389>,3:56]], empid=[[@5,28:32='empid',<381>,1:28]]}, table_dictionary={pivot={}}, def_values0={query_dictionary={empid=[[@42,132:136='empid',<381>,2:98], [@3,22:26='p_src',<381>,1:22]], month_name=[[@44,139:148='month_name',<381>,2:105], [@57,200:204='p_src',<381>,3:35]], sales_amount=[[@46,151:162='sales_amount',<381>,2:117], [@52,176:180='p_src',<381>,3:11]]}, interface={empid=[], month_name=[], sales_amount=[]}}, derivation={derived_columns={p={jan_sales_SUM=[[@50,172:174='SUM',<141>,3:7], [@62,221:231=''jan_sales'',<389>,3:56]]}}}, filters=[{name=jan_sales_SUM, table_ref=p}], interface={jan_sales_SUM=[{name=jan_sales_SUM, table_ref=pivot}], empid=[{name=empid, table_ref=p_src}]}, table_alias={p_src=values0}}}",
				extractor.getSymbolTable().toString());
	}

	/** Phase 17.7.11 — Table function (GENERATOR) FROM before a single PIVOT. */
	@Test
	public void singlePivotTableFunctionFromV17_7_11Test() {
		final String query =
				"SELECT jan_sales_SUM, p_src.empid\n"
				+ "FROM TABLE(GENERATOR(ROWCOUNT => 3)) p_src\n"
				+ "PIVOT (SUM(sales_amount) FOR month_name IN ('jan_sales')) p\n"
				+ "WHERE p.jan_sales_SUM > 0;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=jan_sales_SUM, table_ref=null}}, 2={column={name=empid, table_ref=p_src}}}, from={pivot={value={function={function_name=SUM, parameters={column={name=sales_amount, table_ref=null}}}}, for={column={name=month_name, table_ref=null}}, in={1={pivot_literal='jan_sales'}}}, alias=p, table={alias=p_src, table_function={function_name=GENERATOR, parameters={rowcount={literal=3}}}}}, where={condition={left={column={name=jan_sales_SUM, table_ref=p}}, right={literal=0}, operator=>}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[jan_sales_SUM, empid]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{generator0={empid=[[@3,22:26='p_src',<381>,1:22]], month_name=[[@24,106:115='month_name',<381>,3:29]], sales_amount=[[@21,88:99='sales_amount',<381>,3:11]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{generator0={month_name=[[@24,106:115='month_name',<381>,3:29]], sales_amount=[[@21,88:99='sales_amount',<381>,3:11]]}, query1={jan_sales_SUM=[[@1,7:19='jan_sales_SUM',<381>,1:7], [@34,145:157='jan_sales_SUM',<381>,4:8], [@19,84:86='SUM',<141>,3:7], [@27,121:131=''jan_sales'',<389>,3:44]], empid=[[@5,28:32='empid',<381>,1:28]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query1={query_dictionary={jan_sales_SUM=[[@1,7:19='jan_sales_SUM',<381>,1:7], [@34,145:157='jan_sales_SUM',<381>,4:8], [@19,84:86='SUM',<141>,3:7], [@27,121:131=''jan_sales'',<389>,3:44]], empid=[[@5,28:32='empid',<381>,1:28]]}, table_dictionary={generator0={month_name=[[@24,106:115='month_name',<381>,3:29]], empid=[[@3,22:26='p_src',<381>,1:22]], sales_amount=[[@21,88:99='sales_amount',<381>,3:11]]}}, derivation={source_columns={p=[{name=month_name, table_ref=p_src}, {name=sales_amount, table_ref=p_src}]}, derived_columns={p={jan_sales_SUM=[[@19,84:86='SUM',<141>,3:7], [@27,121:131=''jan_sales'',<389>,3:44]]}}}, filters=[{name=jan_sales_SUM, table_ref=p}], interface={jan_sales_SUM=[{name=jan_sales_SUM, table_ref=p}, {name=month_name, table_ref=p_src}, {name=sales_amount, table_ref=p_src}], empid=[{name=empid, table_ref=p_src}]}, table_alias={p=p_src, p_src=generator0}}}",
				extractor.getSymbolTable().toString());
	}

	/** Phase 17.7.11 — Subquery-backed wide-format FROM before a single UNPIVOT. */
	@Test
	public void singleUnpivotSubqueryFromV17_7_11Test() {
		final String query =
				"SELECT u.sales_amount, u.month_name, m1.empid\n"
				+ "FROM (SELECT empid, jan_sales, feb_sales, mar_sales FROM monthly_sales) m1\n"
				+ "UNPIVOT (sales_amount FOR month_name IN (jan_sales, feb_sales)) u\n"
				+ "WHERE u.sales_amount > 10;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=sales_amount, table_ref=u}}, 2={column={name=month_name, table_ref=u}}, 3={column={name=empid, table_ref=m1}}}, from={unpivot={value={column={name=sales_amount, table_ref=null}}, for={column={name=month_name, table_ref=null}}, in={1={name=jan_sales, table_ref=null}, 2={name=feb_sales, table_ref=null}}}, alias=u, table={alias=m1, query={select={1={column={name=empid, table_ref=null}}, 2={column={name=jan_sales, table_ref=null}}, 3={column={name=feb_sales, table_ref=null}}, 4={column={name=mar_sales, table_ref=null}}}, from={table={alias=null, table=monthly_sales}}}}}, where={condition={left={column={name=sales_amount, table_ref=u}}, right={literal=10}, operator=>}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[month_name, empid, sales_amount]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{monthly_sales={jan_sales=[[@17,66:74='jan_sales',<381>,2:20]], empid=[[@15,59:63='empid',<381>,2:13]], mar_sales=[[@21,88:96='mar_sales',<381>,2:42]], feb_sales=[[@19,77:85='feb_sales',<381>,2:31]]}, m1={jan_sales=[[@33,162:170='jan_sales',<381>,3:41]], feb_sales=[[@35,173:181='feb_sales',<381>,3:52]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={jan_sales=[[@17,66:74='jan_sales',<381>,2:20]], empid=[[@15,59:63='empid',<381>,2:13], [@9,37:38='m1',<381>,1:37]], mar_sales=[[@21,88:96='mar_sales',<381>,2:42]], feb_sales=[[@19,77:85='feb_sales',<381>,2:31]]}, query1={month_name=[[@7,25:34='month_name',<381>,1:25], [@30,147:156='month_name',<381>,3:26]], empid=[[@11,40:44='empid',<381>,1:40]], sales_amount=[[@3,9:20='sales_amount',<381>,1:9], [@42,195:206='sales_amount',<381>,4:8], [@28,130:141='sales_amount',<381>,3:9]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query1={query_dictionary={month_name=[[@7,25:34='month_name',<381>,1:25], [@30,147:156='month_name',<381>,3:26]], empid=[[@11,40:44='empid',<381>,1:40]], sales_amount=[[@3,9:20='sales_amount',<381>,1:9], [@42,195:206='sales_amount',<381>,4:8], [@28,130:141='sales_amount',<381>,3:9]]}, table_dictionary={m1={jan_sales=[[@33,162:170='jan_sales',<381>,3:41]], feb_sales=[[@35,173:181='feb_sales',<381>,3:52]]}}, def_query0={query_dictionary={jan_sales=[[@17,66:74='jan_sales',<381>,2:20]], empid=[[@15,59:63='empid',<381>,2:13], [@9,37:38='m1',<381>,1:37]], mar_sales=[[@21,88:96='mar_sales',<381>,2:42]], feb_sales=[[@19,77:85='feb_sales',<381>,2:31]]}, table_dictionary={monthly_sales={jan_sales=[[@17,66:74='jan_sales',<381>,2:20]], empid=[[@15,59:63='empid',<381>,2:13]], mar_sales=[[@21,88:96='mar_sales',<381>,2:42]], feb_sales=[[@19,77:85='feb_sales',<381>,2:31]]}}, interface={jan_sales=[{name=jan_sales, table_ref=monthly_sales}], empid=[{name=empid, table_ref=monthly_sales}], mar_sales=[{name=mar_sales, table_ref=monthly_sales}], feb_sales=[{name=feb_sales, table_ref=monthly_sales}]}}, derivation={source_columns={u=[{name=jan_sales, table_ref=m1}, {name=feb_sales, table_ref=m1}]}, derived_columns={u={sales_amount=[[@28,130:141='sales_amount',<381>,3:9]], month_name=[[@30,147:156='month_name',<381>,3:26]]}}}, filters=[{name=sales_amount, table_ref=u}], interface={month_name=[{name=month_name, table_ref=u}], empid=[{name=empid, table_ref=m1}], sales_amount=[{name=jan_sales, table_ref=u}, {name=feb_sales, table_ref=u}]}, table_alias={m1=query0, u=m1}}}",
				extractor.getSymbolTable().toString());
	}

	/** Phase 17.7.11 — Tuple substitution variable wide-format FROM before a single UNPIVOT. */
	@Test
	public void singleUnpivotVariableFromV17_7_11Test() {
		final String query =
				"SELECT u.sales_amount, u.month_name, m1.empid\n"
				+ "FROM <monthly_sales wide source> AS m1\n"
				+ "UNPIVOT (sales_amount FOR month_name IN (jan_sales, feb_sales)) u\n"
				+ "WHERE u.sales_amount > 10;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=sales_amount, table_ref=u}}, 2={column={name=month_name, table_ref=u}}, 3={column={name=empid, table_ref=m1}}}, from={unpivot={value={column={name=sales_amount, table_ref=null}}, for={column={name=month_name, table_ref=null}}, in={1={name=jan_sales, table_ref=null}, 2={name=feb_sales, table_ref=null}}}, alias=u, table={alias=m1, substitution={name=<monthly_sales wide source>, type=tuple}}}, where={condition={left={column={name=sales_amount, table_ref=u}}, right={literal=10}, operator=>}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[month_name, empid, sales_amount]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{<monthly_sales wide source>=tuple}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{<monthly_sales wide source>={jan_sales=[[@23,126:134='jan_sales',<381>,3:41]], empid=[[@9,37:38='m1',<381>,1:37]], feb_sales=[[@25,137:145='feb_sales',<381>,3:52]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={month_name=[[@7,25:34='month_name',<381>,1:25], [@20,111:120='month_name',<381>,3:26]], empid=[[@11,40:44='empid',<381>,1:40]], sales_amount=[[@3,9:20='sales_amount',<381>,1:9], [@32,159:170='sales_amount',<381>,4:8], [@18,94:105='sales_amount',<381>,3:9]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={month_name=[[@7,25:34='month_name',<381>,1:25], [@20,111:120='month_name',<381>,3:26]], empid=[[@11,40:44='empid',<381>,1:40]], sales_amount=[[@3,9:20='sales_amount',<381>,1:9], [@32,159:170='sales_amount',<381>,4:8], [@18,94:105='sales_amount',<381>,3:9]]}, table_dictionary={<monthly_sales wide source>={jan_sales=[[@23,126:134='jan_sales',<381>,3:41]], empid=[[@9,37:38='m1',<381>,1:37]], feb_sales=[[@25,137:145='feb_sales',<381>,3:52]]}}, derivation={source_columns={u=[{name=jan_sales, table_ref=m1}, {name=feb_sales, table_ref=m1}]}, derived_columns={u={sales_amount=[[@18,94:105='sales_amount',<381>,3:9]], month_name=[[@20,111:120='month_name',<381>,3:26]]}}}, filters=[{name=sales_amount, table_ref=u}], interface={month_name=[{name=month_name, table_ref=u}], empid=[{name=empid, table_ref=m1}], sales_amount=[{name=jan_sales, table_ref=u}, {name=feb_sales, table_ref=u}]}, table_alias={m1=<monthly_sales wide source>, u=m1}}}",
				extractor.getSymbolTable().toString());
	}

	/** Phase 17.7.11 — Jinja tuple FROM before a single UNPIVOT. */
	@Test
	public void singleUnpivotJinjaFromV17_7_11Test() {
		final String query =
				"SELECT u.sales_amount, u.month_name, m1.empid\n"
				+ "FROM {{ ref('monthly_sales') }} m1\n"
				+ "UNPIVOT (sales_amount FOR month_name IN (jan_sales, feb_sales)) u\n"
				+ "WHERE u.sales_amount > 10;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=sales_amount, table_ref=u}}, 2={column={name=month_name, table_ref=u}}, 3={column={name=empid, table_ref=m1}}}, from={unpivot={value={column={name=sales_amount, table_ref=null}}, for={column={name=month_name, table_ref=null}}, in={1={name=jan_sales, table_ref=null}, 2={name=feb_sales, table_ref=null}}}, alias=u, table={alias=m1, substitution={name={{ ref('monthly_sales') }}, parts={jinja_table={function_name=ref, parameters={1={literal='monthly_sales'}}}}, type=tuple}}}, where={condition={left={column={name=sales_amount, table_ref=u}}, right={literal=10}, operator=>}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[month_name, empid, sales_amount]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{{{ ref('monthly_sales') }}=tuple}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{{{ ref('monthly_sales') }}={jan_sales=[[@27,122:130='jan_sales',<381>,3:41]], empid=[[@9,37:38='m1',<381>,1:37]], feb_sales=[[@29,133:141='feb_sales',<381>,3:52]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={month_name=[[@7,25:34='month_name',<381>,1:25], [@24,107:116='month_name',<381>,3:26]], empid=[[@11,40:44='empid',<381>,1:40]], sales_amount=[[@3,9:20='sales_amount',<381>,1:9], [@36,155:166='sales_amount',<381>,4:8], [@22,90:101='sales_amount',<381>,3:9]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={month_name=[[@7,25:34='month_name',<381>,1:25], [@24,107:116='month_name',<381>,3:26]], empid=[[@11,40:44='empid',<381>,1:40]], sales_amount=[[@3,9:20='sales_amount',<381>,1:9], [@36,155:166='sales_amount',<381>,4:8], [@22,90:101='sales_amount',<381>,3:9]]}, table_dictionary={{{ ref('monthly_sales') }}={jan_sales=[[@27,122:130='jan_sales',<381>,3:41]], empid=[[@9,37:38='m1',<381>,1:37]], feb_sales=[[@29,133:141='feb_sales',<381>,3:52]]}}, derivation={source_columns={u=[{name=jan_sales, table_ref=m1}, {name=feb_sales, table_ref=m1}]}, derived_columns={u={sales_amount=[[@22,90:101='sales_amount',<381>,3:9]], month_name=[[@24,107:116='month_name',<381>,3:26]]}}}, filters=[{name=sales_amount, table_ref=u}], interface={month_name=[{name=month_name, table_ref=u}], empid=[{name=empid, table_ref=m1}], sales_amount=[{name=jan_sales, table_ref=u}, {name=feb_sales, table_ref=u}]}, table_alias={m1={{ ref('monthly_sales') }}, u=m1}}}",
				extractor.getSymbolTable().toString());
	}

	/** Phase 17.7.11 — VALUES primary wide-format FROM before a single UNPIVOT. */
	@Test
	public void singleUnpivotValuesFromV17_7_11Test() {
		final String query =
				"SELECT u.sales_amount, u.month_name, m1.empid\n"
				+ "FROM (VALUES (1, 100.0, 200.0, 300.0), (2, 110.0, 210.0, 310.0)) AS m1 (empid, jan_sales, feb_sales, mar_sales)\n"
				+ "UNPIVOT (sales_amount FOR month_name IN (jan_sales, feb_sales)) u\n"
				+ "WHERE u.sales_amount > 10;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=sales_amount, table_ref=u}}, 2={column={name=month_name, table_ref=u}}, 3={column={name=empid, table_ref=m1}}}, from={unpivot={value={column={name=sales_amount, table_ref=null}}, for={column={name=month_name, table_ref=null}}, in={1={name=jan_sales, table_ref=null}, 2={name=feb_sales, table_ref=null}}}, values={columns={1={column={name=empid, table_ref=null}}, 2={column={name=jan_sales, table_ref=null}}, 3={column={name=feb_sales, table_ref=null}}, 4={column={name=mar_sales, table_ref=null}}}, alias=m1, matrix={1={row={1={literal=1}, 2={literal=100.0}, 3={literal=200.0}, 4={literal=300.0}}}, 2={row={1={literal=2}, 2={literal=110.0}, 3={literal=210.0}, 4={literal=310.0}}}}}, alias=u}, where={condition={left={column={name=sales_amount, table_ref=u}}, right={literal=10}, operator=>}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[month_name, empid, sales_amount]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{unpivot={jan_sales=[[@65,199:207='jan_sales',<381>,3:41]], feb_sales=[[@67,210:218='feb_sales',<381>,3:52]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{values0={jan_sales=[[@52,125:133='jan_sales',<381>,2:79]], empid=[[@50,118:122='empid',<381>,2:72], [@9,37:38='m1',<381>,1:37]], mar_sales=[[@56,147:155='mar_sales',<381>,2:101]], feb_sales=[[@54,136:144='feb_sales',<381>,2:90]]}, query1={month_name=[[@7,25:34='month_name',<381>,1:25], [@62,184:193='month_name',<381>,3:26]], empid=[[@11,40:44='empid',<381>,1:40]], sales_amount=[[@3,9:20='sales_amount',<381>,1:9], [@74,232:243='sales_amount',<381>,4:8], [@60,167:178='sales_amount',<381>,3:9]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query1={query_dictionary={month_name=[[@7,25:34='month_name',<381>,1:25], [@62,184:193='month_name',<381>,3:26]], empid=[[@11,40:44='empid',<381>,1:40]], sales_amount=[[@3,9:20='sales_amount',<381>,1:9], [@74,232:243='sales_amount',<381>,4:8], [@60,167:178='sales_amount',<381>,3:9]]}, table_dictionary={unpivot={jan_sales=[[@65,199:207='jan_sales',<381>,3:41]], feb_sales=[[@67,210:218='feb_sales',<381>,3:52]]}}, def_values0={query_dictionary={jan_sales=[[@52,125:133='jan_sales',<381>,2:79]], empid=[[@50,118:122='empid',<381>,2:72], [@9,37:38='m1',<381>,1:37]], mar_sales=[[@56,147:155='mar_sales',<381>,2:101]], feb_sales=[[@54,136:144='feb_sales',<381>,2:90]]}, interface={jan_sales=[], empid=[], mar_sales=[], feb_sales=[]}}, derivation={derived_columns={u={sales_amount=[[@60,167:178='sales_amount',<381>,3:9]], month_name=[[@62,184:193='month_name',<381>,3:26]]}}}, filters=[{name=sales_amount, table_ref=u}], interface={month_name=[{name=month_name, table_ref=u}], empid=[{name=empid, table_ref=m1}], sales_amount=[{name=sales_amount, table_ref=u}]}, table_alias={m1=values0}}}",
				extractor.getSymbolTable().toString());
	}

	/** Phase 17.7.11 — Table function (GENERATOR) FROM before a single UNPIVOT. */
	@Test
	public void singleUnpivotTableFunctionFromV17_7_11Test() {
		final String query =
				"SELECT u.sales_amount, u.month_name, m1.empid\n"
				+ "FROM TABLE(GENERATOR(ROWCOUNT => 3)) m1\n"
				+ "UNPIVOT (sales_amount FOR month_name IN (jan_sales, feb_sales)) u\n"
				+ "WHERE u.sales_amount > 10;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);
		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=sales_amount, table_ref=u}}, 2={column={name=month_name, table_ref=u}}, 3={column={name=empid, table_ref=m1}}}, from={unpivot={value={column={name=sales_amount, table_ref=null}}, for={column={name=month_name, table_ref=null}}, in={1={name=jan_sales, table_ref=null}, 2={name=feb_sales, table_ref=null}}}, alias=u, table={alias=m1, table_function={function_name=GENERATOR, parameters={rowcount={literal=3}}}}}, where={condition={left={column={name=sales_amount, table_ref=u}}, right={literal=10}, operator=>}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[month_name, empid, sales_amount]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{generator0={jan_sales=[[@30,127:135='jan_sales',<381>,3:41]], empid=[[@9,37:38='m1',<381>,1:37]], feb_sales=[[@32,138:146='feb_sales',<381>,3:52]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query0={month_name=[[@7,25:34='month_name',<381>,1:25], [@27,112:121='month_name',<381>,3:26]], empid=[[@11,40:44='empid',<381>,1:40]], sales_amount=[[@3,9:20='sales_amount',<381>,1:9], [@39,160:171='sales_amount',<381>,4:8], [@25,95:106='sales_amount',<381>,3:9]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query0={query_dictionary={month_name=[[@7,25:34='month_name',<381>,1:25], [@27,112:121='month_name',<381>,3:26]], empid=[[@11,40:44='empid',<381>,1:40]], sales_amount=[[@3,9:20='sales_amount',<381>,1:9], [@39,160:171='sales_amount',<381>,4:8], [@25,95:106='sales_amount',<381>,3:9]]}, table_dictionary={generator0={jan_sales=[[@30,127:135='jan_sales',<381>,3:41]], empid=[[@9,37:38='m1',<381>,1:37]], feb_sales=[[@32,138:146='feb_sales',<381>,3:52]]}}, derivation={source_columns={u=[{name=jan_sales, table_ref=m1}, {name=feb_sales, table_ref=m1}]}, derived_columns={u={sales_amount=[[@25,95:106='sales_amount',<381>,3:9]], month_name=[[@27,112:121='month_name',<381>,3:26]]}}}, filters=[{name=sales_amount, table_ref=u}], interface={month_name=[{name=month_name, table_ref=u}], empid=[{name=empid, table_ref=m1}], sales_amount=[{name=jan_sales, table_ref=u}, {name=feb_sales, table_ref=u}]}, table_alias={m1=generator0, u=m1}}}",
				extractor.getSymbolTable().toString());
	}

	// --- §17.7.7-gap-fill (focused matrix cells) ---

	/**
	 * Matrix: subset=E | topo=S3 (P–U–P) | bucket=GROUP_BY,HAVING,ORDER_BY | kind=derived (qualified) |
	 * outcome=happy.
	 */
	@Test
	public void gapFill17_7_7_S3PivotUnpivotPivotGroupByHavingQualifiedDerivedV1Test() {
		final String query =
				"SELECT p.jan_sales_SUM, u.month_name, u.sales_amount\n"
						+ "FROM monthly_sales_long p_src\n"
						+ "PIVOT (SUM(sales_amount) FOR month_name IN ('jan_sales', 'feb_sales')) p\n"
						+ "JOIN monthly_sales u_src\n"
						+ "UNPIVOT (sales_amount FOR month_name IN (jan_sales, feb_sales)) u\n"
						+ "  ON p.jan_sales_SUM = u.sales_amount AND u.month_name = 'jan_sales'\n"
						+ "JOIN monthly_sales_long q_src\n"
						+ "PIVOT (SUM(sales_amount) FOR month_name IN ('feb_sales')) q\n"
						+ "  ON u.sales_amount = q.feb_sales_SUM\n"
						+ "GROUP BY p.jan_sales_SUM, u.month_name, u.sales_amount\n"
						+ "HAVING p.jan_sales_SUM > 0 AND u.sales_amount > 10 AND q.feb_sales_SUM > 0\n"
						+ "ORDER BY p.jan_sales_SUM, u.month_name;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertDiagnosticAtPosition(
				extractor.getSnippet(),
				"AMBIGUOUS_COLUMN_REFERENCE",
				ParseDiagnostic.Severity.SEVERE_WARNING,
				"Ambiguous column reference 'month_name' at (l:1 c:26). Possible sources: [p, q]",
				"month_name",
				1,
				26);
		assertDiagnosticAtPosition(
				extractor.getSnippet(),
				"AMBIGUOUS_COLUMN_REFERENCE",
				ParseDiagnostic.Severity.SEVERE_WARNING,
				"Ambiguous column reference 'sales_amount' at (l:1 c:40). Possible sources: [p, q]",
				"sales_amount",
				1,
				40);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=jan_sales_SUM, table_ref=p}}, 2={column={name=month_name, table_ref=u}}, 3={column={name=sales_amount, table_ref=u}}}, having={and={1={condition={left={column={name=jan_sales_SUM, table_ref=p}}, right={literal=0}, operator=>}}, 2={condition={left={column={name=sales_amount, table_ref=u}}, right={literal=10}, operator=>}}, 3={condition={left={column={name=feb_sales_SUM, table_ref=q}}, right={literal=0}, operator=>}}}}, orderby={1={null_order=null, predicand={column={name=jan_sales_SUM, table_ref=p}}, sort_order=ASC}, 2={null_order=null, predicand={column={name=month_name, table_ref=u}}, sort_order=ASC}}, from={join={1={pivot={value={function={function_name=SUM, parameters={column={name=sales_amount, table_ref=null}}}}, for={column={name=month_name, table_ref=null}}, in={1={pivot_literal='jan_sales'}, 2={pivot_literal='feb_sales'}}}, alias=p, table={alias=p_src, table=monthly_sales_long}}, 2={join=JOIN, on={and={1={condition={left={column={name=jan_sales_SUM, table_ref=p}}, right={column={name=sales_amount, table_ref=u}}, operator==}}, 2={condition={left={column={name=month_name, table_ref=u}}, right={literal='jan_sales'}, operator==}}}}}, 3={unpivot={value={column={name=sales_amount, table_ref=null}}, for={column={name=month_name, table_ref=null}}, in={1={name=jan_sales, table_ref=null}, 2={name=feb_sales, table_ref=null}}}, alias=u, table={alias=u_src, table=monthly_sales}}, 4={join=JOIN, on={condition={left={column={name=sales_amount, table_ref=u}}, right={column={name=feb_sales_SUM, table_ref=q}}, operator==}}}, 5={pivot={value={function={function_name=SUM, parameters={column={name=sales_amount, table_ref=null}}}}, for={column={name=month_name, table_ref=null}}, in={1={pivot_literal='feb_sales'}}}, alias=q, table={alias=q_src, table=monthly_sales_long}}}}, groupby={1={column={name=jan_sales_SUM, table_ref=p}}, 2={column={name=month_name, table_ref=u}}, 3={column={name=sales_amount, table_ref=u}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[jan_sales_SUM, month_name, sales_amount]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{monthly_sales={jan_sales=[[@41,222:230='jan_sales',<381>,5:41]], feb_sales=[[@43,233:241='feb_sales',<381>,5:52]]}, monthly_sales_long={month_name=[[@22,112:121='month_name',<381>,3:29], [@71,375:384='month_name',<381>,8:29]], sales_amount=[[@19,94:105='sales_amount',<381>,3:11], [@68,357:368='sales_amount',<381>,8:11]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query2={jan_sales_SUM=[[@3,9:21='jan_sales_SUM',<381>,1:9], [@50,254:266='jan_sales_SUM',<381>,6:7], [@102,508:520='jan_sales_SUM',<381>,11:9], [@90,455:467='jan_sales_SUM',<381>,10:11], [@121,585:597='jan_sales_SUM',<381>,12:11], [@17,90:92='SUM',<141>,3:7], [@25,127:137=''jan_sales'',<389>,3:44]], month_name=[[@7,26:35='month_name',<381>,1:26], [@58,291:300='month_name',<381>,6:44], [@94,472:481='month_name',<381>,10:28], [@125,602:611='month_name',<381>,12:28], [@38,207:216='month_name',<381>,5:26]], sales_amount=[[@11,40:51='sales_amount',<381>,1:40], [@54,272:283='sales_amount',<381>,6:25], [@81,413:424='sales_amount',<381>,9:7], [@108,532:543='sales_amount',<381>,11:33], [@98,486:497='sales_amount',<381>,10:42], [@36,190:201='sales_amount',<381>,5:9]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query2={query_dictionary={jan_sales_SUM=[[@3,9:21='jan_sales_SUM',<381>,1:9], [@50,254:266='jan_sales_SUM',<381>,6:7], [@102,508:520='jan_sales_SUM',<381>,11:9], [@90,455:467='jan_sales_SUM',<381>,10:11], [@121,585:597='jan_sales_SUM',<381>,12:11], [@17,90:92='SUM',<141>,3:7], [@25,127:137=''jan_sales'',<389>,3:44]], month_name=[[@7,26:35='month_name',<381>,1:26], [@58,291:300='month_name',<381>,6:44], [@94,472:481='month_name',<381>,10:28], [@125,602:611='month_name',<381>,12:28], [@38,207:216='month_name',<381>,5:26]], sales_amount=[[@11,40:51='sales_amount',<381>,1:40], [@54,272:283='sales_amount',<381>,6:25], [@81,413:424='sales_amount',<381>,9:7], [@108,532:543='sales_amount',<381>,11:33], [@98,486:497='sales_amount',<381>,10:42], [@36,190:201='sales_amount',<381>,5:9]]}, table_dictionary={monthly_sales={jan_sales=[[@41,222:230='jan_sales',<381>,5:41]], feb_sales=[[@43,233:241='feb_sales',<381>,5:52]]}, monthly_sales_long={month_name=[[@22,112:121='month_name',<381>,3:29], [@71,375:384='month_name',<381>,8:29]], sales_amount=[[@19,94:105='sales_amount',<381>,3:11], [@68,357:368='sales_amount',<381>,8:11]]}}, grouped_by=[{name=jan_sales_SUM, table_ref=p}, {name=month_name, table_ref=u}, {name=sales_amount, table_ref=u}], derivation={source_columns={p=[{name=month_name, table_ref=p_src}, {name=sales_amount, table_ref=p_src}], q=[{name=month_name, table_ref=q_src}, {name=sales_amount, table_ref=q_src}], u=[{name=jan_sales, table_ref=u_src}, {name=feb_sales, table_ref=u_src}]}, derived_columns={p={jan_sales_SUM=[[@17,90:92='SUM',<141>,3:7], [@25,127:137=''jan_sales'',<389>,3:44]], feb_sales_SUM=[[@17,90:92='SUM',<141>,3:7], [@27,140:150=''feb_sales'',<389>,3:57]]}, q={feb_sales_SUM=[[@66,353:355='SUM',<141>,8:7], [@74,390:400=''feb_sales'',<389>,8:44]]}, u={sales_amount=[[@36,190:201='sales_amount',<381>,5:9]], month_name=[[@38,207:216='month_name',<381>,5:26]]}}}, ordered_by=[{name=jan_sales_SUM, table_ref=p}, {name=month_name, table_ref=u}], filters=[{name=jan_sales_SUM, table_ref=p}, {name=sales_amount, table_ref=u}, {name=month_name, table_ref=u}, {name=feb_sales_SUM, table_ref=q}], interface={jan_sales_SUM=[{name=jan_sales_SUM, table_ref=p}, {name=month_name, table_ref=p_src}, {name=sales_amount, table_ref=p_src}], month_name=[{name=month_name, table_ref=u}, {name=jan_sales, table_ref=u_src}, {name=feb_sales, table_ref=u_src}], sales_amount=[{name=sales_amount, table_ref=u}, {name=jan_sales, table_ref=u_src}, {name=feb_sales, table_ref=u_src}]}, table_alias={p=p_src, q=q_src, p_src=monthly_sales_long, q_src=monthly_sales_long, u=u_src, u_src=monthly_sales}}}",
				extractor.getSymbolTable().toString());

	}

	/**
	 * Matrix: subset=E | topo=S3 (U–P–U) | bucket=GROUP_BY | kind=derived (unqualified) | outcome=unhappy.
	 */
	@Test
	public void gapFill17_7_7_S3UnpivotPivotUnpivotGroupByAmbiguousDerivedFatalV1Test() {
		final String query =
				"SELECT p.jan_sales_SUM\n"
						+ "FROM monthly_sales u1_src\n"
						+ "UNPIVOT (sales_amount FOR month_name IN (jan_sales, feb_sales)) u1\n"
						+ "JOIN monthly_sales_long p_src\n"
						+ "PIVOT (SUM(sales_amount) FOR month_name IN ('jan_sales')) p\n"
						+ "  ON u1.sales_amount = p.jan_sales_SUM\n"
						+ "JOIN monthly_sales u2_src\n"
						+ "UNPIVOT (sales_amount FOR month_name IN (feb_sales, mar_sales)) u2\n"
						+ "  ON p.jan_sales_SUM = u2.sales_amount\n"
						+ "GROUP BY sales_amount;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertFatalDiagnosticAtPosition(
				extractor.getSnippet(),
				"AMBIGUOUS_DERIVED_COLUMN_REFERENCE",
				"Ambiguous derived column reference 'sales_amount' at (l:10 c:9). Possible sources: [u1, u2]",
				"sales_amount",
				10,
				9);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=jan_sales_SUM, table_ref=p}}}, from={join={1={unpivot={value={column={name=sales_amount, table_ref=null}}, for={column={name=month_name, table_ref=null}}, in={1={name=jan_sales, table_ref=null}, 2={name=feb_sales, table_ref=null}}}, alias=u1, table={alias=u1_src, table=monthly_sales}}, 2={join=JOIN, on={condition={left={column={name=sales_amount, table_ref=u1}}, right={column={name=jan_sales_SUM, table_ref=p}}, operator==}}}, 3={pivot={value={function={function_name=SUM, parameters={column={name=sales_amount, table_ref=null}}}}, for={column={name=month_name, table_ref=null}}, in={1={pivot_literal='jan_sales'}}}, alias=p, table={alias=p_src, table=monthly_sales_long}}, 4={join=JOIN, on={condition={left={column={name=jan_sales_SUM, table_ref=p}}, right={column={name=sales_amount, table_ref=u2}}, operator==}}}, 5={unpivot={value={column={name=sales_amount, table_ref=null}}, for={column={name=month_name, table_ref=null}}, in={1={name=feb_sales, table_ref=null}, 2={name=mar_sales, table_ref=null}}}, alias=u2, table={alias=u2_src, table=monthly_sales}}}}, groupby={1={column={name=sales_amount, table_ref=null}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[jan_sales_SUM]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{monthly_sales={jan_sales=[[@14,90:98='jan_sales',<381>,3:41]], mar_sales=[[@57,323:331='mar_sales',<381>,8:52]], feb_sales=[[@55,312:320='feb_sales',<381>,8:41], [@16,101:109='feb_sales',<381>,3:52]]}, monthly_sales_long={month_name=[[@30,175:184='month_name',<381>,5:29]], sales_amount=[[@27,157:168='sales_amount',<381>,5:11]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query1={jan_sales_SUM=[[@3,9:21='jan_sales_SUM',<381>,1:9], [@44,231:243='jan_sales_SUM',<381>,6:25], [@64,345:357='jan_sales_SUM',<381>,9:7], [@25,153:155='SUM',<141>,5:7], [@33,190:200=''jan_sales'',<389>,5:44]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query1={query_dictionary={jan_sales_SUM=[[@3,9:21='jan_sales_SUM',<381>,1:9], [@44,231:243='jan_sales_SUM',<381>,6:25], [@64,345:357='jan_sales_SUM',<381>,9:7], [@25,153:155='SUM',<141>,5:7], [@33,190:200=''jan_sales'',<389>,5:44]]}, table_dictionary={monthly_sales={jan_sales=[[@14,90:98='jan_sales',<381>,3:41]], mar_sales=[[@57,323:331='mar_sales',<381>,8:52]], feb_sales=[[@55,312:320='feb_sales',<381>,8:41], [@16,101:109='feb_sales',<381>,3:52]]}, monthly_sales_long={month_name=[[@30,175:184='month_name',<381>,5:29]], sales_amount=[[@27,157:168='sales_amount',<381>,5:11]]}}, grouped_by=[{name=sales_amount, table_ref=null}], derivation={source_columns={p=[{name=month_name, table_ref=p_src}, {name=sales_amount, table_ref=p_src}], u1=[{name=jan_sales, table_ref=u1_src}, {name=feb_sales, table_ref=u1_src}], u2=[{name=feb_sales, table_ref=u2_src}, {name=mar_sales, table_ref=u2_src}]}, derived_columns={p={jan_sales_SUM=[[@25,153:155='SUM',<141>,5:7], [@33,190:200=''jan_sales'',<389>,5:44]]}, u1={sales_amount=[[@9,58:69='sales_amount',<381>,3:9]], month_name=[[@11,75:84='month_name',<381>,3:26]]}, u2={sales_amount=[[@50,280:291='sales_amount',<381>,8:9]], month_name=[[@52,297:306='month_name',<381>,8:26]]}}}, filters=[{name=sales_amount, table_ref=u1}, {name=jan_sales_SUM, table_ref=p}, {name=sales_amount, table_ref=u2}], interface={jan_sales_SUM=[{name=jan_sales_SUM, table_ref=p}, {name=month_name, table_ref=p_src}, {name=sales_amount, table_ref=p_src}]}, table_alias={p=p_src, p_src=monthly_sales_long, u2_src=monthly_sales, u1_src=monthly_sales, u1=u1_src, u2=u2_src}}}",
				extractor.getSymbolTable().toString());

	}

	/**
	 * Matrix: subset=E | topo=S2-PU | bucket=WHERE,GROUP_BY,HAVING,ORDER_BY | kind=derived (qualified) |
	 * outcome=happy.
	 */
	@Test
	public void gapFill17_7_7_S2PuPivotUnpivotJoinClauseEgressDerivedV1Test() {
		final String query =
				"SELECT p.jan_sales_SUM, u.month_name, u.sales_amount\n"
						+ "FROM monthly_sales_long p_src\n"
						+ "PIVOT (SUM(sales_amount) FOR month_name IN ('jan_sales')) p\n"
						+ "JOIN monthly_sales u_src\n"
						+ "UNPIVOT (sales_amount FOR month_name IN (jan_sales, feb_sales)) u\n"
						+ "  ON p.jan_sales_SUM = u.sales_amount AND u.month_name = 'jan_sales'\n"
						+ "WHERE p.jan_sales_SUM > 0\n"
						+ "GROUP BY p.jan_sales_SUM, u.month_name, u.sales_amount\n"
						+ "HAVING u.sales_amount > 10\n"
						+ "ORDER BY p.jan_sales_SUM, u.month_name;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=jan_sales_SUM, table_ref=p}}, 2={column={name=month_name, table_ref=u}}, 3={column={name=sales_amount, table_ref=u}}}, having={condition={left={column={name=sales_amount, table_ref=u}}, right={literal=10}, operator=>}}, orderby={1={null_order=null, predicand={column={name=jan_sales_SUM, table_ref=p}}, sort_order=ASC}, 2={null_order=null, predicand={column={name=month_name, table_ref=u}}, sort_order=ASC}}, from={join={1={pivot={value={function={function_name=SUM, parameters={column={name=sales_amount, table_ref=null}}}}, for={column={name=month_name, table_ref=null}}, in={1={pivot_literal='jan_sales'}}}, alias=p, table={alias=p_src, table=monthly_sales_long}}, 2={join=JOIN, on={and={1={condition={left={column={name=jan_sales_SUM, table_ref=p}}, right={column={name=sales_amount, table_ref=u}}, operator==}}, 2={condition={left={column={name=month_name, table_ref=u}}, right={literal='jan_sales'}, operator==}}}}}, 3={unpivot={value={column={name=sales_amount, table_ref=null}}, for={column={name=month_name, table_ref=null}}, in={1={name=jan_sales, table_ref=null}, 2={name=feb_sales, table_ref=null}}}, alias=u, table={alias=u_src, table=monthly_sales}}}}, where={condition={left={column={name=jan_sales_SUM, table_ref=p}}, right={literal=0}, operator=>}}, groupby={1={column={name=jan_sales_SUM, table_ref=p}}, 2={column={name=month_name, table_ref=u}}, 3={column={name=sales_amount, table_ref=u}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[jan_sales_SUM, month_name, sales_amount]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{monthly_sales={jan_sales=[[@39,209:217='jan_sales',<381>,5:41]], feb_sales=[[@41,220:228='feb_sales',<381>,5:52]]}, monthly_sales_long={month_name=[[@22,112:121='month_name',<381>,3:29]], sales_amount=[[@19,94:105='sales_amount',<381>,3:11]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query1={jan_sales_SUM=[[@3,9:21='jan_sales_SUM',<381>,1:9], [@48,241:253='jan_sales_SUM',<381>,6:7], [@62,311:323='jan_sales_SUM',<381>,7:8], [@69,340:352='jan_sales_SUM',<381>,8:11], [@88,422:434='jan_sales_SUM',<381>,10:11], [@17,90:92='SUM',<141>,3:7], [@25,127:137=''jan_sales'',<389>,3:44]], month_name=[[@7,26:35='month_name',<381>,1:26], [@56,278:287='month_name',<381>,6:44], [@73,357:366='month_name',<381>,8:28], [@92,439:448='month_name',<381>,10:28], [@36,194:203='month_name',<381>,5:26]], sales_amount=[[@11,40:51='sales_amount',<381>,1:40], [@52,259:270='sales_amount',<381>,6:25], [@81,393:404='sales_amount',<381>,9:9], [@77,371:382='sales_amount',<381>,8:42], [@34,177:188='sales_amount',<381>,5:9]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query1={query_dictionary={jan_sales_SUM=[[@3,9:21='jan_sales_SUM',<381>,1:9], [@48,241:253='jan_sales_SUM',<381>,6:7], [@62,311:323='jan_sales_SUM',<381>,7:8], [@69,340:352='jan_sales_SUM',<381>,8:11], [@88,422:434='jan_sales_SUM',<381>,10:11], [@17,90:92='SUM',<141>,3:7], [@25,127:137=''jan_sales'',<389>,3:44]], month_name=[[@7,26:35='month_name',<381>,1:26], [@56,278:287='month_name',<381>,6:44], [@73,357:366='month_name',<381>,8:28], [@92,439:448='month_name',<381>,10:28], [@36,194:203='month_name',<381>,5:26]], sales_amount=[[@11,40:51='sales_amount',<381>,1:40], [@52,259:270='sales_amount',<381>,6:25], [@81,393:404='sales_amount',<381>,9:9], [@77,371:382='sales_amount',<381>,8:42], [@34,177:188='sales_amount',<381>,5:9]]}, table_dictionary={monthly_sales={jan_sales=[[@39,209:217='jan_sales',<381>,5:41]], feb_sales=[[@41,220:228='feb_sales',<381>,5:52]]}, monthly_sales_long={month_name=[[@22,112:121='month_name',<381>,3:29]], sales_amount=[[@19,94:105='sales_amount',<381>,3:11]]}}, grouped_by=[{name=jan_sales_SUM, table_ref=p}, {name=month_name, table_ref=u}, {name=sales_amount, table_ref=u}], derivation={source_columns={p=[{name=month_name, table_ref=p_src}, {name=sales_amount, table_ref=p_src}], u=[{name=jan_sales, table_ref=u_src}, {name=feb_sales, table_ref=u_src}]}, derived_columns={p={jan_sales_SUM=[[@17,90:92='SUM',<141>,3:7], [@25,127:137=''jan_sales'',<389>,3:44]]}, u={sales_amount=[[@34,177:188='sales_amount',<381>,5:9]], month_name=[[@36,194:203='month_name',<381>,5:26]]}}}, ordered_by=[{name=jan_sales_SUM, table_ref=p}, {name=month_name, table_ref=u}], filters=[{name=jan_sales_SUM, table_ref=p}, {name=sales_amount, table_ref=u}, {name=month_name, table_ref=u}], interface={jan_sales_SUM=[{name=jan_sales_SUM, table_ref=p}, {name=month_name, table_ref=p_src}, {name=sales_amount, table_ref=p_src}], month_name=[{name=month_name, table_ref=u}, {name=jan_sales, table_ref=u_src}, {name=feb_sales, table_ref=u_src}], sales_amount=[{name=sales_amount, table_ref=u}, {name=jan_sales, table_ref=u_src}, {name=feb_sales, table_ref=u_src}]}, table_alias={p=p_src, p_src=monthly_sales_long, u=u_src, u_src=monthly_sales}}}",
				extractor.getSymbolTable().toString());

	}

	/** Matrix: subset=E | topo=S3 (U–P–U) | bucket=ORDER_BY | kind=derived | outcome=unhappy. */
	@Test
	public void gapFill17_7_7_S3UnpivotPivotUnpivotOrderByAmbiguousDerivedMonthNameFatalV1Test() {
		final String query =
				"SELECT p.jan_sales_SUM\n"
						+ "FROM monthly_sales u1_src\n"
						+ "UNPIVOT (sales_amount FOR month_name IN (jan_sales, feb_sales)) u1\n"
						+ "JOIN monthly_sales_long p_src\n"
						+ "PIVOT (SUM(sales_amount) FOR month_name IN ('jan_sales')) p\n"
						+ "  ON u1.sales_amount = p.jan_sales_SUM\n"
						+ "JOIN monthly_sales u2_src\n"
						+ "UNPIVOT (sales_amount FOR month_name IN (feb_sales, mar_sales)) u2\n"
						+ "  ON p.jan_sales_SUM = u2.sales_amount\n"
						+ "ORDER BY month_name;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertFatalDiagnosticAtPosition(
				extractor.getSnippet(),
				"AMBIGUOUS_DERIVED_COLUMN_REFERENCE",
				"Ambiguous derived column reference 'month_name' at (l:10 c:9). Possible sources: [u1, u2]",
				"month_name",
				10,
				9);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=jan_sales_SUM, table_ref=p}}}, orderby={1={null_order=null, predicand={column={name=month_name, table_ref=null}}, sort_order=ASC}}, from={join={1={unpivot={value={column={name=sales_amount, table_ref=null}}, for={column={name=month_name, table_ref=null}}, in={1={name=jan_sales, table_ref=null}, 2={name=feb_sales, table_ref=null}}}, alias=u1, table={alias=u1_src, table=monthly_sales}}, 2={join=JOIN, on={condition={left={column={name=sales_amount, table_ref=u1}}, right={column={name=jan_sales_SUM, table_ref=p}}, operator==}}}, 3={pivot={value={function={function_name=SUM, parameters={column={name=sales_amount, table_ref=null}}}}, for={column={name=month_name, table_ref=null}}, in={1={pivot_literal='jan_sales'}}}, alias=p, table={alias=p_src, table=monthly_sales_long}}, 4={join=JOIN, on={condition={left={column={name=jan_sales_SUM, table_ref=p}}, right={column={name=sales_amount, table_ref=u2}}, operator==}}}, 5={unpivot={value={column={name=sales_amount, table_ref=null}}, for={column={name=month_name, table_ref=null}}, in={1={name=feb_sales, table_ref=null}, 2={name=mar_sales, table_ref=null}}}, alias=u2, table={alias=u2_src, table=monthly_sales}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[jan_sales_SUM]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{monthly_sales={jan_sales=[[@14,90:98='jan_sales',<381>,3:41]], mar_sales=[[@57,323:331='mar_sales',<381>,8:52]], feb_sales=[[@55,312:320='feb_sales',<381>,8:41], [@16,101:109='feb_sales',<381>,3:52]]}, monthly_sales_long={month_name=[[@30,175:184='month_name',<381>,5:29]], sales_amount=[[@27,157:168='sales_amount',<381>,5:11]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query1={jan_sales_SUM=[[@3,9:21='jan_sales_SUM',<381>,1:9], [@44,231:243='jan_sales_SUM',<381>,6:25], [@64,345:357='jan_sales_SUM',<381>,9:7], [@25,153:155='SUM',<141>,5:7], [@33,190:200=''jan_sales'',<389>,5:44]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query1={query_dictionary={jan_sales_SUM=[[@3,9:21='jan_sales_SUM',<381>,1:9], [@44,231:243='jan_sales_SUM',<381>,6:25], [@64,345:357='jan_sales_SUM',<381>,9:7], [@25,153:155='SUM',<141>,5:7], [@33,190:200=''jan_sales'',<389>,5:44]]}, table_dictionary={monthly_sales={jan_sales=[[@14,90:98='jan_sales',<381>,3:41]], mar_sales=[[@57,323:331='mar_sales',<381>,8:52]], feb_sales=[[@55,312:320='feb_sales',<381>,8:41], [@16,101:109='feb_sales',<381>,3:52]]}, monthly_sales_long={month_name=[[@30,175:184='month_name',<381>,5:29]], sales_amount=[[@27,157:168='sales_amount',<381>,5:11]]}}, derivation={source_columns={p=[{name=month_name, table_ref=p_src}, {name=sales_amount, table_ref=p_src}], u1=[{name=jan_sales, table_ref=u1_src}, {name=feb_sales, table_ref=u1_src}], u2=[{name=feb_sales, table_ref=u2_src}, {name=mar_sales, table_ref=u2_src}]}, derived_columns={p={jan_sales_SUM=[[@25,153:155='SUM',<141>,5:7], [@33,190:200=''jan_sales'',<389>,5:44]]}, u1={sales_amount=[[@9,58:69='sales_amount',<381>,3:9]], month_name=[[@11,75:84='month_name',<381>,3:26]]}, u2={sales_amount=[[@50,280:291='sales_amount',<381>,8:9]], month_name=[[@52,297:306='month_name',<381>,8:26]]}}}, ordered_by=[{name=month_name, table_ref=null}], filters=[{name=sales_amount, table_ref=u1}, {name=jan_sales_SUM, table_ref=p}, {name=sales_amount, table_ref=u2}], interface={jan_sales_SUM=[{name=jan_sales_SUM, table_ref=p}, {name=month_name, table_ref=p_src}, {name=sales_amount, table_ref=p_src}]}, table_alias={p=p_src, p_src=monthly_sales_long, u2_src=monthly_sales, u1_src=monthly_sales, u1=u1_src, u2=u2_src}}}",
				extractor.getSymbolTable().toString());

	}

	/** Matrix: subset=E | topo=S3 (P–U–P) | bucket=HAVING | kind=derived | outcome=unhappy. */
	@Test
	public void gapFill17_7_7_S3PivotUnpivotPivotHavingAmbiguousDerivedFebSalesSumFatalV1Test() {
		final String query =
				"SELECT p.jan_sales_SUM, u.month_name, u.sales_amount\n"
						+ "FROM monthly_sales_long p_src\n"
						+ "PIVOT (SUM(sales_amount) FOR month_name IN ('jan_sales', 'feb_sales')) p\n"
						+ "JOIN monthly_sales u_src\n"
						+ "UNPIVOT (sales_amount FOR month_name IN (jan_sales, feb_sales)) u\n"
						+ "  ON p.jan_sales_SUM = u.sales_amount AND u.month_name = 'jan_sales'\n"
						+ "JOIN monthly_sales_long q_src\n"
						+ "PIVOT (SUM(sales_amount) FOR month_name IN ('feb_sales')) q\n"
						+ "  ON u.sales_amount = q.feb_sales_SUM\n"
						+ "HAVING feb_sales_SUM > 0;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertFatalDiagnosticAtPosition(
				extractor.getSnippet(),
				"AMBIGUOUS_DERIVED_COLUMN_REFERENCE",
				"Ambiguous derived column reference 'feb_sales_SUM' at (l:10 c:7). Possible sources: [p, q]",
				"feb_sales_SUM",
				10,
				7);
		assertDiagnosticAtPosition(
				extractor.getSnippet(),
				"AMBIGUOUS_COLUMN_REFERENCE",
				ParseDiagnostic.Severity.SEVERE_WARNING,
				"Ambiguous column reference 'month_name' at (l:1 c:26). Possible sources: [p, q]",
				"month_name",
				1,
				26);
		assertDiagnosticAtPosition(
				extractor.getSnippet(),
				"AMBIGUOUS_COLUMN_REFERENCE",
				ParseDiagnostic.Severity.SEVERE_WARNING,
				"Ambiguous column reference 'sales_amount' at (l:1 c:40). Possible sources: [p, q]",
				"sales_amount",
				1,
				40);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=jan_sales_SUM, table_ref=p}}, 2={column={name=month_name, table_ref=u}}, 3={column={name=sales_amount, table_ref=u}}}, having={condition={left={column={name=feb_sales_SUM, table_ref=null}}, right={literal=0}, operator=>}}, from={join={1={pivot={value={function={function_name=SUM, parameters={column={name=sales_amount, table_ref=null}}}}, for={column={name=month_name, table_ref=null}}, in={1={pivot_literal='jan_sales'}, 2={pivot_literal='feb_sales'}}}, alias=p, table={alias=p_src, table=monthly_sales_long}}, 2={join=JOIN, on={and={1={condition={left={column={name=jan_sales_SUM, table_ref=p}}, right={column={name=sales_amount, table_ref=u}}, operator==}}, 2={condition={left={column={name=month_name, table_ref=u}}, right={literal='jan_sales'}, operator==}}}}}, 3={unpivot={value={column={name=sales_amount, table_ref=null}}, for={column={name=month_name, table_ref=null}}, in={1={name=jan_sales, table_ref=null}, 2={name=feb_sales, table_ref=null}}}, alias=u, table={alias=u_src, table=monthly_sales}}, 4={join=JOIN, on={condition={left={column={name=sales_amount, table_ref=u}}, right={column={name=feb_sales_SUM, table_ref=q}}, operator==}}}, 5={pivot={value={function={function_name=SUM, parameters={column={name=sales_amount, table_ref=null}}}}, for={column={name=month_name, table_ref=null}}, in={1={pivot_literal='feb_sales'}}}, alias=q, table={alias=q_src, table=monthly_sales_long}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[jan_sales_SUM, month_name, sales_amount]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{monthly_sales={jan_sales=[[@41,222:230='jan_sales',<381>,5:41]], feb_sales=[[@43,233:241='feb_sales',<381>,5:52]]}, monthly_sales_long={month_name=[[@22,112:121='month_name',<381>,3:29], [@71,375:384='month_name',<381>,8:29]], sales_amount=[[@19,94:105='sales_amount',<381>,3:11], [@68,357:368='sales_amount',<381>,8:11]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query2={jan_sales_SUM=[[@3,9:21='jan_sales_SUM',<381>,1:9], [@50,254:266='jan_sales_SUM',<381>,6:7], [@17,90:92='SUM',<141>,3:7], [@25,127:137=''jan_sales'',<389>,3:44]], month_name=[[@7,26:35='month_name',<381>,1:26], [@58,291:300='month_name',<381>,6:44], [@38,207:216='month_name',<381>,5:26]], sales_amount=[[@11,40:51='sales_amount',<381>,1:40], [@54,272:283='sales_amount',<381>,6:25], [@81,413:424='sales_amount',<381>,9:7], [@36,190:201='sales_amount',<381>,5:9]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query2={query_dictionary={jan_sales_SUM=[[@3,9:21='jan_sales_SUM',<381>,1:9], [@50,254:266='jan_sales_SUM',<381>,6:7], [@17,90:92='SUM',<141>,3:7], [@25,127:137=''jan_sales'',<389>,3:44]], month_name=[[@7,26:35='month_name',<381>,1:26], [@58,291:300='month_name',<381>,6:44], [@38,207:216='month_name',<381>,5:26]], sales_amount=[[@11,40:51='sales_amount',<381>,1:40], [@54,272:283='sales_amount',<381>,6:25], [@81,413:424='sales_amount',<381>,9:7], [@36,190:201='sales_amount',<381>,5:9]]}, table_dictionary={monthly_sales={jan_sales=[[@41,222:230='jan_sales',<381>,5:41]], feb_sales=[[@43,233:241='feb_sales',<381>,5:52]]}, monthly_sales_long={month_name=[[@22,112:121='month_name',<381>,3:29], [@71,375:384='month_name',<381>,8:29]], sales_amount=[[@19,94:105='sales_amount',<381>,3:11], [@68,357:368='sales_amount',<381>,8:11]]}}, derivation={source_columns={p=[{name=month_name, table_ref=p_src}, {name=sales_amount, table_ref=p_src}], q=[{name=month_name, table_ref=q_src}, {name=sales_amount, table_ref=q_src}], u=[{name=jan_sales, table_ref=u_src}, {name=feb_sales, table_ref=u_src}]}, derived_columns={p={jan_sales_SUM=[[@17,90:92='SUM',<141>,3:7], [@25,127:137=''jan_sales'',<389>,3:44]], feb_sales_SUM=[[@17,90:92='SUM',<141>,3:7], [@27,140:150=''feb_sales'',<389>,3:57]]}, q={feb_sales_SUM=[[@66,353:355='SUM',<141>,8:7], [@74,390:400=''feb_sales'',<389>,8:44]]}, u={sales_amount=[[@36,190:201='sales_amount',<381>,5:9]], month_name=[[@38,207:216='month_name',<381>,5:26]]}}}, filters=[{name=jan_sales_SUM, table_ref=p}, {name=sales_amount, table_ref=u}, {name=month_name, table_ref=u}, {name=feb_sales_SUM, table_ref=q}, {name=feb_sales_SUM, table_ref=null}], interface={jan_sales_SUM=[{name=jan_sales_SUM, table_ref=p}, {name=month_name, table_ref=p_src}, {name=sales_amount, table_ref=p_src}], month_name=[{name=month_name, table_ref=u}, {name=jan_sales, table_ref=u_src}, {name=feb_sales, table_ref=u_src}], sales_amount=[{name=sales_amount, table_ref=u}, {name=jan_sales, table_ref=u_src}, {name=feb_sales, table_ref=u_src}]}, table_alias={p=p_src, q=q_src, p_src=monthly_sales_long, q_src=monthly_sales_long, u=u_src, u_src=monthly_sales}}}",
				extractor.getSymbolTable().toString());

	}

	/** Matrix: subset=E | topo=S2-PP | bucket=GROUP_BY | kind=derived | outcome=unhappy. */
	@Test
	public void gapFill17_7_7_S2PpDualPivotGroupByAmbiguousDerivedJanSalesSumFatalV1Test() {
		final String query =
				"SELECT 1 AS row_tag\n"
						+ "FROM monthly_sales_long p_src\n"
						+ "PIVOT (SUM(sales_amount) FOR month_name IN ('jan_sales')) p\n"
						+ "JOIN monthly_sales_long q_src\n"
						+ "PIVOT (SUM(sales_amount) FOR month_name IN ('jan_sales')) q\n"
						+ "  ON p.jan_sales_SUM = q.jan_sales_SUM\n"
						+ "GROUP BY jan_sales_SUM;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertFatalDiagnosticAtPosition(
				extractor.getSnippet(),
				"AMBIGUOUS_DERIVED_COLUMN_REFERENCE",
				"Ambiguous derived column reference 'jan_sales_SUM' at (l:7 c:9). Possible sources: [p, q]",
				"jan_sales_SUM",
				7,
				9);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={alias=row_tag, literal=1}}, from={join={1={pivot={value={function={function_name=SUM, parameters={column={name=sales_amount, table_ref=null}}}}, for={column={name=month_name, table_ref=null}}, in={1={pivot_literal='jan_sales'}}}, alias=p, table={alias=p_src, table=monthly_sales_long}}, 2={join=JOIN, on={condition={left={column={name=jan_sales_SUM, table_ref=p}}, right={column={name=jan_sales_SUM, table_ref=q}}, operator==}}}, 3={pivot={value={function={function_name=SUM, parameters={column={name=sales_amount, table_ref=null}}}}, for={column={name=month_name, table_ref=null}}, in={1={pivot_literal='jan_sales'}}}, alias=q, table={alias=q_src, table=monthly_sales_long}}}}, groupby={1={column={name=jan_sales_SUM, table_ref=null}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[row_tag]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{monthly_sales_long={month_name=[[@14,79:88='month_name',<381>,3:29], [@31,169:178='month_name',<381>,5:29]], sales_amount=[[@11,61:72='sales_amount',<381>,3:11], [@28,151:162='sales_amount',<381>,5:11]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query2={row_tag=[[@3,12:18='row_tag',<381>,1:12]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query2={query_dictionary={row_tag=[[@3,12:18='row_tag',<381>,1:12]]}, table_dictionary={monthly_sales_long={month_name=[[@14,79:88='month_name',<381>,3:29], [@31,169:178='month_name',<381>,5:29]], sales_amount=[[@11,61:72='sales_amount',<381>,3:11], [@28,151:162='sales_amount',<381>,5:11]]}}, grouped_by=[{name=jan_sales_SUM, table_ref=null}], derivation={source_columns={p=[{name=month_name, table_ref=p_src}, {name=sales_amount, table_ref=p_src}], q=[{name=month_name, table_ref=q_src}, {name=sales_amount, table_ref=q_src}]}, derived_columns={p={jan_sales_SUM=[[@9,57:59='SUM',<141>,3:7], [@17,94:104=''jan_sales'',<389>,3:44]]}, q={jan_sales_SUM=[[@26,147:149='SUM',<141>,5:7], [@34,184:194=''jan_sales'',<389>,5:44]]}}}, filters=[{name=jan_sales_SUM, table_ref=p}, {name=jan_sales_SUM, table_ref=q}], interface={row_tag=[]}, table_alias={p=p_src, q=q_src, p_src=monthly_sales_long, q_src=monthly_sales_long}}}",
				extractor.getSymbolTable().toString());

	}

	/** Matrix: subset=E | topo=S2-PU | bucket=QUALIFY | kind=derived (qualified) | outcome=happy. */
	@Test
	public void gapFill17_7_7_S2PuQualifyDerivedQualifiedHappyV1Test() {
		final String query =
				"SELECT p.jan_sales_SUM, u.month_name, u.sales_amount\n"
						+ "FROM monthly_sales_long p_src\n"
						+ "PIVOT (SUM(sales_amount) FOR month_name IN ('jan_sales')) p\n"
						+ "JOIN monthly_sales u_src\n"
						+ "UNPIVOT (sales_amount FOR month_name IN (jan_sales, feb_sales)) u\n"
						+ "  ON p.jan_sales_SUM = u.sales_amount AND u.month_name = 'jan_sales'\n"
						+ "QUALIFY u.sales_amount > 10;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=jan_sales_SUM, table_ref=p}}, 2={column={name=month_name, table_ref=u}}, 3={column={name=sales_amount, table_ref=u}}}, from={join={1={pivot={value={function={function_name=SUM, parameters={column={name=sales_amount, table_ref=null}}}}, for={column={name=month_name, table_ref=null}}, in={1={pivot_literal='jan_sales'}}}, alias=p, table={alias=p_src, table=monthly_sales_long}}, 2={join=JOIN, on={and={1={condition={left={column={name=jan_sales_SUM, table_ref=p}}, right={column={name=sales_amount, table_ref=u}}, operator==}}, 2={condition={left={column={name=month_name, table_ref=u}}, right={literal='jan_sales'}, operator==}}}}}, 3={unpivot={value={column={name=sales_amount, table_ref=null}}, for={column={name=month_name, table_ref=null}}, in={1={name=jan_sales, table_ref=null}, 2={name=feb_sales, table_ref=null}}}, alias=u, table={alias=u_src, table=monthly_sales}}}}, qualify={condition={left={column={name=sales_amount, table_ref=u}}, right={literal=10}, operator=>}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[jan_sales_SUM, month_name, sales_amount]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{monthly_sales={jan_sales=[[@39,209:217='jan_sales',<381>,5:41]], feb_sales=[[@41,220:228='feb_sales',<381>,5:52]]}, monthly_sales_long={month_name=[[@22,112:121='month_name',<381>,3:29]], sales_amount=[[@19,94:105='sales_amount',<381>,3:11]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query1={jan_sales_SUM=[[@3,9:21='jan_sales_SUM',<381>,1:9], [@48,241:253='jan_sales_SUM',<381>,6:7], [@17,90:92='SUM',<141>,3:7], [@25,127:137=''jan_sales'',<389>,3:44]], month_name=[[@7,26:35='month_name',<381>,1:26], [@56,278:287='month_name',<381>,6:44], [@36,194:203='month_name',<381>,5:26]], sales_amount=[[@11,40:51='sales_amount',<381>,1:40], [@52,259:270='sales_amount',<381>,6:25], [@62,313:324='sales_amount',<381>,7:10], [@34,177:188='sales_amount',<381>,5:9]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query1={query_dictionary={jan_sales_SUM=[[@3,9:21='jan_sales_SUM',<381>,1:9], [@48,241:253='jan_sales_SUM',<381>,6:7], [@17,90:92='SUM',<141>,3:7], [@25,127:137=''jan_sales'',<389>,3:44]], month_name=[[@7,26:35='month_name',<381>,1:26], [@56,278:287='month_name',<381>,6:44], [@36,194:203='month_name',<381>,5:26]], sales_amount=[[@11,40:51='sales_amount',<381>,1:40], [@52,259:270='sales_amount',<381>,6:25], [@62,313:324='sales_amount',<381>,7:10], [@34,177:188='sales_amount',<381>,5:9]]}, table_dictionary={monthly_sales={jan_sales=[[@39,209:217='jan_sales',<381>,5:41]], feb_sales=[[@41,220:228='feb_sales',<381>,5:52]]}, monthly_sales_long={month_name=[[@22,112:121='month_name',<381>,3:29]], sales_amount=[[@19,94:105='sales_amount',<381>,3:11]]}}, derivation={source_columns={p=[{name=month_name, table_ref=p_src}, {name=sales_amount, table_ref=p_src}], u=[{name=jan_sales, table_ref=u_src}, {name=feb_sales, table_ref=u_src}]}, derived_columns={p={jan_sales_SUM=[[@17,90:92='SUM',<141>,3:7], [@25,127:137=''jan_sales'',<389>,3:44]]}, u={sales_amount=[[@34,177:188='sales_amount',<381>,5:9]], month_name=[[@36,194:203='month_name',<381>,5:26]]}}}, filters=[{name=jan_sales_SUM, table_ref=p}, {name=sales_amount, table_ref=u}, {name=month_name, table_ref=u}], interface={jan_sales_SUM=[{name=jan_sales_SUM, table_ref=p}, {name=month_name, table_ref=p_src}, {name=sales_amount, table_ref=p_src}], month_name=[{name=month_name, table_ref=u}, {name=jan_sales, table_ref=u_src}, {name=feb_sales, table_ref=u_src}], sales_amount=[{name=sales_amount, table_ref=u}, {name=jan_sales, table_ref=u_src}, {name=feb_sales, table_ref=u_src}]}, table_alias={p=p_src, p_src=monthly_sales_long, u=u_src, u_src=monthly_sales}}}",
				extractor.getSymbolTable().toString());

	}

	/** Matrix: subset=E | topo=S2-PP | bucket=ORDER_BY | kind=source (unqualified) | outcome=unhappy (SEVERE). */
	@Test
	public void gapFill17_7_7_S2PpDualPivotOrderByAmbiguousSourceMonthNameSevereV1Test() {
		final String query =
				"SELECT p.jan_sales_SUM, q.feb_sales_SUM\n"
						+ "FROM monthly_sales_long p_src\n"
						+ "PIVOT (SUM(sales_amount) FOR month_name IN ('jan_sales')) p\n"
						+ "JOIN monthly_sales_long q_src\n"
						+ "PIVOT (SUM(sales_amount) FOR month_name IN ('feb_sales')) q\n"
						+ "  ON p.jan_sales_SUM > 0 AND q.feb_sales_SUM > 0\n"
						+ "ORDER BY month_name;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertDiagnosticAtPosition(
				extractor.getSnippet(),
				"AMBIGUOUS_COLUMN_REFERENCE",
				ParseDiagnostic.Severity.SEVERE_WARNING,
				"Ambiguous column reference 'month_name' at (l:7 c:9). Possible sources: [p, q]",
				"month_name",
				7,
				9);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=jan_sales_SUM, table_ref=p}}, 2={column={name=feb_sales_SUM, table_ref=q}}}, orderby={1={null_order=null, predicand={column={name=month_name, table_ref=null}}, sort_order=ASC}}, from={join={1={pivot={value={function={function_name=SUM, parameters={column={name=sales_amount, table_ref=null}}}}, for={column={name=month_name, table_ref=null}}, in={1={pivot_literal='jan_sales'}}}, alias=p, table={alias=p_src, table=monthly_sales_long}}, 2={join=JOIN, on={and={1={condition={left={column={name=jan_sales_SUM, table_ref=p}}, right={literal=0}, operator=>}}, 2={condition={left={column={name=feb_sales_SUM, table_ref=q}}, right={literal=0}, operator=>}}}}}, 3={pivot={value={function={function_name=SUM, parameters={column={name=sales_amount, table_ref=null}}}}, for={column={name=month_name, table_ref=null}}, in={1={pivot_literal='feb_sales'}}}, alias=q, table={alias=q_src, table=monthly_sales_long}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[jan_sales_SUM, feb_sales_SUM]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{monthly_sales_long={month_name=[[@18,99:108='month_name',<381>,3:29], [@35,189:198='month_name',<381>,5:29], [@56,278:287='month_name',<381>,7:9]], sales_amount=[[@15,81:92='sales_amount',<381>,3:11], [@32,171:182='sales_amount',<381>,5:11]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query2={jan_sales_SUM=[[@3,9:21='jan_sales_SUM',<381>,1:9], [@45,227:239='jan_sales_SUM',<381>,6:7], [@13,77:79='SUM',<141>,3:7], [@21,114:124=''jan_sales'',<389>,3:44]], feb_sales_SUM=[[@7,26:38='feb_sales_SUM',<381>,1:26], [@51,251:263='feb_sales_SUM',<381>,6:31], [@30,167:169='SUM',<141>,5:7], [@38,204:214=''feb_sales'',<389>,5:44]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query2={query_dictionary={jan_sales_SUM=[[@3,9:21='jan_sales_SUM',<381>,1:9], [@45,227:239='jan_sales_SUM',<381>,6:7], [@13,77:79='SUM',<141>,3:7], [@21,114:124=''jan_sales'',<389>,3:44]], feb_sales_SUM=[[@7,26:38='feb_sales_SUM',<381>,1:26], [@51,251:263='feb_sales_SUM',<381>,6:31], [@30,167:169='SUM',<141>,5:7], [@38,204:214=''feb_sales'',<389>,5:44]]}, table_dictionary={monthly_sales_long={month_name=[[@18,99:108='month_name',<381>,3:29], [@35,189:198='month_name',<381>,5:29], [@56,278:287='month_name',<381>,7:9]], sales_amount=[[@15,81:92='sales_amount',<381>,3:11], [@32,171:182='sales_amount',<381>,5:11]]}}, derivation={source_columns={p=[{name=month_name, table_ref=p_src}, {name=sales_amount, table_ref=p_src}], q=[{name=month_name, table_ref=q_src}, {name=sales_amount, table_ref=q_src}]}, derived_columns={p={jan_sales_SUM=[[@13,77:79='SUM',<141>,3:7], [@21,114:124=''jan_sales'',<389>,3:44]]}, q={feb_sales_SUM=[[@30,167:169='SUM',<141>,5:7], [@38,204:214=''feb_sales'',<389>,5:44]]}}}, ordered_by=[{name=month_name, table_ref=null}], filters=[{name=jan_sales_SUM, table_ref=p}, {name=feb_sales_SUM, table_ref=q}], interface={jan_sales_SUM=[{name=jan_sales_SUM, table_ref=p}, {name=month_name, table_ref=p_src}, {name=sales_amount, table_ref=p_src}], feb_sales_SUM=[{name=feb_sales_SUM, table_ref=q}, {name=month_name, table_ref=q_src}, {name=sales_amount, table_ref=q_src}]}, table_alias={p=p_src, q=q_src, p_src=monthly_sales_long, q_src=monthly_sales_long}}}",
				extractor.getSymbolTable().toString());

	}

	/** Matrix: subset=E | topo=S2-PP | bucket=GROUP_BY,HAVING | kind=derived (qualified) | outcome=happy. */
	@Test
	public void gapFill17_7_7_S2PpDualPivotGroupByHavingQualifiedDerivedHappyV1Test() {
		final String query =
				"SELECT p.jan_sales_SUM, q.feb_sales_SUM\n"
						+ "FROM monthly_sales_long p_src\n"
						+ "PIVOT (SUM(sales_amount) FOR month_name IN ('jan_sales')) p\n"
						+ "JOIN monthly_sales_long q_src\n"
						+ "PIVOT (SUM(sales_amount) FOR month_name IN ('feb_sales')) q\n"
						+ "  ON p.jan_sales_SUM > 0 AND q.feb_sales_SUM > 0\n"
						+ "GROUP BY p.jan_sales_SUM, q.feb_sales_SUM\n"
						+ "HAVING p.jan_sales_SUM > 0 AND q.feb_sales_SUM > 0;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=jan_sales_SUM, table_ref=p}}, 2={column={name=feb_sales_SUM, table_ref=q}}}, having={and={1={condition={left={column={name=jan_sales_SUM, table_ref=p}}, right={literal=0}, operator=>}}, 2={condition={left={column={name=feb_sales_SUM, table_ref=q}}, right={literal=0}, operator=>}}}}, from={join={1={pivot={value={function={function_name=SUM, parameters={column={name=sales_amount, table_ref=null}}}}, for={column={name=month_name, table_ref=null}}, in={1={pivot_literal='jan_sales'}}}, alias=p, table={alias=p_src, table=monthly_sales_long}}, 2={join=JOIN, on={and={1={condition={left={column={name=jan_sales_SUM, table_ref=p}}, right={literal=0}, operator=>}}, 2={condition={left={column={name=feb_sales_SUM, table_ref=q}}, right={literal=0}, operator=>}}}}}, 3={pivot={value={function={function_name=SUM, parameters={column={name=sales_amount, table_ref=null}}}}, for={column={name=month_name, table_ref=null}}, in={1={pivot_literal='feb_sales'}}}, alias=q, table={alias=q_src, table=monthly_sales_long}}}}, groupby={1={column={name=jan_sales_SUM, table_ref=p}}, 2={column={name=feb_sales_SUM, table_ref=q}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[jan_sales_SUM, feb_sales_SUM]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{monthly_sales_long={month_name=[[@18,99:108='month_name',<381>,3:29], [@35,189:198='month_name',<381>,5:29]], sales_amount=[[@15,81:92='sales_amount',<381>,3:11], [@32,171:182='sales_amount',<381>,5:11]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query2={jan_sales_SUM=[[@3,9:21='jan_sales_SUM',<381>,1:9], [@45,227:239='jan_sales_SUM',<381>,6:7], [@66,320:332='jan_sales_SUM',<381>,8:9], [@58,280:292='jan_sales_SUM',<381>,7:11], [@13,77:79='SUM',<141>,3:7], [@21,114:124=''jan_sales'',<389>,3:44]], feb_sales_SUM=[[@7,26:38='feb_sales_SUM',<381>,1:26], [@51,251:263='feb_sales_SUM',<381>,6:31], [@72,344:356='feb_sales_SUM',<381>,8:33], [@62,297:309='feb_sales_SUM',<381>,7:28], [@30,167:169='SUM',<141>,5:7], [@38,204:214=''feb_sales'',<389>,5:44]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query2={query_dictionary={jan_sales_SUM=[[@3,9:21='jan_sales_SUM',<381>,1:9], [@45,227:239='jan_sales_SUM',<381>,6:7], [@66,320:332='jan_sales_SUM',<381>,8:9], [@58,280:292='jan_sales_SUM',<381>,7:11], [@13,77:79='SUM',<141>,3:7], [@21,114:124=''jan_sales'',<389>,3:44]], feb_sales_SUM=[[@7,26:38='feb_sales_SUM',<381>,1:26], [@51,251:263='feb_sales_SUM',<381>,6:31], [@72,344:356='feb_sales_SUM',<381>,8:33], [@62,297:309='feb_sales_SUM',<381>,7:28], [@30,167:169='SUM',<141>,5:7], [@38,204:214=''feb_sales'',<389>,5:44]]}, table_dictionary={monthly_sales_long={month_name=[[@18,99:108='month_name',<381>,3:29], [@35,189:198='month_name',<381>,5:29]], sales_amount=[[@15,81:92='sales_amount',<381>,3:11], [@32,171:182='sales_amount',<381>,5:11]]}}, grouped_by=[{name=jan_sales_SUM, table_ref=p}, {name=feb_sales_SUM, table_ref=q}], derivation={source_columns={p=[{name=month_name, table_ref=p_src}, {name=sales_amount, table_ref=p_src}], q=[{name=month_name, table_ref=q_src}, {name=sales_amount, table_ref=q_src}]}, derived_columns={p={jan_sales_SUM=[[@13,77:79='SUM',<141>,3:7], [@21,114:124=''jan_sales'',<389>,3:44]]}, q={feb_sales_SUM=[[@30,167:169='SUM',<141>,5:7], [@38,204:214=''feb_sales'',<389>,5:44]]}}}, filters=[{name=jan_sales_SUM, table_ref=p}, {name=feb_sales_SUM, table_ref=q}], interface={jan_sales_SUM=[{name=jan_sales_SUM, table_ref=p}, {name=month_name, table_ref=p_src}, {name=sales_amount, table_ref=p_src}], feb_sales_SUM=[{name=feb_sales_SUM, table_ref=q}, {name=month_name, table_ref=q_src}, {name=sales_amount, table_ref=q_src}]}, table_alias={p=p_src, q=q_src, p_src=monthly_sales_long, q_src=monthly_sales_long}}}",
				extractor.getSymbolTable().toString());

	}

	/** Matrix: subset=E | topo=S3 (P–U–P) | bucket=JOIN ON | kind=derived (qualified) | outcome=happy. */
	@Test
	public void gapFill17_7_7_S3PivotUnpivotPivotJoinOnQualifiedDerivedHappyV1Test() {
		final String query =
				"SELECT p.jan_sales_SUM\n"
						+ "FROM monthly_sales_long p_src\n"
						+ "PIVOT (SUM(sales_amount) FOR month_name IN ('jan_sales', 'feb_sales')) p\n"
						+ "JOIN monthly_sales u_src\n"
						+ "UNPIVOT (sales_amount FOR month_name IN (jan_sales, feb_sales)) u\n"
						+ "  ON p.jan_sales_SUM = u.sales_amount AND u.month_name = 'jan_sales'\n"
						+ "JOIN monthly_sales_long q_src\n"
						+ "PIVOT (SUM(sales_amount) FOR month_name IN ('feb_sales')) q\n"
						+ "  ON u.sales_amount = q.feb_sales_SUM AND p.jan_sales_SUM > 0;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=jan_sales_SUM, table_ref=p}}}, from={join={1={pivot={value={function={function_name=SUM, parameters={column={name=sales_amount, table_ref=null}}}}, for={column={name=month_name, table_ref=null}}, in={1={pivot_literal='jan_sales'}, 2={pivot_literal='feb_sales'}}}, alias=p, table={alias=p_src, table=monthly_sales_long}}, 2={join=JOIN, on={and={1={condition={left={column={name=jan_sales_SUM, table_ref=p}}, right={column={name=sales_amount, table_ref=u}}, operator==}}, 2={condition={left={column={name=month_name, table_ref=u}}, right={literal='jan_sales'}, operator==}}}}}, 3={unpivot={value={column={name=sales_amount, table_ref=null}}, for={column={name=month_name, table_ref=null}}, in={1={name=jan_sales, table_ref=null}, 2={name=feb_sales, table_ref=null}}}, alias=u, table={alias=u_src, table=monthly_sales}}, 4={join=JOIN, on={and={1={condition={left={column={name=sales_amount, table_ref=u}}, right={column={name=feb_sales_SUM, table_ref=q}}, operator==}}, 2={condition={left={column={name=jan_sales_SUM, table_ref=p}}, right={literal=0}, operator=>}}}}}, 5={pivot={value={function={function_name=SUM, parameters={column={name=sales_amount, table_ref=null}}}}, for={column={name=month_name, table_ref=null}}, in={1={pivot_literal='feb_sales'}}}, alias=q, table={alias=q_src, table=monthly_sales_long}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[jan_sales_SUM]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{monthly_sales={jan_sales=[[@33,192:200='jan_sales',<381>,5:41]], feb_sales=[[@35,203:211='feb_sales',<381>,5:52]]}, monthly_sales_long={month_name=[[@14,82:91='month_name',<381>,3:29], [@63,345:354='month_name',<381>,8:29]], sales_amount=[[@11,64:75='sales_amount',<381>,3:11], [@60,327:338='sales_amount',<381>,8:11]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query2={jan_sales_SUM=[[@3,9:21='jan_sales_SUM',<381>,1:9], [@42,224:236='jan_sales_SUM',<381>,6:7], [@81,420:432='jan_sales_SUM',<381>,9:44], [@9,60:62='SUM',<141>,3:7], [@17,97:107=''jan_sales'',<389>,3:44]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query2={query_dictionary={jan_sales_SUM=[[@3,9:21='jan_sales_SUM',<381>,1:9], [@42,224:236='jan_sales_SUM',<381>,6:7], [@81,420:432='jan_sales_SUM',<381>,9:44], [@9,60:62='SUM',<141>,3:7], [@17,97:107=''jan_sales'',<389>,3:44]]}, table_dictionary={monthly_sales={jan_sales=[[@33,192:200='jan_sales',<381>,5:41]], feb_sales=[[@35,203:211='feb_sales',<381>,5:52]]}, monthly_sales_long={month_name=[[@14,82:91='month_name',<381>,3:29], [@63,345:354='month_name',<381>,8:29]], sales_amount=[[@11,64:75='sales_amount',<381>,3:11], [@60,327:338='sales_amount',<381>,8:11]]}}, derivation={source_columns={p=[{name=month_name, table_ref=p_src}, {name=sales_amount, table_ref=p_src}], q=[{name=month_name, table_ref=q_src}, {name=sales_amount, table_ref=q_src}], u=[{name=jan_sales, table_ref=u_src}, {name=feb_sales, table_ref=u_src}]}, derived_columns={p={jan_sales_SUM=[[@9,60:62='SUM',<141>,3:7], [@17,97:107=''jan_sales'',<389>,3:44]], feb_sales_SUM=[[@9,60:62='SUM',<141>,3:7], [@19,110:120=''feb_sales'',<389>,3:57]]}, q={feb_sales_SUM=[[@58,323:325='SUM',<141>,8:7], [@66,360:370=''feb_sales'',<389>,8:44]]}, u={sales_amount=[[@28,160:171='sales_amount',<381>,5:9]], month_name=[[@30,177:186='month_name',<381>,5:26]]}}}, filters=[{name=jan_sales_SUM, table_ref=p}, {name=sales_amount, table_ref=u}, {name=month_name, table_ref=u}, {name=feb_sales_SUM, table_ref=q}], interface={jan_sales_SUM=[{name=jan_sales_SUM, table_ref=p}, {name=month_name, table_ref=p_src}, {name=sales_amount, table_ref=p_src}]}, table_alias={p=p_src, q=q_src, p_src=monthly_sales_long, q_src=monthly_sales_long, u=u_src, u_src=monthly_sales}}}",
				extractor.getSymbolTable().toString());

	}

	/** Matrix: subset=E | topo=S3 (P–U–P) | bucket=ORDER_BY | kind=source (unqualified) | outcome=SEVERE. */
	@Test
	public void gapFill17_7_7_S3PivotUnpivotPivotOrderByAmbiguousSourceSalesAmountSevereV1Test() {
		final String query =
				"SELECT p.jan_sales_SUM\n"
						+ "FROM monthly_sales_long p_src\n"
						+ "PIVOT (SUM(sales_amount) FOR month_name IN ('jan_sales', 'feb_sales')) p\n"
						+ "JOIN monthly_sales u_src\n"
						+ "UNPIVOT (sales_amount FOR month_name IN (jan_sales, feb_sales)) u\n"
						+ "  ON p.jan_sales_SUM = u.sales_amount AND u.month_name = 'jan_sales'\n"
						+ "JOIN monthly_sales_long q_src\n"
						+ "PIVOT (SUM(sales_amount) FOR month_name IN ('feb_sales')) q\n"
						+ "  ON u.sales_amount = q.feb_sales_SUM\n"
						+ "ORDER BY sales_amount;";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runParsertest(query, parser);

		assertNoFatalErrors(extractor);
		assertDiagnosticAtPosition(
				extractor.getSnippet(),
				"AMBIGUOUS_COLUMN_REFERENCE",
				ParseDiagnostic.Severity.SEVERE_WARNING,
				"Ambiguous column reference 'sales_amount' at (l:10 c:9). Possible sources: [p, q]",
				"sales_amount",
				10,
				9);

		Assert.assertEquals("AST is wrong", "{SQL={select={1={column={name=jan_sales_SUM, table_ref=p}}}, orderby={1={null_order=null, predicand={column={name=sales_amount, table_ref=null}}, sort_order=ASC}}, from={join={1={pivot={value={function={function_name=SUM, parameters={column={name=sales_amount, table_ref=null}}}}, for={column={name=month_name, table_ref=null}}, in={1={pivot_literal='jan_sales'}, 2={pivot_literal='feb_sales'}}}, alias=p, table={alias=p_src, table=monthly_sales_long}}, 2={join=JOIN, on={and={1={condition={left={column={name=jan_sales_SUM, table_ref=p}}, right={column={name=sales_amount, table_ref=u}}, operator==}}, 2={condition={left={column={name=month_name, table_ref=u}}, right={literal='jan_sales'}, operator==}}}}}, 3={unpivot={value={column={name=sales_amount, table_ref=null}}, for={column={name=month_name, table_ref=null}}, in={1={name=jan_sales, table_ref=null}, 2={name=feb_sales, table_ref=null}}}, alias=u, table={alias=u_src, table=monthly_sales}}, 4={join=JOIN, on={condition={left={column={name=sales_amount, table_ref=u}}, right={column={name=feb_sales_SUM, table_ref=q}}, operator==}}}, 5={pivot={value={function={function_name=SUM, parameters={column={name=sales_amount, table_ref=null}}}}, for={column={name=month_name, table_ref=null}}, in={1={pivot_literal='feb_sales'}}}, alias=q, table={alias=q_src, table=monthly_sales_long}}}}}}",
				extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[jan_sales_SUM]",
				extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}",
				extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{monthly_sales={jan_sales=[[@33,192:200='jan_sales',<381>,5:41]], feb_sales=[[@35,203:211='feb_sales',<381>,5:52]]}, monthly_sales_long={month_name=[[@14,82:91='month_name',<381>,3:29], [@63,345:354='month_name',<381>,8:29]], sales_amount=[[@11,64:75='sales_amount',<381>,3:11], [@60,327:338='sales_amount',<381>,8:11]]}}",
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{query2={jan_sales_SUM=[[@3,9:21='jan_sales_SUM',<381>,1:9], [@42,224:236='jan_sales_SUM',<381>,6:7], [@9,60:62='SUM',<141>,3:7], [@17,97:107=''jan_sales'',<389>,3:44]]}}",
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{def_query2={query_dictionary={jan_sales_SUM=[[@3,9:21='jan_sales_SUM',<381>,1:9], [@42,224:236='jan_sales_SUM',<381>,6:7], [@9,60:62='SUM',<141>,3:7], [@17,97:107=''jan_sales'',<389>,3:44]]}, table_dictionary={monthly_sales={jan_sales=[[@33,192:200='jan_sales',<381>,5:41]], feb_sales=[[@35,203:211='feb_sales',<381>,5:52]]}, monthly_sales_long={month_name=[[@14,82:91='month_name',<381>,3:29], [@63,345:354='month_name',<381>,8:29]], sales_amount=[[@11,64:75='sales_amount',<381>,3:11], [@60,327:338='sales_amount',<381>,8:11]]}}, derivation={source_columns={p=[{name=month_name, table_ref=p_src}, {name=sales_amount, table_ref=p_src}], q=[{name=month_name, table_ref=q_src}, {name=sales_amount, table_ref=q_src}], u=[{name=jan_sales, table_ref=u_src}, {name=feb_sales, table_ref=u_src}]}, derived_columns={p={jan_sales_SUM=[[@9,60:62='SUM',<141>,3:7], [@17,97:107=''jan_sales'',<389>,3:44]], feb_sales_SUM=[[@9,60:62='SUM',<141>,3:7], [@19,110:120=''feb_sales'',<389>,3:57]]}, q={feb_sales_SUM=[[@58,323:325='SUM',<141>,8:7], [@66,360:370=''feb_sales'',<389>,8:44]]}, u={sales_amount=[[@28,160:171='sales_amount',<381>,5:9]], month_name=[[@30,177:186='month_name',<381>,5:26]]}}}, ordered_by=[{name=sales_amount, table_ref=u}, {name=jan_sales, table_ref=u_src}, {name=feb_sales, table_ref=u_src}], filters=[{name=jan_sales_SUM, table_ref=p}, {name=sales_amount, table_ref=u}, {name=month_name, table_ref=u}, {name=feb_sales_SUM, table_ref=q}], interface={jan_sales_SUM=[{name=jan_sales_SUM, table_ref=p}, {name=month_name, table_ref=p_src}, {name=sales_amount, table_ref=p_src}]}, table_alias={p=p_src, q=q_src, p_src=monthly_sales_long, q_src=monthly_sales_long, u=u_src, u_src=monthly_sales}}}",
				extractor.getSymbolTable().toString());

	}


	// --- Phase 17.7.8 closeout: derived outputs must not pollute physical table_dictionary ---

	private static void assertPhysicalTableDictionaryBucketOmitsColumnKeys(
			String tableDictionaryFlat,
			String physicalTableKey,
			String... forbiddenColumnKeys) {
		String bucketMarker = physicalTableKey + "={";
		int bucketStart = tableDictionaryFlat.indexOf(bucketMarker);
		Assert.assertTrue("Expected table_dictionary bucket for " + physicalTableKey, bucketStart >= 0);
		int contentStart = bucketStart + bucketMarker.length();
		int bucketEnd = tableDictionaryFlat.indexOf("}, ", contentStart);
		if (bucketEnd < 0) {
			bucketEnd = tableDictionaryFlat.lastIndexOf('}');
		}
		String bucketBody = tableDictionaryFlat.substring(contentStart, bucketEnd);
		for (String columnKey : forbiddenColumnKeys) {
			Assert.assertFalse(
					"Derived or modifier output column '" + columnKey + "' must not appear on physical table '"
							+ physicalTableKey + "' in table_dictionary; bucket=[" + bucketBody + "]",
					bucketBody.contains(columnKey + "="));
		}
	}

	/**
	 * 17.7.8 closeout (1/4): PIVOT on a physical table — {@code jan_sales_SUM} lives in
	 * {@code derivation.derived_columns} and interface, not on {@code monthly_sales_long} physical dict.
	 */
	@Test
	public void closeout17_7_8_PivotPhysicalSourceDerivedAbsentFromTableDictionaryTest() {
		final String query =
				"SELECT jan_sales_SUM\n"
						+ "FROM monthly_sales_long\n"
						+ "PIVOT (SUM(sales_amount) FOR month_name IN ('jan_sales')) p;";

		SqlParseEventWalker extractor = runParsertest(query, parse(query));
		assertNoFatalErrors(extractor);

		String tableDict = extractor.getTableColumnDictionaryMap().toString();
		assertPhysicalTableDictionaryBucketOmitsColumnKeys(tableDict, "monthly_sales_long", "jan_sales_SUM");

		String sym = extractor.getSymbolTable().toString();
		Assert.assertTrue(sym.contains("derived_columns={p={jan_sales_SUM"));
		Assert.assertTrue(sym.contains("jan_sales_SUM=[{name=jan_sales_SUM, table_ref=p}"));
	}

	/**
	 * 17.7.8 closeout (2/4): UNPIVOT on a physical wide table — VALUE/FOR names {@code sales_amount} /
	 * {@code month_name} must not appear on physical {@code monthly_sales} (IN-list cols only).
	 */
	@Test
	public void closeout17_7_8_UnpivotPhysicalSourceDerivedAbsentFromTableDictionaryTest() {
		final String query =
				"SELECT sales_amount, month_name\n"
						+ "FROM monthly_sales\n"
						+ "UNPIVOT (sales_amount FOR month_name IN (jan_sales, feb_sales)) u;";

		SqlParseEventWalker extractor = runParsertest(query, parse(query));
		assertNoFatalErrors(extractor);

		String tableDict = extractor.getTableColumnDictionaryMap().toString();
		assertPhysicalTableDictionaryBucketOmitsColumnKeys(tableDict, "monthly_sales", "sales_amount", "month_name");
		Assert.assertTrue(tableDict.contains("monthly_sales={jan_sales"));
		Assert.assertTrue(tableDict.contains("feb_sales"));

		String sym = extractor.getSymbolTable().toString();
		Assert.assertTrue(sym.contains("derived_columns={u={sales_amount"));
		Assert.assertTrue(sym.contains("month_name=[{name=month_name, table_ref=u}"));
	}

	/**
	 * 17.7.8 closeout (3/4): PIVOT on subquery-backed source — derived {@code jan_sales_SUM} must not
	 * land on the inner physical {@code monthly_sales_long} bucket after convert egress.
	 */
	@Test
	public void closeout17_7_8_PivotSubquerySourceDerivedAbsentFromPhysicalTableDictionaryTest() {
		final String query =
				"SELECT jan_sales_SUM\n"
						+ "FROM (\n"
						+ "  SELECT empid, month_name, sales_amount FROM monthly_sales_long\n"
						+ ") src\n"
						+ "PIVOT (SUM(sales_amount) FOR month_name IN ('jan_sales')) p;";

		SqlParseEventWalker extractor = runParsertest(query, parse(query));
		assertNoFatalErrors(extractor);

		String tableDict = extractor.getTableColumnDictionaryMap().toString();
		assertPhysicalTableDictionaryBucketOmitsColumnKeys(tableDict, "monthly_sales_long", "jan_sales_SUM");

		String sym = extractor.getSymbolTable().toString();
		Assert.assertTrue(sym.contains("derived_columns={p={jan_sales_SUM"));
		Assert.assertTrue(sym.contains("jan_sales_SUM=[{name=jan_sales_SUM, table_ref=p}"));
	}

	/**
	 * 17.7.8 closeout (4/4): UNPIVOT on subquery-backed source — derived VALUE/FOR must not pollute the
	 * physical {@code monthly_sales} table_dictionary bucket.
	 */
	@Test
	public void closeout17_7_8_UnpivotSubquerySourceDerivedAbsentFromPhysicalTableDictionaryTest() {
		final String query =
				"SELECT sales_amount, month_name\n"
						+ "FROM (\n"
						+ "  SELECT empid, jan_sales, feb_sales FROM monthly_sales\n"
						+ ") src\n"
						+ "UNPIVOT (sales_amount FOR month_name IN (jan_sales, feb_sales)) u;";

		SqlParseEventWalker extractor = runParsertest(query, parse(query));
		assertNoFatalErrors(extractor);

		String tableDict = extractor.getTableColumnDictionaryMap().toString();
		assertPhysicalTableDictionaryBucketOmitsColumnKeys(tableDict, "monthly_sales", "sales_amount", "month_name");

		String sym = extractor.getSymbolTable().toString();
		Assert.assertTrue(sym.contains("derived_columns={u={sales_amount"));
		Assert.assertTrue(sym.contains("month_name=[{name=month_name, table_ref=u}"));
	}

	/**
	 * 17.7.8 closeout — dual PIVOT on physical sources: unqualified derived in SELECT still diagnoses
	 * {@code AMBIGUOUS_DERIVED_COLUMN_REFERENCE} (derived vs derived), not physical {@code jan_sales}.
	 */
	@Test
	public void closeout17_7_8_PivotPhysicalDualModifierDerivedAmbiguousInSelectTest() {
		final String query =
				"SELECT jan_sales_SUM\n"
						+ "FROM monthly_sales_long p_src\n"
						+ "PIVOT (SUM(sales_amount) FOR month_name IN ('jan_sales')) p\n"
						+ "JOIN monthly_sales_long q_src\n"
						+ "PIVOT (SUM(sales_amount) FOR month_name IN ('jan_sales')) q;";

		SqlParseEventWalker extractor = runParsertest(query, parse(query));
		assertFatalDiagnosticAtPosition(
				extractor.getSnippet(),
				"AMBIGUOUS_DERIVED_COLUMN_REFERENCE",
				"Ambiguous derived column reference 'jan_sales_SUM' at (l:1 c:7). Possible sources: [p, q]",
				"jan_sales_SUM",
				1,
				7);
		Assert.assertTrue(
				extractor.getSymbolTable().toString().contains(
						"jan_sales_SUM=[{name=jan_sales_SUM, table_ref=null}]"));
	}

	/**
	 * 17.7.3 closeout (1/2): triple sibling PIVOT — convert egress must keep PIVOT operand columns on the
	 * physical {@code monthly_sales_long} bucket while structured derived outputs stay off physical keys.
	 */
	@Test
	public void closeout17_7_3_TriplePivotOperandColumnsRemainOnPhysicalTableDictionaryTest() {
		final String query =
				"SELECT jan_sales_SUM, feb_sales_SUM, mar_sales_SUM\n"
						+ "FROM monthly_sales_long p_src\n"
						+ "PIVOT (SUM(sales_amount) FOR month_name IN ('jan_sales')) p\n"
						+ "JOIN monthly_sales_long q_src\n"
						+ "PIVOT (SUM(sales_amount) FOR month_name IN ('feb_sales')) q\n"
						+ "  ON p.jan_sales_SUM = q.feb_sales_SUM\n"
						+ "JOIN monthly_sales_long r_src\n"
						+ "PIVOT (SUM(sales_amount) FOR month_name IN ('mar_sales')) r\n"
						+ "  ON q.feb_sales_SUM = r.mar_sales_SUM\n"
						+ "WHERE p.jan_sales_SUM > 0;";

		SqlParseEventWalker extractor = runParsertest(query, parse(query));
		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);

		String tableDict = extractor.getTableColumnDictionaryMap().toString();
		Assert.assertTrue(tableDict.contains("monthly_sales_long={month_name="));
		Assert.assertTrue(tableDict.contains("sales_amount="));
		assertPhysicalTableDictionaryBucketOmitsColumnKeys(
				tableDict,
				"monthly_sales_long",
				"jan_sales_SUM",
				"feb_sales_SUM",
				"mar_sales_SUM");
	}

	/**
	 * 17.7.3 closeout (2/2): triple sibling UNPIVOT — IN-list wide columns stay on physical
	 * {@code monthly_sales}; structured VALUE/FOR derived names must not pollute that bucket.
	 */
	@Test
	public void closeout17_7_3_TripleUnpivotInListOperandsRemainOnPhysicalTableDictionaryTest() {
		final String query =
				"SELECT u1.sales_amount, u1.month_name\n"
						+ "FROM monthly_sales m1\n"
						+ "UNPIVOT (sales_amount FOR month_name IN (jan_sales, feb_sales)) u1\n"
						+ "JOIN monthly_sales m2\n"
						+ "UNPIVOT (sales_amount FOR month_name IN (jan_sales, feb_sales)) u2\n"
						+ "  ON u1.month_name = u2.month_name\n"
						+ "JOIN monthly_sales m3\n"
						+ "UNPIVOT (sales_amount FOR month_name IN (jan_sales, mar_sales)) u3\n"
						+ "  ON u2.month_name = u3.month_name\n"
						+ "WHERE u1.sales_amount > 10;";

		SqlParseEventWalker extractor = runParsertest(query, parse(query));
		assertNoFatalErrors(extractor);
		assertNoWalkerDiagnostics(extractor);

		String tableDict = extractor.getTableColumnDictionaryMap().toString();
		Assert.assertTrue(tableDict.contains("monthly_sales={jan_sales="));
		Assert.assertTrue(tableDict.contains("feb_sales"));
		Assert.assertTrue(tableDict.contains("mar_sales"));
		assertPhysicalTableDictionaryBucketOmitsColumnKeys(
				tableDict,
				"monthly_sales",
				"sales_amount",
				"month_name");
	}

	/**
	 * Phase 17.7.5b.6: one ambiguous derived SELECT ref vs two refs to the same name under dual
	 * PIVOT siblings — per-site {@code AMBIGUOUS_DERIVED_COLUMN_REFERENCE} fatals and unqualified
	 * interface publish; single-modifier control has no derived ambiguity.
	 */
	@Test
	public void pivotDerivedAmbiguousConvertEgressPhaseParityOneVsTwoSelectRefsTest() {
		final String dualPivotFrom =
				"FROM monthly_sales_long p_src\n"
						+ "PIVOT (SUM(sales_amount) FOR month_name IN ('jan_sales')) p\n"
						+ "JOIN monthly_sales_long q_src\n"
						+ "PIVOT (SUM(sales_amount) FOR month_name IN ('jan_sales')) q\n";

		final String oneRefQuery = "SELECT jan_sales_SUM\n" + dualPivotFrom + ";";
		final String twoRefQuery = "SELECT jan_sales_SUM, jan_sales_SUM\n" + dualPivotFrom + ";";

		SqlParseEventWalker oneRefExtractor = runParsertest(oneRefQuery, parse(oneRefQuery));
		SqlParseEventWalker twoRefExtractor = runParsertest(twoRefQuery, parse(twoRefQuery));

		assertFatalDiagnosticCount(
				oneRefExtractor.getSnippet(),
				"AMBIGUOUS_DERIVED_COLUMN_REFERENCE",
				null,
				"jan_sales_SUM",
				1);
		assertFatalDiagnosticAtPosition(
				oneRefExtractor.getSnippet(),
				"AMBIGUOUS_DERIVED_COLUMN_REFERENCE",
				"Ambiguous derived column reference 'jan_sales_SUM' at (l:1 c:7). Possible sources: [p, q]",
				"jan_sales_SUM",
				1,
				7);

		assertFatalDiagnosticCount(
				twoRefExtractor.getSnippet(),
				"AMBIGUOUS_DERIVED_COLUMN_REFERENCE",
				null,
				"jan_sales_SUM",
				2);
		assertFatalDiagnosticAtPosition(
				twoRefExtractor.getSnippet(),
				"AMBIGUOUS_DERIVED_COLUMN_REFERENCE",
				"Ambiguous derived column reference 'jan_sales_SUM' at (l:1 c:7). Possible sources: [p, q]",
				"jan_sales_SUM",
				1,
				7);

		Assert.assertTrue(
				"Dual-modifier single ref must leave derived interface unqualified",
				oneRefExtractor.getSymbolTable().toString().contains(
						"jan_sales_SUM=[{name=jan_sales_SUM, table_ref=null}]"));
		Assert.assertTrue(
				"Dual-modifier two refs must leave derived interface unqualified",
				twoRefExtractor.getSymbolTable().toString().contains(
						"jan_sales_SUM=[{name=jan_sales_SUM, table_ref=null}]"));

		final String singlePivotQuery =
				"SELECT jan_sales_SUM\n"
						+ "FROM monthly_sales_long p_src\n"
						+ "PIVOT (SUM(sales_amount) FOR month_name IN ('jan_sales')) p;";
		SqlParseEventWalker singleModifierExtractor =
				runParsertest(singlePivotQuery, parse(singlePivotQuery));
		assertFatalDiagnosticCount(
				singleModifierExtractor.getSnippet(),
				"AMBIGUOUS_DERIVED_COLUMN_REFERENCE",
				null,
				null,
				0);
		Assert.assertTrue(
				"Single modifier must bind derived output to bucket alias",
				singleModifierExtractor.getSymbolTable().toString().contains(
						"jan_sales_SUM=[{name=jan_sales_SUM, table_ref=p}"));
	}

	/*
		TUPLE TESTS WITH PIVOT AND UNPIVOT
	*/
	@Test
	public void generatorDirectFromListTupleEndpointNakedSyntaxBuildsSameAstShapeTest() {
		final String query = "tab1 pivot (sum(col1) for col2 in ('A', 'B'))";

		final SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runTupleParsertest(query, parser);

		assertNoFatalErrors(extractor);
		Assert.assertEquals("AST is wrong", "{TUPLE={pivot={value={function={function_name=sum, parameters={column={name=col1, table_ref=null}}}}, for={column={name=col2, table_ref=null}}, in={1={pivot_literal='A'}, 2={pivot_literal='B'}}}, table={table=tab1}}}", extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", "[]", extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", "{}", extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", "{}", extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", "{}", extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", "{table_dictionary={tab1={col2=[[@8,26:29='col2',<381>,1:26]], col1=[[@5,16:19='col1',<381>,1:16]]}}, unresolved_column={col2={column={name=col2, table_ref=null}, locations=[[@8,26:29='col2',<381>,1:26]]}, col1={column={name=col1, table_ref=null}, locations=[[@5,16:19='col1',<381>,1:16]]}}, derivation={source_columns={tuple_0=[{name=col2, table_ref=tab1}, {name=col1, table_ref=tab1}]}, interface_source_ref=tab1, pivot_derived_source_bindings={tuple_0={B_sum=col1, A_sum=col1}}, source_ref=tab1, derived_columns={tuple_0={A_sum=[[@3,12:14='sum',<141>,1:12], [@11,35:37=''A'',<389>,1:35]], B_sum=[[@3,12:14='sum',<141>,1:12], [@13,40:42=''B'',<389>,1:40]]}}}}", extractor.getSymbolTable().toString());
	}

}
