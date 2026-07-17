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

	@Test
	public void coverageDrivenAbstractHelperShowTraceFlagBranchesTest() {
		SqlASTWalkerHelper helper = new SqlASTWalkerHelper();

		Assert.assertFalse(helper.getShowparse());
		Assert.assertFalse(helper.getShowsymbols());
		Assert.assertFalse(helper.getShowother());
		Assert.assertTrue(helper.getShowresults());

		helper.showParse = true;
		helper.showSymbols = true;
		helper.showOther = true;
		helper.showResults = true;

		helper.showTrace(AbstractASTWalkerHelper.parseTrace, "parse");
		helper.showTrace(AbstractASTWalkerHelper.symbolTrace, "symbol");
		helper.showTrace(AbstractASTWalkerHelper.otherTrace, "other");
		helper.showTrace(AbstractASTWalkerHelper.resultTrace, "result");

		Assert.assertTrue("Trace branch exercise should not alter parse flag", helper.getShowparse());
		Assert.assertTrue("Trace branch exercise should not alter symbol flag", helper.getShowsymbols());
		Assert.assertTrue("Trace branch exercise should not alter other flag", helper.getShowother());
		Assert.assertTrue("Trace branch exercise should not alter results flag", helper.getShowresults());
	}
}
