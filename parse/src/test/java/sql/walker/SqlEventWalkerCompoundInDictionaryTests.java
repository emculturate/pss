package sql.walker;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

public class SqlEventWalkerCompoundInDictionaryTests extends AbstractSqlParseEventWalkerTest {
    private void assertRow9922(String fixture) {
        String query = readTestResource("/compound-in/" + fixture + ".sql");
        String[] golden = readTestResource("/compound-in/" + fixture + "-golden.txt").split("\n", -1);
        Assert.assertEquals(7, golden.length);
        SqlParseEventWalker extractor = runParsertest(query, parse(query));
        assertOutputs(extractor, golden[0], golden[1], golden[2], golden[3], golden[4], golden[5]);
        SqlParseEventWalker queryExtractor = runQueryParsertest(query, parse(query));
        assertOutputs(queryExtractor, golden[0].replace("{SQL=", "{QUERY="),
                golden[1], golden[2], golden[3], golden[4], golden[5]);

        Object source = extractor.getTableColumnDictionaryMap().get("<[AMS].[donor_source]>");
        Assert.assertTrue("Expected donor source dictionary", source instanceof Map<?, ?>);
        Map<?, ?> columns = (Map<?, ?>) source;
        Assert.assertEquals(21, columns.size());
        Assert.assertEquals(1, ((List<?>) columns.get("src_donor_id")).size());
        for (String family : List.of("end_date", "constituent_code")) {
            for (int i = 0; i < 10; i++) {
                String name = family + (i == 0 ? "" : "_" + i);
                Object value = columns.get(name);
                Assert.assertTrue("Missing column " + name, value instanceof List<?>);
                List<?> locations = (List<?>) value;
                int expectedCount = family.equals("constituent_code") && i == 0 ? 41 : 40;
                Assert.assertEquals(name, expectedCount, locations.size());
                Assert.assertEquals(name + " occurrence tokens must be distinct", expectedCount,
                        new HashSet<>(locations).size());
            }
        }
    }

    @Test
    public void originalRow9922Test() {
        assertRow9922("row0");
    }

    @Test
    public void swappedRow9922Test() {
        assertRow9922("row1");
    }

    private void assertOutputs(SqlParseEventWalker extractor, String ast, String queryInterface,
            String substitutions, String tableDictionary, String queryDictionary, String symbolTable) {
        assertNoWalkerDiagnostics(extractor, Set.of());
        Assert.assertEquals("All diagnostics", 0, extractor.getSnippet().getParserDiagnosticList().size());
        Assert.assertEquals("AST", ast, extractor.getAsTree().toString());
        Assert.assertEquals("Interface", queryInterface, extractor.getInterface().toString());
        Assert.assertEquals("Substitutions", substitutions, extractor.getSubstitutionsMap().toString());
        Assert.assertEquals("Table dictionary", tableDictionary, extractor.getTableColumnDictionaryMap().toString());
        Assert.assertEquals("Query dictionary", queryDictionary, extractor.getQueryColumnDictionaryMap().toString());
        Assert.assertEquals("Symbol table", symbolTable, extractor.getSymbolTable().toString());
    }

    @Test
    public void caseAndTest() {
        String query = "select case when ('x' IN (a1, a2) and 'y' IN (b1, b2)) then 1 else 0 end as flag from <[D].[T]> t";
        SqlParseEventWalker extractor = runParsertest(query, parse(query));
        assertOutputs(extractor,
                "{SQL={select={1={alias=flag, case={clauses={1={then={literal=1}, when={parentheses={and={1={in={item={literal='x'}, in_list={list={1={column={name=a1, table_ref=null}}, 2={column={name=a2, table_ref=null}}}}}}, 2={in={item={literal='y'}, in_list={list={1={column={name=b1, table_ref=null}}, 2={column={name=b2, table_ref=null}}}}}}}}}}}, else={literal=0}}}}, from={table={alias=t, substitution={name=<[D].[T]>, parts={1=[D], 2=[T]}, type=tuple}}}}}",
                "[flag]",
                "{<[D].[T]>=tuple}",
                "{<[D].[T]>={a1=[[@7,26:27='a1',<459>,1:26]], b2=[[@17,50:51='b2',<459>,1:50]], a2=[[@9,30:31='a2',<459>,1:30]], b1=[[@15,46:47='b1',<459>,1:46]]}}",
                "{query0={flag=[[@26,76:79='flag',<459>,1:76]]}}",
                "{def_query0={query_dictionary={flag=[[@26,76:79='flag',<459>,1:76]]}, table_dictionary={<[D].[T]>={a1=[[@7,26:27='a1',<459>,1:26]], b2=[[@17,50:51='b2',<459>,1:50]], a2=[[@9,30:31='a2',<459>,1:30]], b1=[[@15,46:47='b1',<459>,1:46]]}}, interface={flag=[{name=a1, table_ref=<[D].[T]>}, {name=a2, table_ref=<[D].[T]>}, {name=b1, table_ref=<[D].[T]>}, {name=b2, table_ref=<[D].[T]>}]}, table_alias={t=<[D].[T]>}}}");
    }

    @Test
    public void caseReversedTest() {
        String query = "select case when ('y' IN (b1, b2) and 'x' IN (a1, a2)) then 1 else 0 end as flag from <[D].[T]> t";
        SqlParseEventWalker extractor = runParsertest(query, parse(query));
        assertOutputs(extractor,
                "{SQL={select={1={alias=flag, case={clauses={1={then={literal=1}, when={parentheses={and={1={in={item={literal='y'}, in_list={list={1={column={name=b1, table_ref=null}}, 2={column={name=b2, table_ref=null}}}}}}, 2={in={item={literal='x'}, in_list={list={1={column={name=a1, table_ref=null}}, 2={column={name=a2, table_ref=null}}}}}}}}}}}, else={literal=0}}}}, from={table={alias=t, substitution={name=<[D].[T]>, parts={1=[D], 2=[T]}, type=tuple}}}}}",
                "[flag]",
                "{<[D].[T]>=tuple}",
                "{<[D].[T]>={b2=[[@9,30:31='b2',<459>,1:30]], a1=[[@15,46:47='a1',<459>,1:46]], a2=[[@17,50:51='a2',<459>,1:50]], b1=[[@7,26:27='b1',<459>,1:26]]}}",
                "{query0={flag=[[@26,76:79='flag',<459>,1:76]]}}",
                "{def_query0={query_dictionary={flag=[[@26,76:79='flag',<459>,1:76]]}, table_dictionary={<[D].[T]>={b2=[[@9,30:31='b2',<459>,1:30]], a1=[[@15,46:47='a1',<459>,1:46]], a2=[[@17,50:51='a2',<459>,1:50]], b1=[[@7,26:27='b1',<459>,1:26]]}}, interface={flag=[{name=b1, table_ref=<[D].[T]>}, {name=b2, table_ref=<[D].[T]>}, {name=a1, table_ref=<[D].[T]>}, {name=a2, table_ref=<[D].[T]>}]}, table_alias={t=<[D].[T]>}}}");
    }

