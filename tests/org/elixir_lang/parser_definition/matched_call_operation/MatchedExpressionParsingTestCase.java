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
        assertParsedWithErrors();
    }

    public void testCaptureNumericOperation() {
        assertParsedWithErrors();
    }

    public void testUnaryNumericOperation() {
        assertParsedWithErrors();
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
        assertParsedWithErrors();
    }

    public void testAtom() {
        assertParsedWithErrors();
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

    public void testDecimalFloatDecimalFloat() {
        assertParsedWithErrors();
    }

    public void testDecimalWholeNumber() {
        assertParsedWithErrors();
    }

    public void testStringLine() {
        assertParsedWithErrors();
    }

    public void testStringHeredoc() {
        assertParsedWithErrors();
    }

    public void testCharListLine() {
        assertParsedWithErrors();
    }

    public void testCharListHeredoc() {
        assertParsedWithErrors();
    }

    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/matched_expression_parsing_test_case";
    }
}
