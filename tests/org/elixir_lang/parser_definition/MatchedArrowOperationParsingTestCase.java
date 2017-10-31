package org.elixir_lang.parser_definition;

import org.elixir_lang.sdk.elixir.Release;

import static org.elixir_lang.test.ElixirVersion.elixirSdkRelease;

/**
 * Created by luke.imhoff on 9/17/14.
 */
public class MatchedArrowOperationParsingTestCase extends ParsingTestCase {
    public void testAssociativity() {
        if (!isTravis()) {
            assertParsedAndQuotedCorrectly();
        }
    }

    /*
     * matchedInOperand
     */

    public void testCaptureNonNumericOperation() {
        assertParsedAndQuotedCorrectly();
    }

    public void testMatchedInOperation() {
        if (!isTravis() && elixirSdkRelease().compareTo(Release.V_1_2) > 0) {
            assertParsedAndQuotedCorrectly();
        }
    }

    public void testMatchedTwoOperation() {
        assertParsedAndQuotedCorrectly();
    }

    public void testMatchedAdditionOperation() {
        assertParsedAndQuotedCorrectly();
    }

    public void testMatchedMultiplicationOperation() {
        assertParsedAndQuotedCorrectly();
    }

    public void testUnaryNonNumericOperation() {
        assertParsedAndQuotedCorrectly(false);
    }

    public void testMatchedDotOperation() {
        assertParsedAndQuotedCorrectly();
    }

    public void testAtNonNumericOperation() {
        assertParsedAndQuotedCorrectly(false);
    }

    public void testMatchedCallOperation() {
        assertParsedAndQuotedCorrectly();
    }

    public void testVariable() {
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
        assertParsedAndQuotedCorrectly(false);
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

    public void testAtom() {
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

    /*
     * number
     */

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

    /*
     * decimalNumber
     */

    public void testDecimalFloat() {
        assertParsedAndQuotedCorrectly();
    }

    public void testDecimalWholeNumber() {
        assertParsedAndQuotedCorrectly();
    }

    /*
     * binaryString
     */

    public void testStringLine() {
        assertParsedAndQuotedCorrectly();
    }

    public void testStringHeredoc() throws Exception {
        registerProjectFileIndex();

        assertParsedAndQuotedCorrectly();
    }

    /*
     * listString
     */

    public void testCharListLine() {
        assertParsedAndQuotedCorrectly();
    }

    public void testCharListHeredoc() throws Exception {
        registerProjectFileIndex();

        assertParsedAndQuotedCorrectly();
    }

    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/matched_arrow_operation_parsing_test_case";
    }
}
