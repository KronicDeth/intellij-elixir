package org.elixir_lang.parser_definition;

public class AnonymousFunctionParsingTestCase extends ParsingTestCase {
    public void testFnEmptyParenthesesStabEnd() {
        assertParsedAndQuotedCorrectly();
    }

    public void testFnEndOfExpressionStabEnd() {
        assertParsedAndQuotedCorrectly();
    }

    public void testFnExpressionEnd() {
        assertParsedAndQuotedCorrectly();
    }

    public void testFnKeywordsInParenthesesStabEnd() {
        assertParsedAndQuotedCorrectly();
    }

    public void testFnKeywordsInParenthesesWhenExpressionStabEnd() {
        assertParsedAndQuotedCorrectly();
    }

    public void testFnMatchedExpressionStabEnd() {
        assertParsedAndQuotedCorrectly();
    }

    public void testFnNoParenthesesKeywordsStabExpressionEnd() {
        assertParsedAndQuotedCorrectly();
    }

    public void testFnNoParenthesesManyArgumentsStabExpressionEnd() {
        assertParsedAndQuotedCorrectly();
    }

    public void testFnNoParenthesesWhenStabEnd() {
        assertParsedAndQuotedCorrectly();
    }

    public void testFnPositionalsAndKeywordsInParenthesesStabEnd() {
        assertParsedAndQuotedCorrectly();
    }

    public void testFnPositionalsAndKeywordsInParenthesesWhenExpressionStabEnd() {
        assertParsedAndQuotedCorrectly();
    }

    public void testFnStabEnd() {
        assertParsedAndQuotedCorrectly();
    }

    public void testFnStabEndOfExpressionExpressionListEndOfExpressionEnd() {
        assertParsedAndQuotedCorrectly();
    }

    public void testFnStabExpressionEnd() {
        assertParsedAndQuotedCorrectly();
    }

    public void testFnStabExpressionListEnd() {
        assertParsedAndQuotedCorrectly();
    }

    public void testFnStabSignatureStabExpressionEnd() {
        assertParsedAndQuotedCorrectly();
    }

    public void testFnUnqualifiedNoParenthesesManyArgumentsCallStabExpressionEnd() {
        assertParsedAndQuotedCorrectly();
    }

    public void testSpliceOnStab() {
        assertParsedAndQuotedCorrectly();
    }

    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/anonymous_function_parsing_test_case";
    }
}
