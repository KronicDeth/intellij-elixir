package org.elixir_lang.parser_definition;

public class BitStringParsingTestCase extends ParsingTestCase {
    public void testEmpty() {
        assertParsedAndQuotedCorrectly();
    }

    public void testNoParenthesesPositionalKeywords() {
        assertParsedAndQuotedAroundError();
    }

    public void testOne() {
        assertParsedAndQuotedCorrectly(false);
    }

    public void testOneWithTrailingComma() {
        assertParsedAndQuotedCorrectly(false);
    }

    public void testTwo() {
        assertParsedAndQuotedCorrectly(false);
    }

    public void testTwoWithKeywords() {
        assertParsedAndQuotedCorrectly(false);
    }

    public void testThree() {
        assertParsedAndQuotedCorrectly(false);
    }

    public void testWhenKeyword() {
        assertParsedAndQuotedAroundError();
    }

    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/bit_string_parsing_test_case";
    }
}