    @Test
    public void caseOrTest() {
        String query = "select case when ('x' IN (a1, a2) or 'y' IN (b1, b2)) then 1 else 0 end as flag from <[D].[T]> t";
        SqlParseEventWalker extractor = runParsertest(query, parse(query));
        assertOutputs(extractor,
                "{SQL={select={1={alias=flag, case={clauses={1={then={literal=1}, when={parentheses={or={1={in={item={literal='x'}, in_list={list={1={column={name=a1, table_ref=null}}, 2={column={name=a2, table_ref=null}}}}}}, 2={in={item={literal='y'}, in_list={list={1={column={name=b1, table_ref=null}}, 2={column={name=b2, table_ref=null}}}}}}}}}}}, else={literal=0}}}}, from={table={alias=t, substitution={name=<[D].[T]>, parts={1=[D], 2=[T]}, type=tuple}}}}}",
                "[flag]",
                "{<[D].[T]>=tuple}",
                "{<[D].[T]>={a1=[[@7,26:27='a1',<459>,1:26]], b2=[[@17,49:50='b2',<459>,1:49]], a2=[[@9,30:31='a2',<459>,1:30]], b1=[[@15,45:46='b1',<459>,1:45]]}}",
                "{query0={flag=[[@26,75:78='flag',<459>,1:75]]}}",
                "{def_query0={query_dictionary={flag=[[@26,75:78='flag',<459>,1:75]]}, table_dictionary={<[D].[T]>={a1=[[@7,26:27='a1',<459>,1:26]], b2=[[@17,49:50='b2',<459>,1:49]], a2=[[@9,30:31='a2',<459>,1:30]], b1=[[@15,45:46='b1',<459>,1:45]]}}, interface={flag=[{name=a1, table_ref=<[D].[T]>}, {name=a2, table_ref=<[D].[T]>}, {name=b1, table_ref=<[D].[T]>}, {name=b2, table_ref=<[D].[T]>}]}, table_alias={t=<[D].[T]>}}}");
    }

    @Test
    public void mixedTest() {
        String query = "select case when (b1 = 2 and 'x' IN (a1, a2)) then 1 else 0 end as flag from <[D].[T]> t";
        SqlParseEventWalker extractor = runParsertest(query, parse(query));
        assertOutputs(extractor,
                "{SQL={select={1={alias=flag, case={clauses={1={then={literal=1}, when={parentheses={and={1={condition={left={column={name=b1, table_ref=null}}, right={literal=2}, operator==}}, 2={in={item={literal='x'}, in_list={list={1={column={name=a1, table_ref=null}}, 2={column={name=a2, table_ref=null}}}}}}}}}}}, else={literal=0}}}}, from={table={alias=t, substitution={name=<[D].[T]>, parts={1=[D], 2=[T]}, type=tuple}}}}}",
                "[flag]",
                "{<[D].[T]>=tuple}",
                "{<[D].[T]>={a1=[[@11,37:38='a1',<459>,1:37]], a2=[[@13,41:42='a2',<459>,1:41]], b1=[[@4,18:19='b1',<459>,1:18]]}}",
                "{query0={flag=[[@22,67:70='flag',<459>,1:67]]}}",
                "{def_query0={query_dictionary={flag=[[@22,67:70='flag',<459>,1:67]]}, table_dictionary={<[D].[T]>={a1=[[@11,37:38='a1',<459>,1:37]], a2=[[@13,41:42='a2',<459>,1:41]], b1=[[@4,18:19='b1',<459>,1:18]]}}, interface={flag=[{name=b1, table_ref=<[D].[T]>}, {name=a1, table_ref=<[D].[T]>}, {name=a2, table_ref=<[D].[T]>}]}, table_alias={t=<[D].[T]>}}}");
    }

    @Test
    public void mixedReversedTest() {
        String query = "select case when ('x' IN (a1, a2) and b1 = 2) then 1 else 0 end as flag from <[D].[T]> t";
        SqlParseEventWalker extractor = runParsertest(query, parse(query));
        assertOutputs(extractor,
                "{SQL={select={1={alias=flag, case={clauses={1={then={literal=1}, when={parentheses={and={1={in={item={literal='x'}, in_list={list={1={column={name=a1, table_ref=null}}, 2={column={name=a2, table_ref=null}}}}}}, 2={condition={left={column={name=b1, table_ref=null}}, right={literal=2}, operator==}}}}}}}, else={literal=0}}}}, from={table={alias=t, substitution={name=<[D].[T]>, parts={1=[D], 2=[T]}, type=tuple}}}}}",
                "[flag]",
                "{<[D].[T]>=tuple}",
                "{<[D].[T]>={a1=[[@7,26:27='a1',<459>,1:26]], a2=[[@9,30:31='a2',<459>,1:30]], b1=[[@12,38:39='b1',<459>,1:38]]}}",
                "{query0={flag=[[@22,67:70='flag',<459>,1:67]]}}",
                "{def_query0={query_dictionary={flag=[[@22,67:70='flag',<459>,1:67]]}, table_dictionary={<[D].[T]>={a1=[[@7,26:27='a1',<459>,1:26]], a2=[[@9,30:31='a2',<459>,1:30]], b1=[[@12,38:39='b1',<459>,1:38]]}}, interface={flag=[{name=a1, table_ref=<[D].[T]>}, {name=a2, table_ref=<[D].[T]>}, {name=b1, table_ref=<[D].[T]>}]}, table_alias={t=<[D].[T]>}}}");
    }

