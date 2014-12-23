package org.elixir_lang.parser_definition.at.expression;

public abstract class ParsingTestCase extends org.elixir_lang.parser_definition.at.ParsingTestCase {
    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/expression";
    }
}
