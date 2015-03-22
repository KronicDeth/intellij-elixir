package org.elixir_lang.parser_definition;

/**
 * Created by luke.imhoff on 9/17/14.
 */
public class MatchedNonNumericAtRightOperationParsingTestCase extends ParsingTestCase {
    /*
     * matchedNonNumericAtRightOperand
     */

    public void testNoParenthesesManyArgumentsCall() {
        assertParsedAndQuotedCorrectly();
    }

    public void testAtNoParenthesesManyArgumentsCall() {
        assertParsedAndQuotedCorrectly();
    }

    public void testCaptureNoParenthesesManyArgumentsCall() {
        assertParsedAndQuotedCorrectly();
    }

    public void testUnaryNoParenthesesManyArgumentsCall() {
        assertParsedAndQuotedCorrectly();
    }

    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/matched_non_numeric_at_right_operation_parsing_test_case";
    }
}
