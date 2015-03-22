package org.elixir_lang.parser_definition;

public class NoParenthesesStrictParsingTestCase extends ParsingTestCase {
    public void testEmptyParentheses() {
        doTest(true);
    }

    public void testKeywordsInParentheses() {
        doTest(true);
    }

    public void testPositionalsAndKeywordsInParentheses() {
        doTest(true);
    }

    public void testPositionalsInParentheses() {
        doTest(true);
    }

    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/no_parentheses_strict_parsing_test_case";
    }
}
