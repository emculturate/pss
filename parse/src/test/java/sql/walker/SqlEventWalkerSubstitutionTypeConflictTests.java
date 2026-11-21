package sql.walker;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;

import access.Snippet;
import access.SqlParserAccess;
import errorhandling.ParseDiagnostic;
import sql.SQLSelectParserParser;

public class SqlEventWalkerSubstitutionTypeConflictTests extends AbstractSqlParseEventWalkerTest {

	private static final String CODE = "SUBSTITUTION_VARIABLE_TYPE_CONFLICT";

	@Test
	public void whereClauseConflictPlacementTest() {
		Snippet snippet = parsePlacement(
				"SELECT <shared> AS v FROM events e WHERE <shared>", "SQL");
		assertConflictDiagnostics(snippet, "predicand", new String[] {"condition"}, new int[] {41});
		assertFullGoldens(snippet,
				"{SQL={select={1={substitution={name=<shared>, type=predicand}, alias=v}}, from={table={alias=e, table=events}}, where={substitution={name=<shared>, type=condition}}}}",
				"[v]", "{<shared>=predicand}", "{events={}}",
				"{query0={v=[[@3,19:19='v',<459>,1:19]]}}",
				"{def_query0={query_dictionary={v=[[@3,19:19='v',<459>,1:19]]}, table_dictionary={events={}}, filters=[], interface={v=[{name=<shared>, type=predicand}]}, table_alias={e=events}}}");
	}

	@Test
	public void groupByClauseConflictPlacementTest() {
		Snippet snippet = parsePlacement(
				"SELECT e.<shared> AS v FROM events e GROUP BY <shared>", "SQL");
		assertConflictDiagnostics(snippet, "column", new String[] {"predicand"}, new int[] {46});
		assertFullGoldens(snippet,
				"{SQL={select={1={column={substitution={name=<shared>, type=column}, table_ref=e}, alias=v}}, from={table={alias=e, table=events}}, groupby={1={substitution={name=<shared>, type=predicand}}}}}",
				"[v]", "{<shared>=column}",
				"{events={<shared>=[[@1,7:7='e',<459>,1:7]]}}",
				"{query0={v=[[@5,21:21='v',<459>,1:21]]}}",
				"{def_query0={query_dictionary={v=[[@5,21:21='v',<459>,1:21]]}, table_dictionary={events={<shared>=[[@1,7:7='e',<459>,1:7]]}}, grouped_by=[{name=<shared>, type=predicand}], interface={v=[{substitution={name=<shared>, type=column}, table_ref=e}]}, table_alias={e=events}}}");
	}

	@Test
	public void havingClauseConflictPlacementTest() {
		Snippet snippet = parsePlacement(
				"SELECT <shared> AS v FROM events e GROUP BY e.id HAVING <shared>", "SQL");
		assertConflictDiagnostics(snippet, "predicand", new String[] {"condition"}, new int[] {56});
		assertFullGoldens(snippet,
				"{SQL={select={1={substitution={name=<shared>, type=predicand}, alias=v}}, having={substitution={name=<shared>, type=condition}}, from={table={alias=e, table=events}}, groupby={1={column={name=id, table_ref=e}}}}}",
				"[v]", "{<shared>=predicand}",
				"{events={id=[[@9,44:44='e',<459>,1:44]]}}",
				"{query0={v=[[@3,19:19='v',<459>,1:19]]}}",
				"{def_query0={query_dictionary={v=[[@3,19:19='v',<459>,1:19]]}, table_dictionary={events={id=[[@9,44:44='e',<459>,1:44]]}}, grouped_by=[{name=id, table_ref=e}], filters=[], interface={v=[{name=<shared>, type=predicand}]}, table_alias={e=events}}}");
	}

	@Test
	public void orderByClauseConflictPlacementTest() {
		Snippet snippet = parsePlacement(
				"SELECT e.<shared> AS v FROM events e ORDER BY <shared>", "SQL");
		assertConflictDiagnostics(snippet, "column", new String[] {"predicand"}, new int[] {46});
		assertFullGoldens(snippet,
				"{SQL={select={1={column={substitution={name=<shared>, type=column}, table_ref=e}, alias=v}}, orderby={1={null_order=null, predicand={substitution={name=<shared>, type=predicand}}, sort_order=ASC}}, from={table={alias=e, table=events}}}}",
				"[v]", "{<shared>=column}",
				"{events={<shared>=[[@1,7:7='e',<459>,1:7]]}}",
				"{query0={v=[[@5,21:21='v',<459>,1:21]]}}",
				"{def_query0={query_dictionary={v=[[@5,21:21='v',<459>,1:21]]}, table_dictionary={events={<shared>=[[@1,7:7='e',<459>,1:7]]}}, ordered_by=[{name=<shared>, type=predicand}], interface={v=[{substitution={name=<shared>, type=column}, table_ref=e}]}, table_alias={e=events}}}");
	}

	@Test
	public void qualifyClauseConflictPlacementTest() {
		Snippet snippet = parsePlacement(
				"SELECT <shared> AS v FROM events e QUALIFY <shared>", "SQL");
		assertConflictDiagnostics(snippet, "predicand", new String[] {"condition"}, new int[] {43});
		assertFullGoldens(snippet,
				"{SQL={select={1={substitution={name=<shared>, type=predicand}, alias=v}}, from={table={alias=e, table=events}}, qualify={substitution={name=<shared>, type=condition}}}}",
				"[v]", "{<shared>=predicand}", "{events={}}",
				"{query0={v=[[@3,19:19='v',<459>,1:19]]}}",
				"{def_query0={query_dictionary={v=[[@3,19:19='v',<459>,1:19]]}, table_dictionary={events={}}, filters=[], interface={v=[{name=<shared>, type=predicand}]}, table_alias={e=events}}}");
	}

