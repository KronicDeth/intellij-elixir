package org.elixir_lang.parser_definition;

/**
 * Created by luke.imhoff on 8/16/14.
 */
public class SigilParsingTestCase extends ParsingTestCase {
    public void testInterpolatingDoubleQuotedHeredoc() {
        doTest(true);
    }

    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/sigil_parsing_test_case";
    }
}
