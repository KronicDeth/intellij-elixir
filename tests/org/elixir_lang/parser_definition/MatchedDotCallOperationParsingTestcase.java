package org.elixir_lang.parser_definition;

public class MatchedDotCallOperationParsingTestcase extends ParsingTestCase {
    public void testMatchedCallOperation() {
        assertParsedAndQuotedCorrectly();
    }

    public void testMatchedCallOperationAtEnd() {
        assertParsedAndQuotedAroundError();
    }

    public void testMatchedCallOperationInMiddle() {
        assertParsedAndQuotedAroundError();
    }

    public void testMatchedDotMatchedCallOperation() {
        assertParsedAndQuotedCorrectly();
    }

    public void testMatchedDotMatchedCallOperationAtEnd() {
        assertParsedAndQuotedAroundError();
    }

    public void testMatchedDotMatchedCallOperationInMiddle() {
        assertParsedAndQuotedAroundError();
    }

    public void testMatchedDotOperatorCallOperation() {
        assertParsedAndQuotedCorrectly();
    }

    public void testMatchedWhenNoParenthesesKeywordsOperation() {
        assertParsedAndQuotedCorrectly();
    }

    public void testMatchedWhenNoParenthesesKeywordsOperationAtEnd() {
        assertParsedAndQuotedCorrectly();
    }

    public void testMatchedWhenNoParenthesesKeywordsOperationInMiddle() {
        assertParsedAndQuotedAroundError();
    }


  public void testAssociativity() {
        assertParsedAndQuotedCorrectly();
    }

    /*
     * matchedDotOperand
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

    public void testEOLs() {
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
        // Parses as just a DecimalFloat
        assertParsedAndQuotedCorrectly();
    }

    public void testStringLine() {
        assertParsedAndQuotedCorrectly();
    }

    public void testStringHeredoc() {
        assertParsedAndQuotedCorrectly();
    }

    public void testCharListLine() {
        assertParsedAndQuotedCorrectly();
    }

    public void testCharListHeredoc() {
        assertParsedAndQuotedCorrectly();
    }

    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/matched_dot_call_operation_parsing_test_case";
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        registerProjectFileIndex();
    }
}
