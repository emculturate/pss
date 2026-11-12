package sql.walker;

import org.junit.Assert;
import org.junit.Test;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import access.Snippet;
import access.SqlParserAccess;

public class SqlEventWalkerSnowflakeDatetimeManipulationTests {

	private Snippet parseDateadd(String sql) {
		SqlParserAccess parser = new SqlParserAccess(false, false, false);
		parser.executeTheParse(sql, "SQL");
		Assert.assertEquals(0, parser.getParser().getNumberOfSyntaxErrors());
		Assert.assertEquals(parser.getFatalErrorList().toString(), 0, parser.getFatalErrorCount());
		Assert.assertFalse(parser.getSnippet().getSqlAbstractTree().toString().contains("Type="));
		return parser.getSnippet();
	}

	private JsonElement ast(Snippet snippet) {
		return JsonParser.parseString(snippet.getSqlAbstractTreeJson());
	}

	@Test
	public void dateaddHasNamedArgumentsAndPartMetadata() {
		Snippet snippet = parseDateadd(
				"select dateadd(year, 1, a.created_at) as shifted from events a");
		Assert.assertEquals(JsonParser.parseString("""
				{"SQL":{
				  "select":{"1":{"datetime_function":{
				    "invocation":"DATEADD",
				    "level":{"canonical":"YEAR","text":"year","form":"KEYWORD"},
				    "increment":{"literal":"1"},
				    "value":{"column":{"name":"created_at","table_ref":"a"}}
				  },"alias":"shifted"}},
				  "from":{"table":{"alias":"a","table":"events"}}
				}}
				"""), ast(snippet));
	}

	@Test
	public void dateaddCanonicalizesPartsWithoutLosingSpellingOrForm() {
		String[][] parts = {
				{"yEaR", "YEAR", "yEaR", "KEYWORD"},
				{"mm", "MONTH", "mm", "IDENTIFIER"},
				{"'mOn'", "MONTH", "mOn", "STRING"},
				{"dayofmonth", "DAY", "dayofmonth", "KEYWORD"},
				{"m", "MINUTE", "m", "IDENTIFIER"},
				{"milliseconds", "MILLISECOND", "milliseconds", "KEYWORD"},
				{"ns", "NANOSECOND", "ns", "IDENTIFIER"},
				{"'engine''part'", "ENGINE'PART", "engine'part", "STRING"}
		};
		for (String[] part : parts) {
			Snippet snippet = parseDateadd(
					"select DaTeAdD(" + part[0] + ", 1, a.ts) as shifted from events a");
			var function = ast(snippet).getAsJsonObject().getAsJsonObject("SQL")
					.getAsJsonObject("select").getAsJsonObject("1").getAsJsonObject("datetime_function");
			Assert.assertEquals("DATEADD", function.get("invocation").getAsString());
			var level = function.getAsJsonObject("level");
			Assert.assertEquals(part[0], part[1], level.get("canonical").getAsString());
			Assert.assertEquals(part[0], part[2], level.get("text").getAsString());
			Assert.assertEquals(part[0], part[3], level.get("form").getAsString());
		}
	}

	@Test
	public void dateaddRetainsNestedExpressionsAndARealYearColumn() {
		Snippet snippet = parseDateadd(
				"select coalesce(dateadd(year, a.year + 1, "
						+ "dateadd(day, 2, a.ts)), a.ts) as shifted from events a");
		Assert.assertEquals(JsonParser.parseString("""
				{"SQL":{
				  "select":{"1":{"function":{"function_name":"coalesce","parameters":{
				    "1":{"datetime_function":{
				      "invocation":"DATEADD",
				      "level":{"canonical":"YEAR","text":"year","form":"KEYWORD"},
				      "increment":{"calc":{"left":{"column":{"name":"year","table_ref":"a"}},
				        "operator":"+","right":{"literal":"1"}}},
				      "value":{"datetime_function":{
				        "invocation":"DATEADD",
				        "level":{"canonical":"DAY","text":"day","form":"KEYWORD"},
				        "increment":{"literal":"2"},
				        "value":{"column":{"name":"ts","table_ref":"a"}}
				      }}
				    }},
				    "2":{"column":{"name":"ts","table_ref":"a"}}
				  }},"alias":"shifted"}},
				  "from":{"table":{"alias":"a","table":"events"}}
				}}
				"""), ast(snippet));
	}

	@Test
	public void dateaddExpressionArgumentsRetainPredicandSubstitutions() {
		Snippet snippet = parseDateadd(
				"select dateadd(year, <increment>, <date value>) as shifted");
		Assert.assertEquals(JsonParser.parseString("""
				{"SQL":{"select":{"1":{"datetime_function":{
				  "invocation":"DATEADD",
				  "level":{"canonical":"YEAR","text":"year","form":"KEYWORD"},
				  "increment":{"substitution":{"name":"<increment>","type":"predicand"}},
				  "value":{"substitution":{"name":"<date value>","type":"predicand"}}
				},"alias":"shifted"}}}}
				"""), ast(snippet));
		Assert.assertEquals("predicand", snippet.getSubstitutionsMap().get("<increment>"));
		Assert.assertEquals("predicand", snippet.getSubstitutionsMap().get("<date value>"));
	}

	@Test
	public void qualifiedDateaddRemainsAGenericFunction() {
		Snippet snippet = parseDateadd(
				"select custom.dateadd(a.part, 1, a.ts) as shifted from events a");
		var item = ast(snippet).getAsJsonObject().getAsJsonObject("SQL")
				.getAsJsonObject("select").getAsJsonObject("1");
		Assert.assertTrue(item.has("function"));
		Assert.assertFalse(item.has("datetime_function"));
	}
}
