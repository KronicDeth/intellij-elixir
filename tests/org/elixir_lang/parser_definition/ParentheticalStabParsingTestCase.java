package org.elixir_lang.parser_definition;

public class ParentheticalStabParsingTestCase extends ParsingTestCase {
    public void testBlock() {
        assertParsedAndQuotedCorrectly();
    }

    public void testEmpty() {
        assertParsedAndQuotedCorrectly();
    }

    public void testEmptyParenthesesStab() {
        assertParsedAndQuotedCorrectly();
    }

    public void testEndOfExpressionStab() {
        assertParsedAndQuotedCorrectly();
    }

    public void testExpression() {
        assertParsedAndQuotedCorrectly();
    }

    public void testKeywordsInParenthesesStab() {
        assertParsedAndQuotedCorrectly();
    }

    public void testKeywordsInParenthesesWhenExpressionStab() {
        assertParsedAndQuotedCorrectly();
    }

    public void testMatchedExpressionStab() {
        assertParsedAndQuotedCorrectly();
    }

    public void testMultiStab() {
        assertParsedAndQuotedCorrectly();
    }

    public void testNoParenthesesKeywordsStabExpression() {
        assertParsedAndQuotedCorrectly();
    }

    public void testNoParenthesesManyArgumentsStabExpression() {
        assertParsedAndQuotedCorrectly();
    }

    public void testNoParenthesesWhenStab() {
        assertParsedAndQuotedCorrectly();
    }

    public void testParentheticalStabInNoParenthesesStabSignature() {
        assertParsedAndQuotedCorrectly();
    }

    public void testPositionalsAndKeywordsInParenthesesStab() {
        assertParsedAndQuotedCorrectly();
    }

    public void testPositionalsAndKeywordsInParenthesesWhenExpressionStab() {
        assertParsedAndQuotedCorrectly();
    }

    public void testStab() {
        assertParsedAndQuotedCorrectly();
    }

    public void testStabEndOfExpressionExpressionListEndOfExpression() {
        assertParsedAndQuotedCorrectly();
    }

    public void testStabExpression() {
        assertParsedAndQuotedCorrectly();
    }

    public void testStabExpressionList() {
        assertParsedAndQuotedCorrectly();
    }

    public void testStabMultiEndOfExpressionStab() {
        assertParsedAndQuotedCorrectly();
    }

    public void testUnqualifiedNoParenthesesManyArgumentsCallStabExpression() {
        assertParsedAndQuotedCorrectly();
    }

    public void testSpliceOnStab() {
        assertParsedAndQuotedCorrectly();
    }

    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/parenthetical_stab_parsing_test_case";
    }
}
