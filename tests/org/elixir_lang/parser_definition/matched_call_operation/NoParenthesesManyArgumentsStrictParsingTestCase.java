package org.elixir_lang.parser_definition.matched_call_operation;

public class NoParenthesesManyArgumentsStrictParsingTestCase extends ParsingTestCase {
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
        assertParsedWithErrors(false);
    }

    public void testCaptureNumericOperation() {
        assertParsedWithErrors();
    }

    public void testUnaryNumericOperation() {
        assertParsedWithErrors(false);
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

    public void testAlias() {
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
        return super.getTestDataPath() + "/no_parentheses_many_arguments_strict_parsing_test_case";
    }
}