    @Test
    public void booleanOutputTest() {
        String query = "select id, ('x' IN (a1, a2) and 'y' IN (b1, b2)) as both_match, name from <[D].[T]> t";
        SqlParseEventWalker extractor = runParsertest(query, parse(query));
        assertOutputs(extractor,
                "{SQL={select={1={column={name=id, table_ref=null}}, 2={parentheses={and={1={in={item={literal='x'}, in_list={list={1={column={name=a1, table_ref=null}}, 2={column={name=a2, table_ref=null}}}}}}, 2={in={item={literal='y'}, in_list={list={1={column={name=b1, table_ref=null}}, 2={column={name=b2, table_ref=null}}}}}}}}, alias=both_match}, 3={column={name=name, table_ref=null}}}, from={table={alias=t, substitution={name=<[D].[T]>, parts={1=[D], 2=[T]}, type=tuple}}}}}",
                "[both_match, name, id]",
                "{<[D].[T]>=tuple}",
                "{<[D].[T]>={a1=[[@7,20:21='a1',<459>,1:20]], b2=[[@17,44:45='b2',<459>,1:44]], a2=[[@9,24:25='a2',<459>,1:24]], name=[[@23,64:67='name',<225>,1:64]], id=[[@1,7:8='id',<459>,1:7]], b1=[[@15,40:41='b1',<459>,1:40]]}}",
                "{query0={name=[[@23,64:67='name',<225>,1:64]], both_match=[[@21,52:61='both_match',<459>,1:52]], id=[[@1,7:8='id',<459>,1:7]]}}",
                "{def_query0={query_dictionary={both_match=[[@21,52:61='both_match',<459>,1:52]], name=[[@23,64:67='name',<225>,1:64]], id=[[@1,7:8='id',<459>,1:7]]}, table_dictionary={<[D].[T]>={a1=[[@7,20:21='a1',<459>,1:20]], b2=[[@17,44:45='b2',<459>,1:44]], a2=[[@9,24:25='a2',<459>,1:24]], name=[[@23,64:67='name',<225>,1:64]], id=[[@1,7:8='id',<459>,1:7]], b1=[[@15,40:41='b1',<459>,1:40]]}}, interface={both_match=[{name=a1, table_ref=<[D].[T]>}, {name=a2, table_ref=<[D].[T]>}, {name=b1, table_ref=<[D].[T]>}, {name=b2, table_ref=<[D].[T]>}], name=[{name=name, table_ref=<[D].[T]>}], id=[{name=id, table_ref=<[D].[T]>}]}, table_alias={t=<[D].[T]>}}}");
    }

    @Test
    public void separateOutputsTest() {
        String query = "select id, 'x' IN (a1, a2) as bool_a, name, 'y' IN (b1, b2) as bool_b from <[D].[T]> t";
        SqlParseEventWalker extractor = runParsertest(query, parse(query));
        assertOutputs(extractor,
                "{SQL={select={1={column={name=id, table_ref=null}}, 2={in={item={literal='x'}, in_list={list={1={column={name=a1, table_ref=null}}, 2={column={name=a2, table_ref=null}}}}}, alias=bool_a}, 3={column={name=name, table_ref=null}}, 4={in={item={literal='y'}, in_list={list={1={column={name=b1, table_ref=null}}, 2={column={name=b2, table_ref=null}}}}}, alias=bool_b}}, from={table={alias=t, substitution={name=<[D].[T]>, parts={1=[D], 2=[T]}, type=tuple}}}}}",
                "[bool_b, bool_a, name, id]",
                "{<[D].[T]>=tuple}",
                "{<[D].[T]>={a1=[[@6,19:20='a1',<459>,1:19]], b2=[[@20,56:57='b2',<459>,1:56]], a2=[[@8,23:24='a2',<459>,1:23]], name=[[@13,38:41='name',<225>,1:38]], id=[[@1,7:8='id',<459>,1:7]], b1=[[@18,52:53='b1',<459>,1:52]]}}",
                "{query0={name=[[@13,38:41='name',<225>,1:38]], id=[[@1,7:8='id',<459>,1:7]], bool_b=[[@23,63:68='bool_b',<459>,1:63]], bool_a=[[@11,30:35='bool_a',<459>,1:30]]}}",
                "{def_query0={query_dictionary={bool_b=[[@23,63:68='bool_b',<459>,1:63]], bool_a=[[@11,30:35='bool_a',<459>,1:30]], name=[[@13,38:41='name',<225>,1:38]], id=[[@1,7:8='id',<459>,1:7]]}, table_dictionary={<[D].[T]>={a1=[[@6,19:20='a1',<459>,1:19]], b2=[[@20,56:57='b2',<459>,1:56]], a2=[[@8,23:24='a2',<459>,1:23]], name=[[@13,38:41='name',<225>,1:38]], id=[[@1,7:8='id',<459>,1:7]], b1=[[@18,52:53='b1',<459>,1:52]]}}, interface={bool_b=[{name=b1, table_ref=<[D].[T]>}, {name=b2, table_ref=<[D].[T]>}], bool_a=[{name=a1, table_ref=<[D].[T]>}, {name=a2, table_ref=<[D].[T]>}], name=[{name=name, table_ref=<[D].[T]>}], id=[{name=id, table_ref=<[D].[T]>}]}, table_alias={t=<[D].[T]>}}}");
    }

    @Test
    public void multipleOutputsTest() {
        String query = "select id, ('x' IN (a1, a2) and 'y' IN (b1, b2)) as ab, name, ('y' IN (b1, b2) and 'z' IN (c1, c2)) as bc from <[D].[T]> t";
        SqlParseEventWalker extractor = runParsertest(query, parse(query));
        assertOutputs(extractor,
                "{SQL={select={1={column={name=id, table_ref=null}}, 2={parentheses={and={1={in={item={literal='x'}, in_list={list={1={column={name=a1, table_ref=null}}, 2={column={name=a2, table_ref=null}}}}}}, 2={in={item={literal='y'}, in_list={list={1={column={name=b1, table_ref=null}}, 2={column={name=b2, table_ref=null}}}}}}}}, alias=ab}, 3={column={name=name, table_ref=null}}, 4={parentheses={and={1={in={item={literal='y'}, in_list={list={1={column={name=b1, table_ref=null}}, 2={column={name=b2, table_ref=null}}}}}}, 2={in={item={literal='z'}, in_list={list={1={column={name=c1, table_ref=null}}, 2={column={name=c2, table_ref=null}}}}}}}}, alias=bc}}, from={table={alias=t, substitution={name=<[D].[T]>, parts={1=[D], 2=[T]}, type=tuple}}}}}",
                "[ab, bc, name, id]",
                "{<[D].[T]>=tuple}",
                "{<[D].[T]>={a1=[[@7,20:21='a1',<459>,1:20]], b2=[[@17,44:45='b2',<459>,1:44], [@31,75:76='b2',<459>,1:75]], a2=[[@9,24:25='a2',<459>,1:24]], name=[[@23,56:59='name',<225>,1:56]], id=[[@1,7:8='id',<459>,1:7]], c1=[[@37,91:92='c1',<459>,1:91]], b1=[[@15,40:41='b1',<459>,1:40], [@29,71:72='b1',<459>,1:71]], c2=[[@39,95:96='c2',<459>,1:95]]}}",
                "{query0={name=[[@23,56:59='name',<225>,1:56]], ab=[[@21,52:53='ab',<459>,1:52]], bc=[[@43,103:104='bc',<459>,1:103]], id=[[@1,7:8='id',<459>,1:7]]}}",
                "{def_query0={query_dictionary={ab=[[@21,52:53='ab',<459>,1:52]], bc=[[@43,103:104='bc',<459>,1:103]], name=[[@23,56:59='name',<225>,1:56]], id=[[@1,7:8='id',<459>,1:7]]}, table_dictionary={<[D].[T]>={a1=[[@7,20:21='a1',<459>,1:20]], b2=[[@17,44:45='b2',<459>,1:44], [@31,75:76='b2',<459>,1:75]], a2=[[@9,24:25='a2',<459>,1:24]], name=[[@23,56:59='name',<225>,1:56]], id=[[@1,7:8='id',<459>,1:7]], c1=[[@37,91:92='c1',<459>,1:91]], b1=[[@15,40:41='b1',<459>,1:40], [@29,71:72='b1',<459>,1:71]], c2=[[@39,95:96='c2',<459>,1:95]]}}, interface={ab=[{name=a1, table_ref=<[D].[T]>}, {name=a2, table_ref=<[D].[T]>}, {name=b1, table_ref=<[D].[T]>}, {name=b2, table_ref=<[D].[T]>}], bc=[{name=b1, table_ref=<[D].[T]>}, {name=b2, table_ref=<[D].[T]>}, {name=c1, table_ref=<[D].[T]>}, {name=c2, table_ref=<[D].[T]>}], name=[{name=name, table_ref=<[D].[T]>}], id=[{name=id, table_ref=<[D].[T]>}]}, table_alias={t=<[D].[T]>}}}");
    }

