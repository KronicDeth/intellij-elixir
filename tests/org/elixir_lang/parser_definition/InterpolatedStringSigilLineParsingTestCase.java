package org.elixir_lang.parser_definition;

/**
 * Created by kadie.enheduanna.inanna on 8/8/14.
 */
public class InterpolatedStringSigilLineParsingTestCase extends ParsingTestCase {
    /** An escaped EOL immediately before an interpolation, where below 1.12 neither contributes a segment. */
    public void testEscapedEOLBeforeInterpolation() {
        assertParsedAndQuotedCorrectly();
    }

    /** The same trailing the interpolation, so the final flush decides rather than the interpolation one. */
    public void testEscapedEOLAfterInterpolation() {
        assertParsedAndQuotedCorrectly();
    }

    public void testBraces() {
        assertParsedAndQuotedCorrectly();
    }

    public void testBrackets() {
        assertParsedAndQuotedCorrectly();
    }

    public void testChevrons() {
        assertParsedAndQuotedCorrectly();
    }

    public void testDoubleQuotes() {
        assertParsedAndQuotedCorrectly();
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

    public void testSingleQuotes() {
        assertParsedAndQuotedCorrectly();
    }

    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/interpolated_string_sigil_line_parsing_test_case";
    }
}
