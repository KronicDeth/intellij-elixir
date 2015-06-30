package org.elixir_lang.parser_definition;

public class MatchedExpressionHatUnmatchedExpressionParsingTestCase extends ParsingTestCase {
    public void testUnaryNonNumeric() {
        assertParsedAndQuotedCorrectly();
    }

    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/matched_expression_hat_unmatched_expression_parsing_test_case";
    }
}