    @Test
    public void joinAndTest() {
        String query = "select l.id from <[D].[L]> l join <[D].[R]> r on ('x' IN (l.a1, l.a2) and 'y' IN (r.b1, r.b2))";
        SqlParseEventWalker extractor = runParsertest(query, parse(query));
        assertOutputs(extractor,
                "{SQL={select={1={column={name=id, table_ref=l}}}, from={join={1={table={alias=l, substitution={name=<[D].[L]>, parts={1=[D], 2=[L]}, type=tuple}}}, 2={join=join, on={and={1={in={item={literal='x'}, in_list={list={1={column={name=a1, table_ref=l}}, 2={column={name=a2, table_ref=l}}}}}}, 2={in={item={literal='y'}, in_list={list={1={column={name=b1, table_ref=r}}, 2={column={name=b2, table_ref=r}}}}}}}}}, 3={table={alias=r, substitution={name=<[D].[R]>, parts={1=[D], 2=[R]}, type=tuple}}}}}}}",
                "[id]",
                "{<[D].[L]>=tuple, <[D].[R]>=tuple}",
                "{<[D].[L]>={a1=[[@15,58:58='l',<459>,1:58]], a2=[[@19,64:64='l',<459>,1:64]], id=[[@1,7:7='l',<459>,1:7]]}, <[D].[R]>={b2=[[@31,88:88='r',<459>,1:88]], b1=[[@27,82:82='r',<459>,1:82]]}}",
                "{query0={id=[[@3,9:10='id',<459>,1:9]]}}",
                "{def_query0={query_dictionary={id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={<[D].[L]>={a1=[[@15,58:58='l',<459>,1:58]], a2=[[@19,64:64='l',<459>,1:64]], id=[[@1,7:7='l',<459>,1:7]]}, <[D].[R]>={b2=[[@31,88:88='r',<459>,1:88]], b1=[[@27,82:82='r',<459>,1:82]]}}, filters=[{name=a1, table_ref=l}, {name=a2, table_ref=l}, {name=b1, table_ref=r}, {name=b2, table_ref=r}], interface={id=[{name=id, table_ref=l}]}, table_alias={r=<[D].[R]>, l=<[D].[L]>}}}");
    }

    @Test
    public void joinOrTest() {
        String query = "select l.id from <[D].[L]> l join <[D].[R]> r on ('x' IN (l.a1, l.a2) or 'y' IN (r.b1, r.b2))";
        SqlParseEventWalker extractor = runParsertest(query, parse(query));
        assertOutputs(extractor,
                "{SQL={select={1={column={name=id, table_ref=l}}}, from={join={1={table={alias=l, substitution={name=<[D].[L]>, parts={1=[D], 2=[L]}, type=tuple}}}, 2={join=join, on={or={1={in={item={literal='x'}, in_list={list={1={column={name=a1, table_ref=l}}, 2={column={name=a2, table_ref=l}}}}}}, 2={in={item={literal='y'}, in_list={list={1={column={name=b1, table_ref=r}}, 2={column={name=b2, table_ref=r}}}}}}}}}, 3={table={alias=r, substitution={name=<[D].[R]>, parts={1=[D], 2=[R]}, type=tuple}}}}}}}",
                "[id]",
                "{<[D].[L]>=tuple, <[D].[R]>=tuple}",
                "{<[D].[L]>={a1=[[@15,58:58='l',<459>,1:58]], a2=[[@19,64:64='l',<459>,1:64]], id=[[@1,7:7='l',<459>,1:7]]}, <[D].[R]>={b2=[[@31,87:87='r',<459>,1:87]], b1=[[@27,81:81='r',<459>,1:81]]}}",
                "{query0={id=[[@3,9:10='id',<459>,1:9]]}}",
                "{def_query0={query_dictionary={id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={<[D].[L]>={a1=[[@15,58:58='l',<459>,1:58]], a2=[[@19,64:64='l',<459>,1:64]], id=[[@1,7:7='l',<459>,1:7]]}, <[D].[R]>={b2=[[@31,87:87='r',<459>,1:87]], b1=[[@27,81:81='r',<459>,1:81]]}}, filters=[{name=a1, table_ref=l}, {name=a2, table_ref=l}, {name=b1, table_ref=r}, {name=b2, table_ref=r}], interface={id=[{name=id, table_ref=l}]}, table_alias={r=<[D].[R]>, l=<[D].[L]>}}}");
    }

