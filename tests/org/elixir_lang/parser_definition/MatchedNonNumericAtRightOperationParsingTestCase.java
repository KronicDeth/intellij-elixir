package org.elixir_lang.parser_definition;

/**
 * Created by luke.imhoff on 9/17/14.
 */
public class MatchedNonNumericAtRightOperationParsingTestCase extends ParsingTestCase {
    /*
     * matchedNonNumericAtRightOperand
     */

    public void testNoParenthesesManyArgumentsCall() {
        assertParsedAndQuotedCorrectly();
    }

    public void testAtNoParenthesesManyArgumentsCall() {
        assertParsedAndQuotedCorrectly();
    }

    public void testCaptureNoParenthesesManyArgumentsCall() {
        assertParsedAndQuotedCorrectly();
    }

    public void testUnaryNoParenthesesManyArgumentsCall() {
        assertParsedAndQuotedCorrectly();
    }

    /*
     * matchedNonNumericAtLeftOperand
     */

    public void testCaptureLeftOperation() {
        assertParsedAndQuotedCorrectly();
    }

    public void testUnaryLeftOperation() {
        assertParsedAndQuotedCorrectly();
    }

    public void testAtLeftOperation() {
        assertParsedAndQuotedCorrectly();
    }

    /*
     * matchedOperandExpression
     */

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

    public void testBinaryWholeNumber() {
        assertParsedAndQuotedCorrectly();
    }

    public void testDecimalFloat() {
        assertParsedAndQuotedCorrectly();
    }

    public void testDecimalWholeNumber() {
        assertParsedAndQuotedCorrectly();
    }

    public void testOctalWholeNumber() {
        assertParsedAndQuotedCorrectly();
    }

    public void testUnknownBaseWholeNumber() {
        assertParsedAndQuotedAroundError();
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
        return super.getTestDataPath() + "/matched_non_numeric_at_right_operation_parsing_test_case";
    }
}
