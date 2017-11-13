package org.elixir_lang.elixir_flex_lexer.atom_start;

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
 * Created by luke.imhoff on 9/28/14.
 */
@RunWith(Theories.class)
public class WholeTest extends org.elixir_lang.elixir_flex_lexer.Test {
    /*
     * Methods
     */

    @DataPoints
    public static CharSequence[] dataPoints() {
        return new CharSequence[] {
                "!",
                "!=",
                "!==",
                "%",
                "%{}",
                "&",
                "&&",
                "&&&",
                "*",
                "+",
                "++",
                "-",
                "--",
                "->",
                ".",
                "...",
                "/",
                "::",
                "<",
                "<-",
                "<<<",
                "<<>>",
                "<<~",
                "<=",
                "<>",
                "<|>",
                "<~",
                "<~>",
                "=",
                "==",
                "===",
                "=~",
                ">",
                ">=",
                ">>>",
                "@",
                "\\\\",
                "^",
                "^^^",
                "{}",
                "|",
                "|>",
                "||",
                "|||",
                "~>",
                "~>>",
                "~~~"
        };
    }

    @Override
    protected void start(@NotNull CharSequence charSequence) {
        // start to trigger ATOM_START state
        CharSequence fullCharSequence = ":" + charSequence;
        super.start(fullCharSequence);
        // consume ':'
        lexer.advance();
    }

    @Theory
    public void operatorIsWholeAtom(CharSequence charSequence) {
        start(charSequence);

        lexer.advance();

        assertEquals(ElixirTypes.ATOM_FRAGMENT, lexer.getTokenType());
        assertEquals(initialState(), lexer.getState());

        lexer.advance();

        assertTrue("Failure: expected all of \"" + charSequence + "\" to be consumed", lexer.getTokenType() == null);
    }
}
