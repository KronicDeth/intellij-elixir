package org.elixir_lang.parser_definition;

/**
 * Created by luke.imhoff on 9/17/14.
 */
public class CharTokenParsingTestCase extends ParsingTestCase {
    public void testCharacter() {
        assertParsedAndQuotedCorrectly();
    }

    /*
     * Whitespace
     */

    public void testSpace() {
        assertParsedAndQuotedCorrectly();
    }

    public void testTab() {
        assertParsedAndQuotedCorrectly();
    }

    /*
     * escapeSequence
     */

    public void testEscapedEOL() {
        assertParsedAndQuotedCorrectly();
    }

    public void testEscapedCharacter() {
        assertParsedAndQuotedCorrectly();
    }

    /*
     * byte hexadecimalEscapeSequence
     */

    public void testOpenHexadecimalEscapeSequence() {
        assertParsedAndQuotedCorrectly();
    }

    public void testEnclosedHexadecimalEscapeSequence() {
        assertParsedAndQuotedCorrectly();
    }


    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/char_token_parsing_test_case";
    }
}
