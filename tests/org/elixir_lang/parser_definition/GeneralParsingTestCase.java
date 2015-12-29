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
        assertParsedWithErrors();
    }

    public void testMultipleStringsOnLine() {
        assertParsedWithErrors();
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

    /* The pygment example text contains intentional errors to ensure that pygment can handle errors, but to check
       completeness of the parser, the text needs to be error free so the quoting can be checked */
    public void testPygmentWithoutErrors() {
        assertParsedAndQuotedCorrectly();
    }

    public void testRealistic() {
        assertParsedAndQuotedCorrectly();
    }

    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/general_parsing_test_case";
    }
}