    @Test
    public void whereControlTest() {
        String query = "select id from <[D].[T]> t where ('x' IN (a1, a2) and 'y' IN (b1, b2))";
        SqlParseEventWalker extractor = runParsertest(query, parse(query));
        assertOutputs(extractor,
                "{SQL={select={1={column={name=id, table_ref=null}}}, from={table={alias=t, substitution={name=<[D].[T]>, parts={1=[D], 2=[T]}, type=tuple}}}, where={parentheses={and={1={in={item={literal='x'}, in_list={list={1={column={name=a1, table_ref=null}}, 2={column={name=a2, table_ref=null}}}}}}, 2={in={item={literal='y'}, in_list={list={1={column={name=b1, table_ref=null}}, 2={column={name=b2, table_ref=null}}}}}}}}}}}",
                "[id]",
                "{<[D].[T]>=tuple}",
                "{<[D].[T]>={a1=[[@10,42:43='a1',<459>,1:42]], b2=[[@20,66:67='b2',<459>,1:66]], a2=[[@12,46:47='a2',<459>,1:46]], id=[[@1,7:8='id',<459>,1:7]], b1=[[@18,62:63='b1',<459>,1:62]]}}",
                "{query0={id=[[@1,7:8='id',<459>,1:7]]}}",
                "{def_query0={query_dictionary={id=[[@1,7:8='id',<459>,1:7]]}, table_dictionary={<[D].[T]>={a1=[[@10,42:43='a1',<459>,1:42]], b2=[[@20,66:67='b2',<459>,1:66]], a2=[[@12,46:47='a2',<459>,1:46]], id=[[@1,7:8='id',<459>,1:7]], b1=[[@18,62:63='b1',<459>,1:62]]}}, filters=[{name=a1, table_ref=<[D].[T]>}, {name=a2, table_ref=<[D].[T]>}, {name=b1, table_ref=<[D].[T]>}, {name=b2, table_ref=<[D].[T]>}], interface={id=[{name=id, table_ref=<[D].[T]>}]}, table_alias={t=<[D].[T]>}}}");
    }

    @Test
    public void repeatedWhenTest() {
        String query = "select src_donor_id, case when '' IN (end_date, end_date_1) and 'Future Donor' IN (constituent_code, constituent_code_1) then 'Future Donor' when '' IN (end_date, end_date_1) and 'Student' IN (constituent_code, constituent_code_1) then 'Student' else 'Other' end as donor_type from donor_source";
        SqlParseEventWalker extractor = runParsertest(query, parse(query));
        assertOutputs(extractor,
                "{SQL={select={1={column={name=src_donor_id, table_ref=null}}, 2={alias=donor_type, case={clauses={1={then={literal='Future Donor'}, when={and={1={in={item={literal=''}, in_list={list={1={column={name=end_date, table_ref=null}}, 2={column={name=end_date_1, table_ref=null}}}}}}, 2={in={item={literal='Future Donor'}, in_list={list={1={column={name=constituent_code, table_ref=null}}, 2={column={name=constituent_code_1, table_ref=null}}}}}}}}}, 2={then={literal='Student'}, when={and={1={in={item={literal=''}, in_list={list={1={column={name=end_date, table_ref=null}}, 2={column={name=end_date_1, table_ref=null}}}}}}, 2={in={item={literal='Student'}, in_list={list={1={column={name=constituent_code, table_ref=null}}, 2={column={name=constituent_code_1, table_ref=null}}}}}}}}}}, else={literal='Other'}}}}, from={table={alias=null, table=donor_source}}}}",
                "[donor_type, src_donor_id]",
                "{}",
                "{donor_source={end_date=[[@8,38:45='end_date',<459>,1:38], [@26,153:160='end_date',<459>,1:153]], constituent_code=[[@16,83:98='constituent_code',<459>,1:83], [@34,193:208='constituent_code',<459>,1:193]], end_date_1=[[@10,48:57='end_date_1',<459>,1:48], [@28,163:172='end_date_1',<459>,1:163]], constituent_code_1=[[@18,101:118='constituent_code_1',<459>,1:101], [@36,211:228='constituent_code_1',<459>,1:211]], src_donor_id=[[@1,7:18='src_donor_id',<459>,1:7]]}}",
                "{query0={donor_type=[[@44,266:275='donor_type',<459>,1:266]], src_donor_id=[[@1,7:18='src_donor_id',<459>,1:7]]}}",
                "{def_query0={query_dictionary={donor_type=[[@44,266:275='donor_type',<459>,1:266]], src_donor_id=[[@1,7:18='src_donor_id',<459>,1:7]]}, table_dictionary={donor_source={end_date=[[@8,38:45='end_date',<459>,1:38], [@26,153:160='end_date',<459>,1:153]], constituent_code=[[@16,83:98='constituent_code',<459>,1:83], [@34,193:208='constituent_code',<459>,1:193]], end_date_1=[[@10,48:57='end_date_1',<459>,1:48], [@28,163:172='end_date_1',<459>,1:163]], constituent_code_1=[[@18,101:118='constituent_code_1',<459>,1:101], [@36,211:228='constituent_code_1',<459>,1:211]], src_donor_id=[[@1,7:18='src_donor_id',<459>,1:7]]}}, interface={donor_type=[{name=end_date, table_ref=donor_source}, {name=end_date_1, table_ref=donor_source}, {name=constituent_code, table_ref=donor_source}, {name=constituent_code_1, table_ref=donor_source}], src_donor_id=[{name=src_donor_id, table_ref=donor_source}]}}}");
    }

    @Test
    public void overlappingNamesTest() {
        String query = "select id, case when 'x' IN (a1, a2) and 'y' IN (a1, b1) then 1 else 0 end as flag from <[D].[T]> t";
        SqlParseEventWalker extractor = runParsertest(query, parse(query));
        assertOutputs(extractor,
                "{SQL={select={1={column={name=id, table_ref=null}}, 2={alias=flag, case={clauses={1={then={literal=1}, when={and={1={in={item={literal='x'}, in_list={list={1={column={name=a1, table_ref=null}}, 2={column={name=a2, table_ref=null}}}}}}, 2={in={item={literal='y'}, in_list={list={1={column={name=a1, table_ref=null}}, 2={column={name=b1, table_ref=null}}}}}}}}}}, else={literal=0}}}}, from={table={alias=t, substitution={name=<[D].[T]>, parts={1=[D], 2=[T]}, type=tuple}}}}}",
                "[flag, id]",
                "{<[D].[T]>=tuple}",
                "{<[D].[T]>={a1=[[@8,29:30='a1',<459>,1:29], [@16,49:50='a1',<459>,1:49]], a2=[[@10,33:34='a2',<459>,1:33]], id=[[@1,7:8='id',<459>,1:7]], b1=[[@18,53:54='b1',<459>,1:53]]}}",
                "{query0={flag=[[@26,78:81='flag',<459>,1:78]], id=[[@1,7:8='id',<459>,1:7]]}}",
                "{def_query0={query_dictionary={flag=[[@26,78:81='flag',<459>,1:78]], id=[[@1,7:8='id',<459>,1:7]]}, table_dictionary={<[D].[T]>={a1=[[@8,29:30='a1',<459>,1:29], [@16,49:50='a1',<459>,1:49]], a2=[[@10,33:34='a2',<459>,1:33]], id=[[@1,7:8='id',<459>,1:7]], b1=[[@18,53:54='b1',<459>,1:53]]}}, interface={flag=[{name=a1, table_ref=<[D].[T]>}, {name=a2, table_ref=<[D].[T]>}, {name=b1, table_ref=<[D].[T]>}], id=[{name=id, table_ref=<[D].[T]>}]}, table_alias={t=<[D].[T]>}}}");
    }

