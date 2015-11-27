package org.elixir_lang.parser_definition;

/**
 * Created by luke.imhoff on 9/17/14.
 */
public class MatchedAndOperationParsingTestCase extends ParsingTestCase {
    public void testAssociativity() {
        assertParsedAndQuotedCorrectly();
    }

    /*
     * matchedAndOperand
     */

    public void testCaptureNonNumericOperation() {
        assertParsedAndQuotedCorrectly();
    }

    public void testMatchedComparisonOperation() {
        assertParsedAndQuotedCorrectly();
    }

    public void testMatchedRelationalOperation() {
        assertParsedAndQuotedCorrectly();
    }

    public void testMatchedArrowOperation() {
        assertParsedAndQuotedCorrectly();
    }

    public void testMatchedInOperation() {
        assertParsedAndQuotedCorrectly();
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
        assertParsedAndQuotedCorrectly();
    }

    public void testMatchedDotOperation() {
        assertParsedAndQuotedCorrectly();
    }

    public void testAtNonNumericOperation() {
        assertParsedAndQuotedCorrectly();
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
        assertParsedAndQuotedCorrectly();
    }

    public void testCaptureNumericOperation() {
        assertParsedAndQuotedCorrectly();
    }

    public void testUnaryNumericOperation() {
        assertParsedAndQuotedCorrectly();
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

    public void testStringHeredoc() {
        assertParsedAndQuotedCorrectly();
    }

    /*
     * listString
     */

    public void testCharListLine() {
        assertParsedAndQuotedCorrectly();
    }

    public void testCharListHeredoc() {
        assertParsedAndQuotedCorrectly();
    }

    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/matched_and_operation_parsing_test_case";
    }
}
