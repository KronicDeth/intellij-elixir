package org.elixir_lang.parser_definition;

/**
 * Created by luke.imhoff on 9/17/14.
 */
public class NoParenthesesNoArgumentsCallParsingTestCase extends ParsingTestCase {

    /*
     *
     * Vary Qualifier
     *
     */

    public void testAtOperationDotIdentifier() {
        assertParsedAndQuotedCorrectly();
    }

    /*
     * matchedAtNonNumericLeftOperand
     */

    public void testCaptureLeftOperationDotIdentifier() {
        assertParsedAndQuotedCorrectly();
    }

    public void testUnaryLeftOperationDotIdentifier() {
        assertParsedAndQuotedCorrectly();
    }

    public void testAtLeftOperationDotIdentifier() {
        assertParsedAndQuotedCorrectly();
    }

    /*
     * matchedOperandExpression
     */

    public void testVariableDotIdentifier() {
        assertParsedAndQuotedCorrectly();
    }

    /*
     * accessExpression
     */

    public void testAtNumericOperationDotIdentifier() {
        assertParsedAndQuotedCorrectly();
    }

    /* underscore is a special case of identifier because _ is also a decimal separator, so this checks that _ as an
       identifier is favored. */
    public void testAtNumericOperationDotUnderscore() {
        assertParsedAndQuotedCorrectly();
    }

    public void testCaptureNumericOperationDotIdentifier() {
        assertParsedAndQuotedCorrectly();
    }

    public void testUnaryNumericOperationDotIdentifier() {
        assertParsedAndQuotedCorrectly();
    }

    public void testEmptyBlockDotIdentifier() {
        assertParsedAndQuotedCorrectly();
    }

    public void testListDotIdentifier() {
        assertParsedAndQuotedCorrectly();
    }

    public void testSigilDotIdentifier() {
        assertParsedAndQuotedCorrectly();
    }

    public void testAtomKeywordDotIdentifier() {
        assertParsedAndQuotedCorrectly();
    }

    public void testAtomDotIdentifier() {
        assertParsedAndQuotedCorrectly();
    }

    public void testAliasDotIdentifier() {
        assertParsedAndQuotedCorrectly();
    }

    /*
     * numeric
     */

    public void testCharTokenDotIdentifier() {
        assertParsedAndQuotedCorrectly();
    }

    public void testBinaryWholeNumberDotIdentifier() {
        assertParsedAndQuotedCorrectly();
    }

    public void testDecimalFloatDotIdentifier() {
        assertParsedAndQuotedCorrectly();
    }

    public void testDecimalWholeNumberDotIdentifier() {
        assertParsedAndQuotedCorrectly();
    }

    public void testOctalWholeNumberDotIdentifier() {
        assertParsedAndQuotedCorrectly();
    }

    public void testUnknownBaseWholeNumberDotIdentifier() {
        assertParsedAndQuotedAroundError();
    }

    /*
     * binaryString
     */

    public void testStringLineDotIdentifier() {
        assertParsedAndQuotedCorrectly();
    }

    public void testStringHeredocDotIdentifier() throws Exception {
        registerProjectFileIndex();

        assertParsedAndQuotedCorrectly();
    }

    /*
     * listString
     */

    public void testCharListLineDotIdentifier() {
        assertParsedAndQuotedCorrectly();
    }

    public void testCharListHeredocDotIdentifier() throws Exception {
        registerProjectFileIndex();

        assertParsedAndQuotedCorrectly();
    }

    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/no_parentheses_no_arguments_call_parsing_test_case";
    }
}
