package org.elixir_lang.parser_definition;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by luke.imhoff on 8/8/14.
 */
public class LiteralWordsLineParsingTestCase extends ParsingTestCase {
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

    public void testEscapeSequences()
            throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException,
            IllegalAccessException {
        setProjectSdkFromEbinDirectory();

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
        return super.getTestDataPath() + "/literal_words_line_parsing_test_case";
    }
}
