package org.elixir_lang.parser_definition;

public class NoParenthesesExpressionParsingTestCase extends ParsingTestCase {
    public void testCharTokenNumber() {
        doTest(true);
    }

    public void testFunction() {
        doTest(true);
    }

    public void testFunctionSpaceEmptyParentheses() {
        doTest(true);
    }

    public void testFunctionSpaceKeywordsInParentheses() {
        doTest(true);
    }

    public void testFunctionSpacePositionalsAndKeywordsInParentheses() {
        doTest(true);
    }

    public void testFunctionSpacePositionalsInParentheses() {
        doTest(true);
    }

    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/no_parentheses_expression_parsing_test_case";
    }
}
