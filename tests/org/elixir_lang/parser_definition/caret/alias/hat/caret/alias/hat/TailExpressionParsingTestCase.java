package org.elixir_lang.parser_definition.caret.alias.hat.caret.alias.hat;

/**
 * Created by luke.imhoff on 12/26/14.
 */
public class TailExpressionParsingTestCase extends ParsingTestCase {
    public void testNoParenthesesCall() {
        assertParsedAndQuotedCorrectly();
    }

    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/tail_expression_parsing_test_case";
    }
}
