package org.elixir_lang.parser_definition.capture;

import org.junit.Ignore;

/**
 * Created by luke.imhoff on 8/8/14.
 */
@Ignore("abstract")
public abstract class ParsingTestCase extends org.elixir_lang.parser_definition.ParsingTestCase {
    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/capture";
    }
}