	@Test
	public void windowClauseConflictPlacementTest() {
		Snippet snippet = parsePlacement(
				"SELECT e.<shared> AS v, ROW_NUMBER() OVER "
						+ "(PARTITION BY <shared> ORDER BY <shared>) AS rn FROM events e",
				"SQL");
		assertConflictDiagnostics(snippet, "column",
				new String[] {"predicand", "predicand"}, new int[] {56, 74});
		assertFullGoldens(snippet,
				"{SQL={select={1={column={substitution={name=<shared>, type=column}, table_ref=e}, alias=v}, 2={alias=rn, window_function={over={partition_by={1={substitution={name=<shared>, type=predicand}}}, orderby={1={null_order=null, predicand={substitution={name=<shared>, type=predicand}}, sort_order=ASC}}}, function={function_name=ROW_NUMBER, parameters=null}}}}, from={table={alias=e, table=events}}}}",
				"[v, rn]", "{<shared>=column}",
				"{events={<shared>=[[@1,7:7='e',<459>,1:7]]}}",
				"{query0={rn=[[@20,87:88='rn',<459>,1:87]], v=[[@5,21:21='v',<459>,1:21]]}}",
				"{def_query0={window_ordered_by=[{name=<shared>, type=predicand}], query_dictionary={v=[[@5,21:21='v',<459>,1:21]], rn=[[@20,87:88='rn',<459>,1:87]]}, table_dictionary={events={<shared>=[[@1,7:7='e',<459>,1:7]]}}, window_partition_by=[{name=<shared>, type=predicand}], interface={v=[{substitution={name=<shared>, type=column}, table_ref=e}], rn=[{name=<shared>, type=predicand}]}, table_alias={e=events}}}");
	}

	@Test
	public void cteScopeConflictPlacementTest() {
		Snippet snippet = parsePlacement(
				"WITH base AS (SELECT <shared> AS v FROM events) "
						+ "SELECT v FROM base WHERE <shared>",
				"SQL");
		assertConflictDiagnostics(snippet, "predicand", new String[] {"condition"}, new int[] {73});
		assertFullGoldens(snippet,
				"{SQL={with={1={cte={select={1={substitution={name=<shared>, type=predicand}, alias=v}}, from={table={alias=null, table=events}}}, alias=base}}, query={select={1={column={name=v, table_ref=null}}}, from={table={alias=null, table=base}}, where={substitution={name=<shared>, type=condition}}}}}",
				"[v]", "{<shared>=predicand}", "{events={}}",
				"{query0={v=[[@7,33:33='v',<459>,1:33], [@12,55:55='v',<459>,1:55]]}, query1={v=[[@12,55:55='v',<459>,1:55]]}}",
				"{def_query1={context_list={base=query0}, query_dictionary={v=[[@12,55:55='v',<459>,1:55]]}, def_query0={query_dictionary={v=[[@7,33:33='v',<459>,1:33], [@12,55:55='v',<459>,1:55]]}, table_dictionary={events={}}, interface={v=[{name=<shared>, type=predicand}]}}, filters=[], interface={v=[{name=v, table_ref=query0}]}, table_alias={base=query0}}}");
	}

	@Test
	public void derivedScopeConflictPlacementTest() {
		Snippet snippet = parsePlacement(
				"SELECT d.v FROM (SELECT <shared> AS v FROM events) d WHERE <shared>", "SQL");
		assertConflictDiagnostics(snippet, "predicand", new String[] {"condition"}, new int[] {59});
		assertFullGoldens(snippet,
				"{SQL={select={1={column={name=v, table_ref=d}}}, from={table={alias=d, query={select={1={substitution={name=<shared>, type=predicand}, alias=v}}, from={table={alias=null, table=events}}}}}, where={substitution={name=<shared>, type=condition}}}}",
				"[v]", "{<shared>=predicand}", "{events={}}",
				"{query0={v=[[@9,36:36='v',<459>,1:36], [@1,7:7='d',<459>,1:7]]}, query1={v=[[@3,9:9='v',<459>,1:9]]}}",
				"{def_query1={query_dictionary={v=[[@3,9:9='v',<459>,1:9]]}, def_query0={query_dictionary={v=[[@9,36:36='v',<459>,1:36], [@1,7:7='d',<459>,1:7]]}, table_dictionary={events={}}, interface={v=[{name=<shared>, type=predicand}]}}, filters=[], interface={v=[{name=v, table_ref=d}]}, table_alias={d=query0}}}");
	}

	@Test
	public void caseWhenConflictPlacementTest() {
		Snippet snippet = parsePlacement(
				"SELECT <shared> AS original_value, "
						+ "CASE WHEN <shared> THEN e.amount ELSE 0 END AS v FROM events e",
				"SQL");
		assertConflictDiagnostics(snippet, "predicand", new String[] {"condition"}, new int[] {45});
		assertFullGoldens(snippet,
				"{SQL={select={1={substitution={name=<shared>, type=predicand}, alias=original_value}, 2={alias=v, case={clauses={1={then={column={name=amount, table_ref=e}}, when={substitution={name=<shared>, type=condition}}}}, else={literal=0}}}}, from={table={alias=e, table=events}}}}",
				"[original_value, v]", "{<shared>=predicand}",
				"{events={amount=[[@9,59:59='e',<459>,1:59]]}}",
				"{query0={original_value=[[@3,19:32='original_value',<459>,1:19]], v=[[@16,82:82='v',<459>,1:82]]}}",
				"{def_query0={query_dictionary={original_value=[[@3,19:32='original_value',<459>,1:19]], v=[[@16,82:82='v',<459>,1:82]]}, table_dictionary={events={amount=[[@9,59:59='e',<459>,1:59]]}}, interface={original_value=[{name=<shared>, type=predicand}], v=[{name=amount, table_ref=e}]}, table_alias={e=events}}}");
	}

