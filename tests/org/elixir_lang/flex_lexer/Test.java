package org.elixir_lang.flex_lexer;

import com.intellij.lexer.Lexer;
import org.elixir_lang.junit.EscapedNameRunnerFactory;
import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.runners.Parameterized;


// @UseParametersRunnerFactory is @Inherited, so every parameterised subclass gets escaped test names without
// having to repeat the annotation. Subclasses that aren't parameterised ignore it.
@Parameterized.UseParametersRunnerFactory(EscapedNameRunnerFactory.class)
public abstract class Test<L extends Lexer> {
    /*
     * Fields
     */

    protected L lexer;

    /*
     * Methods
     */

    protected abstract L lexer();

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
