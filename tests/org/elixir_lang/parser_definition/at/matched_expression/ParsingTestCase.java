package org.elixir_lang.parser_definition.at.matched_expression;

public abstract class ParsingTestCase extends org.elixir_lang.parser_definition.at.ParsingTestCase {
    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/matched_expression";
    }
}