	@Test
	public void insertConflictPlacementTest() {
		Snippet snippet = parsePlacement(
				"INSERT INTO target (v) SELECT <shared> AS v FROM source WHERE <shared>", "INSERT");
		assertConflictDiagnostics(snippet, "predicand", new String[] {"condition"}, new int[] {62});
		assertFullGoldens(snippet,
				"{INSERT={insert={preamble=insert_into, from={from={table={alias=null, table=source}}, where={substitution={name=<shared>, type=condition}}, select={1={substitution={name=<shared>, type=predicand}, alias=v}}}, target_table={table={alias=null, table=target}}, columns={1={column={name=v, table_ref=null}}}}}}",
				"[v]", "{<shared>=predicand}",
				"{source={}, target={v=[[@4,20:20='v',<459>,1:20]]}}",
				"{query0={v=[[@9,42:42='v',<459>,1:42]]}, insert1={v=[[@4,20:20='v',<459>,1:20]]}}",
				"{def_insert1={query_dictionary={v=[[@4,20:20='v',<459>,1:20]]}, table_dictionary={target={v=[[@4,20:20='v',<459>,1:20]]}}, def_query0={query_dictionary={v=[[@9,42:42='v',<459>,1:42]]}, table_dictionary={source={}}, filters=[], interface={v=[{name=<shared>, type=predicand}]}}, interface={v=[{name=v, table_ref=query0}]}}}");
	}

	@Test
	public void updateConflictPlacementTest() {
		Snippet snippet = parsePlacement(
				"UPDATE events SET v = <shared> WHERE <shared>", "UPDATE");
		assertConflictDiagnostics(snippet, "predicand", new String[] {"condition"}, new int[] {37});
		assertFullGoldens(snippet,
				"{UPDATE={update={table={alias=null, table=events}, where={substitution={name=<shared>, type=condition}}, assignments={1={set={column={name=v, table_ref=null}}, to={substitution={name=<shared>, type=predicand}}}}}}}",
				"[v]", "{<shared>=predicand}",
				"{events={<shared>=[[@5,22:29='<shared>',<327>,1:22]]}}",
				"{update0={v=[[@3,18:18='v',<459>,1:18]]}}",
				"{def_update0={assignments={v=[{name=<shared>, type=predicand}]}, table_dictionary={events={<shared>=[[@5,22:29='<shared>',<327>,1:22]]}}, update_dictionary={v=[[@3,18:18='v',<459>,1:18]]}, target_table={events={v=[[@3,18:18='v',<459>,1:18]]}}, filters=[], lhs_unresolved_columns={v={column={name=v, table_ref=null}, locations=[[@3,18:18='v',<459>,1:18]]}}}}");
	}

	@Test
	public void deleteConflictPlacementTest() {
		Snippet snippet = parsePlacement(
				"DELETE FROM events WHERE id = <shared> AND <shared>", "DELETE");
		assertConflictDiagnostics(snippet, "predicand", new String[] {"condition"}, new int[] {43});
		assertFullGoldens(snippet,
				"{DELETE={delete={table={alias=null, table=events}, where={and={1={condition={left={column={name=id, table_ref=null}}, right={substitution={name=<shared>, type=predicand}}, operator==}}, 2={substitution={name=<shared>, type=condition}}}}}}}",
				"[]", "{<shared>=predicand}",
				"{events={id=[[@4,25:26='id',<459>,1:25]]}}",
				"{}",
				"{def_delete0={query_dictionary={}, table_dictionary={events={id=[[@4,25:26='id',<459>,1:25]]}}, filters=[{name=id, table_ref=events}, {name=<shared>, type=predicand}], interface=null}}");
	}

	@Test
	public void largeMixedTypeConflictTest() {
		SqlParseEventWalker extractor = parseQuery(
				"SELECT <shared> AS predicand_value, t.<shared> AS column_value "
						+ "FROM <shared> t JOIN other u ON <shared> <shared> "
						+ "WHERE t.id IN <shared> UNION ALL <shared>");
		assertConflictDiagnostics(extractor, "predicand",
				new String[] {"column", "tuple", "condition", "join_extension", "in_list", "query"},
				new int[] {38, 68, 95, 104, 127, 146});
		assertFullGoldens(
				extractor,
				"{SQL={union={1={select={1={substitution={name=<shared>, type=predicand}, alias=predicand_value}, 2={column={substitution={name=<shared>, type=column}, table_ref=t}, alias=column_value}}, from={extension={substitution={name=<shared>, type=join_extension}}, join={1={table={alias=t, substitution={name=<shared>, type=tuple}}}, 2={join=JOIN, on={substitution={name=<shared>, type=condition}}}, 3={table={alias=u, table=other}}}}, where={in={item={column={name=id, table_ref=t}}, in_list={substitution={name=<shared>, type=in_list}}}}}, 2={union={qualifier=ALL, operator=UNION}}, 3={substitution={name=<shared>, type=query}}}}}",
				"[predicand_value, column_value]",
				"{<shared>=predicand}",
				"{other={}, <shared>={<shared>=[[@5,36:36='t',<459>,1:36]], id=[[@20,119:119='t',<459>,1:119]]}}",
				"{query0={predicand_value=[[@3,19:33='predicand_value',<459>,1:19]], column_value=[[@9,50:61='column_value',<459>,1:50]]}}",
				"{def_union1={def_query0={query_dictionary={predicand_value=[[@3,19:33='predicand_value',<459>,1:19]], column_value=[[@9,50:61='column_value',<459>,1:50]]}, table_dictionary={other={}, <shared>={<shared>=[[@5,36:36='t',<459>,1:36]], id=[[@20,119:119='t',<459>,1:119]]}}, filters=[{name=id, table_ref=t}], interface={predicand_value=[{name=<shared>, type=predicand}], column_value=[{substitution={name=<shared>, type=column}, table_ref=t}]}, table_alias={t=<shared>, u=other}}, interface={predicand_value=query_column, column_value=query_column}}}");
	}

