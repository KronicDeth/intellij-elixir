package org.elixir_lang.parser_definition;

public class NoParenthesesStrictParsingTestCase extends ParsingTestCase {
    public void testEmptyParentheses() {
        assertParsedAndQuotedCorrectly();
    }

    public void testKeywordsInParentheses() {
        assertParsedAndQuotedAroundError();
    }

    public void testPositionalsAndKeywordsInParentheses() {
        assertParsedAndQuotedAroundError();
    }

    public void testPositionalsInParentheses() {
        assertParsedAndQuotedAroundError();
    }

    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/no_parentheses_strict_parsing_test_case";
    }
}
