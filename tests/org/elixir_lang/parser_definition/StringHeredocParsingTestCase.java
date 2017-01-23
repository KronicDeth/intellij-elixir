package org.elixir_lang.parser_definition;

/**
 * Created by luke.imhoff on 8/8/14.
 */
public class StringHeredocParsingTestCase extends ParsingTestCase {
    public void testEmpty() {
        assertParsedAndQuotedAroundError();
    }

    public void testEscapeSequences() throws Exception {
        registerProjectFileIndex();

        assertParsedAndQuotedCorrectly();
    }

    public void testInterpolation() throws Exception {
        registerProjectFileIndex();

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
        return super.getTestDataPath() + "/string_heredoc_parsing_test_case";
    }
}