	@Test
	public void columnFirstMixedTypeConflictTest() {
		SqlParseEventWalker extractor = parseQuery(
				"WITH seed AS (SELECT b.<shared> AS id FROM base b) " + mixedTypeTail());
		assertConflictDiagnostics(extractor, "column",
				new String[] {"tuple", "condition", "join_extension", "predicand", "in_list", "query"},
				new int[] {91, 118, 127, 149, 170, 189});
		assertFullGoldens(
				extractor,
				"{SQL={with={1={cte={select={1={column={substitution={name=<shared>, type=column}, table_ref=b}, alias=id}}, from={table={alias=b, table=base}}}, alias=seed}}, query={union={1={select={1={column={name=id, table_ref=t}, alias=id}, 2={column={substitution={name=<shared>, type=column}, table_ref=t}, alias=c}}, from={extension={substitution={name=<shared>, type=join_extension}}, join={1={table={alias=t, substitution={name=<shared>, type=tuple}}}, 2={join=JOIN, on={substitution={name=<shared>, type=condition}}}, 3={table={alias=u, table=other}}}}, where={and={1={condition={left={column={name=id, table_ref=t}}, right={substitution={name=<shared>, type=predicand}}, operator==}}, 2={in={item={column={name=id, table_ref=t}}, in_list={substitution={name=<shared>, type=in_list}}}}}}}, 2={union={qualifier=ALL, operator=UNION}}, 3={substitution={name=<shared>, type=query}}}}}}",
				"[c, id]",
				"{<shared>=column}",
				"{other={}, <shared>={<shared>=[[@21,70:70='t',<459>,1:70]], id=[[@15,58:58='t',<459>,1:58], [@36,142:142='t',<459>,1:142], [@42,162:162='t',<459>,1:162]]}, base={<shared>=[[@5,21:21='b',<459>,1:21]]}}",
				"{query0={id=[[@9,35:36='id',<459>,1:35]]}, query1={c=[[@25,84:84='c',<459>,1:84]], id=[[@19,66:67='id',<459>,1:66]]}}",
				"{def_union2={context_list={seed=query0}, def_query1={context_list={seed=query0}, query_dictionary={c=[[@25,84:84='c',<459>,1:84]], id=[[@19,66:67='id',<459>,1:66]]}, table_dictionary={other={}, <shared>={<shared>=[[@21,70:70='t',<459>,1:70]], id=[[@15,58:58='t',<459>,1:58], [@36,142:142='t',<459>,1:142], [@42,162:162='t',<459>,1:162]]}}, filters=[{name=id, table_ref=t}, {name=<shared>, type=predicand}], interface={c=[{substitution={name=<shared>, type=column}, table_ref=t}], id=[{name=id, table_ref=t}]}, table_alias={seed=query0, t=<shared>, u=other}}, def_query0={query_dictionary={id=[[@9,35:36='id',<459>,1:35]]}, table_dictionary={base={<shared>=[[@5,21:21='b',<459>,1:21]]}}, interface={id=[{substitution={name=<shared>, type=column}, table_ref=b}]}, table_alias={b=base}}, interface={c=query_column, id=query_column}, table_alias={seed=query0}}}");
	}

	@Test
	public void tupleFirstMixedTypeConflictTest() {
		SqlParseEventWalker extractor = parseQuery(
				"WITH seed AS (SELECT id FROM <shared>) " + mixedTypeTail());
		assertConflictDiagnostics(extractor, "tuple",
				new String[] {"column", "condition", "join_extension", "predicand", "in_list", "query"},
				new int[] {60, 106, 115, 137, 158, 177});
		assertFullGoldens(
				extractor,
				"{SQL={with={1={cte={select={1={column={name=id, table_ref=null}}}, from={table={substitution={name=<shared>, type=tuple}, alias=null}}}, alias=seed}}, query={union={1={select={1={column={name=id, table_ref=t}, alias=id}, 2={column={substitution={name=<shared>, type=column}, table_ref=t}, alias=c}}, from={extension={substitution={name=<shared>, type=join_extension}}, join={1={table={alias=t, substitution={name=<shared>, type=tuple}}}, 2={join=JOIN, on={substitution={name=<shared>, type=condition}}}, 3={table={alias=u, table=other}}}}, where={and={1={condition={left={column={name=id, table_ref=t}}, right={substitution={name=<shared>, type=predicand}}, operator==}}, 2={in={item={column={name=id, table_ref=t}}, in_list={substitution={name=<shared>, type=in_list}}}}}}}, 2={union={qualifier=ALL, operator=UNION}}, 3={substitution={name=<shared>, type=query}}}}}}",
				"[c, id]",
				"{<shared>=tuple}",
				"{other={}, <shared>={<shared>=[[@16,58:58='t',<459>,1:58]], id=[[@5,21:22='id',<459>,1:21], [@10,46:46='t',<459>,1:46], [@31,130:130='t',<459>,1:130], [@37,150:150='t',<459>,1:150]]}}",
				"{query0={id=[[@5,21:22='id',<459>,1:21]]}, query1={c=[[@20,72:72='c',<459>,1:72]], id=[[@14,54:55='id',<459>,1:54]]}}",
				"{def_union2={context_list={seed=query0}, def_query1={context_list={seed=query0}, query_dictionary={c=[[@20,72:72='c',<459>,1:72]], id=[[@14,54:55='id',<459>,1:54]]}, table_dictionary={other={}, <shared>={<shared>=[[@16,58:58='t',<459>,1:58]], id=[[@10,46:46='t',<459>,1:46], [@31,130:130='t',<459>,1:130], [@37,150:150='t',<459>,1:150]]}}, filters=[{name=id, table_ref=t}, {name=<shared>, type=predicand}], interface={c=[{substitution={name=<shared>, type=column}, table_ref=t}], id=[{name=id, table_ref=t}]}, table_alias={seed=query0, t=<shared>, u=other}}, def_query0={query_dictionary={id=[[@5,21:22='id',<459>,1:21]]}, table_dictionary={<shared>={id=[[@5,21:22='id',<459>,1:21], [@10,46:46='t',<459>,1:46], [@31,130:130='t',<459>,1:130], [@37,150:150='t',<459>,1:150]]}}, interface={id=[{name=id, table_ref=<shared>}]}}, interface={c=query_column, id=query_column}, table_alias={seed=query0}}}");
	}

