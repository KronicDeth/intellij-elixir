package org.elixir_lang.parser_definition.capture.alias;

public abstract class ParsingTestCase extends org.elixir_lang.parser_definition.capture.ParsingTestCase {
    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/alias";
    }
}
