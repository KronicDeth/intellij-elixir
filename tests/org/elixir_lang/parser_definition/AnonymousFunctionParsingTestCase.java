package org.elixir_lang.parser_definition;

public class AnonymousFunctionParsingTestCase extends ParsingTestCase {
    public void testFnEndOfExpressionStabEnd() {
        assertParsedAndQuotedCorrectly();
    }

    public void testFnExpressionEnd() {
        assertParsedAndQuotedCorrectly();
    }

    public void testFnKeywordsInParenthesesStabEnd() {
        assertParsedAndQuotedCorrectly();
    }

    public void testFnStabEnd() {
        assertParsedAndQuotedCorrectly();
    }

    public void testFnStabExpressionEnd() {
        assertParsedAndQuotedCorrectly();
    }

    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/anonymous_function_parsing_test_case";
    }
}
