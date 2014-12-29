package org.elixir_lang.parser_definition;

/**
 * Created by luke.imhoff on 12/24/14.
 */
public class MatchedExpressionParsingTestCase extends ParsingTestCase {
    public void testAlias() {
        doTest(true);
    }

    public void testCharList() {
        doTest(true);
    }

    public void testCharListHeredoc() {
        doTest(true);
    }

    public void testCharToken() {
        doTest(true);
    }

    public void testEmptyBlock() {
        doTest(true);
    }

    public void testFalse() {
        doTest(true);
    }

    public void testIdentifier() {
        doTest(true);
    }

    public void testList() {
        doTest(true);
    }

    public void testNil() {
        doTest(true);
    }

    public void testNumber() {
        doTest(true);
    }

    public void testSigil() {
        doTest(true);
    }

    public void testString() {
        doTest(true);
    }

    public void testStringHeredoc() {
        doTest(true);
    }

    public void testTrue() {
        doTest(true);
    }

    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/matched_expression_parsing_test_case";
    }
}
