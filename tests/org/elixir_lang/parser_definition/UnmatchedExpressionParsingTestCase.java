package org.elixir_lang.parser_definition;

public class UnmatchedExpressionParsingTestCase extends ParsingTestCase {
    public void testUnqualifiedNoArgumentsBlock() {
        assertParsedAndQuotedCorrectly();
    }

    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/unmatched_expression_parsing_test_case";
    }
}
