package org.elixir_lang.elixir_flex_lexer.group.sigil;

import com.intellij.psi.tree.IElementType;
import org.elixir_lang.ElixirFlexLexer;
import org.elixir_lang.elixir_flex_lexer.group.GroupTest;
import org.elixir_lang.psi.ElixirTypes;
import org.junit.Before;

import java.io.IOException;
import java.io.Reader;

import static org.junit.Assert.assertEquals;

/**
 * Created by luke.imhoff on 9/6/14.
 */
public abstract class Test extends GroupTest {
    protected IElementType fragmentType() {
        return ElixirTypes.SIGIL_FRAGMENT;
    }

    protected void reset(CharSequence charSequence) throws IOException {
        // start of "~x" + promoter to trigger GROUP state
        CharSequence fullCharSequence = "~x" + promoter() + charSequence;
        // consume ~
        super.reset(fullCharSequence);
        // consume x
        flexLexer.advance();
        // consume promoter
        flexLexer.advance();
    }

    protected abstract char promoter();

    @org.junit.Test
    public void singleQuote() throws IOException {
        reset("'");

        assertEquals(fragmentType(), flexLexer.advance());
        assertEquals(ElixirFlexLexer.GROUP, flexLexer.yystate());
    }

    @org.junit.Test
    public void doubleQuotes() throws IOException {
        reset("\"");

        assertEquals(fragmentType(), flexLexer.advance());
        assertEquals(ElixirFlexLexer.GROUP, flexLexer.yystate());
    }
}
