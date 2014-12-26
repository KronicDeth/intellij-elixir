package org.elixir_lang.parser_definition.capture.alias.hat.capture;

/**
 * Created by luke.imhoff on 12/24/14.
 */
public class TailExpressionParsingTestCase extends ParsingTestCase {
    public void testEmptyParentheses() {
        doTest(true);
    }

    public void testNoParenthesesCall() {
        doTest(true);
    }

    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/tail_expression_parsing_test_case";
    }
}
