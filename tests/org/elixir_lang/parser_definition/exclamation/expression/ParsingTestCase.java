package org.elixir_lang.parser_definition.exclamation.expression;

public abstract class ParsingTestCase extends org.elixir_lang.parser_definition.exclamation.ParsingTestCase {
    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/expression";
    }
}
