package org.elixir_lang.parser_definition;

import org.elixir_lang.parser_definition.ParsingTestCase;

/**
 * Created by luke.imhoff on 8/8/14.
 */
public class HeredocParsingTestCase extends ParsingTestCase {
    public void testEmpty() {
        doTest(true);
    }

    public void testEscapeSequences() {
        doTest(true);
    }

    public void testMinimal() {
        doTest(true);
    }

    public void testWhitespaceEndPrefix() {
        doTest(true);
    }

    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/heredoc_parsing_test_case";
    }
}
