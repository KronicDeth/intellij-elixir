package org.elixir_lang.elixir_flex_lexer;

import org.elixir_lang.ElixirFlexLexer;
import org.elixir_lang.ElixirLexer;
import org.junit.Ignore;

@Ignore("abstract")
public abstract class Test extends org.elixir_lang.flex_lexer.Test<ElixirLexer> {
    /*
     * Constants
     */

    public static final int INITIAL_STATE = ElixirFlexLexer.YYINITIAL;

    /*
     * Methods
     */

    @Override
    protected ElixirLexer lexer() {
        return new ElixirLexer();
    }

    @Override
    protected int initialState() {
        return INITIAL_STATE;
    }
}
