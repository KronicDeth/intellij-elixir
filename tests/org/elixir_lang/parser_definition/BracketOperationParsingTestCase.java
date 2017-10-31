package org.elixir_lang.parser_definition;

import org.elixir_lang.sdk.elixir.Release;

import static org.elixir_lang.test.ElixirVersion.elixirSdkRelease;

/**
 * Created by luke.imhoff on 9/17/14.
 */
public class BracketOperationParsingTestCase extends ParsingTestCase {
    /*
     * matchedExpression
     */

    public void testAfter() {
        assertParsedAndQuotedCorrectly();
    }

    public void testAndOperator() {
        assertParsedAndQuotedCorrectly();
    }

    public void testArrowOperator() {
        assertParsedAndQuotedCorrectly();
    }

    public void testAtOperator() {
        assertParsedAndQuotedCorrectly();
    }

    public void testCaptureOperator() {
        assertParsedAndQuotedCorrectly();
    }

    public void testComparisonOperator() {
        assertParsedAndQuotedCorrectly();
    }

    public void testDo() {
        assertParsedAndQuotedCorrectly();
    }

    public void testDualOperator() {
        assertParsedAndQuotedCorrectly();
    }

    public void testThreeOperator() {
        assertParsedAndQuotedCorrectly();
    }

    public void testInMatchOperator() {
        assertParsedAndQuotedCorrectly();
    }

    public void testInOperator() {
        assertParsedAndQuotedCorrectly();
    }

    public void testMatchOperator() {
        assertParsedAndQuotedCorrectly();
    }

    public void testMultiplicationOperator() {
        assertParsedAndQuotedCorrectly();
    }

    public void testOrOperator() {
        assertParsedAndQuotedCorrectly();
    }

    public void testPipeOperator() {
        assertParsedAndQuotedCorrectly();
    }

    public void testRelationalOperator() {
        assertParsedAndQuotedCorrectly();
    }

    public void testStabOperator() {
        assertParsedAndQuotedCorrectly();
    }

    public void testStructOperator() {
        if (elixirSdkRelease().compareTo(Release.V_1_5) < 0) {
            assertParsedAndQuotedCorrectly();
        } else {
            assertTrue(elixirSdkRelease().compareTo(Release.V_1_5) >= 0);
        }
    }

    public void testTwoOperator() {
        assertParsedAndQuotedCorrectly();
    }

    public void testUnaryOperator() {
        assertParsedAndQuotedCorrectly();
    }

    public void testWhenOperator() {
        assertParsedAndQuotedCorrectly();
    }

    public void testMatchedDotCallOperation() {
        assertParsedAndQuotedCorrectly();
    }

    public void testMatchedQualifiedAliasOperation() {
        assertParsedAndQuotedCorrectly();
    }

    public void testMatchedQualifiedCallOperation() {
        assertParsedAndQuotedCorrectly();
    }

    public void testMatchedQualifiedIdentifier() {
        assertParsedAndQuotedCorrectly();
    }

    public void testMatchedUnqualifiedCallOperation() {
        assertParsedAndQuotedCorrectly();
    }

    public void testMatchedUnqualifiedIdentifier() {
        assertParsedAndQuotedCorrectly();
    }

    /*
     * accessExpression
     */

    public void testAtNumericOperation() {
        assertParsedAndQuotedCorrectly(false);
    }

    public void testCaptureNumericOperation() {
        assertParsedAndQuotedCorrectly();
    }

    public void testUnaryNumericOperation() {
        if (elixirSdkRelease().compareTo(Release.V_1_5) < 0) {
            assertParsedAndQuotedCorrectly();
        } else {
            assertTrue(elixirSdkRelease().compareTo(Release.V_1_5) >= 0);
        }
    }

    public void testEmptyBlock() {
        assertParsedAndQuotedCorrectly();
    }

    public void testList() {
        assertParsedAndQuotedCorrectly();
    }

    public void testSigil() {
        assertParsedAndQuotedCorrectly();
    }

    public void testAtomKeyword() {
        assertParsedAndQuotedCorrectly();
    }

    public void testAlias() {
        assertParsedAndQuotedCorrectly();
    }

    /*
     * numeric
     */

    public void testCharToken() {
        assertParsedAndQuotedCorrectly();
    }

    public void testBinaryWholeNumber() {
        assertParsedAndQuotedCorrectly();
    }

    public void testHexadecimalWholeNumber() {
        assertParsedAndQuotedCorrectly();
    }

    public void testOctalWholeNumber() {
        assertParsedAndQuotedCorrectly();
    }

    public void testUnknownBaseWholeNumber() {
        assertParsedAndQuotedAroundError();
    }

    public void testDecimalFloat() {
        assertParsedAndQuotedCorrectly();
    }

    public void testDecimalWholeNumber() {
        assertParsedAndQuotedCorrectly();
    }

    public void testStringLine() {
        assertParsedAndQuotedCorrectly();
    }

    public void testStringHeredoc() throws Exception {
        registerProjectFileIndex();

        assertParsedAndQuotedCorrectly();
    }

    public void testCharListLine() {
        assertParsedAndQuotedCorrectly();
    }

    public void testCharListHeredoc() throws Exception {
        registerProjectFileIndex();

        assertParsedAndQuotedCorrectly();
    }

    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/bracket_operation_parsing_test_case";
    }
}
