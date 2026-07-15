package sql.walker;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.junit.Test;

import sql.symboltree.ArchivedClauseDualProbeDiffRecorder;

/**
 * Phase C1: run dual-probe diff instrumentation across the full consolidation gate plus
 * supplemental DML nested-CTE cases not in the gate. Informational only.
 *
 * Run:
 * {@code mvn -Dtest=sql.walker.ArchivedClauseDualProbeDiffGateTest test}
 */
public class ArchivedClauseDualProbeDiffGateTest {

	private final List<String> gateCaseFailures = new ArrayList<String>();

	@Test
	public void reportDualProbeDiffOnFullConsolidationGate() throws Exception {
		runGateSession("consolidation-gate", () -> {
			SymbolTableResolutionConsolidationTestSuite suite =
					new SymbolTableResolutionConsolidationTestSuite();
			invokeAllTests(suite, SymbolTableResolutionConsolidationTestSuite.class);
		});
	}

	@Test
	public void reportDualProbeDiffOnSupplementalDmlNestedCteCases() throws Exception {
		runGateSession("supplemental-dml-nested-cte", () -> {
			SqlEventWalkerDmlUpdateInsertDeleteTruncateTests dmlTests =
					new SqlEventWalkerDmlUpdateInsertDeleteTruncateTests();
			invokeTests(
					dmlTests,
					SqlEventWalkerDmlUpdateInsertDeleteTruncateTests.class,
					"updateComplexSubstitutionU4NestedWithInCteBody",
					"insertComplexSubstitutionI4NestedWithInCteBody",
					"deleteComplexSubstitutionD4NestedWithInCteBody",
					"updateFromNestedSubqueryDepth2CorrelatedTargetQualifiedColumnV13",
					"updateFromNestedSubqueryDepth3CorrelatedTargetQualifiedColumnV14");
		});
	}

	private void runGateSession(String sessionLabel, GateSessionRunnable runnable) throws Exception {
		String previousProperty = System.getProperty(ArchivedClauseDualProbeDiffRecorder.SYSPROP);
		System.setProperty(ArchivedClauseDualProbeDiffRecorder.SYSPROP, "true");
		ArchivedClauseDualProbeDiffRecorder.resetGateSession();
		gateCaseFailures.clear();
		try {
			runnable.run();
			String report = ArchivedClauseDualProbeDiffRecorder.formatGateSessionReport();
			System.out.println("Session: " + sessionLabel);
			System.out.println(report);
			if (!gateCaseFailures.isEmpty()) {
				System.out.println("Gate case assertion failures (walk still instrumented): "
						+ gateCaseFailures.size());
				for (String failure : gateCaseFailures) {
					System.out.println("  - " + failure);
				}
			}
		} finally {
			if (previousProperty == null) {
				System.clearProperty(ArchivedClauseDualProbeDiffRecorder.SYSPROP);
			} else {
				System.setProperty(ArchivedClauseDualProbeDiffRecorder.SYSPROP, previousProperty);
			}
			ArchivedClauseDualProbeDiffRecorder.resetGateSession();
		}
	}

	private void invokeAllTests(Object testInstance, Class<?> testClass) throws Exception {
		List<Method> testMethods = new ArrayList<>();
		for (Method method : testClass.getDeclaredMethods()) {
			if (method.getAnnotation(Test.class) != null) {
				testMethods.add(method);
			}
		}
		testMethods.sort(Comparator.comparing(Method::getName));
		for (Method method : testMethods) {
			invokeTest(testInstance, method);
		}
	}

	private void invokeTests(
			Object testInstance,
			Class<?> testClass,
			String... methodNames) throws Exception {
		for (String methodName : methodNames) {
			Method method = testClass.getDeclaredMethod(methodName);
			invokeTest(testInstance, method);
		}
	}

	private void invokeTest(Object testInstance, Method method) throws Exception {
		ArchivedClauseDualProbeDiffRecorder.beginGateCase(method.getName());
		try {
			method.invoke(testInstance);
		} catch (InvocationTargetException invocationTargetException) {
			Throwable cause = invocationTargetException.getCause();
			if (cause instanceof AssertionError assertionError) {
				gateCaseFailures.add(method.getName() + ": " + assertionError.getMessage());
				return;
			}
			if (cause instanceof Exception exception) {
				throw exception;
			}
			if (cause instanceof Error error) {
				throw error;
			}
			throw invocationTargetException;
		} finally {
			ArchivedClauseDualProbeDiffRecorder.endGateCase();
		}
	}

	@FunctionalInterface
	private interface GateSessionRunnable {
		void run() throws Exception;
	}
}
