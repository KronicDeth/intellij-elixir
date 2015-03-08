package org.elixir_lang.parser_definition;

/**
 * Created by luke.imhoff on 8/3/14.
 */
public class GeneralParsingTestCase extends ParsingTestCase {
    public void testBlankPrefix() {
        assertParsedAndQuotedCorrectly();
    }

    public void testCharToken() {
        doTest(true);
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
        doTest(true);
    }

    public void testEOLWindows() {
        doTest(true);
    }

    public void testMultipleNumbersOnLine() {
        doTest(true);
    }

    public void testMultipleStringsOnLine() {
        doTest(true);
    }

    public void testNumber() {
        doTest(true);
    }

    public void testNoEOLAtEOF() {
        doTest(true);
    }

    public void testParentheses() {
        doTest(true);
    }

    public void testPygment() {
        doTest(true);
    }

    public void testRealistic() {
        doTest(true);
    }

    public void testSigilTerminatorEscape() {
        doTest(true);
    }

    public void testValidEscapeSequences() {
        doTest(true);
    }

    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/general_parsing_test_case";
    }
}
