package org.elixir_lang.parser_definition.caret.expression;

public abstract class ParsingTestCase extends org.elixir_lang.parser_definition.caret.ParsingTestCase {
    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/expression";
    }
}
