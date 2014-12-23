package org.elixir_lang.parser_definition.at;

public abstract class ParsingTestCase extends org.elixir_lang.parser_definition.ParsingTestCase {
    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/at";
    }
}
