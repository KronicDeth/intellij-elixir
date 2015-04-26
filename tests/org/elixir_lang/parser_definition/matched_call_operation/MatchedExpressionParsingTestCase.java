package org.elixir_lang.parser_definition.matched_call_operation;

public class MatchedExpressionParsingTestCase extends ParsingTestCase {
    /*
     * matchedCallOperation
     */

    public void testAtNonNumericOperation() {
        assertParsedAndQuotedCorrectly();
    }

    public void testVariable() {
        assertParsedAndQuotedCorrectly();
    }

    /*
     * accessExpression
     */

    public void testAtNumericOperation() {
        assertParsedAndQuotedAroundError();
    }

    public void testCaptureNumericOperation() {
        assertParsedAndQuotedAroundError();
    }

    public void testUnaryNumericOperation() {
        assertParsedAndQuotedAroundError();
    }

    public void testEmptyBlock() {
        assertParsedAndQuotedAroundError();
    }

    public void testList() {
        assertParsedAndQuotedAroundError();
    }

    public void testSigil() {
        assertParsedAndQuotedAroundError();
    }

    public void testAtomKeyword() {
        assertParsedAndQuotedAroundError();
    }

    public void testAtom() {
        assertParsedAndQuotedAroundError();
    }

    public void testAlias() {
        assertParsedAndQuotedAroundError();
    }

    /*
     * numeric
     */

    public void testCharToken() {
        assertParsedAndQuotedAroundError();
    }

    public void testBinaryWholeNumber() {
        assertParsedAndQuotedAroundError();
    }

    public void testHexadecimalWholeNumber() {
        assertParsedAndQuotedAroundError();
    }

    public void testOctalWholeNumber() {
        assertParsedAndQuotedAroundError();
    }

    public void testUnknownBaseWholeNumber() {
        assertParsedAndQuotedAroundError();
    }

    public void testDecimalFloat() {
        assertParsedAndQuotedAroundError();
    }

    public void testDecimalWholeNumber() {
        assertParsedAndQuotedAroundError();
    }

    public void testStringLine() {
        assertParsedAndQuotedAroundError();
    }

    public void testStringHeredoc() {
        assertParsedAndQuotedAroundError();
    }

    public void testCharListLine() {
        assertParsedAndQuotedAroundError();
    }

    public void testCharListHeredoc() {
        assertParsedAndQuotedAroundError();
    }

    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/matched_expression_parsing_test_case";
    }
}
