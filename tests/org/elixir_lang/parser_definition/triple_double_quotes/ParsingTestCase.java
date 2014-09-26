package org.elixir_lang.parser_definition.triple_double_quotes;

/**
 * Created by luke.imhoff on 8/8/14.
 */
public abstract class ParsingTestCase extends org.elixir_lang.parser_definition.ParsingTestCase {
    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/triple_double_quotes";
    }
}
