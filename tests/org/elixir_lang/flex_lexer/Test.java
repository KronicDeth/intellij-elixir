package org.elixir_lang.flex_lexer;

import com.intellij.lexer.Lexer;
import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Ignore;

import java.io.IOException;


@Ignore("abstract")
public abstract class Test<L extends Lexer> {
    /*
     * Fields
     */

    protected L lexer;

    /*
     * Methods
     */

    protected abstract L lexer();

    protected abstract int initialState();

    protected void start(@NotNull CharSequence charSequence) {
        lexer.start(charSequence);
    }

    /*
     * Callbacks
     */

    @Before
    public void setUp() {
        lexer = lexer();
    }
}
