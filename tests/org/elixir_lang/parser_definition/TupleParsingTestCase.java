package org.elixir_lang.parser_definition;

public class TupleParsingTestCase extends ParsingTestCase {
    public void testEmpty() {
        assertParsedAndQuotedCorrectly();
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

    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/tuple_parsing_test_case";
    }
}
