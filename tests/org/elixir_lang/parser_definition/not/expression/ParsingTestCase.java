package org.elixir_lang.parser_definition.not.expression;

public abstract class ParsingTestCase extends org.elixir_lang.parser_definition.not.ParsingTestCase {
    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/expression";
    }
}
