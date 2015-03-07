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
        assertParsedWithError();
    }

    public void testCharListColon() {
        assertParsedAndQuotedCorrectly();
    }

    public void testCharListWhitespaceColon() {
        assertParsedWithError();
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
        doTest(true);
    }

    public void testIdentifierWhitespaceColon() {
        assertParsedWithError();
    }

    public void testKeywordKey() {
        doTest(true);
    }

    public void testKeywordKeyEOLColon() {
        assertParsedWithError();
    }

    public void testKeywordPair() {
        doTest(true);
    }

    public void testStringColon() {
        assertParsedAndQuotedCorrectly();
    }

    public void testStringWhitespaceColon() {
        assertParsedWithError();
    }

    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/list_parsing_test_case";
    }
}
