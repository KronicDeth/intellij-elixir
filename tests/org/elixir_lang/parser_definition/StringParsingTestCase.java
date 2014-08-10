package org.elixir_lang.parser_definition;

/**
 * Created by luke.imhoff on 8/7/14.
 */
public class StringParsingTestCase extends ParsingTestCase {
    public void testEmpty() {
        doTest(true);
    }

    public void testEscapedNewline() {
        doTest(true);
    }

    public void testEscapedSingleQuote() {
        doTest(true);
    }

    public void testMultiline() {
        doTest(true);
    }

    public void testUnescapedNewline() {
        doTest(true);
    }

    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/string_parsing_test_case";
    }
}
