package org.elixir_lang.parser_definition;

/**
 * Created by luke.imhoff on 8/8/14.
 */
public class StringHeredocParsingTestCase extends ParsingTestCase {
    public void testEmpty() {
        assertParsedAndQuotedAroundError();
    }

    public void testEmptyHexadecimalEscapeSequence() {
        assertParsedAndQuotedAroundExit();
    }

    public void testEscapeSequences() {
        assertParsedAndQuotedCorrectly();
    }

    public void testInterpolation() {
        assertParsedAndQuotedCorrectly();
    }

    public void testMinimal() {
        assertParsedAndQuotedCorrectly();
    }

    public void testWhitespaceEndPrefix() {
        assertParsedAndQuotedCorrectly();
    }

    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/string_heredoc_parsing_test_case";
    }
}
