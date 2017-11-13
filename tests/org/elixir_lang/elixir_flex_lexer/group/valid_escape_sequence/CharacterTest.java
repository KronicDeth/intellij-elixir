package org.elixir_lang.elixir_flex_lexer.group.valid_escape_sequence;

import org.elixir_lang.ElixirFlexLexer;
import org.elixir_lang.psi.ElixirTypes;
import org.jetbrains.annotations.NotNull;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by luke.imhoff on 10/14/14.
 */
@RunWith(Theories.class)
public class CharacterTest extends org.elixir_lang.elixir_flex_lexer.Test {
    /*
     * Constants
     */

    public static final int MINIMUM_PRINTABLE_CHAR = 32;
    public static final int MAXIMUM_PRINTABLE_CHAR = 126;
    // -1 for x
    public static final int PRINTABLE_CHAR_COUNT = MAXIMUM_PRINTABLE_CHAR - MINIMUM_PRINTABLE_CHAR + 1 - 2;

    /*
     * Methods
     */

    @DataPoints
    public static CharSequence[] dataPoints() {
        CharSequence[] charSequences = new CharSequence[PRINTABLE_CHAR_COUNT];

        int i = 0;
        for (char c = MINIMUM_PRINTABLE_CHAR; c <= MAXIMUM_PRINTABLE_CHAR; c++) {
            if (c != 'x' && c != 'u') {
                charSequences[i++] = "\\" + c;
            }
        }

        return charSequences;
    }

    @Override
    protected void start(@NotNull CharSequence charSequence) {
        // start of "\"" + promoter to trigger GROUP state
        CharSequence fullCharSequence = "\"" + charSequence;
        super.start(fullCharSequence);
        // consume "
        lexer.advance();
    }

    @Theory
    public void validCharacterCode(@NotNull CharSequence charSequence) throws IOException {
        start(charSequence);

        lexer.advance();

        assertEquals(ElixirTypes.ESCAPE, lexer.getTokenType());
        assertEquals(ElixirFlexLexer.ESCAPE_SEQUENCE, lexer.getState());

        lexer.advance();

        assertEquals(ElixirTypes.ESCAPED_CHARACTER_TOKEN, lexer.getTokenType());
        assertEquals(ElixirFlexLexer.GROUP, lexer.getState());

        lexer.advance();

        assertTrue("Failure: expected all of \"" + charSequence + "\" to be consumed", lexer.getTokenType() == null);
    };
}
