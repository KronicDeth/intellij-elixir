package org.elixir_lang.parser_definition;

/**
 * Created by luke.imhoff on 8/8/14.
 */
public class LiteralSigilHeredocParsingTestCase extends ParsingTestCase {
    public void testEmpty() {
        assertParsedAndQuotedAroundError();
    }

    public void testEmptyHexadecimalEscapeSequence() throws Exception {
        registerProjectFileIndex();

        assertParsedAndQuotedCorrectly();
    }

    public void testEmptyUnicodeEscapeSequence() throws Exception {
        registerProjectFileIndex();

        assertParsedAndQuotedCorrectly();
    }

    public void testEscapeSequences() throws Exception {
        setProjectSdkFromEbinDirectory();

        assertParsedAndQuotedCorrectly();
    }

    public void testInterpolation() throws Exception {
        registerProjectFileIndex();

        assertParsedAndQuotedCorrectly();
    }

    public void testMinimal() {
        assertParsedAndQuotedCorrectly();
    }

    public void testSigilModifiers() {
        assertParsedAndQuotedCorrectly();
    }

    public void testWhitespaceEndPrefix() throws Exception {
        registerProjectFileIndex();

        assertParsedAndQuotedAroundError();
    }

    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/literal_sigil_heredoc_parsing_test_case";
    }
}
