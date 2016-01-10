package org.elixir_lang.parser_definition;

/**
 * Created by luke.imhoff on 8/8/14.
 */
public class LiteralStringSigilHeredocParsingTestCase extends ParsingTestCase {
    public void testEmpty() {
        assertParsedAndQuotedAroundError();
    }

    public void testEmptyHexadecimalEscapeSequence() {
        assertParsedAndQuotedCorrectly();
    }

    public void testEmptyUnicodeEscapeSequence() {
        assertParsedAndQuotedCorrectly();
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

    public void testSigilModifiers() {
        assertParsedAndQuotedCorrectly();
    }

    public void testWhitespaceEndPrefix() {
        assertParsedAndQuotedAroundError();
    }

    public void testEmbeddedStringHeredoc() {
        assertParsedAndQuotedCorrectly();
    }

    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/literal_string_sigil_heredoc_parsing_test_case";
    }
}
