package org.elixir_lang.parser_definition;

public class MapOperationParsingTestCase extends ParsingTestCase {
    public void testEmpty() {
        assertParsedAndQuotedCorrectly();
    }

    public void testInterpolation() {
        assertParsedAndQuotedCorrectly();
    }

    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/map_operation_parsing_test_case";
    }
}
