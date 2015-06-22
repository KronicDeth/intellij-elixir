package org.elixir_lang.parser_definition;

/**
 * Created by luke.imhoff on 8/3/14.
 */
public class GeneralParsingTestCase extends ParsingTestCase {
    public void testBlankPrefix() {
        assertParsedAndQuotedCorrectly();
    }

    public void testCommentAfterNumber() {
        assertParsedAndQuotedCorrectly();
    }

    public void testComments() {
        assertParsedAndQuotedCorrectly();
    }

    public void testCommentEOL() {
        assertParsedAndQuotedCorrectly();
    }

    public void testEmpty() {
        assertParsedAndQuotedCorrectly();
    }

    public void testEOLPosix() {
        assertParsedAndQuotedCorrectly();
    }

    public void testEOLWindows() {
        assertParsedAndQuotedCorrectly();
    }

    public void testMultipleNumbersOnLine() {
        assertParsedAndQuotedAroundError();
    }

    public void testMultipleStringsOnLine() {
        assertParsedAndQuotedAroundError();
    }

    public void testNoEOLAtEOF() {
        assertParsedAndQuotedCorrectly();
    }

    public void testParentheses() {
        assertParsedAndQuotedCorrectly();
    }

    public void testPygment() {
        assertParsedWithErrors();
    }

    public void testRealistic() {
        assertParsedAndQuotedCorrectly();
    }

    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/general_parsing_test_case";
    }
}
