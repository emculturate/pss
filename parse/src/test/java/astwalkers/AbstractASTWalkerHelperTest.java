package astwalkers;

import java.util.HashMap;

import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import errorhandling.ParseDiagnostic;

public class AbstractASTWalkerHelperTest {

	@Test
	public void coverageDrivenAbstractHelperOverrideDiagnosticCodeValidationTest() {
		SqlASTWalkerHelper helper = new SqlASTWalkerHelper();

		Assert.assertThrows(IllegalArgumentException.class,
				() -> helper.overrideDiagnosticCode(null, "X"));
		Assert.assertThrows(IllegalArgumentException.class,
				() -> helper.overrideDiagnosticCode("UNKNOWN_KEY", "X"));

		helper.overrideDiagnosticCode(AbstractASTWalkerHelper.DIAG_APPLICATION_ISSUE_FATAL, "MY_FATAL");
		Assert.assertEquals("MY_FATAL",
				helper.getDiagnosticCode(AbstractASTWalkerHelper.DIAG_APPLICATION_ISSUE_FATAL));
	}

	@Test
	public void coverageDrivenAbstractHelperOverrideDiagnosticMessageValidationTest() {
		SqlASTWalkerHelper helper = new SqlASTWalkerHelper();

		Assert.assertThrows(IllegalArgumentException.class,
				() -> helper.overrideDiagnosticMessage(null, "X"));
		Assert.assertThrows(IllegalArgumentException.class,
				() -> helper.overrideDiagnosticMessage("UNKNOWN_KEY", "X"));

		helper.overrideDiagnosticMessage(AbstractASTWalkerHelper.DIAG_APPLICATION_ISSUE_WARNING, "custom warning message");
		Assert.assertEquals("custom warning message",
				helper.getDiagnosticMessage(AbstractASTWalkerHelper.DIAG_APPLICATION_ISSUE_WARNING));
	}

	@Test
	public void coverageDrivenAbstractHelperDiagnosticDeduplicationTest() {
		SqlASTWalkerHelper helper = new SqlASTWalkerHelper();

		ParseDiagnostic first = new ParseDiagnostic(
				ParseDiagnostic.Severity.FATAL,
				"TEST_FATAL",
				"duplicate me",
				1,
				2,
				"unit-test",
				null,
				"tok",
				false,
				"ast-walk",
				null,
				new HashMap<String, String>());
		ParseDiagnostic duplicate = new ParseDiagnostic(
				ParseDiagnostic.Severity.FATAL,
				"TEST_FATAL",
				"duplicate me",
				1,
				2,
				"unit-test",
				null,
				"tok",
				false,
				"ast-walk",
				null,
				new HashMap<String, String>());
		ParseDiagnostic distinct = new ParseDiagnostic(
				ParseDiagnostic.Severity.FATAL,
				"TEST_FATAL",
				"distinct",
				1,
				2,
				"unit-test",
				null,
				"tok",
				false,
				"ast-walk",
				null,
				new HashMap<String, String>());

		helper.addWalkerDiagnostic(first);
		helper.addWalkerDiagnostic(duplicate);
		helper.addWalkerDiagnostic(distinct);
		helper.addWalkerDiagnostic((ParseDiagnostic) null);

		Assert.assertEquals("Duplicate diagnostic should be de-duplicated", 2, helper.getWalkerDiagnostics().size());

		helper.clearWalkerDiagnostics();
		Assert.assertTrue("clearWalkerDiagnostics should empty the list", helper.getWalkerDiagnostics().isEmpty());
	}

	@Test
	public void removeNodeMapOnMissingFrameRecordsStackMisalignFatal() {
		SqlASTWalkerHelper helper = new SqlASTWalkerHelper();

		Map<String, Object> subMap = helper.requireNodeMap(99, 0, 3, 7, "test_rule", "TOK");
		Assert.assertTrue(subMap.isEmpty());
		Assert.assertTrue(helper.isWalkAborted());

		helper.requireNodeMap(1, 0);
		long misalignCount = helper.getWalkerDiagnostics().stream()
				.filter(d -> AbstractASTWalkerHelper.DIAG_AST_WALKER_STACK_MISALIGN.equals(d.code()))
				.count();
		Assert.assertEquals(1L, misalignCount);

		ParseDiagnostic fatal = helper.getWalkerDiagnostics().get(0);
		Assert.assertEquals(ParseDiagnostic.Severity.FATAL, fatal.severity());
		Assert.assertEquals(Integer.valueOf(3), fatal.line());
		Assert.assertEquals(Integer.valueOf(7), fatal.charPositionInLine());
		Assert.assertTrue(fatal.message().contains("test_rule"));
	}
}