	@Test
	public void conditionFirstMixedTypeConflictTest() {
		SqlParseEventWalker extractor = parseQuery(
				"WITH seed AS (SELECT id FROM base WHERE <shared>) " + mixedTypeTail());
		assertConflictDiagnostics(extractor, "condition",
				new String[] {"column", "tuple", "join_extension", "predicand", "in_list", "query"},
				new int[] {71, 90, 126, 148, 169, 188});
		assertFullGoldens(
				extractor,
				"{SQL={with={1={cte={select={1={column={name=id, table_ref=null}}}, from={table={alias=null, table=base}}, where={substitution={name=<shared>, type=condition}}}, alias=seed}}, query={union={1={select={1={column={name=id, table_ref=t}, alias=id}, 2={column={substitution={name=<shared>, type=column}, table_ref=t}, alias=c}}, from={extension={substitution={name=<shared>, type=join_extension}}, join={1={table={alias=t, substitution={name=<shared>, type=tuple}}}, 2={join=JOIN, on={substitution={name=<shared>, type=condition}}}, 3={table={alias=u, table=other}}}}, where={and={1={condition={left={column={name=id, table_ref=t}}, right={substitution={name=<shared>, type=predicand}}, operator==}}, 2={in={item={column={name=id, table_ref=t}}, in_list={substitution={name=<shared>, type=in_list}}}}}}}, 2={union={qualifier=ALL, operator=UNION}}, 3={substitution={name=<shared>, type=query}}}}}}",
				"[c, id]",
				"{<shared>=condition}",
				"{other={}, <shared>={<shared>=[[@18,69:69='t',<459>,1:69]], id=[[@12,57:57='t',<459>,1:57], [@33,141:141='t',<459>,1:141], [@39,161:161='t',<459>,1:161]]}, base={id=[[@5,21:22='id',<459>,1:21]]}}",
				"{query0={id=[[@5,21:22='id',<459>,1:21]]}, query1={c=[[@22,83:83='c',<459>,1:83]], id=[[@16,65:66='id',<459>,1:65]]}}",
				"{def_union2={context_list={seed=query0}, def_query1={context_list={seed=query0}, query_dictionary={c=[[@22,83:83='c',<459>,1:83]], id=[[@16,65:66='id',<459>,1:65]]}, table_dictionary={other={}, <shared>={<shared>=[[@18,69:69='t',<459>,1:69]], id=[[@12,57:57='t',<459>,1:57], [@33,141:141='t',<459>,1:141], [@39,161:161='t',<459>,1:161]]}}, filters=[{name=id, table_ref=t}, {name=<shared>, type=predicand}], interface={c=[{substitution={name=<shared>, type=column}, table_ref=t}], id=[{name=id, table_ref=t}]}, table_alias={seed=query0, t=<shared>, u=other}}, def_query0={query_dictionary={id=[[@5,21:22='id',<459>,1:21]]}, table_dictionary={base={id=[[@5,21:22='id',<459>,1:21]]}}, filters=[], interface={id=[{name=id, table_ref=base}]}}, interface={c=query_column, id=query_column}, table_alias={seed=query0}}}");
	}

	@Test
	public void inListFirstMixedTypeConflictTest() {
		SqlParseEventWalker extractor = parseQuery(
				"WITH seed AS (SELECT id FROM base WHERE id IN <shared>) " + mixedTypeTail());
		assertConflictDiagnostics(extractor, "in_list",
				new String[] {"column", "tuple", "condition", "join_extension", "predicand", "query"},
				new int[] {77, 96, 123, 132, 154, 194});
		assertFullGoldens(
				extractor,
				"{SQL={with={1={cte={select={1={column={name=id, table_ref=null}}}, from={table={alias=null, table=base}}, where={in={item={column={name=id, table_ref=null}}, in_list={substitution={name=<shared>, type=in_list}}}}}, alias=seed}}, query={union={1={select={1={column={name=id, table_ref=t}, alias=id}, 2={column={substitution={name=<shared>, type=column}, table_ref=t}, alias=c}}, from={extension={substitution={name=<shared>, type=join_extension}}, join={1={table={alias=t, substitution={name=<shared>, type=tuple}}}, 2={join=JOIN, on={substitution={name=<shared>, type=condition}}}, 3={table={alias=u, table=other}}}}, where={and={1={condition={left={column={name=id, table_ref=t}}, right={substitution={name=<shared>, type=predicand}}, operator==}}, 2={in={item={column={name=id, table_ref=t}}, in_list={substitution={name=<shared>, type=in_list}}}}}}}, 2={union={qualifier=ALL, operator=UNION}}, 3={substitution={name=<shared>, type=query}}}}}}",
				"[c, id]",
				"{<shared>=in_list}",
				"{other={}, <shared>={<shared>=[[@20,75:75='t',<459>,1:75]], id=[[@14,63:63='t',<459>,1:63], [@35,147:147='t',<459>,1:147], [@41,167:167='t',<459>,1:167]]}, base={id=[[@5,21:22='id',<459>,1:21], [@9,40:41='id',<459>,1:40]]}}",
				"{query0={id=[[@5,21:22='id',<459>,1:21]]}, query1={c=[[@24,89:89='c',<459>,1:89]], id=[[@18,71:72='id',<459>,1:71]]}}",
				"{def_union2={context_list={seed=query0}, def_query1={context_list={seed=query0}, query_dictionary={c=[[@24,89:89='c',<459>,1:89]], id=[[@18,71:72='id',<459>,1:71]]}, table_dictionary={other={}, <shared>={<shared>=[[@20,75:75='t',<459>,1:75]], id=[[@14,63:63='t',<459>,1:63], [@35,147:147='t',<459>,1:147], [@41,167:167='t',<459>,1:167]]}}, filters=[{name=id, table_ref=t}, {name=<shared>, type=predicand}], interface={c=[{substitution={name=<shared>, type=column}, table_ref=t}], id=[{name=id, table_ref=t}]}, table_alias={seed=query0, t=<shared>, u=other}}, def_query0={query_dictionary={id=[[@5,21:22='id',<459>,1:21]]}, table_dictionary={base={id=[[@5,21:22='id',<459>,1:21], [@9,40:41='id',<459>,1:40]]}}, filters=[{name=id, table_ref=base}], interface={id=[{name=id, table_ref=base}]}}, interface={c=query_column, id=query_column}, table_alias={seed=query0}}}");
	}

