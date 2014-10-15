package org.elixir_lang.parser_definition;

/**
 * Created by luke.imhoff on 8/3/14.
 */
public class GeneralParsingTestCase extends ParsingTestCase {
    public void testBadCharacterPrefix() {
        doTest(true);
    }

    public void testBlankPrefix() {
        doTest(true);
    }

    public void testCommentAfterNumber() {
        doTest(true);
    }

    public void testComments() {
        doTest(true);
    }

    public void testEmpty() {
        doTest(true);
    }

    public void testMultipleNumbersOnLine() {
        doTest(true);
    }

    public void testMultipleStringsOnLine() {
        doTest(true);
    }

    public void testNoEOLAtEOF() {
        doTest(true);
    }

    public void testRealistic() {
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
