package sql.walker;

import org.junit.Test;

/**
 * Reusable validation bundle for symbol-table resolution consolidation (phases 5–8).
 *
 * Includes unaliased derived-table V1–V16 and set-operation interface validation V1–V5.
 *
 * Run:
 * {@code mvn -Dtest=sql.walker.SymbolTableResolutionConsolidationTestSuite test}
 *
 * Or use the Maven profile:
 * {@code mvn -Psymbol-table-resolution-consolidation test}
 */
public class SymbolTableResolutionConsolidationTestSuite {

	private final SqlEventWalkerSubqueriesAndClauseSemanticsTests unaliasedTests =
			new SqlEventWalkerSubqueriesAndClauseSemanticsTests();
	private final SqlParseEventWalkerWithAccessObjectTest setOpTests =
			new SqlParseEventWalkerWithAccessObjectTest();

	@Test
	public void unaliasedDerivedSimpleAllOuterClausesV1Test() {
		unaliasedTests.unaliasedDerivedSimpleAllOuterClausesV1Test();
	}

	@Test
	public void unaliasedDerivedSimpleFilteredInnerV2Test() {
		unaliasedTests.unaliasedDerivedSimpleFilteredInnerV2Test();
	}

	@Test
	public void unaliasedDerivedSimpleInnerFromTab2V3Test() {
		unaliasedTests.unaliasedDerivedSimpleInnerFromTab2V3Test();
	}

	@Test
	public void unaliasedDerivedSimpleInnerFromTab3V4Test() {
		unaliasedTests.unaliasedDerivedSimpleInnerFromTab3V4Test();
	}

	@Test
	public void unaliasedDerivedSimpleInnerNotNullFilterV5Test() {
		unaliasedTests.unaliasedDerivedSimpleInnerNotNullFilterV5Test();
	}

	@Test
	public void unaliasedDerivedSimpleInnerGroupedV6Test() {
		unaliasedTests.unaliasedDerivedSimpleInnerGroupedV6Test();
	}

	@Test
	public void unaliasedDerivedUnionAllOuterClausesV7Test() {
		unaliasedTests.unaliasedDerivedUnionAllOuterClausesV7Test();
	}

	@Test
	public void unaliasedDerivedUnionAllInnerAllOuterClausesV8Test() {
		unaliasedTests.unaliasedDerivedUnionAllInnerAllOuterClausesV8Test();
	}

	@Test
	public void unaliasedDerivedIntersectAllOuterClausesV9Test() {
		unaliasedTests.unaliasedDerivedIntersectAllOuterClausesV9Test();
	}

	@Test
	public void unaliasedDerivedExceptAllOuterClausesV10Test() {
		unaliasedTests.unaliasedDerivedExceptAllOuterClausesV10Test();
	}

	@Test
	public void unaliasedDerivedUnionIntersectAllOuterClausesV11Test() {
		unaliasedTests.unaliasedDerivedUnionIntersectAllOuterClausesV11Test();
	}

	@Test
	public void unaliasedDerivedUnionMixedColumnNamesV12Test() {
		unaliasedTests.unaliasedDerivedUnionMixedColumnNamesV12Test();
	}

	@Test
	public void unaliasedValuesPositionalAllOuterClausesV13Test() {
		unaliasedTests.unaliasedValuesPositionalAllOuterClausesV13Test();
	}

	@Test
	public void unaliasedValuesAliasOnlyAllOuterClausesV14Test() {
		unaliasedTests.unaliasedValuesAliasOnlyAllOuterClausesV14Test();
	}

	@Test
	public void unaliasedValuesNamedColumnsAllOuterClausesV15Test() {
		unaliasedTests.unaliasedValuesNamedColumnsAllOuterClausesV15Test();
	}

	@Test
	public void unaliasedDerivedFlattenInnerSelectAllOuterClausesV16Test() {
		unaliasedTests.unaliasedDerivedFlattenInnerSelectAllOuterClausesV16Test();
	}

	@Test
	public void multipleIntersectSubqueryInterfaceValidationV1Test() {
		setOpTests.multipleIntersectSubqueryInterfaceValidationV1Test();
	}

	@Test
	public void multipleIntersectSubqueryInterfaceValidationV2Test() {
		setOpTests.multipleIntersectSubqueryInterfaceValidationV2Test();
	}

	@Test
	public void multipleUnionedIntersectSubqueryInterfaceValidationWOAliasesV3Test() {
		setOpTests.multipleUnionedIntersectSubqueryInterfaceValidationWOAliasesV3Test();
	}

	@Test
	public void multipleIntersectedUnionSubqueryInterfaceValidationV4Test() {
		setOpTests.multipleIntersectedUnionSubqueryInterfaceValidationV4Test();
	}

	@Test
	public void multipleIntersectedUnionSubqueryInterfaceValidationWOAliasesV5Test() {
		setOpTests.multipleIntersectedUnionSubqueryInterfaceValidationWOAliasesV5Test();
	}
}
