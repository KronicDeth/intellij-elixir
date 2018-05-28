package org.elixir_lang.elixir_flex_lexer;

import org.elixir_lang.ElixirLexer;
import org.junit.Ignore;

import static org.elixir_lang.test.ElixirVersion.elixirSdkLevel;

@Ignore("abstract")
public abstract class Test extends org.elixir_lang.flex_lexer.Test<ElixirLexer> {
    /*
     * Methods
     */

    @Override
    protected ElixirLexer lexer() {
        ElixirLexer elixirLexer = new ElixirLexer();
        elixirLexer.setLevel(elixirSdkLevel());

        return elixirLexer;
    }
}
