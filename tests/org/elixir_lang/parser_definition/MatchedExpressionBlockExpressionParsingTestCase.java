package org.elixir_lang.parser_definition;

public class MatchedExpressionBlockExpressionParsingTestCase extends ParsingTestCase {
    public void testHat() {
        assertParsedAndQuotedCorrectly();
    }

    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/matched_expression_block_expression_parsing_test_case";
    }
}
