package org.elixir_lang.elixir_flex_lexer;

import org.elixir_lang.ElixirFlexLexer;
import org.junit.Before;

import java.io.IOException;
import java.io.Reader;

/**
 * Created by luke.imhoff on 9/28/14.
 */
public abstract class Test {
    /*
     * Constants
     */

    public static final int INITIAL_STATE = ElixirFlexLexer.YYINITIAL;

    /*
     * Fields
     */

    protected ElixirFlexLexer flexLexer;

    /*
     * Methods
     */

    protected int initialState() {
        return INITIAL_STATE;
    }

    protected void reset(CharSequence charSequence) throws IOException {
        reset(charSequence, initialState());
    }

    protected void reset(CharSequence charSequence, int initialState) throws IOException {
        flexLexer.reset(charSequence, 0, charSequence.length(), initialState);
    }

    /*
     * Callbacks
     */

    @Before
    public void setUp() {
        flexLexer = new ElixirFlexLexer((Reader) null);
    }
}
