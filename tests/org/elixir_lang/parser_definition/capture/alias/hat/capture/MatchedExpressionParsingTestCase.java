package org.elixir_lang.parser_definition.capture.alias.hat.capture;

/**
 * Created by luke.imhoff on 12/24/14.
 */
public class MatchedExpressionParsingTestCase extends ParsingTestCase {
    public void testAlias() {
        assertParsedAndQuotedCorrectly();
    }

    public void testCharList() {
        assertParsedAndQuotedCorrectly();
    }

    public void testCharListHeredoc() {
        assertParsedAndQuotedCorrectly();
    }

    public void testCharToken() {
        assertParsedAndQuotedCorrectly();
    }

    public void testEmptyBlock() {
        assertParsedAndQuotedCorrectly();
    }

    public void testFalse() {
        assertParsedAndQuotedCorrectly();
    }

    public void testIdentifier() {
        assertParsedAndQuotedCorrectly();
    }

    public void testList() {
        assertParsedAndQuotedCorrectly();
    }

    public void testNil() {
        assertParsedAndQuotedCorrectly();
    }

    public void testNumber() {
        assertParsedAndQuotedCorrectly();
    }

    public void testSigil() {
        assertParsedAndQuotedCorrectly();
    }

    public void testString() {
        assertParsedAndQuotedCorrectly();
    }

    public void testStringHeredoc() {
        assertParsedAndQuotedCorrectly();
    }

    public void testTrue() {
        assertParsedAndQuotedCorrectly();
    }

    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/matched_expression_parsing_test_case";
    }
}
