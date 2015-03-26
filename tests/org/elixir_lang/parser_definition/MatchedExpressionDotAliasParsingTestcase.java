package org.elixir_lang.parser_definition;

public class MatchedExpressionDotAliasParsingTestcase extends ParsingTestCase {
    public void testAddition() {
        assertParsedAndQuotedCorrectly();
    }

    public void testAnd() {
        assertParsedAndQuotedCorrectly();
    }

    public void testArrow() {
        assertParsedAndQuotedCorrectly();
    }

    public void testAt() {
        assertParsedAndQuotedCorrectly();
    }

    public void testCapture() {
        assertParsedAndQuotedCorrectly();
    }

    public void testComparison() {
        assertParsedAndQuotedCorrectly();
    }

    public void testHat() {
        assertParsedAndQuotedAroundError();
    }

    public void testIn() {
        assertParsedAndQuotedCorrectly();
    }

    public void testInMatch() {
        assertParsedAndQuotedCorrectly();
    }

    public void testMatch() {
        assertParsedAndQuotedCorrectly();
    }

    public void testMultiplication() {
        assertParsedAndQuotedAroundError();
    }

    public void testOr() {
        assertParsedAndQuotedCorrectly();
    }

    public void testPipe() {
        assertParsedAndQuotedCorrectly();
    }

    public void testRelational() {
        assertParsedAndQuotedCorrectly();
    }

    public void testTwo() {
        assertParsedAndQuotedCorrectly();
    }

    public void testType() {
        assertParsedAndQuotedCorrectly();
    }

    public void testUnary() {
        assertParsedAndQuotedCorrectly();
    }

    public void testValid() {
        assertParsedAndQuotedCorrectly();
    }

    public void testWhen() {
        assertParsedAndQuotedCorrectly();
    }

    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/matched_expression_dot_alias_parsing_test_case";
    }
}
