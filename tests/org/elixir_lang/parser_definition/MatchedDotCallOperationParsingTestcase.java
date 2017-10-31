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

    public void testEmptyBlock() {
        assertParsedAndQuotedCorrectly(false);
    }

    public void testEOLs() {
        assertParsedAndQuotedCorrectly();
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
        assertParsedAndQuotedAroundError();
    }

    public void testDecimalFloat() {
        assertParsedAndQuotedCorrectly(false);
    }

    public void testDecimalWholeNumber() {
        // Parses as just a DecimalFloat
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
        return super.getTestDataPath() + "/matched_dot_call_operation_parsing_test_case";
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        registerProjectFileIndex();
    }
}
