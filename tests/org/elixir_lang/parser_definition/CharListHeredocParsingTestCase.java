package org.elixir_lang.parser_definition;

/**
 * Created by luke.imhoff on 8/8/14.
 */
public class CharListHeredocParsingTestCase extends ParsingTestCase {
    public void testEmpty() {
        assertParsedAndQuotedAroundError();
    }

    public void testEmptyHexadecimalEscapeSequence() throws Exception {
        registerProjectFileIndex();

        assertParsedAndQuotedAroundExit();
    }

    public void testEmptyUnicodeEscapeSequence() throws Exception {
        registerProjectFileIndex();

        assertParsedAndQuotedAroundExit();
    }

    public void testEscapeSequences() throws Exception {
        registerProjectFileIndex();

        assertParsedAndQuotedCorrectly();
    }

    public void testInterpolation() throws Exception {
        setProjectSdkFromEbinDirectory();

        assertParsedAndQuotedCorrectly();
    }

    public void testMinimal() {
        assertParsedAndQuotedCorrectly();
    }

    public void testWhitespaceEndPrefix() throws Exception {
        registerProjectFileIndex();

        assertParsedAndQuotedAroundError();
    }

    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/char_list_heredoc_parsing_test_case";
    }
}