	@Test
	public void queryFirstMixedTypeConflictTest() {
		SqlParseEventWalker extractor = parseQuery(
				"WITH seed AS (<shared>) " + mixedTypeTail());
		assertConflictDiagnostics(extractor, "query",
				new String[] {"column", "tuple", "condition", "join_extension", "predicand", "in_list"},
				new int[] {45, 64, 91, 100, 122, 143});
		assertFullGoldens(
				extractor,
				"{SQL={with={1={cte={substitution={name=<shared>, type=query}}, alias=seed}}, query={union={1={select={1={column={name=id, table_ref=t}, alias=id}, 2={column={substitution={name=<shared>, type=column}, table_ref=t}, alias=c}}, from={extension={substitution={name=<shared>, type=join_extension}}, join={1={table={alias=t, substitution={name=<shared>, type=tuple}}}, 2={join=JOIN, on={substitution={name=<shared>, type=condition}}}, 3={table={alias=u, table=other}}}}, where={and={1={condition={left={column={name=id, table_ref=t}}, right={substitution={name=<shared>, type=predicand}}, operator==}}, 2={in={item={column={name=id, table_ref=t}}, in_list={substitution={name=<shared>, type=in_list}}}}}}}, 2={union={qualifier=ALL, operator=UNION}}, 3={substitution={name=<shared>, type=query}}}}}}",
				"[c, id]",
				"{<shared>=query}",
				"{other={}, <shared>={<shared>=[[@13,43:43='t',<459>,1:43]], id=[[@7,31:31='t',<459>,1:31], [@28,115:115='t',<459>,1:115], [@34,135:135='t',<459>,1:135]]}}",
				"{query0={}, query1={c=[[@17,57:57='c',<459>,1:57]], id=[[@11,39:40='id',<459>,1:39]]}}",
				"{def_union2={context_list={seed=query0}, def_query1={context_list={seed=query0}, query_dictionary={c=[[@17,57:57='c',<459>,1:57]], id=[[@11,39:40='id',<459>,1:39]]}, table_dictionary={other={}, <shared>={<shared>=[[@13,43:43='t',<459>,1:43]], id=[[@7,31:31='t',<459>,1:31], [@28,115:115='t',<459>,1:115], [@34,135:135='t',<459>,1:135]]}}, filters=[{name=id, table_ref=t}, {name=<shared>, type=predicand}], interface={c=[{substitution={name=<shared>, type=column}, table_ref=t}], id=[{name=id, table_ref=t}]}, table_alias={seed=query0, t=<shared>, u=other}}, def_query0={query_dictionary={}, interface={}}, interface={c=query_column, id=query_column}, table_alias={seed=query0}}}");
	}

	@Test
	public void joinExtensionFirstMixedTypeConflictTest() {
		SqlParseEventWalker extractor = parseQuery(
				"WITH seed AS (SELECT id FROM base <shared>) " + mixedTypeTail());
		assertConflictDiagnostics(extractor, "join_extension",
				new String[] {"column", "tuple", "condition", "predicand", "in_list", "query"},
				new int[] {65, 84, 111, 142, 163, 182});
		assertFullGoldens(
				extractor,
				"{SQL={with={1={cte={select={1={column={name=id, table_ref=null}}}, from={extension={substitution={name=<shared>, type=join_extension}}, table={alias=null, table=base}}}, alias=seed}}, query={union={1={select={1={column={name=id, table_ref=t}, alias=id}, 2={column={substitution={name=<shared>, type=column}, table_ref=t}, alias=c}}, from={extension={substitution={name=<shared>, type=join_extension}}, join={1={table={alias=t, substitution={name=<shared>, type=tuple}}}, 2={join=JOIN, on={substitution={name=<shared>, type=condition}}}, 3={table={alias=u, table=other}}}}, where={and={1={condition={left={column={name=id, table_ref=t}}, right={substitution={name=<shared>, type=predicand}}, operator==}}, 2={in={item={column={name=id, table_ref=t}}, in_list={substitution={name=<shared>, type=in_list}}}}}}}, 2={union={qualifier=ALL, operator=UNION}}, 3={substitution={name=<shared>, type=query}}}}}}",
				"[c, id]",
				"{<shared>=join_extension}",
				"{other={}, <shared>={<shared>=[[@17,63:63='t',<459>,1:63]], id=[[@11,51:51='t',<459>,1:51], [@32,135:135='t',<459>,1:135], [@38,155:155='t',<459>,1:155]]}, base={id=[[@5,21:22='id',<459>,1:21]]}}",
				"{query0={id=[[@5,21:22='id',<459>,1:21]]}, query1={c=[[@21,77:77='c',<459>,1:77]], id=[[@15,59:60='id',<459>,1:59]]}}",
				"{def_union2={context_list={seed=query0}, def_query1={context_list={seed=query0}, query_dictionary={c=[[@21,77:77='c',<459>,1:77]], id=[[@15,59:60='id',<459>,1:59]]}, table_dictionary={other={}, <shared>={<shared>=[[@17,63:63='t',<459>,1:63]], id=[[@11,51:51='t',<459>,1:51], [@32,135:135='t',<459>,1:135], [@38,155:155='t',<459>,1:155]]}}, filters=[{name=id, table_ref=t}, {name=<shared>, type=predicand}], interface={c=[{substitution={name=<shared>, type=column}, table_ref=t}], id=[{name=id, table_ref=t}]}, table_alias={seed=query0, t=<shared>, u=other}}, def_query0={query_dictionary={id=[[@5,21:22='id',<459>,1:21]]}, table_dictionary={base={id=[[@5,21:22='id',<459>,1:21]]}}, interface={id=[{name=id, table_ref=base}]}}, interface={c=query_column, id=query_column}, table_alias={seed=query0}}}");
	}