    @Test
    public void qualifiedCaseTest() {
        String query = "select t.id, case when 'x' IN (t.a1, t.a2) and 'y' IN (t.b1, t.b2) then 1 else 0 end as flag from <[D].[T]> t";
        SqlParseEventWalker extractor = runParsertest(query, parse(query));
        assertOutputs(extractor,
                "{SQL={select={1={column={name=id, table_ref=t}}, 2={alias=flag, case={clauses={1={then={literal=1}, when={and={1={in={item={literal='x'}, in_list={list={1={column={name=a1, table_ref=t}}, 2={column={name=a2, table_ref=t}}}}}}, 2={in={item={literal='y'}, in_list={list={1={column={name=b1, table_ref=t}}, 2={column={name=b2, table_ref=t}}}}}}}}}}, else={literal=0}}}}, from={table={alias=t, substitution={name=<[D].[T]>, parts={1=[D], 2=[T]}, type=tuple}}}}}",
                "[flag, id]",
                "{<[D].[T]>=tuple}",
                "{<[D].[T]>={a1=[[@10,31:31='t',<459>,1:31]], b2=[[@26,61:61='t',<459>,1:61]], a2=[[@14,37:37='t',<459>,1:37]], id=[[@1,7:7='t',<459>,1:7]], b1=[[@22,55:55='t',<459>,1:55]]}}",
                "{query0={flag=[[@36,88:91='flag',<459>,1:88]], id=[[@3,9:10='id',<459>,1:9]]}}",
                "{def_query0={query_dictionary={flag=[[@36,88:91='flag',<459>,1:88]], id=[[@3,9:10='id',<459>,1:9]]}, table_dictionary={<[D].[T]>={a1=[[@10,31:31='t',<459>,1:31]], b2=[[@26,61:61='t',<459>,1:61]], a2=[[@14,37:37='t',<459>,1:37]], id=[[@1,7:7='t',<459>,1:7]], b1=[[@22,55:55='t',<459>,1:55]]}}, interface={flag=[{name=a1, table_ref=t}, {name=a2, table_ref=t}, {name=b1, table_ref=t}, {name=b2, table_ref=t}], id=[{name=id, table_ref=t}]}, table_alias={t=<[D].[T]>}}}");
    }

    @Test
    public void threeListsTest() {
        String query = "select id, case when ('x' IN (a1, a2) and ('y' IN (b1, b2) or 'z' IN (c1, c2))) then 1 else 0 end as flag from <[D].[T]> t";
        SqlParseEventWalker extractor = runParsertest(query, parse(query));
        assertOutputs(extractor,
                "{SQL={select={1={column={name=id, table_ref=null}}, 2={alias=flag, case={clauses={1={then={literal=1}, when={parentheses={and={1={in={item={literal='x'}, in_list={list={1={column={name=a1, table_ref=null}}, 2={column={name=a2, table_ref=null}}}}}}, 2={parentheses={or={1={in={item={literal='y'}, in_list={list={1={column={name=b1, table_ref=null}}, 2={column={name=b2, table_ref=null}}}}}}, 2={in={item={literal='z'}, in_list={list={1={column={name=c1, table_ref=null}}, 2={column={name=c2, table_ref=null}}}}}}}}}}}}}}, else={literal=0}}}}, from={table={alias=t, substitution={name=<[D].[T]>, parts={1=[D], 2=[T]}, type=tuple}}}}}",
                "[flag, id]",
                "{<[D].[T]>=tuple}",
                "{<[D].[T]>={a1=[[@9,30:31='a1',<459>,1:30]], b2=[[@20,55:56='b2',<459>,1:55]], a2=[[@11,34:35='a2',<459>,1:34]], id=[[@1,7:8='id',<459>,1:7]], c1=[[@26,70:71='c1',<459>,1:70]], b1=[[@18,51:52='b1',<459>,1:51]], c2=[[@28,74:75='c2',<459>,1:74]]}}",
                "{query0={flag=[[@38,101:104='flag',<459>,1:101]], id=[[@1,7:8='id',<459>,1:7]]}}",
                "{def_query0={query_dictionary={flag=[[@38,101:104='flag',<459>,1:101]], id=[[@1,7:8='id',<459>,1:7]]}, table_dictionary={<[D].[T]>={a1=[[@9,30:31='a1',<459>,1:30]], b2=[[@20,55:56='b2',<459>,1:55]], a2=[[@11,34:35='a2',<459>,1:34]], id=[[@1,7:8='id',<459>,1:7]], c1=[[@26,70:71='c1',<459>,1:70]], b1=[[@18,51:52='b1',<459>,1:51]], c2=[[@28,74:75='c2',<459>,1:74]]}}, interface={flag=[{name=a1, table_ref=<[D].[T]>}, {name=a2, table_ref=<[D].[T]>}, {name=b1, table_ref=<[D].[T]>}, {name=b2, table_ref=<[D].[T]>}, {name=c1, table_ref=<[D].[T]>}, {name=c2, table_ref=<[D].[T]>}], id=[{name=id, table_ref=<[D].[T]>}]}, table_alias={t=<[D].[T]>}}}");
    }

    @Test
    public void notInTest() {
        String query = "select id, case when 'x' NOT IN (a1, a2) and 'y' NOT IN (b1, b2) then 1 else 0 end as flag from <[D].[T]> t";
        SqlParseEventWalker extractor = runParsertest(query, parse(query));
        assertOutputs(extractor,
                "{SQL={select={1={column={name=id, table_ref=null}}, 2={alias=flag, case={clauses={1={then={literal=1}, when={and={1={in={item={literal='x'}, not_in_list={list={1={column={name=a1, table_ref=null}}, 2={column={name=a2, table_ref=null}}}}}}, 2={in={item={literal='y'}, not_in_list={list={1={column={name=b1, table_ref=null}}, 2={column={name=b2, table_ref=null}}}}}}}}}}, else={literal=0}}}}, from={table={alias=t, substitution={name=<[D].[T]>, parts={1=[D], 2=[T]}, type=tuple}}}}}",
                "[flag, id]",
                "{<[D].[T]>=tuple}",
                "{<[D].[T]>={a1=[[@9,33:34='a1',<459>,1:33]], b2=[[@20,61:62='b2',<459>,1:61]], a2=[[@11,37:38='a2',<459>,1:37]], id=[[@1,7:8='id',<459>,1:7]], b1=[[@18,57:58='b1',<459>,1:57]]}}",
                "{query0={flag=[[@28,86:89='flag',<459>,1:86]], id=[[@1,7:8='id',<459>,1:7]]}}",
                "{def_query0={query_dictionary={flag=[[@28,86:89='flag',<459>,1:86]], id=[[@1,7:8='id',<459>,1:7]]}, table_dictionary={<[D].[T]>={a1=[[@9,33:34='a1',<459>,1:33]], b2=[[@20,61:62='b2',<459>,1:61]], a2=[[@11,37:38='a2',<459>,1:37]], id=[[@1,7:8='id',<459>,1:7]], b1=[[@18,57:58='b1',<459>,1:57]]}}, interface={flag=[{name=a1, table_ref=<[D].[T]>}, {name=a2, table_ref=<[D].[T]>}, {name=b1, table_ref=<[D].[T]>}, {name=b2, table_ref=<[D].[T]>}], id=[{name=id, table_ref=<[D].[T]>}]}, table_alias={t=<[D].[T]>}}}");
    }

