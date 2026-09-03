package astwalkers;

import java.util.HashMap;

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
}
