package org.elixir_lang.parser_definition;

import static org.elixir_lang.Level.V_1_3;
import static org.elixir_lang.test.ElixirVersion.elixirSdkLevel;

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
        if (elixirSdkLevel().supportsOpenHexadecimalEscapeSequence) {
            assertParsedAndQuotedCorrectly();
        } else {
            assertParsedAndQuotedAroundError();
        }
    }

    public void testEnclosedHexadecimalEscapeSequence() {
        if (elixirSdkLevel().compareTo(V_1_3) < 0) {
            assertParsedAndQuotedCorrectly();
        } else {
            assertParsedAndQuotedAroundError();
        }
    }


    @Override
    protected String getTestDataPath() {
        return super.getTestDataPath() + "/char_token_parsing_test_case";
    }
}
