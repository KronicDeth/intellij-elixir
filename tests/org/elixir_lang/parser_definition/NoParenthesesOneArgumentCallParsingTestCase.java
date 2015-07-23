package org.elixir_lang.parser_definition;

/**
 * Created by luke.imhoff on 9/17/14.
 */
public class NoParenthesesOneArgumentCallParsingTestCase extends ParsingTestCase {
    public void testUnqualifiedSpaceOpeningParenthesisDualVariableClosingParentheses() {
        assertParsedAndQuotedCorrectly();
    }

    public void testUnqualifiedSpaceOpeningParenthesisDualVariableClosingParenthesesDoBlock() {
        assertParsedAndQuotedCorrectly();
    }

    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/no_parentheses_one_argument_call_parsing_test_case";
    }
}
