package sql.walker;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Opt-in utility for regenerating nested-CTE symbol-table golden text files.
 * <p>
 * Not part of the default {@code mvn test} suite. Requires pre-populated SQL fixtures under
 * {@code target/nested-cte-sql/} (developer workflow only). The authoritative regression tests
 * are the matching methods in {@link SqlEventWalkerSubqueriesAndClauseSemanticsTests}.
 * <p>
 * To run manually after placing SQL files:
 * {@code mvn -Dtest=DumpNestedCteSymbolGoldensTest test}
 */
@Ignore("Manual golden dump utility; requires target/nested-cte-sql fixtures (not on clean builds).")
public class DumpNestedCteSymbolGoldensTest extends AbstractSqlParseEventWalkerTest {
	private void dump(String method) throws Exception {
		String sql = Files.readString(
				Path.of("target", "nested-cte-sql", method + ".sql"),
				StandardCharsets.UTF_8);
		SqlParseEventWalker extractor = runParsertest(sql, parse(sql));
		Path out = Path.of("target", "nested-cte-symbol-goldens", method + ".txt");
		Files.createDirectories(out.getParent());
		Files.writeString(out, extractor.getSymbolTable().toString(), StandardCharsets.UTF_8);
	}

	@Test
	public void dump_nestedWithScalarHavingAaaBbbThenCccDddEeeParsesWithoutErrors() throws Exception {
		dump("nestedWithScalarHavingAaaBbbThenCccDddEeeParsesWithoutErrors");
	}

	@Test
	public void dump_nestedWithScalarHavingAaaThenBbbCccThenDddEeeParsesWithoutErrors() throws Exception {
		dump("nestedWithScalarHavingAaaThenBbbCccThenDddEeeParsesWithoutErrors");
	}

	@Test
	public void dump_nestedWithScalarHavingAaaBbbThenCccDddThenEeeParsesWithoutErrors() throws Exception {
		dump("nestedWithScalarHavingAaaBbbThenCccDddThenEeeParsesWithoutErrors");
	}

	@Test
	public void dump_nestedWithScalarHavingAaaBbbCccThenDddEeeParsesWithoutErrors() throws Exception {
		dump("nestedWithScalarHavingAaaBbbCccThenDddEeeParsesWithoutErrors");
	}

	@Test
	public void dump_nestedWithScalarHavingAliasHeavyExemplarParsesWithoutErrors() throws Exception {
		dump("nestedWithScalarHavingAliasHeavyExemplarParsesWithoutErrors");
	}

	@Test
	public void dump_nestedWithScalarHavingAliasHeavyValuesSubqueryExemplarParsesWithoutErrors() throws Exception {
		dump("nestedWithScalarHavingAliasHeavyValuesSubqueryExemplarParsesWithoutErrors");
	}

	@Test
	public void dump_nestedWithScalarHavingAliasHeavyUnnamedValuesRejectsNamedColumnReferences() throws Exception {
		dump("nestedWithScalarHavingAliasHeavyUnnamedValuesRejectsNamedColumnReferences");
	}

	@Test
	public void dump_nestedWithScalarHavingAliasHeavyValuesCteVirtualColumnsExemplarParsesWithoutErrors() throws Exception {
		dump("nestedWithScalarHavingAliasHeavyValuesCteVirtualColumnsExemplarParsesWithoutErrors");
	}

	@Test
	public void dump_nestedWithScalarHavingAliasHeavySnowflakeTableFunctionColumnsExemplarParsesWithoutErrors() throws Exception {
		dump("nestedWithScalarHavingAliasHeavySnowflakeTableFunctionColumnsExemplarParsesWithoutErrors");
	}

	@Test
	public void dump_nestedWithScalarHavingAliasHeavyValuesCteVirtualPositionRefsExemplarParsesWithoutErrors() throws Exception {
		dump("nestedWithScalarHavingAliasHeavyValuesCteVirtualPositionRefsExemplarParsesWithoutErrors");
	}

	@Test
	public void dump_nestedWithScalarHavingAliasHeavyDeleteCteVirtualColumnsExemplarParsesWithoutErrors() throws Exception {
		dump("nestedWithScalarHavingAliasHeavyDeleteCteVirtualColumnsExemplarParsesWithoutErrors");
	}
}