	@Test
	public void tupleThenParenthesizedQueryConflictTest() {
		SqlParseEventWalker extractor = parseQuery(
				"SELECT x.id FROM <shared> x JOIN (<shared>) q ON x.id = q.id");
		assertConflictDiagnostics(extractor, "tuple", new String[] {"query"}, new int[] {34}, 1);
		assertFullGoldens(
				extractor,
				"{SQL={select={1={column={name=id, table_ref=x}}}, from={join={1={table={alias=x, substitution={name=<shared>, type=tuple}}}, 2={join=JOIN, on={condition={left={column={name=id, table_ref=x}}, right={column={name=id, table_ref=q}}, operator==}}}, 3={table={alias=q, substitution={name=<shared>, type=query}}}}}}}",
				"[id]",
				"{<shared>=tuple}",
				"{<shared>={id=[[@1,7:7='x',<459>,1:7], [@13,49:49='x',<459>,1:49]]}}",
				"{query0={id=[[@17,56:56='q',<459>,1:56]]}, query1={id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query1={query_dictionary={id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={<shared>={id=[[@1,7:7='x',<459>,1:7], [@13,49:49='x',<459>,1:49]]}}, def_query0={query_dictionary={id=[[@17,56:56='q',<459>,1:56]]}, interface={id=[]}}, filters=[{name=id, table_ref=x}, {name=id, table_ref=q}], interface={id=[{name=id, table_ref=x}]}, table_alias={q=query0, x=<shared>}}}");
	}

	@Test
	public void parenthesizedQueryThenTupleConflictTest() {
		SqlParseEventWalker extractor = parseQuery(
				"SELECT q.id FROM (<shared>) q JOIN <shared> x ON q.id = x.id");
		assertConflictDiagnostics(extractor, "query", new String[] {"tuple"}, new int[] {35}, 1);
		assertFullGoldens(
				extractor,
				"{SQL={select={1={column={name=id, table_ref=q}}}, from={join={1={table={alias=q, substitution={name=<shared>, type=query}}}, 2={join=JOIN, on={condition={left={column={name=id, table_ref=q}}, right={column={name=id, table_ref=x}}, operator==}}}, 3={table={alias=x, substitution={name=<shared>, type=tuple}}}}}}}",
				"[id]",
				"{<shared>=query}",
				"{<shared>={id=[[@17,56:56='x',<459>,1:56]]}}",
				"{query0={id=[[@1,7:7='q',<459>,1:7], [@13,49:49='q',<459>,1:49]]}, query1={id=[[@3,9:10='id',<459>,1:9]]}}",
				"{def_query1={query_dictionary={id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={<shared>={id=[[@17,56:56='x',<459>,1:56]]}}, def_query0={query_dictionary={id=[[@1,7:7='q',<459>,1:7], [@13,49:49='q',<459>,1:49]]}, interface={id=[]}}, filters=[{name=id, table_ref=q}, {name=id, table_ref=x}], interface={id=[{name=id, table_ref=q}]}, table_alias={q=query0, x=<shared>}}}");
	}

	@Test
	public void repeatedConflictingTypeTest() {
		SqlParseEventWalker extractor = parseQuery(
				"SELECT <shared> AS original_value FROM data t "
						+ "WHERE t.<shared> = 1 AND t.<shared> = 2 AND t.<shared> = 3");
		assertConflictDiagnostics(extractor, "predicand",
				new String[] {"column", "column", "column"},
				new int[] {54, 73, 92});
		assertFullGoldens(
				extractor,
				"{SQL={select={1={substitution={name=<shared>, type=predicand}, alias=original_value}}, from={table={alias=t, table=data}}, where={and={1={condition={left={column={substitution={name=<shared>, type=column}, table_ref=t}}, right={literal=1}, operator==}}, 2={condition={left={column={substitution={name=<shared>, type=column}, table_ref=t}}, right={literal=2}, operator==}}, 3={condition={left={column={substitution={name=<shared>, type=column}, table_ref=t}}, right={literal=3}, operator==}}}}}}",
				"[original_value]",
				"{<shared>=predicand}",
				"{data={<shared>=[[@8,52:52='t',<459>,1:52], [@14,71:71='t',<459>,1:71], [@20,90:90='t',<459>,1:90]]}}",
				"{query0={original_value=[[@3,19:32='original_value',<459>,1:19]]}}",
				"{def_query0={query_dictionary={original_value=[[@3,19:32='original_value',<459>,1:19]]}, table_dictionary={data={<shared>=[[@8,52:52='t',<459>,1:52], [@14,71:71='t',<459>,1:71], [@20,90:90='t',<459>,1:90]]}}, filters=[{substitution={name=<shared>, type=column}, table_ref=t}], interface={original_value=[{name=<shared>, type=predicand}]}, table_alias={t=data}}}");
	}

	@Test
	public void sameTypeReuseTest() {
		SqlParseEventWalker extractor = parseQuery(
				"SELECT <shared> AS v1, <shared> AS v2 FROM data");
		assertNoWalkerDiagnostics(extractor);
		assertFullGoldens(
				extractor,
				"{SQL={select={1={substitution={name=<shared>, type=predicand}, alias=v1}, 2={substitution={name=<shared>, type=predicand}, alias=v2}}, from={table={alias=null, table=data}}}}",
				"[v1, v2]",
				"{<shared>=predicand}",
				"{data={}}",
				"{query0={v2=[[@7,35:36='v2',<459>,1:35]], v1=[[@3,19:20='v1',<459>,1:19]]}}",
				"{def_query0={query_dictionary={v1=[[@3,19:20='v1',<459>,1:19]], v2=[[@7,35:36='v2',<459>,1:35]]}, table_dictionary={data={}}, interface={v1=[{name=<shared>, type=predicand}], v2=[{name=<shared>, type=predicand}]}}}");
	}

	@Test
	public void scriptStatementIsolationTest() {
		String query = "SELECT <shared> AS v1 FROM data; SELECT * FROM <shared>;";
		SQLSelectParserParser parser = parse(query);
		SqlParseEventWalker extractor = runScriptParsertest(query, parser);
		assertNoWalkerDiagnostics(extractor);
		assertFullGoldens(
				extractor,
				"{SCRIPT={1={select={1={substitution={name=<shared>, type=predicand}, alias=v1}}, from={table={alias=null, table=data}}}, 2={select={1={column={name=*, table_ref=*}}}, from={table={substitution={name=<shared>, type=tuple}, alias=null}}}}}",
				"[]",
				"{SCRIPT={1={<shared>=predicand}, 2={<shared>=tuple}}}",
				"{SCRIPT={1={data={}}, 2={<shared>={*=[[@8,40:40='*',<291>,1:40]]}}}}",
				"{SCRIPT={1={query0={v1=[[@3,19:20='v1',<459>,1:19]]}}, 2={query0={*=[[@8,40:40='*',<291>,1:40]]}}}}",
				"{SCRIPT={1={def_query0={query_dictionary={v1=[[@3,19:20='v1',<459>,1:19]]}, table_dictionary={data={}}, interface={v1=[{name=<shared>, type=predicand}]}}}, 2={def_query0={query_dictionary={*=[[@8,40:40='*',<291>,1:40]]}, table_dictionary={<shared>={*=[[@8,40:40='*',<291>,1:40]]}}, interface={*=[{name=*, table_ref=*}]}}}}}");
	}

