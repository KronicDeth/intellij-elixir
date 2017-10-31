package org.elixir_lang.parser_definition;

public class AnonymousFunctionParsingTestCase extends ParsingTestCase {
    public void testBlock() {
        assertParsedAndQuotedAroundError();
    }

    public void testFnEmptyParenthesesStabEnd() {
        assertParsedAndQuotedCorrectly();
    }

    public void testFnEndOfExpressionStabEnd() {
        assertParsedAndQuotedCorrectly();
    }

    public void testFnExpressionEnd() throws Exception {
        registerProjectFileIndex();

        assertParsedAndQuotedAroundError(false);
    }

    public void testFnKeywordsInParenthesesStabEnd() {
        assertParsedAndQuotedCorrectly();
    }

    public void testFnKeywordsInParenthesesWhenExpressionStabEnd() throws Exception {
        registerProjectFileIndex();

        assertParsedAndQuotedCorrectly(false);
    }

    public void testFnMatchedExpressionStabEnd() throws Exception {
        registerProjectFileIndex();

        assertParsedAndQuotedCorrectly(false);
    }

    public void testFnMultiNewlineStab() {
        assertParsedAndQuotedCorrectly();
    }

    public void testFnMultiStabEnd() {
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

    public void testFnPositionalsAndKeywordsInParenthesesWhenExpressionStabEnd() throws Exception {
        registerProjectFileIndex();

        assertParsedAndQuotedCorrectly(false);
    }

    public void testFnStabEnd() {
        assertParsedAndQuotedCorrectly();
    }

    public void testFnStabEndOfExpressionExpressionListEndOfExpressionEnd() {
        assertParsedAndQuotedCorrectly();
    }

    public void testFnStabExpressionEnd() throws Exception {
        registerProjectFileIndex();

        assertParsedAndQuotedCorrectly(false);
    }

    public void testFnStabExpressionListEnd() {
        assertParsedAndQuotedCorrectly();
    }

    public void testFnStabMultiNewlineStab() {
        assertParsedAndQuotedCorrectly();
    }

    public void testFnUnqualifiedNoParenthesesManyArgumentsCallStabExpressionEnd() {
        assertParsedAndQuotedCorrectly();
    }

    public void testKernelTypespecRegression() {
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
