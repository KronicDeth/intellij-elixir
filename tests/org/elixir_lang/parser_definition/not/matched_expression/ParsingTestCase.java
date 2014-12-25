package org.elixir_lang.parser_definition.not.matched_expression;

public abstract class ParsingTestCase extends org.elixir_lang.parser_definition.not.ParsingTestCase {
    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/matched_expression";
    }
}
