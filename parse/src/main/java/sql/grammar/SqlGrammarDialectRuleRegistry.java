package sql.grammar;

import java.util.HashMap;
import java.util.Map;

import org.antlr.v4.runtime.ParserRuleContext;

import sql.SQLSelectParserParser;

/**
 * Maps ANTLR rule indices for dialect-specific grammar alternatives to {@link SqlGrammarDialect}.
 */
public final class SqlGrammarDialectRuleRegistry {

	public record Registration(SqlGrammarDialect dialect, String constructLabel) {
	}

	private static final Map<Integer, Registration> BY_RULE_INDEX = new HashMap<>();

	static {
		register(SqlGrammarDialect.SNOWFLAKE, SQLSelectParserParser.RULE_truncate_snowflake_expression,
				"truncate_snowflake_expression");
		register(SqlGrammarDialect.POSTGRES, SQLSelectParserParser.RULE_truncate_postgres_expression,
				"truncate_postgres_expression");
		register(SqlGrammarDialect.SNOWFLAKE, SQLSelectParserParser.RULE_delete_snowflake_expression,
				"delete_snowflake_expression");
		register(SqlGrammarDialect.POSTGRES, SQLSelectParserParser.RULE_delete_postgres_expression,
				"delete_postgres_expression");
		register(SqlGrammarDialect.POSTGRES, SQLSelectParserParser.RULE_on_conflict_clause, "on_conflict_clause");
		register(SqlGrammarDialect.POSTGRES, SQLSelectParserParser.RULE_delete_returning, "delete_returning");
		register(SqlGrammarDialect.SNOWFLAKE, SQLSelectParserParser.RULE_snowflake_pivot_aggregate_list,
				"snowflake_pivot_aggregate_list");
		register(SqlGrammarDialect.SNOWFLAKE, SQLSelectParserParser.RULE_qualify_clause, "qualify_clause");
		register(SqlGrammarDialect.SNOWFLAKE, SQLSelectParserParser.RULE_snowflake_quoted_numeric_identifier,
				"snowflake_quoted_numeric_identifier");
		register(SqlGrammarDialect.SNOWFLAKE, SQLSelectParserParser.RULE_snowflake_dollar_function_identifier,
				"snowflake_dollar_function_identifier");
		register(SqlGrammarDialect.SNOWFLAKE, SQLSelectParserParser.RULE_unpivot_clause, "unpivot_clause");
		register(SqlGrammarDialect.SNOWFLAKE, SQLSelectParserParser.RULE_pivot_clause, "pivot_clause");
	}

	private SqlGrammarDialectRuleRegistry() {
	}

	private static void register(SqlGrammarDialect dialect, int ruleIndex, String constructLabel) {
		BY_RULE_INDEX.put(ruleIndex, new Registration(dialect, constructLabel));
	}

	public static Registration registrationForRuleIndex(int ruleIndex) {
		return BY_RULE_INDEX.get(ruleIndex);
	}

	/** Snowflake nested {@code WITH} inside a CTE body ({@code cte_body} → {@code with_query}). */
	public static Registration nestedWithInCteBodyRegistration(ParserRuleContext withQueryCtx) {
		if (withQueryCtx == null || withQueryCtx.getRuleIndex() != SQLSelectParserParser.RULE_with_query) {
			return null;
		}
		ParserRuleContext parent = withQueryCtx.getParent();
		if (parent == null || parent.getRuleIndex() != SQLSelectParserParser.RULE_cte_body) {
			return null;
		}
		return new Registration(SqlGrammarDialect.SNOWFLAKE, "nested_with_in_cte_body");
	}
}
