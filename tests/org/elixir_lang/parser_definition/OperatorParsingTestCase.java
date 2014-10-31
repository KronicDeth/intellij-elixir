package org.elixir_lang.parser_definition;

/**
 * Created by luke.imhoff on 8/3/14.
 */
public class OperatorParsingTestCase extends ParsingTestCase {
    public void testAddition() {
        doTest(true);
    }

    public void testAnd() {
        doTest(true);
    }

    public void testArrow() {
        doTest(true);
    }

    public void testComparison() {
        doTest(true);
    }

    public void testHat() {
        doTest(true);
    }

    public void testMultiplication() {
        doTest(true);
    }

    public void testRelational() {
        doTest(true);
    }

    public void testTwo() {
        doTest(true);
    }

    public void testUnary() {
        doTest(true);
    }

    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/operator_parsing_test_case";
    }
}
