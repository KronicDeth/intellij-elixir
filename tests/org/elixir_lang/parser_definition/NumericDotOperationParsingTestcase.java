package org.elixir_lang.parser_definition;

/**
 * numeric is invalid to the right of `.`, so unlike in {@link MatchedDotOperationParsingTestcase}, this tests only when
 * numeric is left of `.` and the right operand varies based on the test name.  NOTE: UnknownBaseWholeNumber is skipped
 * as a left operand completely since it's only an error handling rule in the IDE and never expected to work in Elixir.
 */
public class NumericDotOperationParsingTestcase extends ParsingTestCase {
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
        return super.getTestDataPath() + "/numeric_dot_operation_parsing_test_case";
    }
}
