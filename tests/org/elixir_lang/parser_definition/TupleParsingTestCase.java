package org.elixir_lang.parser_definition;

public class TupleParsingTestCase extends ParsingTestCase {
    public void testEmpty() {
        assertParsedAndQuotedCorrectly();
    }

    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/tuple_parsing_test_case";
    }
}
