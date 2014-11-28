package org.elixir_lang.parser_definition;

/**
 * Created by luke.imhoff on 8/3/14.
 */
public class AccessExpressionParsingTestcase extends ParsingTestCase {
    public void testRegularKeywords() {
        doTest(true);
    }

    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/access_expression_parsing_test_case";
    }
}
