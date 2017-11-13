package org.elixir_lang.eex.lexer.look_ahead;

import org.elixir_lang.eex.lexer.LookAhead;
import org.elixir_lang.eex.lexer.Flex;
import org.junit.Ignore;

@Ignore("abstract")
public class Test extends org.elixir_lang.flex_lexer.Test<LookAhead> {
    /*
     * Constants
     */

    public static final int INITIAL_STATE = Flex.YYINITIAL;

    /*
     * Methods
     */

    @Override
    protected LookAhead lexer() {
        return new LookAhead();
    }

    @Override
    protected int initialState() {
        return INITIAL_STATE;
    }
}
