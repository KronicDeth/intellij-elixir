package org.elixir_lang.parser_definition;

/**
 * Created by luke.imhoff on 9/17/14.
 */
public class MatchedAtNonNumericOperationParsingTestCase extends ParsingTestCase {
    /*
     * matchedAtNonNumericRightOperand
     */

    public void testNoParenthesesManyArgumentsCall() {
        assertParsedAndQuotedCorrectly();
    }

    public void testAtNoParenthesesManyArgumentsCall() {
        assertParsedAndQuotedCorrectly(false);
    }

    public void testCaptureNoParenthesesManyArgumentsCall() {
        assertParsedAndQuotedCorrectly(false);
    }

    public void testUnaryNoParenthesesManyArgumentsCall() {
        assertParsedAndQuotedCorrectly(false);
    }

    /*
     * matchedAtNonNumericLeftOperand
     */

    public void testCaptureLeftOperation() {
        assertParsedAndQuotedCorrectly(false);
    }

    public void testUnaryLeftOperation() {
        assertParsedAndQuotedCorrectly(false);
    }

    public void testAtLeftOperation() {
        assertParsedAndQuotedCorrectly(false);
    }

    /*
     * matchedOperandExpression
     */

    public void testVariable() {
        assertParsedAndQuotedCorrectly(false);
    }

    /*
     * accessExpression
     */

    public void testAtNumericOperation() {
        assertParsedAndQuotedCorrectly(false);
    }

    public void testCaptureNumericOperation() {
        assertParsedAndQuotedCorrectly(false);
    }

    public void testUnaryNumericOperation() {
        assertParsedAndQuotedCorrectly(false);
    }

    public void testList() {
        assertParsedAndQuotedCorrectly(false);
    }

    public void testSigil() {
        assertParsedAndQuotedCorrectly(false);
    }

    public void testAtomKeyword() {
        assertParsedAndQuotedCorrectly(false);
    }

    public void testAtom() {
        assertParsedAndQuotedCorrectly(false);
    }

    public void testAlias() {
        assertParsedAndQuotedCorrectly(false);
    }

    /*
     * numeric
     */

    public void testBinaryWholeNumber() {
        assertParsedAndQuotedCorrectly(false);
    }

    public void testDecimalFloat() {
        assertParsedAndQuotedCorrectly(false);
    }

    public void testDecimalWholeNumber() {
        assertParsedAndQuotedCorrectly(false);
    }

    public void testOctalWholeNumber() {
        assertParsedAndQuotedCorrectly(false);
    }

    public void testUnknownBaseWholeNumber() {
        assertParsedAndQuotedAroundError(false);
    }

    /*
     * binaryString
     */

    public void testStringLine() {
        assertParsedAndQuotedCorrectly(false);
    }

    public void testStringHeredoc() {
        assertParsedAndQuotedCorrectly(false);
    }

    /*
     * listString
     */

    public void testCharListLine() {
        assertParsedAndQuotedCorrectly(false);
    }

    public void testCharListHeredoc() {
        assertParsedAndQuotedCorrectly(false);
    }


    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/matched_at_non_numeric_operation_parsing_test_case";
    }
}
