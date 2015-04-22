package org.elixir_lang.parser_definition;

/**
 * Created by luke.imhoff on 11/22/14.
 */
public class ListParsingTestCase extends ParsingTestCase {
    public void testAliasColon() {
        assertParsedAndQuotedCorrectly();
    }

    public void testDotDotDotColon() {
        assertParsedAndQuotedCorrectly();
    }

    public void testAliasWhitespaceColon() {
        assertParsedWithErrors();
    }

    public void testCharListColon() {
        assertParsedAndQuotedCorrectly();
    }

    public void testCharListWhitespaceColon() {
        assertParsedWithErrors();
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

    public void testIdentifierColon() {
        assertParsedAndQuotedCorrectly();
    }

    public void testIdentifierWhitespaceColon() {
        assertParsedWithErrors();
    }

    public void testKeywordKey() {
        assertParsedAndQuotedCorrectly();
    }

    public void testKeywordKeyEOLColon() {
        assertParsedWithErrors();
    }

    public void testKeywordPair() {
        assertParsedAndQuotedCorrectly();
    }

    public void testKeywordValue() {
        assertParsedAndQuotedCorrectly();
    }

    public void testMatchedCallOperationKeywordValue() {
        assertParsedAndQuotedAroundError();
    }

    public void testStringColon() {
        assertParsedAndQuotedCorrectly();
    }

    public void testStringWhitespaceColon() {
        assertParsedWithErrors();
    }

    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/list_parsing_test_case";
    }
}
