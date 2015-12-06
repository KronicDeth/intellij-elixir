package org.elixir_lang.parser_definition;

/**
 * Created by luke.imhoff on 11/22/14.
 */
public class ListParsingTestCase extends ParsingTestCase {
    public void testEmptyParentheses() {
        assertParsedAndQuotedCorrectly();
    }

    public void testMatchedExpression() {
        assertParsedAndQuotedCorrectly();
    }

    public void testAdditionKeywordValue() {
        assertParsedAndQuotedCorrectly();
    }

    public void testDotCallKeywordValue() {
        assertParsedAndQuotedCorrectly();
    }

    public void testAliasColon() {
        assertParsedAndQuotedCorrectly();
    }

    public void testDotDotDotColon() {
        assertParsedAndQuotedCorrectly();
    }

    public void testCharListColon() {
        assertParsedAndQuotedCorrectly();
    }

    public void testEmpty() {
        assertParsedAndQuotedCorrectly();
    }

    public void testEmptyBinary() {
        assertParsedAndQuotedCorrectly();
    }

    public void testEmptyMapColon() {
        assertParsedAndQuotedCorrectly();
    }

    public void testEmptyTupleColon() {
        assertParsedAndQuotedCorrectly();
    }

    public void testEOLBeforeClosingBracket() {
        assertParsedAndQuotedCorrectly();
    }

    public void testIdentifierColon() {
        assertParsedAndQuotedCorrectly();
    }

    public void testKeywordKey() {
        assertParsedAndQuotedCorrectly();
    }

    public void testKeywordPair() {
        assertParsedAndQuotedCorrectly();
    }

    public void testKeywordValue() {
        assertParsedAndQuotedAroundError();
    }

    public void testMatchedCallOperationKeywordValue() {
        assertParsedAndQuotedAroundError();
    }

    public void testMatchedDotMatchedCallOperationKeywordValue() {
        assertParsedAndQuotedAroundError();
    }

    public void testStringColon() {
        assertParsedAndQuotedCorrectly();
    }

    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/list_parsing_test_case";
    }
}
