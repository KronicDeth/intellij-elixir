package org.elixir_lang.parser_definition;

import org.elixir_lang.sdk.elixir.Release;

import static org.elixir_lang.test.ElixirVersion.elixirSdkRelease;

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
        if (elixirSdkRelease().compareTo(Release.V_1_3) < 0) {
            assertParsedAndQuotedCorrectly();
        } else {
            assertParsedAndQuotedAroundError();
        }
    }

    public void testEnclosedHexadecimalEscapeSequence() {
        if (elixirSdkRelease().compareTo(Release.V_1_3) < 0) {
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
