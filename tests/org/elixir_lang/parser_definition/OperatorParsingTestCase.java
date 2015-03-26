package org.elixir_lang.parser_definition;

/**
 * Created by luke.imhoff on 8/3/14.
 */
public class OperatorParsingTestCase extends ParsingTestCase {
    public void testAddition() {
        assertParsedAndQuotedCorrectly();
    }

    public void testAnd() {
        assertParsedAndQuotedCorrectly();
    }

    public void testArrow() {
        assertParsedAndQuotedCorrectly();
    }

    public void testAssociation() {
        assertParsedAndQuotedCorrectly();
    }

    public void testAt() {
        assertParsedAndQuotedCorrectly();
    }

    public void testAtNoParenthesesExpression() {
        assertParsedAndQuotedCorrectly();
    }

    public void testBitString() {
        assertParsedAndQuotedCorrectly();
    }

    public void testCapture() {
        assertParsedAndQuotedCorrectly();
    }

    public void testCaptureNumber() {
        assertParsedAndQuotedCorrectly();
    }

    public void testComparison() {
        assertParsedAndQuotedCorrectly();
    }

    public void testDot() {
        assertParsedAndQuotedCorrectly();
    }

    public void testHat() {
        assertParsedAndQuotedCorrectly();
    }

    public void testIn() {
        assertParsedAndQuotedCorrectly();
    }

    public void testInMatch() {
        assertParsedAndQuotedCorrectly();
    }

    public void testMap() {
        assertParsedAndQuotedCorrectly();
    }

    public void testMatch() {
        assertParsedAndQuotedCorrectly();
    }

    public void testMultiplication() {
        assertParsedAndQuotedCorrectly();
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

    public void testStab() {
        assertParsedAndQuotedCorrectly();
    }

    public void testStruct() {
        assertParsedAndQuotedCorrectly();
    }

    public void testTuple() {
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

    public void testUnaryNoParenthesesExpression() {
        assertParsedAndQuotedCorrectly();
    }

    public void testWhen() {
        assertParsedAndQuotedCorrectly();
    }

    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/operator_parsing_test_case";
    }
}
