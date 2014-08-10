package org.elixir_lang.parser_definition;

/**
 * Created by luke.imhoff on 8/7/14.
 */
public class SingleQuotedStringParsingTestCase extends ParsingTestCase {
    public void testEscapedNewline() {
        doTest(true);
    }

    public void testMultiline() {
        doTest(true);
    }

    public void testUnescapedNewline() {
        doTest(true);
    }

    public void testEscapedSingleQuote() {
        doTest(true);
    }

    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/single_quoted_string_parsing_test_case";
    }
}
