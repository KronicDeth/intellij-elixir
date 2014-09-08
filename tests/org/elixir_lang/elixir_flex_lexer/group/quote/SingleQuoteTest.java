package org.elixir_lang.elixir_flex_lexer.group.quote;

import com.intellij.psi.tree.IElementType;
import org.elixir_lang.ElixirFlexLexer;
import org.elixir_lang.psi.ElixirTypes;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.Reader;

import static org.junit.Assert.assertEquals;

/**
 * Created by luke.imhoff on 9/2/14.
 */
public class SingleQuoteTest extends org.elixir_lang.elixir_flex_lexer.group.quote.Test {
    @Override
    protected IElementType fragmentType() {
        return ElixirTypes.CHAR_LIST_FRAGMENT;
    }

    @Override
    protected void reset(CharSequence charSequence) throws IOException {
        // start of single quote to trigger GROUP state with terminator being single quote
        CharSequence fullCharSequence = "'" + charSequence;
        super.reset(fullCharSequence);
    }

    @Test
    public void singleQuote() throws IOException {
        reset("'");

        assertEquals(ElixirTypes.CHAR_LIST_TERMINATOR, flexLexer.advance());
        assertEquals(initialState, flexLexer.yystate());
    }

    @Test
    public void doubleQuotes() throws IOException {
        reset("\"");

        assertEquals(fragmentType(), flexLexer.advance());
        assertEquals(ElixirFlexLexer.GROUP, flexLexer.yystate());
    }
}
