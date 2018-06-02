package org.elixir_lang.parser_definition;

import static org.elixir_lang.Level.V_1_6;
import static org.elixir_lang.test.ElixirVersion.elixirSdkLevel;

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
        // Elixir injects the line as 1 when there are comments, but not where there are no comments, which can't be emulated
        if (elixirSdkLevel().compareTo(V_1_6) < 0) {
            assertParsedAndQuotedCorrectly();
        }
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
        assertParsedWithErrors(false);
    }

    public void testMultipleStringsOnLine() {
        assertParsedWithErrors(false);
    }

    public void testNoEOLAtEOF() {
        assertParsedAndQuotedCorrectly();
    }

    public void testParentheses() {
        assertParsedAndQuotedCorrectly();
    }

    public void testPygment() {
        assertParsedWithErrors(false);
    }

    /* The pygment example text contains intentional errors to ensure that pygment can handle errors, but to check
       completeness of the parser, the text needs to be error free so the quoting can be checked */
    public void testPygmentWithoutErrors() {
        assertParsedAndQuotedCorrectly(false);
    }

    public void testRealistic() {
        assertParsedAndQuotedCorrectly();
    }

    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/general_parsing_test_case";
    }
}
