package org.elixir_lang.parser_definition;

public class BitStringParsingTestCase extends ParsingTestCase {
    public void testEmpty() {
        assertParsedAndQuotedCorrectly();
    }

    public void testNoParenthesesPositionalKeywords() {
        assertParsedAndQuotedAroundError();
    }

    public void testOne() {
        assertParsedAndQuotedCorrectly();
    }

    public void testOneWithTrailingComma() {
        assertParsedAndQuotedCorrectly();
    }

    public void testTwo() {
        assertParsedAndQuotedCorrectly();
    }

    public void testTwoWithKeywords() {
        assertParsedAndQuotedCorrectly();
    }

    public void testThree() {
        assertParsedAndQuotedCorrectly();
    }

    public void testWhenKeyword() {
        assertParsedAndQuotedAroundError();
    }

    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/bit_string_parsing_test_case";
    }
}
