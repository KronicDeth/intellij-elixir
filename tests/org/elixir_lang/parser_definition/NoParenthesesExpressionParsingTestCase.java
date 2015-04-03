package org.elixir_lang.parser_definition;

public class NoParenthesesExpressionParsingTestCase extends ParsingTestCase {
    public void testFunction() {
        assertParsedAndQuotedCorrectly();
    }

    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/no_parentheses_expression_parsing_test_case";
    }
}
