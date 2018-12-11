package org.elixir_lang.parser_definition.matched_call_operation;

public class MatchedExpressionParsingTestCase extends ParsingTestCase {
    /*
     * matchedCallOperation
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
        assertParsedWithErrors(false);
    }

    public void testCaptureNumericOperation() {
        assertParsedWithErrors(false);
    }

    public void testUnaryNumericOperation() {
        assertParsedWithErrors(false);
    }

    public void testList() {
        assertParsedWithErrors(false);
    }

    public void testSigil() {
        assertParsedWithErrors(false);
    }

    public void testAtomKeyword() {
        assertParsedWithErrors(false);
    }

    public void testAtom() {
        assertParsedWithErrors(false);
    }

    /*
     * numeric
     */

    public void testCharToken() {
        assertParsedWithErrors(false);
    }

    public void testBinaryWholeNumber() {
        assertParsedWithErrors(false);
    }

    public void testHexadecimalWholeNumber() {
        assertParsedWithErrors(false);
    }

    public void testOctalWholeNumber() {
        assertParsedWithErrors(false);
    }

    public void testUnknownBaseWholeNumber() {
        assertParsedWithErrors(false);
    }

    public void testDecimalFloat() {
        assertParsedWithErrors(false);
    }

    public void testDecimalFloatDecimalFloat() {
        assertParsedWithErrors(false);
    }

    public void testDecimalWholeNumber() {
        assertParsedWithErrors(false);
    }

    public void testStringLine() {
        assertParsedWithErrors(false);
    }

    public void testStringHeredoc() {
        assertParsedWithErrors(false);
    }

    public void testCharListLine() {
        assertParsedWithErrors(false);
    }

    public void testCharListHeredoc() {
        assertParsedWithErrors(false);
    }

    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/matched_expression_parsing_test_case";
    }
}
