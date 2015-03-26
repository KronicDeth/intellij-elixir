package org.elixir_lang.parser_definition;

public class NoParenthesesStrictParsingTestCase extends ParsingTestCase {
    public void testEmptyParentheses() {
        assertParsedAndQuotedCorrectly();
    }

    public void testKeywordsInParentheses() {
        assertParsedAndQuotedCorrectly();
    }

    public void testPositionalsAndKeywordsInParentheses() {
        assertParsedAndQuotedCorrectly();
    }

    public void testPositionalsInParentheses() {
        assertParsedAndQuotedCorrectly();
    }

    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/no_parentheses_strict_parsing_test_case";
    }
}
