package org.elixir_lang.parser_definition;

/**
 * Created by luke.imhoff on 8/7/14.
 */
public class CharListLineParsingTestCase extends ParsingTestCase {
    public void testEmpty() {
        assertParsedAndQuotedCorrectly();
    }

    public void testEmptyHexadecimalEscapeSequence() {
        assertParsedAndQuotedAroundExit();
    }

    public void testEmptyUnicodeEscapeSequence() {
        assertParsedAndQuotedAroundExit();
    }

    public void testEscapeSequences() {
        assertParsedAndQuotedCorrectly();
    }

    public void testInterpolation() {
        assertParsedAndQuotedCorrectly();
    }

    public void testMultiline() {
        assertParsedAndQuotedCorrectly();
    }

    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/char_list_line_parsing_test_case";
    }
}
