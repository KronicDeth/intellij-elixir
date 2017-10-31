package org.elixir_lang.parser_definition;

public class TupleParsingTestCase extends ParsingTestCase {
    public void testEmpty() {
        assertParsedAndQuotedCorrectly();
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

    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/tuple_parsing_test_case";
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        registerProjectFileIndex();
    }
}