	private void assertConflictDiagnostics(
			SqlParseEventWalker extractor,
			String originalType,
			String[] conflictingTypes,
			int[] characterPositions) {
		assertConflictDiagnostics(extractor, originalType, conflictingTypes, characterPositions, 0);
	}

	private void assertConflictDiagnostics(
			SqlParseEventWalker extractor,
			String originalType,
			String[] conflictingTypes,
			int[] characterPositions,
			int additionalSevereWarnings) {
		assertNoFatalErrors(extractor);
		assertConflictDiagnostics(extractor.getSnippet(), originalType, conflictingTypes, characterPositions,
				additionalSevereWarnings);
	}

	private void assertConflictDiagnostics(
			Snippet snippet,
			String originalType,
			String[] conflictingTypes,
			int[] characterPositions) {
		assertConflictDiagnostics(snippet, originalType, conflictingTypes, characterPositions, 0);
	}

	private void assertConflictDiagnostics(
			Snippet snippet,
			String originalType,
			String[] conflictingTypes,
			int[] characterPositions,
			int additionalSevereWarnings) {
		List<ParseDiagnostic> conflicts = conflictDiagnostics(snippet);
		Assert.assertEquals("Test setup must provide one position per conflict type",
				conflictingTypes.length, characterPositions.length);
		Assert.assertEquals("Unexpected conflict diagnostic count", conflictingTypes.length, conflicts.size());
		Assert.assertEquals("Unexpected severe-warning count", conflictingTypes.length + additionalSevereWarnings,
				snippet.getDiagnosticCountBySeverity(ParseDiagnostic.Severity.SEVERE_WARNING));
		Assert.assertEquals("Unexpected fatal diagnostics: " + snippet.getParserDiagnosticList(), 0,
				snippet.getDiagnosticCountBySeverity(ParseDiagnostic.Severity.FATAL));
		for (int index = 0; index < conflictingTypes.length; index++) {
			ParseDiagnostic diagnostic = conflicts.get(index);
			String expectedMessage = "First occurrence of <shared> registered as " + originalType
					+ ". Conflicting variable of type " + conflictingTypes[index]
					+ " found at (l:1 c:" + characterPositions[index] + ").";
			Assert.assertEquals("Unexpected diagnostic severity", ParseDiagnostic.Severity.SEVERE_WARNING,
					diagnostic.severity());
			Assert.assertEquals("Unexpected diagnostic message", expectedMessage, diagnostic.message());
			Assert.assertEquals("Unexpected diagnostic line", Integer.valueOf(1), diagnostic.line());
			Assert.assertEquals("Unexpected diagnostic position", Integer.valueOf(characterPositions[index]),
					diagnostic.charPositionInLine());
			Assert.assertEquals("Unexpected diagnostic token", "<shared>", diagnostic.tokenText());
			Assert.assertTrue("Conflict diagnostic must be recoverable", diagnostic.recoverable());
		}
	}

	private Snippet parsePlacement(String query, String endpoint) {
		SqlParserAccess parser = new SqlParserAccess(false, false, false);
		parser.executeTheParse(query, endpoint);
		Assert.assertEquals("Syntax errors in " + endpoint, 0, parser.getParser().getNumberOfSyntaxErrors());
		return parser.getSnippet();
	}

	private String mixedTypeTail() {
		return "SELECT t.id AS id, t.<shared> AS c "
				+ "FROM <shared> t JOIN other u ON <shared> <shared> "
				+ "WHERE t.id = <shared> AND t.id IN <shared> UNION ALL <shared>";
	}

	private void assertFullGoldens(
			SqlParseEventWalker extractor,
			String ast,
			String queryInterface,
			String substitutions,
			String tableDictionary,
			String queryDictionary,
			String symbolTable) {
		Assert.assertEquals("AST is wrong", ast, extractor.getAsTree().toString());
		Assert.assertEquals("Interface is wrong", queryInterface, extractor.getInterface().toString());
		Assert.assertEquals("Substitution List is wrong", substitutions, extractor.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", tableDictionary,
				extractor.getTableColumnDictionaryMap().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", queryDictionary,
				extractor.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", symbolTable, extractor.getSymbolTable().toString());
	}

	private void assertFullGoldens(
			Snippet snippet,
			String ast,
			String queryInterface,
			String substitutions,
			String tableDictionary,
			String queryDictionary,
			String symbolTable) {
		Assert.assertEquals("AST is wrong", ast, snippet.getSqlAbstractTree().toString());
		Assert.assertEquals("Interface is wrong", queryInterface, snippet.getQueryInterface().toString());
		Assert.assertEquals("Substitution List is wrong", substitutions, snippet.getSubstitutionsMap().toString());
		Assert.assertEquals("Table Dictionary is wrong", tableDictionary, snippet.getTableDictionary().toString());
		Assert.assertEquals("Query Column Dictionary is wrong", queryDictionary,
				snippet.getQueryColumnDictionaryMap().toString());
		Assert.assertEquals("Symbol Table is wrong", symbolTable, snippet.getSymbolTable().toString());
	}

	private SqlParseEventWalker parseQuery(String query) {
		SQLSelectParserParser parser = parse(query);
		return runParsertest(query, parser);
	}

	private List<ParseDiagnostic> conflictDiagnostics(Snippet snippet) {
		return snippet.getParserDiagnosticList().stream()
				.filter(diagnostic -> diagnostic != null && CODE.equals(diagnostic.code()))
				.collect(Collectors.toList());
	}
}
