package org.elixir_lang.parser_definition;

public class ParentheticalStabParsingTestCase extends ParsingTestCase {
    public void testBlock() {
        assertParsedAndQuotedCorrectly();
    }

    /**
     * A solitary `not`/`!` is wrapped in `__block__` in every block position below Elixir 1.15.0 and
     * only inside parentheses from 1.15.0, so which of these get a wrapper is version-dependent.
     * Quoting is compared against the reference quoter for the Elixir the leg runs, so these assert
     * whichever answer is right there rather than pinning one of the two.
     */
    public void testParensNot() {
        assertParsedAndQuotedCorrectly();
    }

    public void testSolitaryNotAcrossPositions() {
        assertParsedAndQuotedCorrectly();
    }

    public void testSolitaryNotInParentheses() {
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
        assertParsedAndQuotedCorrectly(false);
    }

    public void testMatchedExpressionStab() {
        assertParsedAndQuotedCorrectly(false);
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
        assertParsedAndQuotedCorrectly(false);
    }

    public void testStab() {
        assertParsedAndQuotedCorrectly();
    }

    public void testStabEndOfExpressionExpressionListEndOfExpression() {
        assertParsedAndQuotedCorrectly();
    }

    public void testStabExpression() {
        assertParsedAndQuotedCorrectly(false);
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
