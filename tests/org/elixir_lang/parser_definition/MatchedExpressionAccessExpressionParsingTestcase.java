package org.elixir_lang.parser_definition;

/**
 * Created by luke.imhoff on 8/3/14.
 */
public class MatchedExpressionAccessExpressionParsingTestcase extends ParsingTestCase {
    public void testRegularKeywords() {
        assertParsedAndQuotedCorrectly();
    }

    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/matched_expression_access_expression_parsing_test_case";
    }
}
