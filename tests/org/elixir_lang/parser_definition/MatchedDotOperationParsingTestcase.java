package org.elixir_lang.parser_definition;

public class MatchedDotOperationParsingTestcase extends ParsingTestCase {
    public void testAliasDotIdentifier() {
        assertParsedAndQuotedCorrectly();
    }

    public void testAliasDotIdentifierDotIdentifier() {
        assertParsedAndQuotedCorrectly();
    }

    public void testIdentifierDotIdentifierDotIdentifier() {
        assertParsedAndQuotedCorrectly();
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
        assertParsedWithErrors();
    }

    public void testList() {
        assertParsedWithErrors();
    }

    public void testSigil() {
        assertParsedWithErrors();
    }

    public void testAtomKeyword() {
        assertParsedAndQuotedCorrectly();
    }

    public void testAtom() {
        assertParsedWithErrors();
    }

    public void testAlias() {
        assertParsedAndQuotedCorrectly();
    }

    /*
     * numeric
     */

    public void testCharToken() {
        assertParsedWithErrors();
    }

    public void testBinaryWholeNumber() {
        assertParsedWithErrors();
    }

    public void testHexadecimalWholeNumber() {
        assertParsedWithErrors();
    }

    public void testOctalWholeNumber() {
        assertParsedWithErrors();
    }

    public void testUnknownBaseWholeNumber() {
        assertParsedWithErrors();
    }

    public void testDecimalFloat() {
        assertParsedWithErrors();
    }

    public void testDecimalWholeNumber() {
        // Parses as just a DecimalFloat
        assertParsedAndQuotedCorrectly();
    }

    public void testStringLine() {
        assertParsedAndQuotedCorrectly();
    }

    public void testStringHeredoc() {
        assertParsedWithErrors();
    }

    public void testCharListLine() {
        assertParsedAndQuotedCorrectly();
    }

    public void testCharListHeredoc() {
        assertParsedWithErrors();
    }

    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/matched_dot_operation_parsing_test_case";
    }
}
