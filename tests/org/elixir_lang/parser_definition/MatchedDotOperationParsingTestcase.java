package org.elixir_lang.parser_definition;

public class MatchedDotOperationParsingTestcase extends ParsingTestCase {
  public void testAssociativity() {
        assertParsedAndQuotedCorrectly();
    }

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
        return super.getTestDataPath() + "/matched_dot_operation_parsing_test_case";
    }
}
