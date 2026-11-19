package sql.walker;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.Trees;
import org.junit.Assert;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import errorhandling.ParseDiagnostic;
import errorhandling.ParseErrorCollector;
import sql.SQLSelectParserParser;

public class Capture extends AbstractSqlParseEventWalkerTest {
    public static void main(String[] args) throws Exception {
        Gson gson = new GsonBuilder().serializeNulls().disableHtmlEscaping().setPrettyPrinting().create();
        Capture runner = new Capture();
        JsonArray results = new JsonArray();
        for (var entry : JsonParser.parseString(Files.readString(Path.of(args[0]))).getAsJsonArray()) {
            JsonObject input = entry.getAsJsonObject();
            String name = input.get("name").getAsString();
            String query = input.get("query").getAsString();
            String mode = input.get("runner").getAsString();
            SQLSelectParserParser parser = parse(query);
            ParserRuleContext tree = switch (mode) {
                case "script" -> parser.script();
                case "ddl" -> parser.ddl();
                case "snippet" -> switch (input.get("entry").getAsString()) {
                    case "predicand_value" -> parser.predicand_value();
                    case "condition_value" -> parser.condition_value();
                    case "query_value" -> parser.query_value();
                    case "tuple_value" -> parser.tuple_value();
                    case "in_list_predicate_value" -> parser.in_list_predicate_value();
                    case "join_extension_value" -> parser.join_extension_value();
                    case "values_statement_end" -> parser.values_statement_end();
                    case "insert_end_point" -> parser.insert_end_point();
                    case "update_end_point" -> parser.update_end_point();
                    case "delete_end_point" -> parser.delete_end_point();
                    case "ddl" -> parser.ddl();
                    case "script" -> parser.script();
                    default -> throw new IllegalArgumentException("Unsupported snippet entry: " + input.get("entry"));
                };
                default -> parser.sql();
            };
            ParseErrorCollector collector = (ParseErrorCollector) parser.getErrorHandler();
            JsonObject result = new JsonObject();
            result.addProperty("name", name);
            result.addProperty("errors", collector.getErrorCount());
            List<ParseDiagnostic> diagnostics;
            if (mode.equals("syntax")) {
                Assert.assertTrue(name + ": expected malformed syntax", collector.getErrorCount() > 0);
                diagnostics = new ArrayList<>(collector.getDiagnostics());
                diagnostics.addAll(runner.collectParserListenerDiagnostics(parser));
            } else {
                Assert.assertEquals(name + ": parser errors", 0, collector.getErrorCount());
                Assert.assertEquals(name + ": syntax errors", 0, parser.getNumberOfSyntaxErrors());
                int rule = Arrays.asList(SQLSelectParserParser.ruleNames).indexOf(input.get("rule").getAsString());
                Assert.assertTrue(name + ": unknown rule", rule >= 0);
                Assert.assertEquals(name + ": dedicated routes", input.get("routes").getAsInt(),
                        Trees.findAllRuleNodes(tree, rule).size());
                if (input.has("route_checks")) {
                    for (JsonElement check : input.getAsJsonArray("route_checks")) {
                        JsonObject expected = check.getAsJsonObject();
                        int checkedRule = Arrays.asList(SQLSelectParserParser.ruleNames)
                                .indexOf(expected.get("rule").getAsString());
                        Assert.assertTrue(name + ": unknown checked rule", checkedRule >= 0);
                        Assert.assertEquals(name + ": " + expected.get("rule").getAsString(),
                                expected.get("count").getAsInt(), Trees.findAllRuleNodes(tree, checkedRule).size());
                    }
                }
                SqlParseEventWalker walker = runner.runAnyParsertest(query, parser, tree, true);
                diagnostics = walker.getSnippet().getParserDiagnosticList();
                if (!input.get("legacy").getAsBoolean()) {
                    result.add("outputs", gson.toJsonTree(List.of(walker.getAsTree().toString(),
                            walker.getInterface().toString(), walker.getSubstitutionsMap().toString(),
                            walker.getTableColumnDictionaryMap().toString(),
                            walker.getQueryColumnDictionaryMap().toString(), walker.getSymbolTable().toString())));
                }
                if (mode.equals("snippet")) {
                    access.SqlParserAccess accessParser = new access.SqlParserAccess(false, false, false);
                    accessParser.executeTheParse(query, input.get("endpoint").getAsString());
                    Assert.assertEquals(name + ": access syntax errors", 0, accessParser.getParser().getNumberOfSyntaxErrors());
                    Assert.assertEquals(name + ": access diagnostics", diagnostics, accessParser.getAllDiagnostics());
                    access.Snippet snippet = accessParser.getSnippet();
                    result.add("access_outputs", gson.toJsonTree(List.of(snippet.getSqlAbstractTree().toString(),
                            snippet.getQueryInterface().toString(), snippet.getSubstitutionsMap().toString(),
                            snippet.getTableDictionary().toString(), snippet.getQueryColumnDictionaryMap().toString(),
                            snippet.getSymbolTable().toString())));
                }
            }
            result.add("diagnostics", gson.toJsonTree(diagnostics));
            results.add(result);
        }
        Files.writeString(Path.of(args[1]), gson.toJson(results));
        System.out.println("Captured " + results.size() + " cases; see captured.json for new goldens and diagnostics.");
    }
}
