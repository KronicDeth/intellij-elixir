package org.elixir_lang.parser_definition;

/**
 * Created by luke.imhoff on 11/22/14.
 */
public class ListParsingTestCase extends ParsingTestCase {
    public void testAliasWhitespaceColon() {
        doTest(true);
    }

    public void testCharListWhitespaceColon() {
        doTest(true);
    }

    public void testEmpty() {
        assertParsedAndQuotedCorrectly();
    }

    public void testIdentifierWhitespaceColon() {
        doTest(true);
    }

    public void testKeywordKey() {
        doTest(true);
    }

    public void testKeywordKeyEOLColon() {
        doTest(true);
    }

    public void testKeywordPair() {
        doTest(true);
    }

    public void testStringWhitespaceColon() {
        doTest(true);
    }

    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/list_parsing_test_case";
    }
}