    @Test
    public void fullFamiliesTest() {
        String query = "select src_donor_id, case when '' IN (end_date,end_date_1,end_date_2,end_date_3,end_date_4,end_date_5,end_date_6,end_date_7,end_date_8,end_date_9) and 'Future Donor' IN (constituent_code,constituent_code_1,constituent_code_2,constituent_code_3,constituent_code_4,constituent_code_5,constituent_code_6,constituent_code_7,constituent_code_8,constituent_code_9) then 'Future Donor' when '' IN (end_date,end_date_1,end_date_2,end_date_3,end_date_4,end_date_5,end_date_6,end_date_7,end_date_8,end_date_9) and 'Student' IN (constituent_code,constituent_code_1,constituent_code_2,constituent_code_3,constituent_code_4,constituent_code_5,constituent_code_6,constituent_code_7,constituent_code_8,constituent_code_9) then 'Student' else 'Other' end as donor_type from donor_source";
        SqlParseEventWalker extractor = runParsertest(query, parse(query));
        assertOutputs(extractor,
                "{SQL={select={1={column={name=src_donor_id, table_ref=null}}, 2={alias=donor_type, case={clauses={1={then={literal='Future Donor'}, when={and={1={in={item={literal=''}, in_list={list={1={column={name=end_date, table_ref=null}}, 2={column={name=end_date_1, table_ref=null}}, 3={column={name=end_date_2, table_ref=null}}, 4={column={name=end_date_3, table_ref=null}}, 5={column={name=end_date_4, table_ref=null}}, 6={column={name=end_date_5, table_ref=null}}, 7={column={name=end_date_6, table_ref=null}}, 8={column={name=end_date_7, table_ref=null}}, 9={column={name=end_date_8, table_ref=null}}, 10={column={name=end_date_9, table_ref=null}}}}}}, 2={in={item={literal='Future Donor'}, in_list={list={1={column={name=constituent_code, table_ref=null}}, 2={column={name=constituent_code_1, table_ref=null}}, 3={column={name=constituent_code_2, table_ref=null}}, 4={column={name=constituent_code_3, table_ref=null}}, 5={column={name=constituent_code_4, table_ref=null}}, 6={column={name=constituent_code_5, table_ref=null}}, 7={column={name=constituent_code_6, table_ref=null}}, 8={column={name=constituent_code_7, table_ref=null}}, 9={column={name=constituent_code_8, table_ref=null}}, 10={column={name=constituent_code_9, table_ref=null}}}}}}}}}, 2={then={literal='Student'}, when={and={1={in={item={literal=''}, in_list={list={1={column={name=end_date, table_ref=null}}, 2={column={name=end_date_1, table_ref=null}}, 3={column={name=end_date_2, table_ref=null}}, 4={column={name=end_date_3, table_ref=null}}, 5={column={name=end_date_4, table_ref=null}}, 6={column={name=end_date_5, table_ref=null}}, 7={column={name=end_date_6, table_ref=null}}, 8={column={name=end_date_7, table_ref=null}}, 9={column={name=end_date_8, table_ref=null}}, 10={column={name=end_date_9, table_ref=null}}}}}}, 2={in={item={literal='Student'}, in_list={list={1={column={name=constituent_code, table_ref=null}}, 2={column={name=constituent_code_1, table_ref=null}}, 3={column={name=constituent_code_2, table_ref=null}}, 4={column={name=constituent_code_3, table_ref=null}}, 5={column={name=constituent_code_4, table_ref=null}}, 6={column={name=constituent_code_5, table_ref=null}}, 7={column={name=constituent_code_6, table_ref=null}}, 8={column={name=constituent_code_7, table_ref=null}}, 9={column={name=constituent_code_8, table_ref=null}}, 10={column={name=constituent_code_9, table_ref=null}}}}}}}}}}, else={literal='Other'}}}}, from={table={alias=null, table=donor_source}}}}",
                "[donor_type, src_donor_id]",
                "{}",
                "{donor_source={end_date=[[@8,38:45='end_date',<459>,1:38], [@58,391:398='end_date',<459>,1:391]], constituent_code_9=[[@50,339:356='constituent_code_9',<459>,1:339], [@100,687:704='constituent_code_9',<459>,1:687]], constituent_code=[[@32,170:185='constituent_code',<459>,1:170], [@82,518:533='constituent_code',<459>,1:518]], constituent_code_7=[[@46,301:318='constituent_code_7',<459>,1:301], [@96,649:666='constituent_code_7',<459>,1:649]], constituent_code_8=[[@48,320:337='constituent_code_8',<459>,1:320], [@98,668:685='constituent_code_8',<459>,1:668]], src_donor_id=[[@1,7:18='src_donor_id',<459>,1:7]], end_date_5=[[@18,91:100='end_date_5',<459>,1:91], [@68,444:453='end_date_5',<459>,1:444]], end_date_6=[[@20,102:111='end_date_6',<459>,1:102], [@70,455:464='end_date_6',<459>,1:455]], end_date_7=[[@22,113:122='end_date_7',<459>,1:113], [@72,466:475='end_date_7',<459>,1:466]], end_date_8=[[@24,124:133='end_date_8',<459>,1:124], [@74,477:486='end_date_8',<459>,1:477]], end_date_9=[[@26,135:144='end_date_9',<459>,1:135], [@76,488:497='end_date_9',<459>,1:488]], constituent_code_5=[[@42,263:280='constituent_code_5',<459>,1:263], [@92,611:628='constituent_code_5',<459>,1:611]], constituent_code_6=[[@44,282:299='constituent_code_6',<459>,1:282], [@94,630:647='constituent_code_6',<459>,1:630]], constituent_code_3=[[@38,225:242='constituent_code_3',<459>,1:225], [@88,573:590='constituent_code_3',<459>,1:573]], constituent_code_4=[[@40,244:261='constituent_code_4',<459>,1:244], [@90,592:609='constituent_code_4',<459>,1:592]], end_date_1=[[@10,47:56='end_date_1',<459>,1:47], [@60,400:409='end_date_1',<459>,1:400]], constituent_code_1=[[@34,187:204='constituent_code_1',<459>,1:187], [@84,535:552='constituent_code_1',<459>,1:535]], end_date_2=[[@12,58:67='end_date_2',<459>,1:58], [@62,411:420='end_date_2',<459>,1:411]], constituent_code_2=[[@36,206:223='constituent_code_2',<459>,1:206], [@86,554:571='constituent_code_2',<459>,1:554]], end_date_3=[[@14,69:78='end_date_3',<459>,1:69], [@64,422:431='end_date_3',<459>,1:422]], end_date_4=[[@16,80:89='end_date_4',<459>,1:80], [@66,433:442='end_date_4',<459>,1:433]]}}",
                "{query0={donor_type=[[@108,742:751='donor_type',<459>,1:742]], src_donor_id=[[@1,7:18='src_donor_id',<459>,1:7]]}}",
                "{def_query0={query_dictionary={donor_type=[[@108,742:751='donor_type',<459>,1:742]], src_donor_id=[[@1,7:18='src_donor_id',<459>,1:7]]}, table_dictionary={donor_source={end_date=[[@8,38:45='end_date',<459>,1:38], [@58,391:398='end_date',<459>,1:391]], constituent_code_9=[[@50,339:356='constituent_code_9',<459>,1:339], [@100,687:704='constituent_code_9',<459>,1:687]], constituent_code=[[@32,170:185='constituent_code',<459>,1:170], [@82,518:533='constituent_code',<459>,1:518]], constituent_code_7=[[@46,301:318='constituent_code_7',<459>,1:301], [@96,649:666='constituent_code_7',<459>,1:649]], constituent_code_8=[[@48,320:337='constituent_code_8',<459>,1:320], [@98,668:685='constituent_code_8',<459>,1:668]], src_donor_id=[[@1,7:18='src_donor_id',<459>,1:7]], end_date_5=[[@18,91:100='end_date_5',<459>,1:91], [@68,444:453='end_date_5',<459>,1:444]], end_date_6=[[@20,102:111='end_date_6',<459>,1:102], [@70,455:464='end_date_6',<459>,1:455]], end_date_7=[[@22,113:122='end_date_7',<459>,1:113], [@72,466:475='end_date_7',<459>,1:466]], end_date_8=[[@24,124:133='end_date_8',<459>,1:124], [@74,477:486='end_date_8',<459>,1:477]], end_date_9=[[@26,135:144='end_date_9',<459>,1:135], [@76,488:497='end_date_9',<459>,1:488]], constituent_code_5=[[@42,263:280='constituent_code_5',<459>,1:263], [@92,611:628='constituent_code_5',<459>,1:611]], constituent_code_6=[[@44,282:299='constituent_code_6',<459>,1:282], [@94,630:647='constituent_code_6',<459>,1:630]], constituent_code_3=[[@38,225:242='constituent_code_3',<459>,1:225], [@88,573:590='constituent_code_3',<459>,1:573]], constituent_code_4=[[@40,244:261='constituent_code_4',<459>,1:244], [@90,592:609='constituent_code_4',<459>,1:592]], end_date_1=[[@10,47:56='end_date_1',<459>,1:47], [@60,400:409='end_date_1',<459>,1:400]], constituent_code_1=[[@34,187:204='constituent_code_1',<459>,1:187], [@84,535:552='constituent_code_1',<459>,1:535]], end_date_2=[[@12,58:67='end_date_2',<459>,1:58], [@62,411:420='end_date_2',<459>,1:411]], constituent_code_2=[[@36,206:223='constituent_code_2',<459>,1:206], [@86,554:571='constituent_code_2',<459>,1:554]], end_date_3=[[@14,69:78='end_date_3',<459>,1:69], [@64,422:431='end_date_3',<459>,1:422]], end_date_4=[[@16,80:89='end_date_4',<459>,1:80], [@66,433:442='end_date_4',<459>,1:433]]}}, interface={donor_type=[{name=end_date, table_ref=donor_source}, {name=end_date_1, table_ref=donor_source}, {name=end_date_2, table_ref=donor_source}, {name=end_date_3, table_ref=donor_source}, {name=end_date_4, table_ref=donor_source}, {name=end_date_5, table_ref=donor_source}, {name=end_date_6, table_ref=donor_source}, {name=end_date_7, table_ref=donor_source}, {name=end_date_8, table_ref=donor_source}, {name=end_date_9, table_ref=donor_source}, {name=constituent_code, table_ref=donor_source}, {name=constituent_code_1, table_ref=donor_source}, {name=constituent_code_2, table_ref=donor_source}, {name=constituent_code_3, table_ref=donor_source}, {name=constituent_code_4, table_ref=donor_source}, {name=constituent_code_5, table_ref=donor_source}, {name=constituent_code_6, table_ref=donor_source}, {name=constituent_code_7, table_ref=donor_source}, {name=constituent_code_8, table_ref=donor_source}, {name=constituent_code_9, table_ref=donor_source}], src_donor_id=[{name=src_donor_id, table_ref=donor_source}]}}}");
        SqlParseEventWalker queryExtractor = runQueryParsertest(query, parse(query));
        assertNoWalkerDiagnostics(queryExtractor, Set.of());
        Assert.assertEquals("All QUERY diagnostics", 0,
                queryExtractor.getSnippet().getParserDiagnosticList().size());
        Assert.assertEquals(extractor.getAsTree().toString().replace("{SQL=", "{QUERY="),
                queryExtractor.getAsTree().toString());
        Assert.assertEquals(extractor.getInterface().toString(), queryExtractor.getInterface().toString());
        Assert.assertEquals(extractor.getSubstitutionsMap().toString(), queryExtractor.getSubstitutionsMap().toString());
        Assert.assertEquals(extractor.getTableColumnDictionaryMap().toString(), queryExtractor.getTableColumnDictionaryMap().toString());
        Assert.assertEquals(extractor.getQueryColumnDictionaryMap().toString(), queryExtractor.getQueryColumnDictionaryMap().toString());
        Assert.assertEquals(extractor.getSymbolTable().toString(), queryExtractor.getSymbolTable().toString());
    }

}
