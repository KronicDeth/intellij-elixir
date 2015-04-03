package org.elixir_lang.parser_definition;

/**
 * list is invalid to the right of `.`, so unlike in {@link MatchedDotOperationParsingTestcase}, this tests only when
 * list is left of `.` and the right operand varies based on the test name.
 */
public class ListDotOperationParsingTestCase extends ParsingTestCase {
    /*
     * matchedDotOperand
     */

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
        assertParsedWithError();
    }

    public void testSigil() {
        assertParsedWithError();
    }

    public void testAtomKeyword() {
        assertParsedAndQuotedCorrectly();
    }

    public void testAtom() {
        assertParsedWithError();
    }

    public void testAlias() {
        assertParsedAndQuotedCorrectly();
    }

    /*
     * numeric
     */

    public void testCharToken() {
        assertParsedWithError();
    }

    public void testBinaryWholeNumber() {
        assertParsedWithError();
    }

    public void testHexadecimalWholeNumber() {
        assertParsedWithError();
    }

    public void testOctalWholeNumber() {
        assertParsedWithError();
    }

    public void testUnknownBaseWholeNumber() {
        assertParsedWithError();
    }

    public void testDecimalFloat() {
        assertParsedWithError();
    }

    public void testDecimalWholeNumber() {
        assertParsedWithError();
    }

    public void testStringLine() {
        assertParsedAndQuotedCorrectly();
    }

    public void testStringHeredoc() {
        assertParsedWithError();
    }

    public void testCharListLine() {
        assertParsedAndQuotedCorrectly();
    }

    public void testCharListHeredoc() {
        assertParsedWithError();
    }

    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/list_dot_operation_parsing_test_case";
    }
}
