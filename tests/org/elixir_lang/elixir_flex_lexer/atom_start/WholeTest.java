package org.elixir_lang.elixir_flex_lexer.atom_start;

import org.elixir_lang.psi.ElixirTypes;
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
    protected void reset(CharSequence charSequence) throws IOException {
        // start to trigger ATOM_START state
        CharSequence fullCharSequence = ":" + charSequence;
        super.reset(fullCharSequence);
        // consume ':'
        flexLexer.advance();
    }

    @Theory
    public void operatorIsWholeAtom(CharSequence charSequence) throws IOException {
        reset(charSequence);

        assertEquals(ElixirTypes.ATOM_FRAGMENT, flexLexer.advance());
        assertEquals(initialState(), flexLexer.yystate());
        assertTrue("Failure: expected all of \"" + charSequence + "\" to be consumed", flexLexer.advance() == null);
    }
}
