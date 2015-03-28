package org.elixir_lang.parser_definition;

public class MatchedExpressionDotAliasParsingTestcase extends ParsingTestCase {
    public void testAddition() {
        assertParsedAndQuotedAroundError();
    }

    public void testAnd() {
        assertParsedAndQuotedAroundError();
    }

    public void testArrow() {
        assertParsedAndQuotedAroundError();
    }

    public void testAt() {
        assertParsedAndQuotedCorrectly();
    }

    public void testCapture() {
        assertParsedAndQuotedCorrectly();
    }

    public void testComparison() {
        assertParsedAndQuotedAroundError();
    }

    public void testHat() {
        assertParsedAndQuotedAroundError();
    }

    public void testIn() {
        assertParsedAndQuotedAroundError();
    }

    public void testInMatch() {
        assertParsedAndQuotedAroundError();
    }

    public void testMatch() {
        assertParsedAndQuotedAroundError();
    }

    public void testMultiplication() {
        assertParsedAndQuotedAroundError();
    }

    public void testOr() {
        assertParsedAndQuotedAroundError();
    }

    public void testPipe() {
        assertParsedAndQuotedAroundError();
    }

    public void testRelational() {
        assertParsedAndQuotedAroundError();
    }

    public void testTwo() {
        assertParsedAndQuotedAroundError();
    }

    public void testType() {
        assertParsedAndQuotedAroundError();
    }

    public void testUnary() {
        assertParsedAndQuotedCorrectly();
    }

    public void testValid() {
        assertParsedAndQuotedCorrectly();
    }

    public void testWhen() {
        assertParsedAndQuotedAroundError();
    }

    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/matched_expression_dot_alias_parsing_test_case";
    }
}
