package org.elixir_lang.parser_definition;

public class MatchedExpressionDotAliasParsingTestcase extends ParsingTestCase {
    public void testAddition() {
        doTest(true);
    }

    public void testAnd() {
        doTest(true);
    }

    public void testArrow() {
        doTest(true);
    }

    public void testAt() {
        assertParsedAndQuotedCorrectly();
    }

    public void testCapture() {
        assertParsedAndQuotedCorrectly();
    }

    public void testComparison() {
        doTest(true);
    }

    public void testHat() {
        assertParsedAndQuotedCorrectly();
    }

    public void testIn() {
        doTest(true);
    }

    public void testInMatch() {
        doTest(true);
    }

    public void testMatch() {
        doTest(true);
    }

    public void testMultiplication() {
        assertParsedAndQuotedCorrectly();
    }

    public void testOr() {
        doTest(true);
    }

    public void testPipe() {
        doTest(true);
    }

    public void testRelational() {
        doTest(true);
    }

    public void testTwo() {
        doTest(true);
    }

    public void testType() {
        doTest(true);
    }

    public void testUnary() {
        assertParsedAndQuotedCorrectly();
    }

    public void testValid() {
        doTest(true);
    }

    public void testWhen() {
        doTest(true);
    }

    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/matched_expression_dot_alias_parsing_test_case";
    }
}
