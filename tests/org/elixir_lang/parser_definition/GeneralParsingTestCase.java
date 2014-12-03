package org.elixir_lang.parser_definition;

/**
 * Created by luke.imhoff on 8/3/14.
 */
public class GeneralParsingTestCase extends ParsingTestCase {
    public void testBlankPrefix() {
        doTest(true);
    }

    public void testCharToken() {
        doTest(true);
    }

    public void testCommentAfterNumber() {
        doTest(true);
    }

    public void testComments() {
        doTest(true);
    }

    public void testCommentEOL() {
        doTest(true);
    }

    public void testEmpty() {
        doTest(true);
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

    public void testNoParenthesesExpression() {
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
