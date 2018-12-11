package org.elixir_lang.parser_definition.matched_dot_operator_call_operation;

public class NoArgumentsParsingTestCase extends ParsingTestCase {
    /*
     * matchedDotOperand
     */

    public void testAtNonNumericOperation() {
        assertParsedAndQuotedCorrectly(false);
    }

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

    public void testCharToken() {
        assertParsedAndQuotedCorrectly(false);
    }

    public void testBinaryWholeNumber() {
        assertParsedAndQuotedCorrectly(false);
    }

    public void testHexadecimalWholeNumber() {
        assertParsedAndQuotedCorrectly(false);
    }

    public void testOctalWholeNumber() {
        assertParsedAndQuotedCorrectly(false);
    }

    public void testUnknownBaseWholeNumber() {
        assertParsedAndQuotedAroundError(false);
    }

    public void testDecimalFloat() {
        assertParsedAndQuotedCorrectly(false);
    }

    public void testDecimalWholeNumber() {
        assertParsedAndQuotedCorrectly(false);
    }

    public void testStringLine() {
        assertParsedAndQuotedCorrectly(false);
    }

    public void testStringHeredoc() {
        assertParsedAndQuotedCorrectly(false);
    }

    public void testCharListLine() {
        assertParsedAndQuotedCorrectly(false);
    }

    public void testCharListHeredoc() {
        assertParsedAndQuotedCorrectly(false);
    }

    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/no_arguments_parsing_test_case";
    }
}
